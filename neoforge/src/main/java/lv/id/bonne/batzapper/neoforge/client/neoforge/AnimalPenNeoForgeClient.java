package lv.id.bonne.batzapper.neoforge.client.neoforge;


import lv.id.bonne.batzapper.BatZapper;
import lv.id.bonne.batzapper.client.BatZapperClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;


@EventBusSubscriber(modid = BatZapper.MOD_ID, value = Dist.CLIENT)
public class AnimalPenNeoForgeClient
{
    @SubscribeEvent
    public static void init(FMLClientSetupEvent event)
    {
        BatZapperClient.init();
    }


    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event)
    {

    }
}