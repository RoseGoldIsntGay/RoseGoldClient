package rosegoldclient.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.network.NetHandlerLoginServer;

@Mixin(NetHandlerLoginServer.class)
public class MixinNetHandlerLoginServer {
    @Shadow
    private EntityPlayerMP player;
}
