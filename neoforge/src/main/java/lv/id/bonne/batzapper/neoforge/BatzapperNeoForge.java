package lv.id.bonne.batzapper.neoforge;


import lv.id.bonne.batzapper.BatZapper;
import net.neoforged.fml.common.Mod;


@Mod(BatZapper.MOD_ID)
public final class BatzapperNeoForge
{
    public BatzapperNeoForge()
    {
        // Run our common setup.
        BatZapper.init();
    }
}
