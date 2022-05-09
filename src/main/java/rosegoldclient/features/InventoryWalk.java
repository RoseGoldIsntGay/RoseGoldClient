package rosegoldclient.features;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiCommandBlock;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiEditCommandBlockMinecart;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.MovementInput;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import rosegoldclient.Main;

/*
 * Code modified from: https://github.com/PieKing1215/InvMove-Forge
 */

public class InventoryWalk {

    @SubscribeEvent
    public void onInputUpdate(InputUpdateEvent event) {
        if (allowMovementInScreen(Main.mc.currentScreen)) {
            KeyBinding.updateKeyBindState();
            KeyBinding.setKeyBindState(Minecraft.getMinecraft().gameSettings.keyBindDrop.getKeyCode(), false);
            manualTickMovement(event.getMovementInput(), Minecraft.getMinecraft().player.isSneaking() || Minecraft.getMinecraft().player.isInWater(), Minecraft.getMinecraft().player.isSpectator());
            Minecraft.getMinecraft().player.setSprinting(rawIsKeyDown(Minecraft.getMinecraft().gameSettings.keyBindSprint));
        }
    }

    public static void manualTickMovement(MovementInput input, boolean slow, boolean noDampening) {
        input.forwardKeyDown = rawIsKeyDown(Minecraft.getMinecraft().gameSettings.keyBindForward);
        input.backKeyDown = rawIsKeyDown(Minecraft.getMinecraft().gameSettings.keyBindBack);
        input.leftKeyDown = rawIsKeyDown(Minecraft.getMinecraft().gameSettings.keyBindLeft);
        input.rightKeyDown = rawIsKeyDown(Minecraft.getMinecraft().gameSettings.keyBindRight);
        float f = input.forwardKeyDown == input.backKeyDown ? 0.0f : (input.moveForward = (float)(input.forwardKeyDown ? 1 : -1));
        input.moveStrafe = input.leftKeyDown == input.rightKeyDown ? 0.0f : (float)(input.leftKeyDown ? 1 : -1);
        input.jump = rawIsKeyDown(Minecraft.getMinecraft().gameSettings.keyBindJump);
        rawIsKeyDown(Minecraft.getMinecraft().gameSettings.keyBindSneak);
        input.sneak = false;
        if (!noDampening && slow) {
            input.moveStrafe = (float)((double)input.moveStrafe * 0.3);
            input.moveForward = (float)((double)input.moveForward * 0.3);
        }
    }

    public static boolean rawIsKeyDown(KeyBinding key) {
        try {
            return ObfuscationReflectionHelper.getPrivateValue(KeyBinding.class, key, "field_74513_e");
        } catch (Exception var2) {
            System.out.println("Failed to access KeyBinding.pressed on \"" + key + "\": " + var2.getMessage());
            var2.printStackTrace();
            return false;
        }
    }

    public static boolean allowMovementInScreen(GuiScreen screen) {
        if(!Main.configFile.invWalk) return false;
        if (screen == null) {
            return false;
        }
        if (screen instanceof GuiChat) {
            return false;
        }
        if (screen instanceof GuiCommandBlock) {
            return false;
        }
        if (screen instanceof GuiEditCommandBlockMinecart) {
            return false;
        }
        if (screen instanceof GuiEditSign) {
            return false;
        }
        return true;
    }
}
