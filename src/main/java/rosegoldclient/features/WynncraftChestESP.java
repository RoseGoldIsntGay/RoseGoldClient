package rosegoldclient.features;

import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import rosegoldclient.Main;
import rosegoldclient.config.Config;
import rosegoldclient.events.SettingChangeEvent;
import rosegoldclient.events.TickEndEvent;
import rosegoldclient.utils.RenderUtils;

import java.awt.*;
import java.util.HashSet;

public class WynncraftChestESP {
    public static HashSet<BlockPos> chests = new HashSet<>();

    private static HashSet<BlockPos> toRender = new HashSet<>();
    private static BlockPos lastCheckedPosition = null;

    @SubscribeEvent
    public void onTick(TickEndEvent event) {
        if (Main.mc.player == null || Main.mc.world == null) return;
        if (!Config.wynnChestESP) return;
        if (Config.wynnChestESPRange == 0) {
            toRender.clear();
            toRender.addAll(chests);
            return;
        }
        BlockPos playerPosition = Main.mc.player.getPosition();
        if ((lastCheckedPosition == null || !lastCheckedPosition.equals(playerPosition))) {
            toRender.clear();
            lastCheckedPosition = playerPosition;
            for(BlockPos blockPos : chests) {
                double dist = blockPos.getDistance(playerPosition.getX(), playerPosition.getY(), playerPosition.getZ());
                if(Config.wynnChestESPRange >= dist) {
                    toRender.add(blockPos);
                }
            }
        }
    }

    @SubscribeEvent
    public void onSettingsChanges(SettingChangeEvent event) {
        if (event.setting.name.equals("Wynncraft Chest ESP Range")) {
            lastCheckedPosition = null;
        }
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        if (!Config.wynnChestESP) return;
        for (BlockPos block : toRender) {
            RenderUtils.drawBlockESP(block, new Color(255, 128, 0), event.getPartialTicks());
        }
    }
}
