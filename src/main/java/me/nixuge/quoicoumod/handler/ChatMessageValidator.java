package me.nixuge.quoicoumod.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Map.Entry;

import me.nixuge.quoicoumod.McMod;
import me.nixuge.quoicoumod.config.ConfigCache;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;

public class ChatMessageValidator {
    private static Minecraft mc = Minecraft.getMinecraft();
    private static ConfigCache config = McMod.getInstance().getConfigCache();
    private static Random random = new Random();

    private String msg;
    private char[] msgArray;
    private int msgArrayLen;

    // Constructor unused but just in case
    public ChatMessageValidator(String msg) {
        setMsg(msg);
    }
    public ChatMessageValidator() {}

    public void setMsg(String msg) {
        this.msg = msg.toLowerCase().trim();
        this.msgArray = this.msg.toCharArray();
        this.msgArrayLen = this.msgArray.length;
    }

    public boolean compareMessageEnd(char[] entry) {
        if (entry.length > msgArrayLen)
            return false;
        
        for (int i = 0; i < entry.length; i++) {
            if (entry[i] != msgArray[msgArrayLen - 1 - i])
                return false;
        }
        return true;
    }

    private boolean shouldTryToAnswer() {
        // Command check
        if (this.msg.startsWith("/"))
            return false;

        // Global checks
        if (config.isEnabledAllServers() && !mc.isIntegratedServerRunning())
            return true;
        
        if (config.isEnabledSolo() && mc.isIntegratedServerRunning()) 
            return true;
        

        // Server specific checks
        if (config.isEnabledServerList() && !mc.isIntegratedServerRunning()) {
            ServerData sd = mc.getCurrentServerData();
            if (sd == null) // redundant (=solo) but just in case
                return false;
            
            String ip = sd.serverIP;
            for (String server : config.getServerList()) {
                if (ip.contains(server))
                    return true;
            }
        }

        
        // Otherwise if nothing enabled/no servers matched
        return false;
    }

    private String tryGrabMapAnswer() {
        List<String> matches = new ArrayList<String>(0);
        for (Entry<char[], String> entry : config.getTextAnswerMap().entrySet()) {
            if (compareMessageEnd(entry.getKey()))
                matches.add(entry.getValue());
        }

        if (matches.size() > 0) 
            return grabPrefix() + matches.get(random.nextInt(matches.size()));
        
        return null;
    }

    private String grabPrefix() {
        ServerData sd = mc.getCurrentServerData();
        if (sd == null)
            return "";
        String ip = sd.serverIP;

        for (Entry<String, String> entry : config.getPrefixesMap().entrySet()) {
            if (ip.contains(entry.getKey()))
                return entry.getValue();
        }
        return "";
    }

    public String getAnswer() {
        if (!shouldTryToAnswer())
            return null;
        
        return tryGrabMapAnswer();
    }
}
