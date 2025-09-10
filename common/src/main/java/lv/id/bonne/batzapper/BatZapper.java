package lv.id.bonne.batzapper;


import lv.id.bonne.batzapper.registries.BatZapperBlockRegistry;
import lv.id.bonne.batzapper.registries.BatZapperCreativeTabRegistry;
import lv.id.bonne.batzapper.registries.BatZapperItemRegistry;


public final class BatZapper
{
    public static final String MOD_ID = "bat_zapper";


    public static void init()
    {
        BatZapperCreativeTabRegistry.register();
        BatZapperBlockRegistry.register();
        BatZapperItemRegistry.register();
    }
}
