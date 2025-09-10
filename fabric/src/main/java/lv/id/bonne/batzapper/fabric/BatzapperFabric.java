package lv.id.bonne.batzapper.fabric;


import lv.id.bonne.batzapper.BatZapper;
import net.fabricmc.api.ModInitializer;


public final class BatzapperFabric implements ModInitializer
{
    @Override
    public void onInitialize()
    {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        // Run our common setup.
        BatZapper.init();
    }
}
