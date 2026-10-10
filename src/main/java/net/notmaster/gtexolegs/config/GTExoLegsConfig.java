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
    public float[] creativeFlightSpeedMultipliers = { 1.5F, 2.0F, 2.5F, 3.0F, 3.5F };

    @Configurable
    @Configurable.Comment({ "Jump boost modifier per tier", "Must be >= 0" })
    @Configurable.FixedSize
    @Configurable.DecimalRange(min = 0)
    public double[] jumpBoostModifiers = { 0.0, 0.1, 0.2, 0.3, 0.4 };

    @Configurable
    public Jetpack jetpack = new Jetpack();

    public class Jetpack {

        @Configurable
        @Configurable.Comment({ "Base/slow vertical hover speed multiplier per tier", "Must be >= 0" })
        @Configurable.FixedSize
        @Configurable.DecimalRange(min = 0.0)
        public double[] verticalHoverSlowMultiplier = { 1.3, 1.6, 1.9, 2.2, 2.5 };

        @Configurable
        @Configurable.Comment({ "Active vertical hover speed multiplier per tier", "Must be >= 1" })
        @Configurable.FixedSize
        @Configurable.DecimalRange(min = 1.0)
        public double[] verticalHoverMultiplier = { 1.3, 1.6, 1.9, 2.2, 2.5 };

        @Configurable
        @Configurable.Comment({ "Vertical acceleration multiplier per tier", "Must be >= 1" })
        @Configurable.FixedSize
        @Configurable.DecimalRange(min = 1.0)
        public double[] verticalAccelerationMultiplier = { 1.3, 1.6, 1.9, 2.2, 2.5 };

        @Configurable
        @Configurable.Comment({ "Vertical speed multiplier per tier", "Must be >= 1" })
        @Configurable.FixedSize
        @Configurable.DecimalRange(min = 1.0)
        public double[] verticalSpeedMultiplier = { 1.3, 1.6, 1.9, 2.2, 2.5 };

        @Configurable
        @Configurable.Comment({ "Horizontal speed multiplier per tier", "Must be >= 1" })
        @Configurable.FixedSize
        @Configurable.DecimalRange(min = 1.0)
        public double[] horizontalSpeedMultiplier = { 1.8, 2.6, 3.4, 4.2, 5.0 };
    }
}
