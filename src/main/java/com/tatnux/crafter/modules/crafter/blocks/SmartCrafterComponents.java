package com.tatnux.crafter.modules.crafter.blocks;

import com.mojang.serialization.Codec;
import com.tatnux.crafter.SmartCrafter;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.UnaryOperator;

public class SmartCrafterComponents {

    private static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, SmartCrafter.MOD_ID);

    public static final DataComponentType<ItemContainerContents> SMART_CRAFTER_INVENTORY = register(
            "smart_crafter_inventory",
            builder -> builder.persistent(ItemContainerContents.CODEC).networkSynchronized(ItemContainerContents.STREAM_CODEC));

    public static final DataComponentType<Boolean> SMART_CRAFTER_KEEP_MODE = register(
            "smart_crafter_keep_mode",
            builder -> builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));

    public static final DataComponentType<Byte> SMART_CRAFTER_SELECTED_INDEX = register(
            "smart_crafter_selected_index",
            builder -> builder.persistent(Codec.BYTE).networkSynchronized(ByteBufCodecs.BYTE));

    private static <T> DataComponentType<T> register(String name, UnaryOperator<DataComponentType.Builder<T>> builder) {
        DataComponentType<T> type = builder.apply(DataComponentType.builder()).build();
        DATA_COMPONENTS.register(name, () -> type);
        return type;
    }

    public static void register(IEventBus modEventBus) {
        DATA_COMPONENTS.register(modEventBus);
    }

}
