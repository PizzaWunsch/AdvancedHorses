package dev.pizzawunsch.advancedhorses.book;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dev.pizzawunsch.advancedhorses.horse.HorseByte;
import dev.pizzawunsch.advancedhorses.horse.HorseLevel;
import dev.pizzawunsch.advancedhorses.horse.HorseLife;
import dev.pizzawunsch.advancedhorses.horse.HorseStat;
import dev.pizzawunsch.advancedhorses.horse.HorseStats;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.level.storage.LevelResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class HorseRegistry {
    private static final Logger LOGGER = LoggerFactory.getLogger("advancedhorses");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Type TYPE = new TypeToken<Map<UUID, Map<UUID, HorseEntry>>>() { }.getType();
    private static final String FILE = "advancedhorses_horses.json";

    private static final Map<UUID, Map<UUID, HorseEntry>> ENTRIES = new HashMap<>();
    private static boolean dirty;

    private HorseRegistry() {
    }

    public static void update(AbstractHorse horse) {
        UUID owner = HorseLife.ownerId(horse);
        if (owner == null) {
            return;
        }
        HorseEntry entry = new HorseEntry(horse.getUUID(), horse.getName().getString(), HorseStats.breed(horse).id(),
            (float) HorseStats.blocksPerSecond(HorseStats.speed(horse)), Math.round(HorseStats.stamina(horse) * 100.0F),
            Math.round(HorseStats.strength(horse) * 100.0F), Math.round(HorseStats.bond(horse) * 100.0F),
            Math.round(HorseStats.get(horse, HorseStat.HUNGER)), HorseStats.get(horse, HorseByte.AGE), HorseLevel.level(horse),
            horse.level().dimension().identifier().toString(), horse.getBlockX(), horse.getBlockY(), horse.getBlockZ(),
            horse.level().getGameTime());
        ENTRIES.computeIfAbsent(owner, key -> new LinkedHashMap<>()).put(horse.getUUID(), entry);
        dirty = true;
    }

    public static void onDeath(LivingEntity entity) {
        if (entity instanceof AbstractHorse horse) {
            UUID owner = HorseLife.ownerId(horse);
            if (owner != null && ENTRIES.containsKey(owner) && ENTRIES.get(owner).remove(horse.getUUID()) != null) {
                dirty = true;
            }
        }
    }

    public static List<HorseEntry> entries(UUID owner) {
        List<HorseEntry> list = new ArrayList<>(ENTRIES.getOrDefault(owner, Map.of()).values());
        list.sort(Comparator.comparing(HorseEntry::name, String.CASE_INSENSITIVE_ORDER));
        return list;
    }

    private static Path path(MinecraftServer server) {
        return server.getWorldPath(LevelResource.ROOT).resolve(FILE);
    }

    public static void load(MinecraftServer server) {
        ENTRIES.clear();
        dirty = false;
        Path path = path(server);
        if (!Files.exists(path)) {
            return;
        }
        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            Map<UUID, Map<UUID, HorseEntry>> data = GSON.fromJson(reader, TYPE);
            if (data != null) {
                data.forEach((owner, horses) -> ENTRIES.put(owner, new LinkedHashMap<>(horses)));
            }
        } catch (IOException | RuntimeException exception) {
            LOGGER.warn("Could not read {}", path, exception);
        }
    }

    public static void save(MinecraftServer server, boolean force) {
        if (!dirty && !force) {
            return;
        }
        Path path = path(server);
        try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            GSON.toJson(ENTRIES, TYPE, writer);
            dirty = false;
        } catch (IOException exception) {
            LOGGER.warn("Could not write {}", path, exception);
        }
    }
}
