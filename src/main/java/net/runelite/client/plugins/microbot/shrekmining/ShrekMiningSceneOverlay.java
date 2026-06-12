package net.runelite.client.plugins.microbot.shrekmining;

import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.*;

import static net.runelite.client.ui.overlay.OverlayUtil.renderPolygon;

public class ShrekMiningSceneOverlay extends Overlay {

    private static final Color AREA_BORDER_COLOR = new Color(0, 255, 255, 127);
    private static final Color ROCK_HIGHLIGHT_COLOR = new Color(255, 255, 0, 127);

    private final Client client;
    private final ShrekMiningConfig config;

    @Inject
    ShrekMiningSceneOverlay(Client client, ShrekMiningConfig config) {
        this.client = client;
        this.config = config;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
        setNaughty();
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        renderCurrentRock(graphics);
        renderStrayArea(graphics);
        return null;
    }

    private void renderCurrentRock(Graphics2D graphics) {
        GameObject rock = ShrekMiningScript.currentRock;
        if (rock == null) return;

        Polygon poly = Perspective.getCanvasTileAreaPoly(client, rock.getLocalLocation(), 3);
        if (poly != null) {
            renderPolygon(graphics, poly, ROCK_HIGHLIGHT_COLOR);
        }
    }

    private void renderStrayArea(Graphics2D graphics) {
        WorldPoint center = ShrekMiningScript.strayCenter;
        if (center == null) return;

        int radius = config.distanceToStray();
        if (radius <= 0) return;

        LocalPoint lp = LocalPoint.fromWorld(client.getTopLevelWorldView(), center);
        if (lp == null) return;

        Polygon poly = Perspective.getCanvasTileAreaPoly(client, lp, radius * 2);
        if (poly != null) {
            renderPolygon(graphics, poly, AREA_BORDER_COLOR);
        }
    }
}

