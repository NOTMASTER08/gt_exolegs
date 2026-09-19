package net.notmaster.gtexolegs;

import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.notmaster.gtexolegs.common.data.ExoLegsDataGen;
import net.notmaster.gtexolegs.common.data.GTExoLegsCreativeTabs;
import net.notmaster.gtexolegs.common.data.GTExoLegsItems;
import net.notmaster.gtexolegs.common.entity.attribute.GTExoLegsAttributes;
import net.notmaster.gtexolegs.config.GTExoLegsConfig;
import net.notmaster.gtexolegs.network.PacketHandler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.artifact.versioning.ArtifactVersion;

@Mod(GTExoLegs.MOD_ID)
public class GTExoLegs {

    public static final String MOD_ID = "gtexolegs";

    @SuppressWarnings("unused")
    public static final Logger LOGGER = LogManager.getLogger();
    public static final ArtifactVersion VERSION = ModLoadingContext.get().getActiveContainer().getModInfo()
            .getVersion();
    public static GTRegistrate REGISTRATE = GTRegistrate.create(GTExoLegs.MOD_ID);

    public static final TagKey<Item> EXOLEGS_TAG = ItemTags.create(new ResourceLocation("curios", "exoskeleton_legs"));

    public static GTExoLegs instance;

    public GTExoLegs() {
        instance = this;
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        GTExoLegsConfig.init();
        GTExoLegsCreativeTabs.init();
        GTExoLegsItems.init();
        GTExoLegsAttributes.init(modEventBus);
        REGISTRATE.registerRegistrate();
        ExoLegsDataGen.init();
        PacketHandler.init();
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
