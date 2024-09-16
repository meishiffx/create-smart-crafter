package com.tatnux.crafter.jei;

import com.tatnux.crafter.SimplyCrafter;
import com.tatnux.crafter.modules.crafter.blocks.CrafterMenu;
import com.tatnux.crafter.modules.crafter.packet.CrafterPacket;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.Objects;
import java.util.function.Supplier;


public record PacketSendRecipe(NonNullList<ItemStack> stacks) implements CrafterPacket {

    public static final ResourceLocation ID = new ResourceLocation(SimplyCrafter.MOD_ID, "sendrecipe");

    public static PacketSendRecipe create(NonNullList<ItemStack> items) {
        return new PacketSendRecipe(items);
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeInt(stacks.size());
        for (ItemStack stack : stacks) {
            buf.writeItem(stack);
        }
    }

    public static PacketSendRecipe create(FriendlyByteBuf buf) {
        int l = buf.readInt();
        NonNullList<ItemStack> stacks = NonNullList.withSize(l, ItemStack.EMPTY);
        for (int i = 0; i < l; i++) {
            stacks.set(i, buf.readItem());
        }
        return new PacketSendRecipe(stacks);
    }

    @Override
    public void handleMenu(CrafterMenu menu) {
        menu.transferRecipe(this.stacks);
    }
}