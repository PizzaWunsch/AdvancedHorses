package dev.pizzawunsch.advancedhorses.inventory;

import net.minecraft.world.SimpleContainer;

public class HorseshoeContainer extends SimpleContainer {
    private final Runnable onChange;

    public HorseshoeContainer(Runnable onChange) {
        super(1);
        this.onChange = onChange;
    }

    @Override
    public void setChanged() {
        super.setChanged();
        this.onChange.run();
    }
}
