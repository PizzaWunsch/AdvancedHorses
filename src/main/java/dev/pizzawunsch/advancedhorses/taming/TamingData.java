package dev.pizzawunsch.advancedhorses.taming;

public final class TamingData {
    public static final int SLOTS = 3;

    private TamingData() {
    }

    public static int packNeeds(TamingNeed[] needs, TamingTreat treat) {
        int packed = 0;
        for (int i = 0; i < SLOTS; i++) {
            packed |= (needs[i].id() & 0xF) << (i * 4);
        }
        return packed | (treat.id() & 0xF) << (SLOTS * 4);
    }

    public static TamingNeed need(int packedNeeds, int slot) {
        return TamingNeed.byId(packedNeeds >> (slot * 4) & 0xF);
    }

    public static TamingTreat treat(int packedNeeds) {
        return TamingTreat.byId(packedNeeds >> (SLOTS * 4) & 0xF);
    }

    public static int packProgress(float[] fractions) {
        int packed = 0;
        for (int i = 0; i < SLOTS; i++) {
            int permille = Math.round(Math.clamp(fractions[i], 0.0F, 1.0F) * 1000.0F);
            packed |= permille << (i * 10);
        }
        return packed;
    }

    public static float progress(int packedProgress, int slot) {
        return (packedProgress >> (slot * 10) & 0x3FF) / 1000.0F;
    }

    public static float totalProgress(int packedProgress) {
        float sum = 0.0F;
        for (int i = 0; i < SLOTS; i++) {
            sum += progress(packedProgress, i);
        }
        return sum / SLOTS;
    }
}
