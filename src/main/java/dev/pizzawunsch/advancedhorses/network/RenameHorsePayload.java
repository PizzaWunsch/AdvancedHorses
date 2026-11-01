package dev.pizzawunsch.advancedhorses.network;

import dev.pizzawunsch.advancedhorses.AdvancedHorses;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record RenameHorsePayload(int entityId, String name) implements CustomPacketPayload {
    public static final int MAX_LENGTH = 32;
    public static final Type<RenameHorsePayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(AdvancedHorses.MOD_ID, "rename_horse"));
    public static final StreamCodec<RegistryFriendlyByteBuf, RenameHorsePayload> CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT, RenameHorsePayload::entityId,
        ByteBufCodecs.stringUtf8(MAX_LENGTH * 4), RenameHorsePayload::name,
        RenameHorsePayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
