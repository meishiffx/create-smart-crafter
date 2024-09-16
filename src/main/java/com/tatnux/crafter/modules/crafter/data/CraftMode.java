package com.tatnux.crafter.modules.crafter.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CraftMode  {
    EXT("Ext"),
    INT("Int"),
    EXTC("ExtC");

    private final String description;
}
