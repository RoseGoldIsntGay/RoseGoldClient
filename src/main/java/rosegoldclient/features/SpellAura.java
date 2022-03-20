package rosegoldclient.features;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import rosegoldclient.Main;
import rosegoldclient.commands.SpellAuraFilter;
import rosegoldclient.config.Config;
import rosegoldclient.events.*;
import rosegoldclient.utils.RenderUtils;
import rosegoldclient.utils.RotationUtils;
import rosegoldclient.utils.Utils;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class SpellAura {

    public static EntityLivingBase target;
    public static ArrayList<Spell> spellCycle = new ArrayList<>();
    public static Spell currentSpell;
    public static boolean spellWasCast = true;
    public static int delay = 0;
    public static int index = 0;

    public static class Spell {
        public int spell;
        public int delay;

        public Spell(int spell, int delay) {
            this.spell = spell;
            this.delay = delay;
        }

        private static String intToSpellType(int num) {
            switch (num) {
                case 0:
                    return "None";
                case 1:
                    return "RRR";
                case 2:
                    return "RLR";
                case 3:
                    return "RLL";
                case 4:
                    return "RRL";
                case 5:
                    return "L";
                default:
                    return "Unrecognized Spell "+num;
            }
        }

        @Override
        public String toString() {
            return intToSpellType(this.spell) + " with delay of " + this.delay;
        }
    }

    @SubscribeEvent
    public void onEnabled(KeybindEnabledEvent event) {
        currentSpell = null;
        spellWasCast = false;
        index = 0;
        spellCycle.clear();
        if (Config.spellOneType != 0) {
            spellCycle.add(new Spell(Config.spellOneType, Config.spellOneDelay));
        }
        if(Config.spellTwoType != 0) {
            spellCycle.add(new Spell(Config.spellTwoType, Config.spellTwoDelay));
        }
        if(Config.spellThreeType != 0) {
            spellCycle.add(new Spell(Config.spellThreeType, Config.spellThreeDelay));
        }
        if(Config.spellFourType != 0) {
            spellCycle.add(new Spell(Config.spellFourType, Config.spellFourDelay));
        }
        if(Config.spellFiveType != 0) {
            spellCycle.add(new Spell(Config.spellFiveType, Config.spellFiveDelay));
        }
        if (Config.spellSixType != 0) {
            spellCycle.add(new Spell(Config.spellSixType, Config.spellSixDelay));
        }
        if(Config.spellSevenType != 0) {
            spellCycle.add(new Spell(Config.spellSevenType, Config.spellSevenDelay));
        }
        if(Config.spellEightType != 0) {
            spellCycle.add(new Spell(Config.spellEightType, Config.spellEightDelay));
        }
        if(Config.spellNineType != 0) {
            spellCycle.add(new Spell(Config.spellNineType, Config.spellNineDelay));
        }
        if(Config.spellTenType != 0) {
            spellCycle.add(new Spell(Config.spellTenType, Config.spellTenDelay));
        }
    }

    @SubscribeEvent
    public void onTick(TickEndEvent event) {
        if(!Main.spellAura) {
            target = null;
            return;
        }
        if(!Config.spellAura || Main.mc.player == null || Main.mc.world == null) return;
        if(!spellWasCast) return;
        if(spellCycle.size() == 0) return;
        target = getEntity();
        if(target == null) return;
        if(delay > 0) {
            delay--;
        } else {
            if(currentSpell == null) {
                currentSpell = spellCycle.get(0);
            } else {
                currentSpell = getNextSpell();
            }
            spellWasCast = false;
        }
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void onUpdatePre(PlayerMoveEvent.Pre pre) {
        if(!Main.spellAura || !Config.spellAura || Main.mc.player == null || Main.mc.world == null) return;
        if (target != null && currentSpell != null) {
            switch (Config.spellAuraType) {
                case 1: //archer
                    RotationUtils.smoothLook(RotationUtils.getBowRotationToEntity(target), 0, () -> {});
                    break;
                case 2: //mage
                    if (currentSpell.spell == 3) { //meteor
                        RotationUtils.Rotation rotation = RotationUtils.getRotationToBlockUnderEntity(target);
                        if (rotation != null) {
                            RotationUtils.smoothLook(rotation, 0, () -> {});
                        } else {
                            RotationUtils.smoothLook(RotationUtils.getRotationToEntity(target), 0, () -> {});
                        }
                    } else {
                        RotationUtils.smoothLook(RotationUtils.getRotationToEntity(target), 0, () -> {});
                    }
                    break;
                case 3: //assassin
                    switch (currentSpell.spell) {
                        case 1: //vanish
                            RotationUtils.smoothLook(new RotationUtils.Rotation(90, Main.mc.player.rotationYaw), 0, () -> {});
                            break;
                        case 4: //smoke bomb
                            RotationUtils.smoothLook(RotationUtils.getRotationToEntityFeet(target), 0, () -> {});
                            break;
                        default:
                            RotationUtils.smoothLook(RotationUtils.getRotationToEntity(target), 0, () -> {});
                            break;
                    }
                    break;
                case 4: //shaman
                    if (currentSpell.spell == 2) { //totem
                        RotationUtils.smoothLook(new RotationUtils.Rotation(90, Main.mc.player.rotationYaw), 0, () -> {});
                    } else {
                        RotationUtils.smoothLook(RotationUtils.getRotationToEntity(target), 0, () -> {});
                    }
                    break;
                default:
                    RotationUtils.smoothLook(RotationUtils.getRotationToEntity(target), 0, () -> {});
                    break;
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onUpdatePost(PlayerMoveEvent.Post post) {
        if(!Main.spellAura || !Config.spellAura || Main.mc.player == null || Main.mc.world == null) return;
        if(target == null) return;
        if(spellWasCast) return;
        castSpell(currentSpell.spell);
        delay = currentSpell.delay;
        spellWasCast = true;
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        if(!Config.highlightSA) return;
        if(target == null) return;
        RenderUtils.drawEntityESP(target, Color.RED, event.getPartialTicks());
    }

    private static Spell getNextSpell() {
        index++;
        //Utils.sendModMessage("Next Spell: "+spellCycle.get((index) % spellCycle.size()));
        return spellCycle.get((index) % spellCycle.size());
    }

    private static EntityLivingBase getEntity() {
        if(Main.mc.currentScreen != null || Main.mc.world == null) return null;
        float range = Config.spellAuraRange;

        List<Entity> entityList = Main.mc.world.getLoadedEntityList().stream().filter(
                entity -> entity instanceof EntityLivingBase
        ).filter(
                entity -> isValid(entity, range, true)
        ).sorted(
                Comparator.comparingDouble(e -> e.getDistance(Main.mc.player))
        ).collect(Collectors.toList());
        Iterator<Entity> iterator = entityList.iterator();
        if(iterator.hasNext()) {
            Entity en = iterator.next();
            return (EntityLivingBase) en;
        }
        return null;
    }

    private static boolean isValid(Entity entity, float range, boolean throughWalls) {
        if(Config.spellAuraCustomNames && !entity.hasCustomName()) {
            return false;
        }
        if(Config.spellAuraFilter) {
            if(Config.spellAuraFilterBlacklist) {
                for (String search : SpellAuraFilter.SASettings) {
                    if (removeFormatting(entity.getCustomNameTag()).contains(search)) {
                        return false;
                    }
                }
            } else {
                b1:
                {
                    for (String search : SpellAuraFilter.SASettings) {
                        if (removeFormatting(entity.getCustomNameTag()).contains(search)) {
                            break b1;
                        }
                    }
                    return false;
                }
            }
        }
        b2: {
            b3: {
                if (entity == Main.mc.player) {
                    break b3;
                }
                if((entity instanceof EntityPlayer || entity instanceof EntityWither || entity instanceof EntityBat) && entity.isInvisible()) {
                    break b3;
                }
                if(entity instanceof EntityArrow) {
                    break b3;
                }
                if(!Main.mc.player.canEntityBeSeen(entity) && !throughWalls) {
                    break b3;
                }
                if(entity.isDead) {
                    break b3;
                }
                double dist = entity.getDistance(Main.mc.player);
                if(range > dist) break b2;
            }
            return false;
        }
        if (entity instanceof EntityPlayer) {
            return true;
        }
        return !(entity instanceof EntityVillager);
    }

    private static String removeFormatting(String input) {
        return input.replaceAll("§[0-9a-fk-or]", "");
    }

    private static void castSpell(int spell) {
        switch (spell) {
            case 1:
                SpellCaster.RRR();
                break;
            case 2:
                SpellCaster.RLR();
                break;
            case 3:
                SpellCaster.RLL();
                break;
            case 4:
                SpellCaster.RRL();
                break;
            case 5:
                SpellCaster.L();
                break;
        }
    }
}
