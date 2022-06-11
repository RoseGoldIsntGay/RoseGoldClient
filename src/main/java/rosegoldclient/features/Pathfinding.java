package rosegoldclient.features;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import rosegoldclient.Main;
import rosegoldclient.events.MillisecondEvent;
import rosegoldclient.utils.Utils;
import rosegoldclient.utils.pathfinding.Pathfinder;

public class Pathfinding {

    private static long lastTeleportTime = 0;

    @SubscribeEvent
    public void onMillisecond(MillisecondEvent event) {
        if (System.currentTimeMillis() - lastTeleportTime < (long) Main.configFile.cursorTeleportPathfindSpeed - 1) return;
        if (Pathfinder.hasPath()) {
            if (Pathfinder.hasNext()) {
                Vec3d next = Pathfinder.getNext();
                tpToBlock(new BlockPos(next));
                lastTeleportTime = System.currentTimeMillis();
            }
            Pathfinder.goNext();
        }
    }

    public static void init() {
        if (Pathfinder.hasPath()) {
            Utils.sendMessage("Navigating to: " + Pathfinder.getGoal());
        }
    }

    private static void tpToBlock(BlockPos blockPos) {
        Main.mc.player.setPosition(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5);
        switch (Main.configFile.cursorTeleportResetVelocity) {
            case 1:
                Main.mc.player.setVelocity(0, Main.mc.player.motionY, 0);
            case 2:
                Main.mc.player.setVelocity(Main.mc.player.motionX, 0, Main.mc.player.motionZ);
            case 3:
                Main.mc.player.setVelocity(Main.mc.player.motionX, Main.mc.player.motionY, Main.mc.player.motionZ);
        }
    }
}
