package net.tazgirl.dolphinfix;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.dolphin.Dolphin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;


// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(DolphinFix.MODID)
public class DolphinFix
{
    public static final String MODID = "dolphin_fix";

    public DolphinFix(IEventBus modEventBus, ModContainer modContainer)
    {
        NeoForge.EVENT_BUS.register(this);
        modContainer.registerConfig(ModConfig.Type.SERVER, Config.SPEC);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onLivingHurt(LivingIncomingDamageEvent event)
    {
        if (!event.getEntity().level().isClientSide() && event.getEntity() instanceof Dolphin &&
                (Config.drowningFix && event.getSource().is(DamageTypes.DROWN)) || (Config.dryoutFix && event.getSource().is(DamageTypes.DRY_OUT)))
        {
            if (!isPlayerNearby((ServerLevel) event.getEntity().level(), event.getEntity()))
            {
                event.setCanceled(true);
                event.getEntity().setAirSupply(Config.safetyAir);
            }
        }
    }

    public static boolean isPlayerNearby(ServerLevel level, Entity entity)
    {
        MinecraftServer server = level.getServer();

        if (server.getPlayerCount() == 0)
        {
            return false;
        }

        //The -16 is just to be safe and shouldn't affect gameplay unless you are trying to drown dolphins
        int simulationDistance = server.getPlayerList().getSimulationDistance() - Config.safetyMargin;

        double distanceSqr = simulationDistance * simulationDistance * 16;

        for (Player player : level.players())
        {
            if (player.distanceToSqr(entity) <= distanceSqr)
            {
                return true;
            }
        }

        return false;
    }

}
