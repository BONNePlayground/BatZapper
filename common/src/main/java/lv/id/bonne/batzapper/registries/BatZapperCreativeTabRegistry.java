//
// Created by BONNe
// Copyright - 2025
//


package lv.id.bonne.batzapper.registries;


import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import lv.id.bonne.batzapper.BatZapper;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;


public class BatZapperCreativeTabRegistry
{
    public static void register()
    {
        REGISTRY.register();
    }

    public static final DeferredRegister<CreativeModeTab> REGISTRY =
        DeferredRegister.create(BatZapper.MOD_ID, Registries.CREATIVE_MODE_TAB);

    public static final RegistrySupplier<CreativeModeTab> BAT_ZAPPER_TAB = REGISTRY.register("bat_zapper", () ->
        CreativeTabRegistry.create(Component.translatable("category.bat_zapper.bat_zapper"),
            () -> new ItemStack(BatZapperBlockRegistry.BAT_ZAPPER.get())));
}
