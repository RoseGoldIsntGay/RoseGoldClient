package rosegoldclient.utils;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.util.text.TextComponentTranslation;
import rosegoldclient.Main;
import rosegoldclient.config.Config;

public class Utils {
    public static void sendMessage(String message) {
        if(Config.silentMode) return;
        if(Main.mc.player != null && Main.mc.world != null) {
            if(!message.contains("§")) {
                message = message.replace("&", "\u00a7");
            }
            Main.mc.player.sendMessage(new TextComponentTranslation(message));
        }
    }

    public static void sendMessageAsPlayer(String message) {
        Main.mc.player.sendChatMessage(message);
    }

    public static void sendModMessage(String message) {
        sendMessage("&f[&aRoseGoldClient&f] " + message);
    }

    public static String getLogo() {
        return "logo";
    }

    public static String getGuiName(GuiScreen gui) {
        if(gui instanceof GuiChest) {
            return ((ContainerChest) ((GuiChest) gui).inventorySlots).getLowerChestInventory().getDisplayName().getUnformattedText();
        }
        return "";
    }
}
