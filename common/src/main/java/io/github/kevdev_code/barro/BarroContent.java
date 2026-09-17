package io.github.kevdev_code.barro;

import java.util.function.Function;
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
import net.minecraft.world.level.block.CarpetBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.VoxelShape;

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
    public static final RegistrySupplier<Block> LOSA_DE_BARRO_COCIDO = slab("losa_de_barro_cocido", BARRO_COCIDO);
    public static final RegistrySupplier<Block> ESCALERAS_DE_BARRO_COCIDO = stairs("escaleras_de_barro_cocido", BARRO_COCIDO);
    public static final RegistrySupplier<Block> PARED_DE_BARRO_COCIDO = wall("pared_de_barro_cocido", BARRO_COCIDO);
    public static final RegistrySupplier<Block> PETATILLO = block("petatillo", BarroContent::barro);
    // Earth and stone
    public static final RegistrySupplier<Block> ADOBE = block("adobe", BarroContent::adobe);
    public static final RegistrySupplier<Block> LOSA_DE_ADOBE = slab("losa_de_adobe", ADOBE);
    public static final RegistrySupplier<Block> ESCALERAS_DE_ADOBE = stairs("escaleras_de_adobe", ADOBE);
    public static final RegistrySupplier<Block> PARED_DE_ADOBE = wall("pared_de_adobe", ADOBE);
    public static final RegistrySupplier<Block> CANTERA = block("cantera", BarroContent::cantera);
    public static final RegistrySupplier<Block> LOSA_DE_CANTERA = slab("losa_de_cantera", CANTERA);
    public static final RegistrySupplier<Block> ESCALERAS_DE_CANTERA = stairs("escaleras_de_cantera", CANTERA);
    public static final RegistrySupplier<Block> PARED_DE_CANTERA = wall("pared_de_cantera", CANTERA);
    // Woven palm and cut paper
    public static final RegistrySupplier<Block> PETATE = block("petate", CarpetBlock::new, () -> BlockBehaviour.Properties.of()
            .mapColor(MapColor.SAND)
            .strength(0.1F)
            .sound(SoundType.GRASS)
            .ignitedByLava());
    public static final RegistrySupplier<Block> PAPEL_PICADO = block("papel_picado", PapelPicadoBlock::new, () -> BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_MAGENTA)
            .instabreak()
            .noCollision()
            .noOcclusion()
            .sound(SoundType.WOOL)
            .ignitedByLava()
            .pushReaction(PushReaction.DESTROY));
    // Pottery and stoneware, each with a model and a hitbox of its own
    public static final RegistrySupplier<Block> MACETA_DE_BARRO = decor("maceta_de_barro", Block.box(3, 0, 3, 13, 10, 13), BarroContent::loza);
    public static final RegistrySupplier<Block> OLLA_DE_BARRO = decor("olla_de_barro", Block.box(2, 0, 2, 14, 14, 14), BarroContent::loza);
    public static final RegistrySupplier<Block> COMAL = decor("comal", Block.box(1, 0, 1, 15, 2, 15), BarroContent::loza);
    public static final RegistrySupplier<Block> MOLCAJETE = decor("molcajete", Block.box(3, 0, 3, 13, 7, 13), () -> BlockBehaviour.Properties.of()
            .mapColor(MapColor.STONE)
            .instrument(NoteBlockInstrument.BASEDRUM)
            .strength(1.5F)
            .sound(SoundType.BASALT)
            .noOcclusion());

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

    // Vanilla mud bricks properties, with an earth map color.
    private static BlockBehaviour.Properties adobe() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.DIRT)
                .instrument(NoteBlockInstrument.BASEDRUM)
                .requiresCorrectToolForDrops()
                .strength(1.5F, 3.0F)
                .sound(SoundType.MUD_BRICKS);
    }

    // Vanilla tuff properties (cantera is volcanic tuff), with a pink map color.
    private static BlockBehaviour.Properties cantera() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.TERRACOTTA_WHITE)
                .instrument(NoteBlockInstrument.BASEDRUM)
                .requiresCorrectToolForDrops()
                .strength(1.5F, 6.0F)
                .sound(SoundType.TUFF);
    }

    // Fired pottery: breaks by hand, and a piston smashes it like vanilla's decorated pot.
    private static BlockBehaviour.Properties loza() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_ORANGE)
                .strength(0.5F)
                .sound(SoundType.DECORATED_POT)
                .pushReaction(PushReaction.DESTROY)
                .noOcclusion();
    }

    private static RegistrySupplier<Block> decor(String name, VoxelShape shape, Supplier<BlockBehaviour.Properties> properties) {
        return block(name, props -> new SmallDecorBlock(shape, props), properties);
    }

    // Slab, stairs and wall copy their base block's properties with ofLegacyCopy, like vanilla does for these.
    // It is deprecated, but ofFullCopy would also copy the base block's drops and description id.
    private static RegistrySupplier<Block> slab(String name, RegistrySupplier<Block> base) {
        return block(name, SlabBlock::new, () -> BlockBehaviour.Properties.ofLegacyCopy(base.get()));
    }

    // StairBlock's constructor is protected in 26.1.2, hence the anonymous subclass.
    private static RegistrySupplier<Block> stairs(String name, RegistrySupplier<Block> base) {
        return block(name, properties -> new StairBlock(base.get().defaultBlockState(), properties) {
        }, () -> BlockBehaviour.Properties.ofLegacyCopy(base.get()));
    }

    private static RegistrySupplier<Block> wall(String name, RegistrySupplier<Block> base) {
        return block(name, WallBlock::new, () -> BlockBehaviour.Properties.ofLegacyCopy(base.get()).forceSolidOn());
    }

    private static RegistrySupplier<Block> block(String name, Supplier<BlockBehaviour.Properties> properties) {
        return block(name, Block::new, properties);
    }

    // Registers a block plus its BlockItem. Since 1.21.2 both Properties need their registry key set before construction.
    private static RegistrySupplier<Block> block(String name, Function<BlockBehaviour.Properties, Block> factory, Supplier<BlockBehaviour.Properties> properties) {
        Identifier id = Identifier.fromNamespaceAndPath(Barro.MOD_ID, name);
        RegistrySupplier<Block> block = BLOCKS.register(id, () -> factory.apply(properties.get().setId(ResourceKey.create(Registries.BLOCK, id))));
        ITEMS.register(id, () -> new BlockItem(block.get(), new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id)).useBlockDescriptionPrefix()));
        return block;
    }
}
