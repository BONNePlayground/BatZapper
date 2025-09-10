package lv.id.bonne.batzapper.fabric.client;


import lv.id.bonne.batzapper.client.BatZapperClient;
import net.fabricmc.api.ClientModInitializer;


public final class BatzapperFabricClient implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        // This entrypoint is suitable for setting up client-specific logic, such as rendering.
        BatZapperClient.init();
    }
}
