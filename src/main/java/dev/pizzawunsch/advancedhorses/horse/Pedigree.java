package dev.pizzawunsch.advancedhorses.horse;

import dev.pizzawunsch.advancedhorses.taming.TamingManager;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.world.entity.animal.equine.AbstractHorse;

public final class Pedigree {
    public static final String MOTHER = "M";
    public static final String FATHER = "F";
    public static final String[] GRANDPARENTS = {"MM", "MF", "FM", "FF"};

    private Pedigree() {
    }

    public static String create(AbstractHorse mother, AbstractHorse father) {
        Map<String, String> parentsOfMother = parse(TamingManager.access(mother).advancedhorses$getPedigree());
        Map<String, String> parentsOfFather = parse(TamingManager.access(father).advancedhorses$getPedigree());
        Map<String, String> entries = new LinkedHashMap<>();
        entries.put(MOTHER, describe(mother));
        entries.put(FATHER, describe(father));
        putName(entries, "MM", parentsOfMother.get(MOTHER));
        putName(entries, "MF", parentsOfMother.get(FATHER));
        putName(entries, "FM", parentsOfFather.get(MOTHER));
        putName(entries, "FF", parentsOfFather.get(FATHER));

        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> entry : entries.entrySet()) {
            if (!builder.isEmpty()) {
                builder.append('\n');
            }
            builder.append(entry.getKey()).append('=').append(entry.getValue());
        }
        return builder.toString();
    }

    private static String describe(AbstractHorse horse) {
        return clean(horse.getName().getString()) + ";" + HorseStats.summary(horse);
    }

    private static void putName(Map<String, String> entries, String key, String parentEntry) {
        if (parentEntry != null) {
            entries.put(key, name(parentEntry));
        }
    }

    private static String clean(String name) {
        return name.replace(';', ' ').replace('=', ' ').replace('\n', ' ');
    }

    public static Map<String, String> parse(String pedigree) {
        Map<String, String> entries = new LinkedHashMap<>();
        if (pedigree == null || pedigree.isEmpty()) {
            return entries;
        }
        for (String line : pedigree.split("\n")) {
            int split = line.indexOf('=');
            if (split > 0) {
                entries.put(line.substring(0, split), line.substring(split + 1));
            }
        }
        return entries;
    }

    public static String name(String entry) {
        int split = entry.indexOf(';');
        return split < 0 ? entry : entry.substring(0, split);
    }

    public static String[] stats(String entry) {
        String[] parts = entry.split(";");
        if (parts.length < 5) {
            return null;
        }
        return new String[]{parts[1], parts[2], parts[3], parts[4]};
    }
}
