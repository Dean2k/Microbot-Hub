package net.runelite.client.plugins.microbot.shrekmining.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.Quest;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public class MiningRockLocations {

    public static List<LocationOption> getLocationsForRock(Rocks rock) {
        switch (rock) {
            case TIN:
                return getTinRockLocations();
            case COPPER:
                return getCopperRockLocations();
            case CLAY:
                return getClayRockLocations();
            case IRON:
                return getIronRockLocations();
            case SILVER:
                return getSilverRockLocations();
            case COAL:
                return getCoalRockLocations();
            case GOLD:
                return getGoldRockLocations();
            case GEM:
                return getGemRockLocations();
            case MITHRIL:
                return getMithrilRockLocations();
            case ADAMANTITE:
                return getAdamantiteRockLocations();
            case RUNITE:
                return getRuniteRockLocations();
            case BASALT:
                return getBasaltRockLocations();
            default:
                return new ArrayList<>();
        }
    }

    public static List<LocationOption> getAccessibleLocationsForRock(Rocks rock) {
        return getLocationsForRock(rock).stream()
                .filter(LocationOption::hasRequirements)
                .collect(Collectors.toList());
    }

    public static LocationOption getBestAccessibleLocation(Rocks rock) {
        List<LocationOption> accessibleLocations = getAccessibleLocationsForRock(rock);

        if (accessibleLocations.isEmpty()) {
            return null;
        }

        WorldPoint playerLocation = Rs2Player.getWorldLocation();
        if (playerLocation != null) {
            return accessibleLocations.stream()
                    .min((loc1, loc2) -> Integer.compare(
                            playerLocation.distanceTo(loc1.getWorldPoint()),
                            playerLocation.distanceTo(loc2.getWorldPoint())
                    ))
                    .orElse(accessibleLocations.get(0));
        }

        return accessibleLocations.get(0);
    }

    public static List<ResourceLocationOption> getResourceLocationsForRock(Rocks rock) {
        switch (rock) {
            case TIN:
                return getTinRockResourceLocations();
            case COPPER:
                return getCopperRockResourceLocations();
            case IRON:
                return getIronRockResourceLocations();
            case COAL:
                return getCoalRockResourceLocations();
            default:
                return new ArrayList<>();
        }
    }

    public static List<ResourceLocationOption> getAccessibleResourceLocationsForRock(Rocks rock) {
        return getResourceLocationsForRock(rock).stream()
                .filter(ResourceLocationOption::hasRequirements)
                .collect(Collectors.toList());
    }

    public static ResourceLocationOption getBestAccessibleResourceLocation(Rocks rock, int minResources) {
        List<ResourceLocationOption> accessibleLocations = getAccessibleResourceLocationsForRock(rock);

        if (accessibleLocations.isEmpty()) {
            return null;
        }

        List<ResourceLocationOption> suitableLocations = accessibleLocations.stream()
                .filter(location -> location.hasMinimumResources(minResources))
                .collect(Collectors.toList());

        if (suitableLocations.isEmpty()) {
            suitableLocations = accessibleLocations;
        }

        WorldPoint playerLocation = Rs2Player.getWorldLocation();
        if (playerLocation != null) {
            return suitableLocations.stream()
                    .max((loc1, loc2) -> Double.compare(
                            loc1.calculateResourceEfficiencyScore(playerLocation),
                            loc2.calculateResourceEfficiencyScore(playerLocation)
                    ))
                    .orElse(suitableLocations.get(0));
        }

        return suitableLocations.stream()
                .max((loc1, loc2) -> Integer.compare(
                        loc1.getNumberOfResources(),
                        loc2.getNumberOfResources()
                ))
                .orElse(suitableLocations.get(0));
    }

    private static List<ResourceLocationOption> getTinRockResourceLocations() {
        List<ResourceLocationOption> locations = new ArrayList<>();

        locations.add(new ResourceLocationOption(
                new WorldPoint(3149, 3148, 0),
                "Lumbridge Swamp West Mine",
                false,
                5
        ));

        locations.add(new ResourceLocationOption(
                new WorldPoint(3296, 3315, 0),
                "Al Kharid Mine",
                false,
                4
        ));

        return locations;
    }

    private static List<ResourceLocationOption> getCopperRockResourceLocations() {
        List<ResourceLocationOption> locations = new ArrayList<>();

        locations.add(new ResourceLocationOption(
                new WorldPoint(3296, 3315, 0),
                "Al Kharid Mine",
                false,
                7
        ));

        locations.add(new ResourceLocationOption(
                new WorldPoint(3149, 3148, 0),
                "Lumbridge Swamp West Mine",
                false,
                5
        ));

        return locations;
    }

    private static List<ResourceLocationOption> getIronRockResourceLocations() {
        List<ResourceLocationOption> locations = new ArrayList<>();

        return locations;
    }

    private static List<ResourceLocationOption> getCoalRockResourceLocations() {
        List<ResourceLocationOption> locations = new ArrayList<>();

        return locations;
    }

    private static List<LocationOption> getTinRockLocations() {
        List<LocationOption> locations = new ArrayList<>();

        locations.add(new LocationOption(
                new WorldPoint(3149, 3148, 0),
                "Lumbridge Swamp West Mine", false
        ));

        locations.add(new LocationOption(
                new WorldPoint(3181, 3377, 0),
                "Varrock South West Mine", false
        ));

        locations.add(new LocationOption(
                new WorldPoint(3296, 3315, 0),
                "Al Kharid Mine", false
        ));

        locations.add(new LocationOption(
                new WorldPoint(3034, 9822, 0),
                "Dwarven Mine", false
        ));

        return locations;
    }

    private static List<LocationOption> getCopperRockLocations() {
        List<LocationOption> locations = new ArrayList<>();

        locations.add(new LocationOption(
                new WorldPoint(3296, 3315, 0),
                "Al Kharid Mine", false
        ));

        locations.add(new LocationOption(
                new WorldPoint(3149, 3148, 0),
                "Lumbridge Swamp West Mine", false
        ));

        locations.add(new LocationOption(
                new WorldPoint(3181, 3377, 0),
                "Varrock South West Mine", false
        ));

        locations.add(new LocationOption(
                new WorldPoint(3034, 9822, 0),
                "Dwarven Mine", false
        ));

        return locations;
    }

    private static List<LocationOption> getClayRockLocations() {
        List<LocationOption> locations = new ArrayList<>();

        locations.add(new LocationOption(
                new WorldPoint(3181, 3377, 0),
                "Varrock South West Mine", false
        ));

        locations.add(new LocationOption(
                new WorldPoint(3034, 9822, 0),
                "Dwarven Mine", false
        ));

        return locations;
    }

    private static List<LocationOption> getIronRockLocations() {
        List<LocationOption> locations = new ArrayList<>();

        Map<Skill, Integer> miningGuildSkills = new HashMap<>();
        miningGuildSkills.put(Skill.MINING, 60);
        locations.add(new LocationOption(
                new WorldPoint(3046, 9756, 0),
                "Mining Guild (Members)",
                true,
                new HashMap<>(),
                miningGuildSkills,
                new HashMap<>(),
                new HashMap<>(),
                new HashMap<>()
        ));

        locations.add(new LocationOption(
                new WorldPoint(3296, 3315, 0),
                "Al Kharid Mine", false
        ));

        locations.add(new LocationOption(
                new WorldPoint(3285, 3363, 0),
                "Varrock South East Mine", false
        ));

        locations.add(new LocationOption(
                new WorldPoint(3034, 9822, 0),
                "Dwarven Mine", false
        ));

        locations.add(new LocationOption(
                new WorldPoint(2704, 3330, 0),
                "Ardougne South East Mine", true
        ));

        locations.add(new LocationOption(
                new WorldPoint(3086, 3763, 0),
                "Bandit Camp Mine (Members)",
                true
        ));

        return locations;
    }

    private static List<LocationOption> getSilverRockLocations() {
        List<LocationOption> locations = new ArrayList<>();

        locations.add(new LocationOption(
                new WorldPoint(3285, 3363, 0),
                "Varrock South East Mine",
                false
        ));

        locations.add(new LocationOption(
                new WorldPoint(3034, 9822, 0),
                "Dwarven Mine",
                false
        ));

        locations.add(new LocationOption(
                new WorldPoint(3296, 3315, 0),
                "Al Kharid Mine", false

        ));

        return locations;
    }

    private static List<LocationOption> getCoalRockLocations() {
        List<LocationOption> locations = new ArrayList<>();

        Map<Skill, Integer> miningGuildSkills = new HashMap<>();
        miningGuildSkills.put(Skill.MINING, 60);
        locations.add(new LocationOption(
                new WorldPoint(3046, 9756, 0),
                "Mining Guild (Members)", true,
                new HashMap<>(),
                miningGuildSkills,
                new HashMap<>(),
                new HashMap<>(),
                new HashMap<>()
        ));

        locations.add(new LocationOption(
                new WorldPoint(3034, 9822, 0),
                "Dwarven Mine", false
        ));

        locations.add(new LocationOption(
                new WorldPoint(3081, 3421, 0),
                "Barbarian Village Mine", false
        ));

        locations.add(new LocationOption(
                new WorldPoint(2569, 3462, 0),
                "Seers' Village Coal Trucks", true
        ));

        return locations;
    }

    private static List<LocationOption> getGoldRockLocations() {
        List<LocationOption> locations = new ArrayList<>();

        locations.add(new LocationOption(
                new WorldPoint(3034, 9822, 0),
                "Dwarven Mine", false
        ));

        locations.add(new LocationOption(
                new WorldPoint(3296, 3315, 0),
                "Al Kharid Mine", false
        ));

        Map<Skill, Integer> craftingGuildSkills = new HashMap<>();
        craftingGuildSkills.put(Skill.CRAFTING, 40);
        Map<Integer, Integer> craftingGuildItems = new HashMap<>();
        craftingGuildItems.put(ItemID.BROWN_APRON, 1);
        locations.add(new LocationOption(
                new WorldPoint(2938, 3283, 0),
                "Crafting Guild", false,
                new HashMap<>(),
                craftingGuildSkills,
                new HashMap<>(),
                new HashMap<>(),
                craftingGuildItems
        ));

        return locations;
    }

    private static List<LocationOption> getGemRockLocations() {
        List<LocationOption> locations = new ArrayList<>();

        Map<Quest, QuestState> shiloQuests = new HashMap<>();
        shiloQuests.put(Quest.SHILO_VILLAGE, QuestState.FINISHED);
        locations.add(new LocationOption(
                new WorldPoint(2824, 2997, 0),
                "Shilo Village Gem Mine", true,
                shiloQuests,
                new HashMap<>(),
                new HashMap<>(),
                new HashMap<>(),
                new HashMap<>()
        ));

        locations.add(new LocationOption(
                new WorldPoint(3296, 3315, 0),
                "Al Kharid Mine", false
        ));

        return locations;
    }

    private static List<LocationOption> getMithrilRockLocations() {
        List<LocationOption> locations = new ArrayList<>();

        Map<Skill, Integer> miningGuildSkills = new HashMap<>();
        miningGuildSkills.put(Skill.MINING, 60);
        locations.add(new LocationOption(
                new WorldPoint(3046, 9756, 0),
                "Mining Guild (Members)", true,
                new HashMap<>(),
                miningGuildSkills,
                new HashMap<>(),
                new HashMap<>(),
                new HashMap<>()
        ));

        locations.add(new LocationOption(
                new WorldPoint(3296, 3315, 0),
                "Al Kharid Mine", false
        ));

        locations.add(new LocationOption(
                new WorldPoint(3034, 9822, 0),
                "Dwarven Mine", false
        ));

        locations.add(new LocationOption(
                new WorldPoint(3229, 3148, 0),
                "Lumbridge Swamp East Mine", false
        ));

        return locations;
    }

    private static List<LocationOption> getAdamantiteRockLocations() {
        List<LocationOption> locations = new ArrayList<>();

        Map<Skill, Integer> miningGuildSkills = new HashMap<>();
        miningGuildSkills.put(Skill.MINING, 60);
        locations.add(new LocationOption(
                new WorldPoint(3046, 9756, 0),
                "Mining Guild (Members)", true,
                new HashMap<>(),
                miningGuildSkills,
                new HashMap<>(),
                new HashMap<>(),
                new HashMap<>()
        ));

        locations.add(new LocationOption(
                new WorldPoint(3296, 3315, 0),
                "Al Kharid Mine", false
        ));

        locations.add(new LocationOption(
                new WorldPoint(3229, 3148, 0),
                "Lumbridge Swamp East Mine", false
        ));

        Map<Quest, QuestState> neitiznotQuests = new HashMap<>();
        neitiznotQuests.put(Quest.THE_FREMENNIK_ISLES, QuestState.FINISHED);
        locations.add(new LocationOption(
                new WorldPoint(2335, 3808, 0),
                "Neitiznot Mine", true,
                neitiznotQuests,
                new HashMap<>(),
                new HashMap<>(),
                new HashMap<>(),
                new HashMap<>()
        ));

        return locations;
    }

    private static List<LocationOption> getRuniteRockLocations() {
        List<LocationOption> locations = new ArrayList<>();

        Map<Skill, Integer> miningGuildSkills = new HashMap<>();
        miningGuildSkills.put(Skill.MINING, 60);
        locations.add(new LocationOption(
                new WorldPoint(3046, 9756, 0),
                "Mining Guild (Members)", true,
                new HashMap<>(),
                miningGuildSkills,
                new HashMap<>(),
                new HashMap<>(),
                new HashMap<>()
        ));

        Map<Quest, QuestState> heroesQuests = new HashMap<>();
        heroesQuests.put(Quest.HEROES_QUEST, QuestState.FINISHED);
        locations.add(new LocationOption(
                new WorldPoint(2916, 3506, 0),
                "Heroes' Guild Mine", true,
                heroesQuests,
                new HashMap<>(),
                new HashMap<>(),
                new HashMap<>(),
                new HashMap<>()
        ));

        Map<Quest, QuestState> neitiznotQuests = new HashMap<>();
        neitiznotQuests.put(Quest.THE_FREMENNIK_ISLES, QuestState.FINISHED);
        locations.add(new LocationOption(
                new WorldPoint(2335, 3808, 0),
                "Neitiznot Mine", true,
                neitiznotQuests,
                new HashMap<>(),
                new HashMap<>(),
                new HashMap<>(),
                new HashMap<>()
        ));

        locations.add(new LocationOption(
                new WorldPoint(3058, 3884, 0),
                "Lava Maze Runite Mine (Wilderness)", false
        ));

        return locations;
    }

    private static List<LocationOption> getBasaltRockLocations() {
        List<LocationOption> locations = new ArrayList<>();

        Map<Quest, QuestState> weissQuests = new HashMap<>();
        weissQuests.put(Quest.MAKING_FRIENDS_WITH_MY_ARM, QuestState.FINISHED);
        locations.add(new LocationOption(
                new WorldPoint(2857, 3937, 0),
                "Weiss Basalt Mine", true,
                weissQuests,
                new HashMap<>(),
                new HashMap<>(),
                new HashMap<>(),
                new HashMap<>()
        ));

        return locations;
    }
}
