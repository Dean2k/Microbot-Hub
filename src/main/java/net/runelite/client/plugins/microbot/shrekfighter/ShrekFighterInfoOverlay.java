package net.runelite.client.plugins.microbot.shrekfighter;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;
@Slf4j
public class ShrekFighterInfoOverlay extends OverlayPanel {
    private final ShrekFighterConfig config;

    @Inject
    ShrekFighterInfoOverlay(ShrekFighterPlugin plugin, ShrekFighterConfig config) {
        super(plugin);
        this.config = config;
        setPosition(OverlayPosition.TOP_LEFT);
        setNaughty();
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        try {
            panelComponent.setPreferredSize(new Dimension(250, 400));
            panelComponent.getChildren().add(TitleComponent.builder()
                    .text("🦆 Shrek Fighter 🦆")
                    .color(Color.ORANGE)
                    .build());

            panelComponent.getChildren().add(LineComponent.builder().build());
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Kill Count: ")
                    .right(String.valueOf(ShrekFighterPlugin.getKillCount()))
                    .build());

            panelComponent.getChildren().add(LineComponent.builder().build());
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Slayer Mode: ")
                    .right(config.slayerMode() ? "Enabled" : "Disabled")
                    .build());

            if (config.slayerMode()) {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Slayer Task: ")
                        .right(config.slayerTask())
                        .build());
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Slayer Task Location: ")
                        .right(config.slayerLocation())
                        .build());
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Remaining kills: ")
                        .right(String.valueOf(config.remainingSlayerKills()))
                        .build());
                panelComponent.getChildren().add(LineComponent.builder().build());
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Slayer Has Task Weakness: ")
                        .right(config.slayerHasTaskWeakness() ? "Yes" : "No")
                        .build());
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Slayer Task Weakness Item: ")
                        .right(config.slayerTaskWeaknessItem())
                        .build());
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Slayer Task Weakness Threshold: ")
                        .right(String.valueOf(config.slayerTaskWeaknessThreshold()))
                        .build());
            }
            panelComponent.getChildren().add(LineComponent.builder().build());
            panelComponent.getChildren().add(LineComponent.builder()
                    .left(Microbot.status)
                    .right("Version:" + ShrekFighterPlugin.version)
                    .build());
        } catch (Exception ex) {
            Microbot.logStackTrace(this.getClass().getSimpleName(), ex);
        }
        return super.render(graphics);
    }

}
