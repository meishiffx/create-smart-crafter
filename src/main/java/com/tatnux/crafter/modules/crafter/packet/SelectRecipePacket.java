package com.tatnux.crafter.modules.crafter.packet;

import com.tatnux.crafter.modules.crafter.blocks.CrafterMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.Objects;
import java.util.function.Supplier;

public record SelectRecipePacket(byte slot) {

    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeByte(slot);
    }

    public static SelectRecipePacket create(FriendlyByteBuf friendlyByteBuf) {
        return new SelectRecipePacket(friendlyByteBuf.readByte());
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                ServerPlayer sender = Objects.requireNonNull(ctx.get().getSender());
                if (sender.containerMenu instanceof CrafterMenu menu) {
                    menu.contentHolder.select(this.slot);
                }
            }
        });
    }
}
