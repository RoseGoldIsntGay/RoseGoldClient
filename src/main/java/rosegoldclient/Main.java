package rosegoldclient;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.inventory.Slot;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.apache.commons.codec.digest.DigestUtils;
import org.lwjgl.input.Keyboard;
import rosegoldclient.commands.*;
import rosegoldclient.config.Config;
import rosegoldclient.config.ConfigLogic;
import rosegoldclient.config.MainCommand;
import rosegoldclient.config.settings.Setting;
import rosegoldclient.events.KeybindEnabledEvent;
import rosegoldclient.events.SecondEvent;
import rosegoldclient.events.SettingChangeEvent;
import rosegoldclient.events.TickEndEvent;
import rosegoldclient.features.*;
import rosegoldclient.utils.ArrayUtils;
import rosegoldclient.utils.DevUtils;
import rosegoldclient.utils.RotationUtils;
import rosegoldclient.utils.Utils;

import java.io.*;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Mod(modid = Main.MODID, name = Main.NAME, version = Main.VERSION, clientSideOnly = true)
public class Main {
    public static final String MODID = "rosegoldclient";
    public static final String NAME = "RoseGoldClient";
    public static final String VERSION = "0.2.1";

    public static Minecraft mc = Minecraft.getMinecraft();

    public static ArrayList<String> cheater = new ArrayList<>();
    public static GuiScreen guiToOpen = null;
    public static JsonObject rgc;
    public static ArrayList<Setting> settings = ConfigLogic.collect(Config.class);
    public static ArrayList<KeyBinding> keybinds = new ArrayList<>();

    public static boolean killAura = false;
    public static boolean spellAura = false;
    public static boolean doPhase = false;

    public static final List<String> alphaNumeric = Arrays.asList("0123456789 abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".split(""));
    public static List<String> shuffle = Arrays.asList("0123456789 abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".split(""));

    public static boolean firstLogin = false;
    public static boolean anyLogin = true;
    public static boolean banned = false;
    private boolean issue = false;
    private String id = "";

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        File directory = new File(event.getModConfigurationDirectory(), "rosegoldclient");
        if (!directory.exists()) {
            firstLogin = true;
            directory.mkdirs();
        }
        File kaSettings = new File(directory, "kaSettings.json");
        File saSettings = new File(directory, "saSettings.json");

        try {
            if (kaSettings.exists()) {
                Reader reader = Files.newBufferedReader(Paths.get("config/rosegoldclient/kaSettings.json"));
                Type type = new TypeToken<HashSet<String>>() {
                }.getType();
                KillAuraFilter.KASettings = new Gson().fromJson(reader, type);
            }
            if (saSettings.exists()) {
                Reader reader = Files.newBufferedReader(Paths.get("config/rosegoldclient/saSettings.json"));
                Type type = new TypeToken<HashSet<String>>() {
                }.getType();
                SpellAuraFilter.SASettings = new Gson().fromJson(reader, type);
            }

            InputStreamReader inputStreamReader = new InputStreamReader(mc.getResourceManager().getResource(new ResourceLocation("rosegoldclient", "lootruns/chests/chests.json")).getInputStream());

            JsonArray jsonArray = (new JsonParser()).parse(Objects.requireNonNull(inputStreamReader)).getAsJsonArray();
            for (JsonElement jsonElement : jsonArray) {
                WynncraftChestESP.chests.add(new BlockPos((int) jsonElement.getAsJsonObject().get("x").getAsFloat(), (int) jsonElement.getAsJsonObject().get("y").getAsFloat(), (int) jsonElement.getAsJsonObject().get("z").getAsFloat()));
            }

        } catch (Exception e) {
            System.out.println("Error while loading RoseGoldClient config files");
            e.printStackTrace();
        }

        Collections.shuffle(shuffle);

        try {
            rgc = getJson("https://gist.github.com/RoseGoldIsntGay/bf410fc3dec34a1f9348d896fafd00dc/raw/").getAsJsonObject();
        } catch (Exception e) {
            e.printStackTrace();
            issue = true;
        }

        ClientCommandHandler.instance.registerCommand(new MainCommand());
        id = mc.getSession().getPlayerID();
        ConfigLogic.load();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        if (issue) return;
        JsonArray jsonArray = rgc.get("cheaters").getAsJsonArray();
        for (JsonElement jsonElement : jsonArray) {
            cheater.add(jsonElement.getAsString());
        }

        for (String cheat : cheater) {
            if (DigestUtils.sha256Hex(id + id).equals(cheat)) {
                ArrayUtils.copy(id, cheat);
            }
        }

        keybinds.add(new KeyBinding("Kill Aura", Keyboard.KEY_NONE, "RoseGoldClient - Combat")); //0
        keybinds.add(new KeyBinding("Phase", Keyboard.KEY_NONE, "RoseGoldClient - Movement")); //1
        keybinds.add(new KeyBinding("Spell Aura", Keyboard.KEY_NONE, "RoseGoldClient - Combat")); //2
        keybinds.add(new KeyBinding("Copy NBT Data", Keyboard.KEY_NONE, "RoseGoldClient")); //3
        keybinds.add(new KeyBinding("Cast RRR", Keyboard.KEY_NONE, "RoseGoldClient - Macros")); //4
        keybinds.add(new KeyBinding("Cast RLR", Keyboard.KEY_NONE, "RoseGoldClient - Macros")); //5
        keybinds.add(new KeyBinding("Cast RLL", Keyboard.KEY_NONE, "RoseGoldClient - Macros")); //6
        keybinds.add(new KeyBinding("Cast RRL", Keyboard.KEY_NONE, "RoseGoldClient - Macros")); //7

        MinecraftForge.EVENT_BUS.register(new TickEndEvent());
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new RotationUtils());
        MinecraftForge.EVENT_BUS.register(new ChestAura());
        MinecraftForge.EVENT_BUS.register(new Phase());
        MinecraftForge.EVENT_BUS.register(new KillAura());
        MinecraftForge.EVENT_BUS.register(new BlockBanPacket());
        MinecraftForge.EVENT_BUS.register(new NoRotate());
        MinecraftForge.EVENT_BUS.register(new Watermark());
        MinecraftForge.EVENT_BUS.register(new SpellAura());
        MinecraftForge.EVENT_BUS.register(new SpellCaster());
        MinecraftForge.EVENT_BUS.register(new WynncraftChestESP());

        ClientCommandHandler.instance.registerCommand(new KillAuraFilter());
        ClientCommandHandler.instance.registerCommand(new SpellAuraFilter());
        ClientCommandHandler.instance.registerCommand(new SelfBan());
        ClientCommandHandler.instance.registerCommand(new AddChest());
        ClientCommandHandler.instance.registerCommand(new RemoveChest());
        ClientCommandHandler.instance.registerCommand(new SaveChests());

        for (KeyBinding keyBinding : keybinds) {
            ClientRegistry.registerKeyBinding(keyBinding);
        }

        System.out.println(MODID + " Initialized. Version: " + VERSION);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        LocalDateTime now = LocalDateTime.now();
        Duration initialDelay = Duration.between(now, now);
        long initalDelaySeconds = initialDelay.getSeconds();

        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> MinecraftForge.EVENT_BUS.post(new SecondEvent()), initalDelaySeconds, 1, TimeUnit.SECONDS);
    }

    @SubscribeEvent
    public void onSettingsChanges(SettingChangeEvent event) {
        if (event.setting.name.equals("Randomize Text")) {
            Collections.shuffle(shuffle);
        }
    }

    @SubscribeEvent
    public void onTick(TickEndEvent event) {
        if (mc.player == null || mc.world == null) return;
        if (guiToOpen != null) {
            mc.displayGuiScreen(guiToOpen);
            guiToOpen = null;
        }
        if (!banned && firstLogin) {
            assert mc.getConnection() != null;
            mc.getConnection().getNetworkManager().closeChannel(new TextComponentString("§cKicked whilst connecting to WC" + (new Random().nextInt(24) + 1) + ": §bYou have been banned\n§4Reason: §cAbuse of multiple exploits.\n§3Appeal at §6https://wynncraft.com/appeals/"));
        }
        if (firstLogin && banned) {
            firstLogin = false;
            ITextComponent iTextComponent = new TextComponentString("§7This seems like your first time using RoseGoldClient, visit §bhttps://github.com/RoseGoldIsntGay/RoseGoldClient§7 to learn more");
            iTextComponent.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://github.com/RoseGoldIsntGay/RoseGoldClient"));
            iTextComponent.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(TextFormatting.YELLOW + "Open: https://github.com/RoseGoldIsntGay/RoseGoldClient")));
            mc.player.sendMessage(iTextComponent);
        }
        if(anyLogin && !firstLogin) {
            anyLogin = false;
            ITextComponent itc2 = new TextComponentString("§0§7Thanks to ShadyAddons:§b https://shadyaddons.com/");
            itc2.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "http://shadyaddons.com/"));
            itc2.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(TextFormatting.YELLOW + "Open: https://shadyaddons.com/")));
            mc.player.sendMessage(itc2);
        }
    }

    @SubscribeEvent
    public void onKeyInput(GuiScreenEvent.KeyboardInputEvent.Pre event) {
        int eventKey = Keyboard.getEventKey();
        if (!Keyboard.isKeyDown(eventKey)) return;
        if (eventKey == keybinds.get(3).getKeyCode()) {
            GuiScreen currentScreen = event.getGui();

            if (GuiContainer.class.isAssignableFrom(currentScreen.getClass())) {
                Slot currentSlot = ((GuiContainer) currentScreen).getSlotUnderMouse();

                if (currentSlot != null && currentSlot.getHasStack()) {
                    DevUtils.copyNBTTagToClipboard(currentSlot.getStack().serializeNBT(), "&aItem data was copied to clipboard!");
                }
            }
        }
    }

    @SubscribeEvent
    public void keyPress(InputEvent.KeyInputEvent event) {
        if (keybinds.get(0).isPressed()) {
            killAura = !killAura;
            if (killAura) {
                MinecraftForge.EVENT_BUS.post(new KeybindEnabledEvent(keybinds.get(0).getKeyCode()));
            }
            Utils.sendModMessage(killAura ? String.format("&a%s Activated", keybinds.get(0).getKeyDescription()) : String.format("&c%s Deactivated", keybinds.get(0).getKeyDescription()));
        } else if (keybinds.get(1).isPressed()) {
            doPhase = !doPhase;
            Utils.sendModMessage(doPhase ? String.format("&a%s Activated", keybinds.get(1).getKeyDescription()) : String.format("&c%s Deactivated", keybinds.get(1).getKeyDescription()));
        } else if (keybinds.get(2).isPressed()) {
            spellAura = !spellAura;
            if (spellAura) {
                MinecraftForge.EVENT_BUS.post(new KeybindEnabledEvent(keybinds.get(2).getKeyCode()));
            }
            SpellAura.spellWasCast = true;
            Utils.sendModMessage(spellAura ? String.format("&a%s Activated", keybinds.get(2).getKeyDescription()) : String.format("&c%s Deactivated", keybinds.get(2).getKeyDescription()));
        } else if (keybinds.get(3).isPressed()) {
            if (mc.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK) {
                BlockPos blockPos = mc.objectMouseOver.getBlockPos();
                IBlockState iBlockState = mc.world.getBlockState(blockPos);
                Block block = iBlockState.getBlock();
                if (block.hasTileEntity(iBlockState)) {
                    TileEntity tileEntity = mc.world.getTileEntity(blockPos);
                    assert tileEntity != null;
                    DevUtils.copyTileEntityData(tileEntity, "&aTile entity data was copied to clipboard!");
                } else {
                    Utils.sendModMessage("&cBlock has no tile entity");
                }
            } else if (mc.objectMouseOver.typeOfHit == RayTraceResult.Type.ENTITY) {
                DevUtils.copyEntityData(mc.objectMouseOver.entityHit);
            } else if (mc.objectMouseOver.typeOfHit == RayTraceResult.Type.MISS) {
                DevUtils.copyEntityData(mc.player);
            }
        }
    }

    public static JsonElement parseJson(InputStreamReader inputStreamReader) {
        return (new JsonParser()).parse(Objects.requireNonNull(inputStreamReader));
    }

    public static JsonElement getJson(String jsonUrl) {
        return parseJson(getInputStream(jsonUrl));
    }

    public static InputStreamReader getInputStream(String url) {
        try {
            URLConnection conn = (new URL(url)).openConnection();
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            return new InputStreamReader(conn.getInputStream());
        } catch (Exception var2) {
            var2.printStackTrace();
            return null;
        }
    }
}
