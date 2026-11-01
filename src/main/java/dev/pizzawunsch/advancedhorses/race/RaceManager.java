package dev.pizzawunsch.advancedhorses.race;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dev.pizzawunsch.advancedhorses.taming.TamingManager;
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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class RaceManager {
    public static final double ZONE_RADIUS = 4.0;
    private static final long TIMEOUT = 20L * 60L * 20L;
    private static final int TOP_SIZE = 10;
    private static final Logger LOGGER = LoggerFactory.getLogger("advancedhorses");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Type TYPE = new TypeToken<Map<String, Race>>() { }.getType();
    private static final String FILE = "advancedhorses_races.json";

    private static final Map<String, Race> RACES = new LinkedHashMap<>();
    private static final Map<UUID, Run> RUNS = new HashMap<>();
    private static boolean dirty;

    public static final class Race {
        public String dimension;
        public double startX;
        public double startY;
        public double startZ;
        public boolean hasFinish;
        public double finishX;
        public double finishY;
        public double finishZ;
        public List<Result> top = new ArrayList<>();

        Vec3 start() {
            return new Vec3(this.startX, this.startY, this.startZ);
        }

        Vec3 finish() {
            return new Vec3(this.finishX, this.finishY, this.finishZ);
        }
    }

    public record Result(String player, String horse, long ticks) {
    }

    private static final class Run {
        final String race;
        long startTick = -1L;

        Run(String race) {
            this.race = race;
        }
    }

    private RaceManager() {
    }

    public static boolean create(String name, ServerPlayer player) {
        if (RACES.containsKey(name)) {
            return false;
        }
        Race race = new Race();
        race.dimension = player.level().dimension().identifier().toString();
        race.startX = player.getX();
        race.startY = player.getY();
        race.startZ = player.getZ();
        RACES.put(name, race);
        dirty = true;
        return true;
    }

    public static boolean setFinish(String name, ServerPlayer player) {
        Race race = RACES.get(name);
        if (race == null || !race.dimension.equals(player.level().dimension().identifier().toString())) {
            return false;
        }
        race.hasFinish = true;
        race.finishX = player.getX();
        race.finishY = player.getY();
        race.finishZ = player.getZ();
        dirty = true;
        return true;
    }

    public static boolean remove(String name) {
        RUNS.values().removeIf(run -> run.race.equals(name));
        dirty |= RACES.remove(name) != null;
        return !RACES.containsKey(name);
    }

    public static List<String> names() {
        return new ArrayList<>(RACES.keySet());
    }

    public static Race get(String name) {
        return RACES.get(name);
    }

    public static String format(long ticks) {
        long millis = ticks * 50L;
        return String.format(Locale.ROOT, "%d:%02d.%02d", millis / 60000L, millis / 1000L % 60L, millis / 10L % 100L);
    }

    public static void tick(MinecraftServer server) {
        long now = server.overworld().getGameTime();
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            tickPlayer(player, now);
        }
        RUNS.keySet().removeIf(id -> server.getPlayerList().getPlayer(id) == null);
        if (now % 1200L == 0L) {
            save(server, false);
        }
    }

    private static void tickPlayer(ServerPlayer player, long now) {
        Run run = RUNS.get(player.getUUID());
        if (!(player.getVehicle() instanceof AbstractHorse horse) || !TamingManager.isTameableType(horse)) {
            if (run != null && run.startTick >= 0L) {
                player.sendOverlayMessage(Component.translatable("advancedhorses.race.cancelled").withStyle(ChatFormatting.RED));
            }
            RUNS.remove(player.getUUID());
            return;
        }

        String dimension = player.level().dimension().identifier().toString();
        Vec3 position = player.position();
        if (run == null) {
            for (Map.Entry<String, Race> entry : RACES.entrySet()) {
                Race race = entry.getValue();
                if (race.hasFinish && race.dimension.equals(dimension) && race.start().closerThan(position, ZONE_RADIUS)) {
                    RUNS.put(player.getUUID(), new Run(entry.getKey()));
                    player.sendOverlayMessage(Component.translatable("advancedhorses.race.ready", entry.getKey()).withStyle(ChatFormatting.GOLD));
                    return;
                }
            }
            return;
        }

        Race race = RACES.get(run.race);
        if (race == null || !race.dimension.equals(dimension)) {
            RUNS.remove(player.getUUID());
            return;
        }

        if (run.startTick < 0L) {
            if (!race.start().closerThan(position, ZONE_RADIUS)) {
                run.startTick = now;
                player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.NOTE_BLOCK_BELL.value(), SoundSource.PLAYERS, 1.0F, 1.5F);
                player.sendOverlayMessage(Component.translatable("advancedhorses.race.go", run.race).withStyle(ChatFormatting.GREEN));
            }
            return;
        }

        long elapsed = now - run.startTick;
        if (elapsed > TIMEOUT) {
            RUNS.remove(player.getUUID());
            player.sendOverlayMessage(Component.translatable("advancedhorses.race.cancelled").withStyle(ChatFormatting.RED));
            return;
        }

        if (race.finish().closerThan(position, ZONE_RADIUS)) {
            RUNS.remove(player.getUUID());
            finish(player, horse, run.race, race, elapsed);
        } else if (elapsed % 5L == 0L) {
            player.sendOverlayMessage(Component.literal(format(elapsed)).withStyle(ChatFormatting.YELLOW));
        }
    }

    private static void finish(ServerPlayer player, AbstractHorse horse, String name, Race race, long ticks) {
        int place = -1;
        Result result = new Result(player.getName().getString(), horse.getName().getString(), ticks);
        race.top.add(result);
        race.top.sort(Comparator.comparingLong(Result::ticks));
        while (race.top.size() > TOP_SIZE) {
            race.top.removeLast();
        }
        place = race.top.indexOf(result);
        dirty = true;

        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0F, 1.0F);
        player.sendSystemMessage(Component.translatable("advancedhorses.race.finished", name, format(ticks), horse.getName()).withStyle(ChatFormatting.GOLD));
        if (place >= 0) {
            player.sendSystemMessage(Component.translatable("advancedhorses.race.place", place + 1).withStyle(ChatFormatting.AQUA));
        }
    }

    private static Path path(MinecraftServer server) {
        return server.getWorldPath(LevelResource.ROOT).resolve(FILE);
    }

    public static void load(MinecraftServer server) {
        RACES.clear();
        RUNS.clear();
        dirty = false;
        Path path = path(server);
        if (!Files.exists(path)) {
            return;
        }
        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            Map<String, Race> data = GSON.fromJson(reader, TYPE);
            if (data != null) {
                for (Iterator<Map.Entry<String, Race>> it = data.entrySet().iterator(); it.hasNext(); ) {
                    Map.Entry<String, Race> entry = it.next();
                    if (entry.getValue().top == null) {
                        entry.getValue().top = new ArrayList<>();
                    }
                    RACES.put(entry.getKey(), entry.getValue());
                }
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
            GSON.toJson(RACES, TYPE, writer);
            dirty = false;
        } catch (IOException exception) {
            LOGGER.warn("Could not write {}", path, exception);
        }
    }
}
