package dev.pizzawunsch.advancedhorses.registry;

import dev.pizzawunsch.advancedhorses.AdvancedHorses;
import dev.pizzawunsch.advancedhorses.item.HorseBookItem;
import dev.pizzawunsch.advancedhorses.item.HorseWhistleItem;
import java.util.function.Function;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;

public final class ModItems {
    public static final Item HORSE_BOOK = register("horse_book", HorseBookItem::new, new Item.Properties().stacksTo(1));
    public static final Item HORSE_WHISTLE = register("horse_whistle", HorseWhistleItem::new, new Item.Properties().stacksTo(1));
    public static final Item IRON_HORSESHOE = register("iron_horseshoe", Item::new, new Item.Properties().durability(3000).enchantable(9));
    public static final Item DIAMOND_HORSESHOE = register("diamond_horseshoe", Item::new, new Item.Properties().durability(9000).enchantable(10));
    public static final Item TROUGH = registerBlockItem("trough");

    public static final ResourceKey<CreativeModeTab> TAB_KEY = ResourceKey.create(Registries.CREATIVE_MODE_TAB,
        Identifier.fromNamespaceAndPath(AdvancedHorses.MOD_ID, "horses"));

    private ModItems() {
    }

    public static void init() {
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, TAB_KEY, FabricCreativeModeTab.builder()
            .title(Component.translatable("itemGroup.advancedhorses.horses"))
            .icon(() -> new ItemStack(Items.SADDLE))
            .displayItems((context, output) -> {
                output.accept(HORSE_BOOK);
                output.accept(HORSE_WHISTLE);
                output.accept(TROUGH);
                output.accept(IRON_HORSESHOE);
                output.accept(DIAMOND_HORSESHOE);
                output.accept(Items.SADDLE);
                output.accept(Items.LEATHER_HORSE_ARMOR);
                output.accept(Items.COPPER_HORSE_ARMOR);
                output.accept(Items.IRON_HORSE_ARMOR);
                output.accept(Items.GOLDEN_HORSE_ARMOR);
                output.accept(Items.DIAMOND_HORSE_ARMOR);
                output.accept(Items.NETHERITE_HORSE_ARMOR);
                output.accept(Items.LEAD);
                output.accept(Items.NAME_TAG);
                output.accept(Items.BRUSH);
                output.accept(Items.HAY_BLOCK);
                output.accept(Items.WHEAT);
                output.accept(Items.SUGAR);
                output.accept(Items.APPLE);
                output.accept(Items.CARROT);
                output.accept(Items.GOLDEN_CARROT);
                output.accept(Items.GOLDEN_APPLE);
                for (Item item : new Item[]{Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION}) {
                    output.accept(PotionContents.createItemStack(item, ModEffects.STAMINA_POTION));
                    output.accept(PotionContents.createItemStack(item, ModEffects.LONG_STAMINA_POTION));
                }
                output.accept(Items.CHEST);
                output.accept(Items.HORSE_SPAWN_EGG);
                output.accept(Items.DONKEY_SPAWN_EGG);
                output.accept(Items.MULE_SPAWN_EGG);
                output.accept(Items.SKELETON_HORSE_SPAWN_EGG);
                output.accept(Items.ZOMBIE_HORSE_SPAWN_EGG);
            })
            .build());
    }

    private static ResourceKey<Item> key(String name) {
        return ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(AdvancedHorses.MOD_ID, name));
    }

    private static Item register(String name, Function<Item.Properties, Item> factory, Item.Properties properties) {
        ResourceKey<Item> key = key(name);
        return Registry.register(BuiltInRegistries.ITEM, key, factory.apply(properties.setId(key)));
    }

    private static Item registerBlockItem(String name) {
        ResourceKey<Item> key = key(name);
        BlockItem item = new BlockItem(ModBlocks.TROUGH, new Item.Properties().useBlockDescriptionPrefix().setId(key));
        item.registerBlocks(Item.BY_BLOCK, item);
        return Registry.register(BuiltInRegistries.ITEM, key, item);
    }
}
