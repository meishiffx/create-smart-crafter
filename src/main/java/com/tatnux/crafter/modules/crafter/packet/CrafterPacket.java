package com.tatnux.crafter.modules.crafter.packet;

import com.tatnux.crafter.modules.crafter.blocks.CrafterMenu;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.Objects;
import java.util.function.Supplier;

public interface CrafterPacket {

    default void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                ServerPlayer sender = Objects.requireNonNull(ctx.get().getSender());
                if (sender.containerMenu instanceof CrafterMenu menu) {
                    this.handleMenu(menu);
                }
            }
        });
    }

    void handleMenu(CrafterMenu menu);

}
