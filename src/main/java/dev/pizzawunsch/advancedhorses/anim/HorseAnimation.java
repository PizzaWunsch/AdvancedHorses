package dev.pizzawunsch.advancedhorses.anim;

public enum HorseAnimation {
    HEAD_SHAKE(120, 22),
    PAW(121, 44),
    BUCK(122, 20),
    NUZZLE(123, 40),
    BODY_SHAKE(124, 28),
    STRETCH(125, 40),
    HOOF_STAMP(126, 18),
    HEAD_TOSS(127, 16);

    private final byte eventId;
    private final int duration;

    HorseAnimation(int eventId, int duration) {
        this.eventId = (byte) eventId;
        this.duration = duration;
    }

    public byte eventId() {
        return this.eventId;
    }

    public int duration() {
        return this.duration;
    }

    public static HorseAnimation byEventId(byte id) {
        for (HorseAnimation animation : values()) {
            if (animation.eventId == id) {
                return animation;
            }
        }
        return null;
    }
}
