package dev.pizzawunsch.advancedhorses.network;

import dev.pizzawunsch.advancedhorses.AdvancedHorses;
import dev.pizzawunsch.advancedhorses.book.HorseEntry;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record HorseBookPayload(List<HorseEntry> entries) implements CustomPacketPayload {
    public static final Type<HorseBookPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(AdvancedHorses.MOD_ID, "horse_book"));
    public static final StreamCodec<RegistryFriendlyByteBuf, HorseBookPayload> CODEC = CustomPacketPayload.codec(HorseBookPayload::write, HorseBookPayload::read);

    private void write(RegistryFriendlyByteBuf buf) {
        buf.writeVarInt(this.entries.size());
        for (HorseEntry entry : this.entries) {
            entry.write(buf);
        }
    }

    private static HorseBookPayload read(RegistryFriendlyByteBuf buf) {
        int size = Math.min(buf.readVarInt(), 512);
        List<HorseEntry> entries = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            entries.add(HorseEntry.read(buf));
        }
        return new HorseBookPayload(entries);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
