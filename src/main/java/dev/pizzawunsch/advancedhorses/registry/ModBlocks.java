package dev.pizzawunsch.advancedhorses.registry;

import dev.pizzawunsch.advancedhorses.AdvancedHorses;
import dev.pizzawunsch.advancedhorses.block.TroughBlock;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public final class ModBlocks {
    public static final ResourceKey<Block> TROUGH_KEY = ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(AdvancedHorses.MOD_ID, "trough"));
    public static final Block TROUGH = Registry.register(BuiltInRegistries.BLOCK, TROUGH_KEY, new TroughBlock(BlockBehaviour.Properties.of()
        .mapColor(MapColor.PODZOL)
        .strength(1.5F)
        .sound(SoundType.WOOD)
        .noOcclusion()
        .setId(TROUGH_KEY)));

    private ModBlocks() {
    }

    public static void init() {
    }
}
