package me.nixuge.quoicoumod.config;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import lombok.Getter;
import me.nixuge.quoicoumod.McMod;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Getter
public class ConfigCache {
    private final Configuration configuration;

    private boolean enabledSolo;
    private boolean enabledAllServers;
    private boolean enabledServerList;
    private String[] serverList;
    private String[] textAnswerList;
    private Map<char[], String> textAnswerMap;

    public ConfigCache(final Configuration configuration) {
        this.configuration = configuration;
        this.loadConfiguration();
        this.configuration.save();
    }
    
    private char[] invertArray(char[] arr) {
        int len = arr.length;
        char[] newArr = new char[len];
        for (int i = 0; i < len; i++) {
            newArr[i] = arr[len - 1 - i];
        }
        return newArr;
    }

    private void genTextAnswerMap() {
        this.textAnswerMap = new HashMap<>();
        for (String string : textAnswerList) {
            String[] data = string.toLowerCase().split("=");
            if (data.length != 2) {
                Logger.getLogger("QuoiCouMod").warning("String " + textAnswerList + " isn't valid !");
                continue;
            }
            textAnswerMap.put(invertArray(data[0].toCharArray()), data[1]);
        }
    }

    private void loadConfiguration() {
        // Enable
        this.enabledAllServers = this.configuration.getBoolean(
                "Enable (all servers)",
                "General",
                true,
                "If the mod should be enabled on all servers, regardless of the \"Enabled servers\" option."
        );
        this.enabledSolo = this.configuration.getBoolean(
                "Enable (local)",
                "General",
                false,
                "If you want the mod to be active in solo worlds."
        );
        this.enabledServerList = this.configuration.getBoolean(
                "Enable (server list)",
                "General",
                true,
                "If you want the mod to be active in the server list you completed below."
        );
        this.serverList = this.configuration.getStringList(
                "Server List",
                "General",
                new String[]{},
                "Servers where the mod takes effect. Only useful if \"Enabled (srver list)\" is on."
        );
        
        // Word lists
        this.textAnswerList = this.configuration.getStringList(
                "Answer List",
                "General",
                new String[]{},
                "What messages ends to answer to and with what answer. Format with MESSAGE=ANSWER."
        );
        genTextAnswerMap();
    }

    @SubscribeEvent
    public void onConfigurationChangeEvent(final ConfigChangedEvent.OnConfigChangedEvent event) {
        this.configuration.save();
        if (event.modID.equalsIgnoreCase(McMod.MOD_ID)) {
            this.loadConfiguration();
        }
    }
}