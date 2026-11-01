package dev.pizzawunsch.advancedhorses.network;

import dev.pizzawunsch.advancedhorses.AdvancedHorses;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record NamePromptPayload(int entityId) implements CustomPacketPayload {
    public static final Type<NamePromptPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(AdvancedHorses.MOD_ID, "name_prompt"));
    public static final StreamCodec<RegistryFriendlyByteBuf, NamePromptPayload> CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT, NamePromptPayload::entityId,
        NamePromptPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
