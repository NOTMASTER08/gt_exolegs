package net.notmaster.gtexolegs.common.data.lang;

import com.tterrag.registrate.providers.RegistrateLangProvider;

public class LangHandler {

    public static void init(RegistrateLangProvider provider) {
        ConfigurationLang.init(provider);

        provider.add("curios.identifier.exoskeleton_legs", "Exoskeleton Legs");

        provider.add("gtexolegs.tooltip.help1", "Crouch+Right Click to toggle selected mode");
        provider.add("gtexolegs.tooltip.help2", "Crouch+Scroll to change selection");
        provider.add("gtexolegs.tooltip.jump_boost", "Jump Boost: %s");
        provider.add("gtexolegs.tooltip.flight_modifier", "Flight Modifier: %s");
        provider.add("gtexolegs.tooltip.enabled", "ON");
        provider.add("gtexolegs.tooltip.disabled", "OFF");
    }
}
