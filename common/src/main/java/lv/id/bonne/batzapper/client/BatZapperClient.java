//
// Created by BONNe
// Copyright - 2025
//


package lv.id.bonne.batzapper.client;


import dev.architectury.registry.client.rendering.RenderTypeRegistry;
import lv.id.bonne.batzapper.registries.BatZapperBlockRegistry;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;


public class BatZapperClient
{
    public static void init()
    {
        RenderTypeRegistry.register(ChunkSectionLayer.TRANSLUCENT, BatZapperBlockRegistry.BAT_ZAPPER.get());
    }
}
