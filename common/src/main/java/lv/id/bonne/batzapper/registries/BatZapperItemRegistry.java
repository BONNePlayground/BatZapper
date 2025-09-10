//
// Created by BONNe
// Copyright - 2025
//


package lv.id.bonne.batzapper.registries;


import dev.architectury.registry.registries.DeferredRegister;
import lv.id.bonne.batzapper.BatZapper;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;


public class BatZapperItemRegistry
{
    public static void register()
    {
        REGISTRY.register();
    }


    /**
     * The main item registry.
     */
    public static final DeferredRegister<Item> REGISTRY =
        DeferredRegister.create(BatZapper.MOD_ID, Registries.ITEM);
}
