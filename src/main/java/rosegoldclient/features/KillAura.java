package rosegoldclient.features;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import rosegoldclient.Main;
import rosegoldclient.commands.KillAuraFilter;
import rosegoldclient.config.Config;
import rosegoldclient.events.PlayerMoveEvent;
import rosegoldclient.events.TickEndEvent;
import rosegoldclient.utils.RenderUtils;
import rosegoldclient.utils.RotationUtils;

import java.awt.*;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class KillAura {

    public static EntityLivingBase target;

    @SubscribeEvent
    public void onTick(TickEndEvent event) {
        if(!Main.killAura || !Config.killAura || Main.mc.player == null || Main.mc.world == null) return;
        target = getEntity();
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void onUpdatePre(PlayerMoveEvent.Pre pre) {
        if(!Main.killAura || !Config.killAura || Main.mc.player == null || Main.mc.world == null) return;
        if (target != null) {
            if(Config.killAuraType == 1) {
                RotationUtils.smoothLook(RotationUtils.getBowRotationToEntity(target), 0, () -> {});
            } else {
                RotationUtils.smoothLook(RotationUtils.getRotationToEntity(target), 0, () -> {});
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onUpdatePost(PlayerMoveEvent.Post post) {
        if(!Main.killAura || !Config.killAura || Main.mc.player == null || Main.mc.world == null) return;
        if(Main.mc.getConnection() == null) return;
        if(target == null) return;
        if(Main.mc.player.ticksExisted % 2 != 0) return;
        if(SpellCaster.packetList.size() != 0) return;
        switch (Config.killAuraType) {
            case 0: //melee
                Main.mc.player.swingArm(EnumHand.MAIN_HAND);
                Main.mc.playerController.attackEntity(Main.mc.player, target);
                break;
            case 1: //bow
                Main.mc.playerController.processRightClick(Main.mc.player, Main.mc.world, EnumHand.MAIN_HAND);
                break;
            default:
                Main.mc.player.swingArm(EnumHand.MAIN_HAND);
        }
    }

    @SubscribeEvent
    public void renderWorld(RenderWorldLastEvent event) {
        if(!Main.killAura || !Config.killAura || Main.mc.player == null || Main.mc.world == null || !Config.highlightKA) return;
        if (target != null) {
            RenderUtils.drawEntityESP(target, Color.RED, event.getPartialTicks());
        }
    }

    private static EntityLivingBase getEntity() {
        if(Main.mc.currentScreen != null || Main.mc.world == null) return null;
        float range = 4F;
        boolean throughWalls = true;
        switch (Config.killAuraType) {
            case 1: //bow
                range = 40F;
                throughWalls = false;
                break;
            case 2: //mage beam
                range = 12F;
                break;
            case 3: //shaman
                range = 30F;
        }

        float finalRange = range;
        boolean finalThroughWalls = throughWalls;

        List<Entity> entityList = Main.mc.world.getLoadedEntityList().stream().filter(
                entity -> entity instanceof EntityLivingBase
        ).filter(
                entity -> isValid((EntityLivingBase)entity, finalRange, finalThroughWalls)
        ).sorted(
                Comparator.comparingDouble(e -> e.getDistance(Main.mc.player))
        ).collect(Collectors.toList());
        //can do more sorting here
        Iterator<Entity> iterator = entityList.iterator();
        if(iterator.hasNext()) {
            Entity en = iterator.next();
            return (EntityLivingBase) en;
        }
        return null;
    }

    private static boolean isValid(EntityLivingBase entity, float range, boolean throughWalls) {
        if(Config.killAuraCustomNames && !entity.hasCustomName()) {
            return false;
        }
        if(Config.killAuraFilter) {
            if(Config.killAuraFilterBlacklist) {
                for (String search : KillAuraFilter.KASettings) {
                    if (removeFormatting(entity.getCustomNameTag()).contains(search)) {
                        return false;
                    }
                }
            } else {
                b1:
                {
                    for (String search : KillAuraFilter.KASettings) {
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
                if(!Main.mc.player.canEntityBeSeen(entity) && !throughWalls) {
                    break b3;
                }
                if(entity.getHealth() <= 0.0f) {
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

}
