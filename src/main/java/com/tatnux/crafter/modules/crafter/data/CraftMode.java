package com.tatnux.crafter.modules.crafter.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.util.ByIdMap;

import java.util.function.IntFunction;

@Getter
@AllArgsConstructor
public enum CraftMode  {
    EXT("Ext"),
    INT("Int"),
    EXTC("ExtC");

    public static final IntFunction<CraftMode> BY_ID =
            ByIdMap.continuous(
                    CraftMode::ordinal,
                    CraftMode.values(),
                    ByIdMap.OutOfBoundsStrategy.ZERO
            );

    private final String description;
}
