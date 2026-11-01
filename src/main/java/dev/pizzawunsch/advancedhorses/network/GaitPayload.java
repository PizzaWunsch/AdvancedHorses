package dev.pizzawunsch.advancedhorses.network;

import dev.pizzawunsch.advancedhorses.AdvancedHorses;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record GaitPayload(int gear) implements CustomPacketPayload {
    public static final Type<GaitPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(AdvancedHorses.MOD_ID, "gait"));
    public static final StreamCodec<RegistryFriendlyByteBuf, GaitPayload> CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT, GaitPayload::gear,
        GaitPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
