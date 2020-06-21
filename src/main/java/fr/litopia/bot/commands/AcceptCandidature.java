package fr.litopia.bot.commands;

import fr.litopia.bukkit.Main;
import fr.litopia.postgres.DBConnection;
import fr.litopia.postgres.Select;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.RestAction;

import java.sql.SQLException;

public class AcceptCandidature extends ListenerAdapter {
    private static final long MILLI_PER_TICK = 1 / 20;
    private static long tchatChanID;
    private static String prefix;
    private static String dbConn;
    private static Main plugin;
    private DBConnection db;
    private Select S;

    public AcceptCandidature(Main main){
        tchatChanID = main.config.getLong("tchatChanelID");
        prefix = main.config.getString("prefix");
        dbConn = main.config.getString("postgresConnString");
        plugin = main;
        this.db = new DBConnection(dbConn);
        this.S = new Select(db.connect());
    }

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        String[] args = event.getMessage().getContentRaw().split("\\s+");
        if (event.getAuthor().isBot()) return;
        if (event.getChannel().getIdLong() == tchatChanID) return;
        if (!event.getMember().hasPermission(Permission.BAN_MEMBERS))return;
        if (args[0].equalsIgnoreCase(prefix + "acceptUser")) {
            if (event.getMessage().getMentionedUsers().size() == 1) {
                try {
                    String nickname = S.getMcNicknameFomDiscordID(event.getMessage().getMentionedUsers().get(0).getId());
                    plugin.addToWhiteListe(nickname);
                    event.getMessage().getMentionedUsers().get(0).openPrivateChannel().flatMap(channel -> channel.sendMessage(":white_check_mark: **Génial, tu est accepter dans la communauté litopienne. Tu peux d'ésaprésent découvrir les autre joueur si cela n'est pas deja faite et venir jouée sur le serveur avec l'IP suivante: `play.litopia.fr`.**")).queue();

                } catch (Exception e) {
                    event.getChannel().sendMessage("**:warning: Une erreur inatendu c'est produite**");
                }
            }else{
                event.getChannel().sendMessage("**:warning: Veuillez mentioner la personne à accepter sur litopia**");
            }
        }
    }
}
