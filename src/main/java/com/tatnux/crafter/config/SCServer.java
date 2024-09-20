package com.tatnux.crafter.config;

import com.simibubi.create.foundation.config.ConfigBase;

public class SCServer extends ConfigBase {

    public final ConfigInt crafterProgressNeeded = i(100, 1, "crafterProgressNeeded", "Greater the number is, longer a recipe will take to craft");
    public final ConfigInt speedRatio = i(24, 1, 56, "speedRatio", "Greater the number is, faster a recipe will take to craft with high su");
    public final ConfigInt crafterStressImpact = i(5, 1, "crafterStressImpact", "The stress impact of the Smart Crafter");

    @Override
    public String getName() {
        return "server";
    }
}
