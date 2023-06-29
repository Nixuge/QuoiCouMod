package me.nixuge.quoicoumod.handler;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ChatMessageHandler {
    Minecraft mc = Minecraft.getMinecraft();
    ChatMessageValidator msgValidator = new ChatMessageValidator();

    @SubscribeEvent
    public void onChatMessage(ClientChatReceivedEvent event) {
        msgValidator.setMsg(event.message.getUnformattedText()); // has less (useless) chars than getUnformattedText()
        String answer = msgValidator.getAnswer();
        if (answer != null)
            mc.thePlayer.sendChatMessage(answer);
    }
}
