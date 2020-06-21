package fr.litopia.bot.event;

import fr.litopia.bukkit.Main;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

public class TchatEvent extends ListenerAdapter {
    private final Main plugin;
    private static long chanelId;

    public TchatEvent(Main main){
        this.plugin = main;
        chanelId = this.plugin.config.getLong("tchatChanelID");
    }

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        if(event.getAuthor().isBot())return;
        if(event.getTextChannel().getIdLong() != chanelId)return;
        this.plugin.sendMessageToMinecraft(event.getMessage());
    }
}
