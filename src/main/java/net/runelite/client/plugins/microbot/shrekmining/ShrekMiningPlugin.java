package net.runelite.client.plugins.microbot.shrekmining;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.microbot.PluginConstants;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = PluginConstants.SHREK + "Shrek Mining",
        description = "Mines and banks ores",
        tags = {"mining", "microbot", "skilling"},
        version = ShrekMiningPlugin.version,
        minClientVersion = "2.0.13",
        cardUrl = "",
        iconUrl = "",
        enabledByDefault = PluginConstants.DEFAULT_ENABLED,
        isExternal = PluginConstants.IS_EXTERNAL
)
@Slf4j
public class ShrekMiningPlugin extends Plugin {
    public static final String version = "1.0.1";
    @Inject
    private ShrekMiningConfig config;
    @Provides
    ShrekMiningConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(ShrekMiningConfig.class);
    }

    @Inject
    private OverlayManager overlayManager;
    @Inject
    private ShrekMiningOverlay shrekMiningOverlay;
    @Inject
    private ShrekMiningSceneOverlay shrekMiningSceneOverlay;

    @Inject
    ShrekMiningScript shrekMiningScript;


    @Override
    protected void startUp() throws AWTException {
        if (overlayManager != null) {
            overlayManager.add(shrekMiningOverlay);
            overlayManager.add(shrekMiningSceneOverlay);
        }
        shrekMiningScript.run(config);
    }

    protected void shutDown() {
        shrekMiningScript.shutdown();
        overlayManager.remove(shrekMiningOverlay);
        overlayManager.remove(shrekMiningSceneOverlay);
    }
}
