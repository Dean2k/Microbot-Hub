package net.runelite.client.plugins.microbot.shrekfighter.loot;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.plugins.grounditems.GroundItem;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.api.tileitem.Rs2TileItemCache;
import net.runelite.client.plugins.microbot.api.tileitem.Rs2TileItemQueryable;
import net.runelite.client.plugins.microbot.api.tileitem.models.Rs2TileItemModel;
import net.runelite.client.plugins.microbot.shrekfighter.ShrekFighterConfig;
import net.runelite.client.plugins.microbot.shrekfighter.ShrekFighterPlugin;
import net.runelite.client.plugins.microbot.shrekfighter.enums.DefaultLooterStyle;
import net.runelite.client.plugins.microbot.shrekfighter.enums.State;
import net.runelite.client.plugins.microbot.util.grounditem.LootingParameters;
import net.runelite.client.plugins.microbot.util.grounditem.Rs2GroundItem;
import net.runelite.client.plugins.microbot.util.grounditem.Rs2LootEngine;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.inventory.Rs2ItemModel;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

@Slf4j
public class LootScript extends Script {

    private static final int DEFAULT_MIN_STACK_EXCLUSIVE_ARROWS = 9; // allow 2+
    private static final int DEFAULT_MIN_STACK_EXCLUSIVE_RUNES  = 1; // allow 2+

    private int minFreeSlots = 0;

    public LootScript() {}

    public boolean run(ShrekFighterConfig config) {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                minFreeSlots = config.bank() ? config.minFreeSlots() : 0;

                if (!super.run()) return;
                if (!Microbot.isLoggedIn()) return;
                if (!config.toggleLootItems()) return;

                final State st = ShrekFighterPlugin.getState();
                if (st == State.BANKING || st == State.WALKING) return;

                boolean waitingForLoot = ShrekFighterPlugin.isWaitingForLoot();
                if ((Rs2Inventory.isFull() || Rs2Inventory.emptySlotCount() <= minFreeSlots) && !config.eatFoodForSpace()) {
                    log.info("Inv Full");
                    return;
                }
                // isInCombat() lingers for 10s after last hitsplat — bypass it while waiting for loot drops
                if (!waitingForLoot && Rs2Player.isInCombat() && !config.toggleForceLoot()) {
                    return;
                }

                LootingParameters params = new LootingParameters(
                        config.minPriceOfItemsToLoot(),
                        config.maxPriceOfItemsToLoot(),
                        config.attackRadius(),
                        /* minQuantity */ 1,
                        /* minInvSlots */ minFreeSlots,
                        config.toggleDelayedLooting(),
                        config.toggleOnlyLootMyItems()
                );
                params.setEatFoodForSpace(config.eatFoodForSpace());

                // Use the live tile-item cache for looting.  The deprecated Rs2GroundItem path
                // builds its menu entry from a stale GroundItem conversion; the tile-item model
                // carries the tile's actual local location, so "Take" lands on the right tile.
                Rs2LootEngine.Builder builder = Rs2LootEngine.with(params)
                        .withLootAction(this::lootViaTileItemCache);

                // custom filter
                if (config.looterStyle() == DefaultLooterStyle.ITEM_LIST || config.looterStyle() == DefaultLooterStyle.MIXED) {
                    addCustomNames(builder, config.listOfItemsToLoot());
                }

                if (config.looterStyle() == DefaultLooterStyle.GE_PRICE_RANGE || config.looterStyle() == DefaultLooterStyle.MIXED) builder.addByValue();
                if (config.toggleBuryBones())       builder.addBones();
                if (config.toggleScatter())         builder.addAshes();
                if (config.toggleLootCoins())       builder.addCoins();
                if (config.toggleLootUntradables()) builder.addUntradables();
                if (config.toggleLootArrows())      builder.addArrows(DEFAULT_MIN_STACK_EXCLUSIVE_ARROWS);
                if (config.toggleLootRunes())       builder.addRunes(DEFAULT_MIN_STACK_EXCLUSIVE_RUNES);

                // Execute one combined, distance-sorted looting pass
                boolean looted = builder.loot();
                if (!looted && log.isDebugEnabled()) {
                    log.debug("LootScript: loot pass completed without clearing all targets");
                }

                if (config.toggleReequipArrows()) {
                    reequipMatchingArrows();
                }

            } catch (Exception ex) {
                Microbot.log("LootScript: " + ex.getMessage());
            }
        }, 0, 200, TimeUnit.MILLISECONDS);

        return true;
    }

    private void reequipMatchingArrows() {
        Rs2ItemModel ammo = Rs2Equipment.get(EquipmentInventorySlot.AMMO);
        if (ammo == null) return;
        Rs2ItemModel arrows = Rs2Inventory.get(ammo.getId());
        if (arrows != null) {
            Rs2Inventory.interact(arrows, "Wield");
        }
    }

    /**
     * Loot action that resolves the live {@link Rs2TileItemModel} from the tile-item cache
     * before clicking "Take".  This avoids the stale GroundItem coordinate conversion that
     * was causing clicks to animate client-side without the server registering the loot.
     */
    private void lootViaTileItemCache(GroundItem groundItem) {
        if (groundItem == null || groundItem.getLocation() == null) return;

        Rs2TileItemModel liveItem = new Rs2TileItemQueryable()
                .withId(groundItem.getId())
                .where(m -> groundItem.getLocation().equals(m.getWorldLocation()))
                .first();

        if (liveItem != null) {
            if (log.isDebugEnabled()) {
                log.debug("LootScript: taking {} at {} via Rs2TileItemCache", groundItem.getName(), groundItem.getLocation());
            }
            liveItem.pickup();
        } else {
            // Cache miss: fall back to the deprecated path rather than dropping the item.
            if (log.isDebugEnabled()) {
                log.debug("LootScript: tile-item cache miss for {} at {}, falling back to coreLoot", groundItem.getName(), groundItem.getLocation());
            }
            Rs2GroundItem.coreLoot(groundItem);
        }
    }

    /**
     * Adds a custom "by names" intent sourced from the config's comma-separated list.
     * (We use a custom predicate so we don't depend on params.getNames()).
     */
    private void addCustomNames(Rs2LootEngine.Builder builder, String csvNames) {
        if (csvNames == null) return;
        final Set<String> needles = new HashSet<>();
        Arrays.stream(csvNames.split(","))
                .map(s -> s == null ? "" : s.trim().toLowerCase())
                .filter(s -> !s.isEmpty())
                .forEach(needles::add);

        if (needles.isEmpty()) return;

        Predicate<GroundItem> byNames = gi -> {
            final String n = gi.getName() == null ? "" : gi.getName().trim().toLowerCase();
            for (String needle : needles) {
                if (n.contains(needle)) return true;
            }
            return false;
        };

        builder.addCustom("names", byNames, /*ignoredLower*/ null);
    }


    @Override
    public void shutdown() {
        super.shutdown();
    }
}
