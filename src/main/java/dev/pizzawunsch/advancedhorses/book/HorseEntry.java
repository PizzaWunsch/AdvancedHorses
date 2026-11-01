package dev.pizzawunsch.advancedhorses.book;

import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;

public record HorseEntry(UUID id, String name, int breed, float speed, int stamina, int strength, int bond, int hunger,
                         int age, int level, String dimension, int x, int y, int z, long lastSeen) {

    public void write(FriendlyByteBuf buf) {
        buf.writeUUID(this.id);
        buf.writeUtf(this.name, 64);
        buf.writeVarInt(this.breed);
        buf.writeFloat(this.speed);
        buf.writeVarInt(this.stamina);
        buf.writeVarInt(this.strength);
        buf.writeVarInt(this.bond);
        buf.writeVarInt(this.hunger);
        buf.writeVarInt(this.age);
        buf.writeVarInt(this.level);
        buf.writeUtf(this.dimension, 128);
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.z);
        buf.writeLong(this.lastSeen);
    }

    public static HorseEntry read(FriendlyByteBuf buf) {
        return new HorseEntry(buf.readUUID(), buf.readUtf(64), buf.readVarInt(), buf.readFloat(), buf.readVarInt(),
            buf.readVarInt(), buf.readVarInt(), buf.readVarInt(), buf.readVarInt(), buf.readVarInt(), buf.readUtf(128),
            buf.readInt(), buf.readInt(), buf.readInt(), buf.readLong());
    }
}
