//
// Created by BONNe
// Copyright - 2025
//


package lv.id.bonne.batzapper.registries;


import java.util.function.Supplier;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import lv.id.bonne.batzapper.BatZapper;
import lv.id.bonne.batzapper.blocks.BatLureBlock;
import lv.id.bonne.batzapper.blocks.BatZapperBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;


public class BatZapperBlockRegistry
{
    public static void register()
    {
        REGISTRY.register();
    }

    private static <T extends Block> RegistrySupplier<T> registerBlock(String name, Supplier<T> block)
    {
        RegistrySupplier<T> toReturn = REGISTRY.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> RegistrySupplier<Item> registerBlockItem(String name, RegistrySupplier<T> block)
    {
        return BatZapperItemRegistry.REGISTRY.register(name, () ->
            new BlockItem(block.get(),
                new Item.Properties().arch$tab(BatZapperCreativeTabRegistry.BAT_ZAPPER_TAB)));
    }


    /**
     * The main block registry.
     */
    public static final DeferredRegister<Block> REGISTRY =
        DeferredRegister.create(BatZapper.MOD_ID, Registries.BLOCK);


    public static final RegistrySupplier<Block> BAT_ZAPPER = registerBlock("bat_zapper",
        () -> new BatZapperBlock(
            BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS).
                strength(1.0f).
                sound(SoundType.GLASS).
                noOcclusion().
                lightLevel(state -> 3))
    );


    public static final RegistrySupplier<Block> BAT_LURE = registerBlock("bat_lure",
        () -> new BatLureBlock(
            BlockBehaviour.Properties.ofFullCopy(Blocks.NOTE_BLOCK).
                strength(1.0f).
                sound(SoundType.WOOD).
                noOcclusion().
                randomTicks().
                lightLevel(state -> 1))
    );
}
