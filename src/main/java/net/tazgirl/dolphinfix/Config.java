package net.tazgirl.dolphinfix;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs
public class Config
{
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue SAFETY_MARGIN = BUILDER
            .comment("Safety margin subtracted from the simulation distance to err on the more lenient side (i.e dolphins who enter range while already underwater and out of breath who need time to get to the surface). Default value will not effect gameplay unless you are trying to drown dolphins.")
            .defineInRange("safetyMargin", 16, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue SAFETY_AIR = BUILDER
            .comment("As mentioned previously dolphins may enter the simulation range while already out of breath. This amount of air is given to the dolphins whenever they almost drown while in the twighlight zone this mod aims to fix. It isn't a perfect system but will reduce the rare chip damage experienced by dolphins rapidly being in and out of the safe zone.")
            .defineInRange("safetyAir", 300, 0, Integer.MAX_VALUE);


    static final ModConfigSpec SPEC = BUILDER.build();

    static int safetyMargin;
    static int safetyAir;

    @SubscribeEvent
    public static void onConfigLoad(ModConfigEvent.Loading event)
    {
        load();
    }

    @SubscribeEvent
    public static void onConfigLoad(ModConfigEvent.Reloading event)
    {
        load();
    }

    static void load()
    {
        safetyMargin = SAFETY_MARGIN.get();
        safetyAir = SAFETY_AIR.get();
    }

}