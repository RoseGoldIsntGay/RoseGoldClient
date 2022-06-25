package rosegoldclient.features.exclude;

import java.util.ArrayList;

import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import rosegoldclient.Main;
import rosegoldclient.events.TickEndEvent;
import rosegoldclient.utils.CheetoRotation;
import rosegoldclient.utils.ColorUtils;
import rosegoldclient.utils.RenderUtils;
import rosegoldclient.utils.Rotation;
import rosegoldclient.utils.TimeHelper;
import rosegoldclient.utils.Utils;
import rosegoldclient.utils.VecUtils;
import rosegoldclient.utils.pathfinding.Pathfinder;

public class Pathfinding {
    private static int stuckTicks = 0;
    private static BlockPos oldPos;
    private static BlockPos curPos;
    private static TimeHelper unstucker;

    public static void init() {
        stuckTicks = 0;
        oldPos = null;
        curPos = null;
        if (Pathfinder.hasPath()) {
            Utils.sendMessage("Navigating to: " + Pathfinder.getGoal());
        }
    }

    private void stopMovement() {
        KeyBinding.setKeyBindState(Main.mc.gameSettings.keyBindLeft.getKeyCode(), false);
        KeyBinding.setKeyBindState(Main.mc.gameSettings.keyBindRight.getKeyCode(), false);
        KeyBinding.setKeyBindState(Main.mc.gameSettings.keyBindForward.getKeyCode(), false);
        KeyBinding.setKeyBindState(Main.mc.gameSettings.keyBindBack.getKeyCode(), false);
        KeyBinding.setKeyBindState(Main.mc.gameSettings.keyBindJump.getKeyCode(), false);
    }

    @SubscribeEvent
    public void onTick(TickEndEvent event) {
        if (Main.mc.currentScreen != null && !(Main.mc.currentScreen instanceof GuiChat)) return;

        if (Pathfinder.hasPath()) {
            if (++stuckTicks >= Main.configFile.pathfindingUnstuckTime * 20) {
                curPos = Main.mc.player.getPosition();
                if (oldPos != null && Math.sqrt(curPos.distanceSq(oldPos)) <= 0.1) {
                    KeyBinding.setKeyBindState(Main.mc.gameSettings.keyBindJump.getKeyCode(), true);
                    KeyBinding.setKeyBindState(Main.mc.gameSettings.keyBindRight.getKeyCode(), true);
                    (unstucker = new TimeHelper()).reset();
                    return;
                }
                oldPos = curPos;
                stuckTicks = 0;
            }
            if (unstucker != null && unstucker.hasReached(2000L)) {
                KeyBinding.setKeyBindState(Main.mc.gameSettings.keyBindJump.getKeyCode(), false);
                KeyBinding.setKeyBindState(Main.mc.gameSettings.keyBindRight.getKeyCode(), false);
                unstucker = null;
            }
            Vec3d first = Pathfinder.getCurrent().addVector(0.5, 0.0, 0.5);
            Rotation needed = CheetoRotation.getRotation(first);
            needed.setPitch(Main.mc.player.rotationPitch);
            if (VecUtils.getHorizontalDistance(Main.mc.player.getPositionVector(), first) > 0.69) {
                if (CheetoRotation.done && needed.getYaw() < 135.0f) {
                    CheetoRotation.setup(needed, (long) Main.configFile.pathfindingLookTime);
                }
                if (Pathfinder.hasNext()) {
                    Vec3d next = Pathfinder.getNext().addVector(0.5, 0.0, 0.5);
                    double xDiff = Math.abs(Math.abs(next.x) - Math.abs(first.x));
                    double zDiff = Math.abs(Math.abs(next.z) - Math.abs(first.z));
                    Main.mc.player.setSprinting((xDiff == 1.0 && zDiff == 0.0) || (xDiff == 0.0 && zDiff == 1.0));
                }
                Vec3d lastTick = new Vec3d(Main.mc.player.lastTickPosX, Main.mc.player.lastTickPosY, Main.mc.player.lastTickPosZ);
                Vec3d diffy = Main.mc.player.getPositionVector().subtract(lastTick);
                diffy = diffy.addVector(diffy.x * 4.0, 0.0, diffy.z * 4.0);
                Vec3d nextTick = Main.mc.player.getPositionVector().add(diffy);
                stopMovement();
                ArrayList<KeyBinding> neededPresses = VecUtils.getNeededKeyPresses(Main.mc.player.getPositionVector(), first);
                if (Math.abs(nextTick.distanceTo(first) - Main.mc.player.getPositionVector().distanceTo(first)) <= 0.05 || nextTick.distanceTo(first) <= Main.mc.player.getPositionVector().distanceTo(first)) {
                    neededPresses.forEach(v -> KeyBinding.setKeyBindState(v.getKeyCode(), true));
                }
                if (Math.abs(Main.mc.player.posY - first.y) > 0.5) {
                    KeyBinding.setKeyBindState(Main.mc.gameSettings.keyBindJump.getKeyCode(), Main.mc.player.posY < first.y);
                }
                else {
                    KeyBinding.setKeyBindState(Main.mc.gameSettings.keyBindJump.getKeyCode(), false);
                }
            }
            else {
                CheetoRotation.reset();
                if (!Pathfinder.goNext()) {
                    stopMovement();
                }
            }
        }
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        if (Pathfinder.path != null && !Pathfinder.path.isEmpty()) {
            RenderUtils.drawLines(Pathfinder.path, 2.0f, event.getPartialTicks());
            Vec3d last = Pathfinder.path.get(Pathfinder.path.size() - 1).addVector(0.0, -1.0, 0.0);
            RenderUtils.drawBlockESP(new BlockPos(last), ColorUtils.getChroma(3000.0f, (int)(last.x + last.y + last.z)), event.getPartialTicks());
        }
        if (Main.mc.currentScreen != null && !(Main.mc.currentScreen instanceof GuiChat)) {
            return;
        }
        if (!CheetoRotation.done) {
            CheetoRotation.update();
        }
    }

}
