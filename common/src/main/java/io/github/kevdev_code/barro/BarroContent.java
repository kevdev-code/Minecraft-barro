package io.github.kevdev_code.barro;

import java.util.function.Supplier;

import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

public final class BarroContent {
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Barro.MOD_ID, Registries.BLOCK);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Barro.MOD_ID, Registries.ITEM);
    private static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Barro.MOD_ID, Registries.CREATIVE_MODE_TAB);

    // Registration order is creative tab order.
    // Talavera patterns
    public static final RegistrySupplier<Block> AZULEJO_TALAVERA_FLOR = block("azulejo_talavera_flor", () -> talavera(DyeColor.WHITE));
    public static final RegistrySupplier<Block> AZULEJO_TALAVERA_ESTRELLA = block("azulejo_talavera_estrella", () -> talavera(DyeColor.WHITE));
    public static final RegistrySupplier<Block> AZULEJO_TALAVERA_ROMBOS = block("azulejo_talavera_rombos", () -> talavera(DyeColor.WHITE));
    public static final RegistrySupplier<Block> AZULEJO_TALAVERA_HOJAS = block("azulejo_talavera_hojas", () -> talavera(DyeColor.WHITE));
    public static final RegistrySupplier<Block> AZULEJO_TALAVERA_CRUZ = block("azulejo_talavera_cruz", () -> talavera(DyeColor.WHITE));
    public static final RegistrySupplier<Block> AZULEJO_TALAVERA_SOL = block("azulejo_talavera_sol", () -> talavera(DyeColor.WHITE));
    public static final RegistrySupplier<Block> AZULEJO_TALAVERA_MEDALLON = block("azulejo_talavera_medallon", () -> talavera(DyeColor.WHITE));
    public static final RegistrySupplier<Block> AZULEJO_TALAVERA_CENEFA = block("azulejo_talavera_cenefa", () -> talavera(DyeColor.WHITE));
    // Plain talavera, for framing the patterns
    public static final RegistrySupplier<Block> AZULEJO_TALAVERA_LISO_AZUL = block("azulejo_talavera_liso_azul", () -> talavera(DyeColor.BLUE));
    public static final RegistrySupplier<Block> AZULEJO_TALAVERA_LISO_AMARILLO = block("azulejo_talavera_liso_amarillo", () -> talavera(DyeColor.YELLOW));
    public static final RegistrySupplier<Block> AZULEJO_TALAVERA_LISO_VERDE = block("azulejo_talavera_liso_verde", () -> talavera(DyeColor.GREEN));
    // Fired clay
    public static final RegistrySupplier<Block> BARRO_COCIDO = block("barro_cocido", BarroContent::barro);
    public static final RegistrySupplier<Block> PETATILLO = block("petatillo", BarroContent::barro);

    // Lists everything in ITEMS in registration order, so new blocks appear without touching the tab.
    public static final RegistrySupplier<CreativeModeTab> TAB = TABS.register("barro", () -> CreativeTabRegistry.create(builder -> builder
            .title(Component.translatable("itemGroup.barro"))
            .icon(() -> new ItemStack(AZULEJO_TALAVERA_FLOR.get()))
            .displayItems((parameters, output) -> ITEMS.forEach(item -> output.accept(item.get())))));

    private BarroContent() {
    }

    public static void register() {
        BLOCKS.register();
        ITEMS.register();
        TABS.register();
    }

    // Vanilla glazed terracotta properties, minus PUSH_ONLY: that is a glazed-terracotta mechanic, not a tile trait.
    private static BlockBehaviour.Properties talavera(DyeColor mapColor) {
        return BlockBehaviour.Properties.of()
                .mapColor(mapColor)
                .instrument(NoteBlockInstrument.BASEDRUM)
                .requiresCorrectToolForDrops()
                .strength(1.4F);
    }

    // Vanilla terracotta properties.
    private static BlockBehaviour.Properties barro() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_ORANGE)
                .instrument(NoteBlockInstrument.BASEDRUM)
                .requiresCorrectToolForDrops()
                .strength(1.25F, 4.2F);
    }

    // Registers a block plus its BlockItem. Since 1.21.2 both Properties need their registry key set before construction.
    private static RegistrySupplier<Block> block(String name, Supplier<BlockBehaviour.Properties> properties) {
        Identifier id = Identifier.fromNamespaceAndPath(Barro.MOD_ID, name);
        RegistrySupplier<Block> block = BLOCKS.register(id, () -> new Block(properties.get().setId(ResourceKey.create(Registries.BLOCK, id))));
        ITEMS.register(id, () -> new BlockItem(block.get(), new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id)).useBlockDescriptionPrefix()));
        return block;
    }
}
