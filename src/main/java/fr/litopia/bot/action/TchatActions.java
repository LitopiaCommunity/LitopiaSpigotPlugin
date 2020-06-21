package fr.litopia.bot.action;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import fr.litopia.bukkit.Main;
import org.bukkit.entity.Player;

public class TchatActions {
    private WebhookClient webhookClient;
    private Main plugin;

    public TchatActions(Main plugin){
        this.plugin = plugin;
        WebhookClientBuilder clientBuilder = new WebhookClientBuilder(plugin.config.getString("TchatWebhookURL"));
        this.webhookClient = clientBuilder.build();
    }

    public void sendMessageToDiscord(Player player, String message){
        WebhookMessageBuilder builder = new WebhookMessageBuilder();
        builder.setUsername(player.getName()); // Definition du nom d'utilisateur
        builder.setAvatarUrl("https://crafatar.com/avatars/" + player.getUniqueId().toString()); // Definition de l'avatar
        builder.setContent(message); //recupération du contenu du message
        this.webhookClient.send(builder.build());
    }

    public void sendBasicBotMessage(String message){
        WebhookMessageBuilder builder = new WebhookMessageBuilder();
        builder.setUsername(plugin.getJda().getSelfUser().getName());
        builder.setAvatarUrl(plugin.getJda().getSelfUser().getAvatarUrl());
        builder.setContent(message);
        this.webhookClient.send(builder.build());
    }
}
