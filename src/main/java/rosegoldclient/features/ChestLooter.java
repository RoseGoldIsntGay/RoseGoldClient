package rosegoldclient.features;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.Slot;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import rosegoldclient.Config;
import rosegoldclient.Main;

public class ChestLooter {
	private long lastClickTime = 0L;
	private final List<Map.Entry<Slot, Boolean>> items = new ArrayList<>(27);
	private Set<String> leftClick;
	@SubscribeEvent
	public void onGuiOpen(GuiOpenEvent event) {
		items.clear();
	}

	@SubscribeEvent
	public void onGuiDraw(GuiScreenEvent.BackgroundDrawnEvent event) {
		
		
		ContainerChest container;
		if (!Main.configFile.chestLoot || !(event.getGui() instanceof GuiChest))
			return;
		this.leftClick = rosegoldclient.utils.StringUtils.toSet(Config.INSTANCE.autoLootedItems);
		if (((GuiChest) event.getGui()).inventorySlots instanceof ContainerChest
				&& StringUtils
						.stripControlCodes((container = (ContainerChest) ((GuiChest) event.getGui()).inventorySlots)
								.getLowerChestInventory().getDisplayName().getUnformattedText())
						.contains("Loot Chest I")) {
			if (items.isEmpty()) {
				HashMap<Slot, Boolean> map = new HashMap<>();
				List<Slot> chestSlots = container.inventorySlots.subList(0, 27);
				for (Slot slot : chestSlots) {
					if (!slot.getHasStack())
						continue;
					String itemName = StringUtils.stripControlCodes(slot.getStack().getDisplayName());
					if (leftClick.stream().anyMatch(itemName::contains)) {
						boolean flag = itemName.contains("Powder"); // powders need to be shift clicked.
						map.put(slot, flag );
						continue;
					}
				}
				items.addAll(map.entrySet());
			} else if (System.currentTimeMillis() - lastClickTime > (long) Main.configFile.chestLootDelay) {
				Main.mc.playerController.windowClick(Main.mc.player.openContainer.windowId,
						items.get(0).getKey().slotNumber, 0,
						items.get(0).getValue() ? ClickType.QUICK_MOVE : ClickType.PICKUP, Main.mc.player);
				items.remove(0);
				lastClickTime = System.currentTimeMillis();
			}
		}
	}
}
