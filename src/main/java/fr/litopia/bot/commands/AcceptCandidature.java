package fr.litopia.bot.commands;

import fr.litopia.bukkit.Main;
import fr.litopia.postgres.DBConnection;
import fr.litopia.postgres.Select;
import fr.litopia.postgres.Update;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
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
    private Update U;

    public AcceptCandidature(Main main){
        tchatChanID = main.config.getLong("tchatChanelID");
        prefix = main.config.getString("prefix");
        dbConn = main.config.getString("postgresConnString");
        plugin = main;
        this.db = new DBConnection(dbConn);
        this.S = new Select(db.connect());
        this.U = new Update(db.connect());
    }

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        String[] args = event.getMessage().getContentRaw().split("\\s+");
        if (event.getAuthor().isBot()) return;
        if (event.getChannel().getIdLong() == tchatChanID) return;
        if (args[0].equalsIgnoreCase(prefix + "acceptUser")) {
            if (event.getGuild().getMember(event.getAuthor()).hasPermission(Permission.BAN_MEMBERS)) {
                if (event.getMessage().getMentionedUsers().size() == 1) {
                    try {
                        String nickname ="";
                        try {
                            nickname = S.getMcNicknameFomDiscordID(event.getMessage().getMentionedUsers().get(0).getId());
                        }catch (SQLException e){
                            event.getChannel().sendMessage("**:warning: <@"+event.getMessage().getMentionedUsers().get(0).getId()+"> n'est pas dans la BDD ou est déjà Litopien**\n`"+e.getMessage()+"`").queue();
                            return;
                        }
                        Member member = event.getGuild().getMemberById(event.getMessage().getMentionedUsers().get(0).getId());
                        event.getMessage().getMentionedUsers().get(0).openPrivateChannel().flatMap(channel -> channel.sendMessage(":white_check_mark: **Génial, tu est accepter dans la communauté litopienne. Tu peux d'ésaprésent découvrir les autre joueur si cela n'est pas deja faite et venir jouée sur le serveur avec l'IP suivante: `play.litopia.fr`.**")).queue();
                        member.getGuild().addRoleToMember(member,member.getGuild().getRoleById("390447376986275842")).queue();
                        U.acceptMembers(member.getId());
                        plugin.addToWhiteListe(nickname);
                        event.getChannel().sendMessage("**:white_check_mark: le membre à bien était ajouter à la whiteliste**").queue();

                    } catch (Exception e) {
                        event.getChannel().sendMessage("**:warning: Une erreur inatendu c'est produite**\n`"+e.getMessage()+"`").queue();
                        e.printStackTrace();
                    }
                } else {
                    event.getChannel().sendMessage("**:warning: Veuillez mentioner la personne à accepter sur litopia**").queue();
                }
            }else{
                event.getChannel().sendMessage("**:warning: Vous n'avez pas la permission d'executer la commande**").queue();
            }
        }
    }
}
