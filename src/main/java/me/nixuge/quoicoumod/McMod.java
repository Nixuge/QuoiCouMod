package me.nixuge.quoicoumod;

import java.io.File;

import lombok.Getter;
import lombok.Setter;
import me.nixuge.quoicoumod.config.ConfigCache;
import me.nixuge.quoicoumod.handler.ChatMessageHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(
        modid = McMod.MOD_ID,
        name = McMod.NAME,
        version = McMod.VERSION,
        guiFactory = "me.nixuge.quoicoumod.gui.GuiFactory",
        clientSideOnly = true
)

@Setter
public class McMod {
    public static final String MOD_ID = "quoicoumod";
    public static final String NAME = "QuoiCouMod";
    public static final String VERSION = "1.0.1";


    @Getter
    @Mod.Instance(value = McMod.MOD_ID)
    private static McMod instance;
    @Getter
    private Configuration configuration;
    @Getter
    private String configDirectory;
    @Getter
    private ConfigCache configCache;

    @Mod.EventHandler
    public void preInit(final FMLPreInitializationEvent event) {
        this.configDirectory = event.getModConfigurationDirectory().toString();
        final File path = new File(this.configDirectory + File.separator + McMod.MOD_ID + ".cfg");
        this.configuration = new Configuration(path);
        this.configCache = new ConfigCache(this.configuration);
    }

    @Mod.EventHandler
    public void init(final FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new ChatMessageHandler());
        MinecraftForge.EVENT_BUS.register(configCache);
    }
}
