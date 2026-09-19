package net.notmaster.gtexolegs.config;

import net.notmaster.gtexolegs.GTExoLegs;

import dev.toma.configuration.Configuration;
import dev.toma.configuration.config.Config;
import dev.toma.configuration.config.Configurable;
import dev.toma.configuration.config.format.ConfigFormats;

@Config(id = GTExoLegs.MOD_ID)
public class GTExoLegsConfig {

    public static GTExoLegsConfig INSTANCE;
    private static final Object LOCK = new Object();

    public static void init() {
        synchronized (LOCK) {
            if (INSTANCE == null) {
                INSTANCE = Configuration.registerConfig(GTExoLegsConfig.class, ConfigFormats.yaml())
                        .getConfigInstance();
            }
        }
    }

    @Configurable
    @Configurable.Comment({ "Step height modifier per tier", "Must be >= 0" })
    @Configurable.FixedSize
    @Configurable.DecimalRange(min = 0)
    public double[] stepHeightModifiers = { 0.4, 0.4, 0.4, 0.4, 0.4 };

    @Configurable
    @Configurable.Comment({ "Movement speed multiplier modifier per tier", "Must be >= 0" })
    @Configurable.FixedSize
    @Configurable.DecimalRange(min = 0)
    public double[] movementSpeedMultipliers = { 0.2, 0.4, 0.6, 0.8, 1.0 };

    @Configurable
    @Configurable.Comment({ "Creative flight speed multiplier per tier", "Must be >= 1" })
    @Configurable.FixedSize
    @Configurable.DecimalRange(min = 1.0F)
    public float[] creativeFlightSpeedMultipliers = { 1.0F, 1.0F, 1.0F, 1.0F, 3.0F };

    @Configurable
    @Configurable.Comment({ "Jump boost modifier per tier", "Must be >= 0" })
    @Configurable.FixedSize
    @Configurable.DecimalRange(min = 0)
    public double[] jumpBoostModifiers = { 0.0, 0.1, 0.2, 0.3, 0.4 };
}
