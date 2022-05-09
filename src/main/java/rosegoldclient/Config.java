package rosegoldclient;

import gg.essential.vigilance.Vigilant;
import gg.essential.vigilance.data.*;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.NotNull;
import rosegoldclient.events.SettingChangeEvent;

import java.io.File;
import java.util.Comparator;

public class Config extends Vigilant {

    public static Config INSTANCE = new Config();

    /*
     * RoseGoldClient
     */

    @Property(type = PropertyType.SWITCH, name = "Watermark",
            category = "RoseGoldClient")
    public boolean watermark = true;

    @Property(type = PropertyType.SWITCH, name = "Silent Mode", description = "Silence all ESPs and mod messages",
            category = "RoseGoldClient")
    public boolean silentMode = false;

    @Property(type = PropertyType.SWITCH, name = "Anonymize", description = "Keep yourself safe",
            category = "RoseGoldClient")
    public boolean anon = false;

    @Property(type = PropertyType.SWITCH, name = "Randomize Text", description = "Randomize text instead of yeeting it",
            category = "RoseGoldClient")
    public boolean randomizeAnon = false;

    /*
     * Loot Running
     */

    @Property(type = PropertyType.SWITCH, name = "Chest Looter", description = "Automatically loot items from chests",
            category = "Loot Running", subcategory = "Chest Looter")
    public boolean chestLoot = false;

    @Property(type = PropertyType.SLIDER, name = "Chest Looter Delay", description = "in milliseconds",
            category = "Loot Running", subcategory = "Chest Looter", max = 500)
    public int chestLootDelay = 100;

    @Property(type = PropertyType.SWITCH, name = "Chest Aura", description = "Open loot chests in range of the player",
            category = "Loot Running", subcategory = "Chest Aura")
    public boolean chestAura = false;

        @Property(type = PropertyType.SWITCH, name = "Only Wynncraft Chests", description = "Only open confirmed Wynncraft chests",
                category = "Loot Running", subcategory = "Chest Aura")
        public boolean onlyConfirmedChests = false;

        @Property(type = PropertyType.SLIDER, name = "Chest Aura Range",
                category = "Loot Running", subcategory = "Chest Aura", min = 1, max = 6)
        public int chestAuraRange = 5;

        @Property(type = PropertyType.SELECTOR, name = "Distance Check Mode",
                category = "Loot Running", subcategory = "Chest Aura", options = {"Distance Calculation", "Coordinate Check"})
        public int chestAuraRangeType = 0;

        @Property(type = PropertyType.SWITCH, name = "Chest ESP", description = "Highlight chests",
                category = "Loot Running", subcategory = "Chest Aura")
        public boolean chestESP = false;

        @Property(type = PropertyType.SLIDER, name = "Chest ESP Range",
                category = "Loot Running", subcategory = "Chest Aura", min = 10, max = 100)
        public int chestESPRange = 20;

    @Property(type = PropertyType.SWITCH, name = "Wynncraft Chest ESP", description = "Highlight confirmed Wynncraft chests",
            category = "Loot Running", subcategory = "Chest ESP")
    public boolean wynnChestESP = false;

        @Property(type = PropertyType.SLIDER, name = "Wynncraft Chest ESP Range",
                category = "Loot Running", subcategory = "Chest ESP", max = 200)
        public int wynnChestESPRange = 50;

    /*
     * ESP
     */

    @Property(type = PropertyType.SWITCH, name = "Entity ESP",
            category = "ESP", subcategory = "Entity ESP")
    public boolean entityESP = false;

    @Property(type = PropertyType.SLIDER, name = "Entity ESP Range", description = "0 = unlimited",
            category = "ESP", subcategory = "Entity ESP", max = 64)
    public int entityESPRange = 0;

    /*
     * Movement
     */

    @Property(type = PropertyType.SWITCH, name = "Phase", description = "Phase through blocks (use movement abilities)",
            category = "Movement", subcategory = "Phase")
    public boolean phase = false;

        @Property(type = PropertyType.SWITCH, name = "Only with keybind", description = "Set keybind under controls",
                category = "Movement", subcategory = "Phase")
        public boolean phaseWithKeybind = false;

        @Property(type = PropertyType.SWITCH, name = "Descent while sneak held",
                category = "Movement", subcategory = "Phase")
        public boolean sneakHeldDescent = false;

    @Property(type = PropertyType.SWITCH, name = "No Rotate",
            category = "Movement", subcategory = "No Rotate")
    public boolean noRotate = false;

    @Property(type = PropertyType.SLIDER, name = "X Velocity Modifier", description = "Multiply x axis velocity",
            category = "Movement", subcategory = "Velocity", min = -10, max = 10)
    public int velocityX = 1;

    @Property(type = PropertyType.SLIDER, name = "Y Velocity Modifier", description = "Multiply y axis velocity",
            category = "Movement", subcategory = "Velocity", min = -10, max = 10)
    public int velocityY = 1;

    @Property(type = PropertyType.SLIDER, name = "Z Velocity Modifier", description = "Multiply z axis velocity",
            category = "Movement", subcategory = "Velocity", min = -10, max = 10)
    public int velocityZ = 1;

    @Property(type = PropertyType.SWITCH, name = "Inventory Walk", description = "Walk while inside inventories",
            category = "Movement", subcategory = "Inventory Walk")
    public boolean invWalk = false;

    /*
     * Combat
     */

    @Property(type = PropertyType.SLIDER, name = "Target HUD X",
            category = "Combat", max = 100)
    public int targetHUDX = 25;

    @Property(type = PropertyType.SLIDER, name = "Target HUD Y",
            category = "Combat", max = 100)
    public int targetHUDY = 25;

    @Property(type = PropertyType.SWITCH, name = "Kill Aura", description = "Set a keybind in controls",
            category = "Combat", subcategory = "Kill Aura")
    public boolean killAura = false;

        @Property(type = PropertyType.SWITCH, name = "Highlight Attacked Entity",
                category = "Combat", subcategory = "Kill Aura")
        public boolean highlightKA = false;

        @Property(type = PropertyType.SELECTOR, name = "Kill Aura Class",
                category = "Combat", subcategory = "Kill Aura", options = {"Melee", "Archer", "Mage", "Shaman"})
        public int killAuraType = 0;

        @Property(type = PropertyType.SWITCH, name = "KA Nametags Only", description = "Only attack entities with a name",
                category = "Combat", subcategory = "Kill Aura")
        public boolean killAuraCustomNames = false;

        @Property(type = PropertyType.SWITCH, name = "KA Custom Filter", description = "Only attack matching /ka filter",
                category = "Combat", subcategory = "Kill Aura")
        public boolean killAuraFilter = false;

        @Property(type = PropertyType.SWITCH, name = "KA Blacklist", description = "Turn filter into a blacklist",
                category = "Combat", subcategory = "Kill Aura")
        public boolean killAuraFilterBlacklist = false;

    @Property(type = PropertyType.SELECTOR, name = "Spell Cast Speed",
            category = "Combat", subcategory = "Spell Aura", options = {"10 CPS", "5 CPS", "3.33 CPS", "2.5 CPS"})
    public int spellCastSpeed = 1;

    @Property(type = PropertyType.SWITCH, name = "Spell Aura", description = "Set a keybind in controls",
            category = "Combat", subcategory = "Spell Aura")
    public boolean spellAura = false;

        @Property(type = PropertyType.SLIDER, name = "SA Range",
                category = "Combat", subcategory = "Spell Aura", min = 10, max = 100)
        public int spellAuraRange = 10;

        @Property(type = PropertyType.SWITCH, name = "Highlight Attacked Entity",
                category = "Combat", subcategory = "Spell Aura")
        public boolean highlightSA = false;

        @Property(type = PropertyType.SELECTOR, name = "Spell Aura Class",
                category = "Combat", subcategory = "Spell Aura", options = {"Melee", "Archer", "Mage", "Shaman"})
        public int spellAuraType = 0;

        @Property(type = PropertyType.SWITCH, name = "SA Nametags Only", description = "Only attack entities with a name",
                category = "Combat", subcategory = "Spell Aura")
        public boolean spellAuraCustomNames = false;

        @Property(type = PropertyType.SWITCH, name = "SA Custom Filter", description = "Only attack matching /sa filter",
                category = "Combat", subcategory = "Spell Aura")
        public boolean spellAuraFilter = false;

        @Property(type = PropertyType.SWITCH, name = "SA Blacklist", description = "Turn filter into a blacklist",
                category = "Combat", subcategory = "Spell Aura")
        public boolean spellAuraFilterBlacklist = false;

    @Property(type = PropertyType.SELECTOR, name = "Spell 1",
            category = "Combat", subcategory = "Spells", options = {"§cNone", "RRR", "RLR", "RLL", "RRL", "L"})
    public int spellOneType = 0;

    @Property(type = PropertyType.SLIDER, name = "Spell 1 Delay", description = "Set delay in ticks",
            category = "Combat", subcategory = "Spells", max = 200)
    public int spellOneDelay = 0;

    @Property(type = PropertyType.SELECTOR, name = "Spell 2",
            category = "Combat", subcategory = "Spells", options = {"§cNone", "RRR", "RLR", "RLL", "RRL", "L"})
    public int spellTwoType = 0;

    @Property(type = PropertyType.SLIDER, name = "Spell 2 Delay", description = "Set delay in ticks",
            category = "Combat", subcategory = "Spells", max = 200)
    public int spellTwoDelay = 0;

    @Property(type = PropertyType.SELECTOR, name = "Spell 3",
            category = "Combat", subcategory = "Spells", options = {"§cNone", "RRR", "RLR", "RLL", "RRL", "L"})
    public int spellThreeType = 0;

    @Property(type = PropertyType.SLIDER, name = "Spell 3 Delay", description = "Set delay in ticks",
            category = "Combat", subcategory = "Spells", max = 200)
    public int spellThreeDelay = 0;

    @Property(type = PropertyType.SELECTOR, name = "Spell 4",
            category = "Combat", subcategory = "Spells", options = {"§cNone", "RRR", "RLR", "RLL", "RRL", "L"})
    public int spellFourType = 0;

    @Property(type = PropertyType.SLIDER, name = "Spell 4 Delay", description = "Set delay in ticks",
            category = "Combat", subcategory = "Spells", max = 200)
    public int spellFourDelay = 0;

    @Property(type = PropertyType.SELECTOR, name = "Spell 5",
            category = "Combat", subcategory = "Spells", options = {"§cNone", "RRR", "RLR", "RLL", "RRL", "L"})
    public int spellFiveType = 0;

    @Property(type = PropertyType.SLIDER, name = "Spell 5 Delay", description = "Set delay in ticks",
            category = "Combat", subcategory = "Spells", max = 200)
    public int spellFiveDelay = 0;

    @Property(type = PropertyType.SELECTOR, name = "Spell 6",
            category = "Combat", subcategory = "Spells", options = {"§cNone", "RRR", "RLR", "RLL", "RRL", "L"})
    public int spellSixType = 0;

    @Property(type = PropertyType.SLIDER, name = "Spell 6 Delay", description = "Set delay in ticks",
            category = "Combat", subcategory = "Spells", max = 200)
    public int spellSixDelay = 0;

    @Property(type = PropertyType.SELECTOR, name = "Spell 7",
            category = "Combat", subcategory = "Spells", options = {"§cNone", "RRR", "RLR", "RLL", "RRL", "L"})
    public int spellSevenType = 0;

    @Property(type = PropertyType.SLIDER, name = "Spell 7 Delay", description = "Set delay in ticks",
            category = "Combat", subcategory = "Spells", max = 200)
    public int spellSevenDelay = 0;

    @Property(type = PropertyType.SELECTOR, name = "Spell 8",
            category = "Combat", subcategory = "Spells", options = {"§cNone", "RRR", "RLR", "RLL", "RRL", "L"})
    public int spellEightType = 0;

    @Property(type = PropertyType.SLIDER, name = "Spell 8 Delay", description = "Set delay in ticks",
            category = "Combat", subcategory = "Spells", max = 200)
    public int spellEightDelay = 0;

    @Property(type = PropertyType.SELECTOR, name = "Spell 9",
            category = "Combat", subcategory = "Spells", options = {"§cNone", "RRR", "RLR", "RLL", "RRL", "L"})
    public int spellNineType = 0;

    @Property(type = PropertyType.SLIDER, name = "Spell 9 Delay", description = "Set delay in ticks",
            category = "Combat", subcategory = "Spells", max = 200)
    public int spellNineDelay = 0;

    @Property(type = PropertyType.SELECTOR, name = "Spell 10",
            category = "Combat", subcategory = "Spells", options = {"§cNone", "RRR", "RLR", "RLL", "RRL", "L"})
    public int spellTenType = 0;

    @Property(type = PropertyType.SLIDER, name = "Spell 10 Delay", description = "Set delay in ticks",
            category = "Combat", subcategory = "Spells", max = 200)
    public int spellTenDelay = 0;

    public Config() {
        super(new File("./config/rosegoldclient/config.toml"), "&aRoseGoldClient", new JVMAnnotationPropertyCollector(), new ConfigSorting());
        initialize();

        /*registerListener("chestESP", (newVal) -> {
            System.out.println("Chest ESP value changed: " + newVal);
            MinecraftForge.EVENT_BUS.post(new SettingChangeEvent("chestESP"));
        });*/
    }

    public static class ConfigSorting extends SortingBehavior {
        @NotNull
        @Override
        public Comparator<Category> getCategoryComparator() {
            return (o1, o2) -> {
                if(o1.getName().equals("RoseGoldClient")) {
                    return -1;
                } else if(o2.getName().equals("RoseGoldClient")) {
                    return 1;
                } else {
                    return o1.getName().compareTo(o2.getName());
                }
            };
        }
    }
}
