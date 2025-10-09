package lv.id.bonne.batzapper;


import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import dev.architectury.event.events.common.CommandRegistrationEvent;
import lv.id.bonne.batzapper.commands.BatZapperCommands;
import lv.id.bonne.batzapper.config.Configuration;
import lv.id.bonne.batzapper.config.ConfigurationManager;
import lv.id.bonne.batzapper.registries.BatZapperBlockRegistry;
import lv.id.bonne.batzapper.registries.BatZapperCreativeTabRegistry;
import lv.id.bonne.batzapper.registries.BatZapperItemRegistry;


public final class BatZapper
{
    public static void init()
    {
        BatZapperCreativeTabRegistry.register();
        BatZapperBlockRegistry.register();
        BatZapperItemRegistry.register();

        BatZapper.CONFIG_MANAGER.readConfig();

        CommandRegistrationEvent.EVENT.register(
            (dispatcher, registry, selection) -> BatZapperCommands.register(dispatcher));
    }


    public static Configuration config()
    {
        return CONFIG_MANAGER.getConfiguration();
    }


    public static final ConfigurationManager CONFIG_MANAGER = new ConfigurationManager();

    public static final String MOD_ID = "bat_zapper";

    public static final Logger LOGGER = LogUtils.getLogger();
}
