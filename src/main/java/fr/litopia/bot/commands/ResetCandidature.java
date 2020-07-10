package fr.litopia.bot.commands;

import fr.litopia.bukkit.Main;
import fr.litopia.postgres.DBConnection;
import fr.litopia.postgres.Delete;
import fr.litopia.postgres.Select;
import fr.litopia.postgres.Update;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

public class ResetCandidature extends ListenerAdapter {
    private static long tchatChanID;
    private static String prefix;
    private static String dbConn;
    private static Main plugin;
    private DBConnection db;
    private Select S;
    private Delete D;

    public ResetCandidature(Main main){
        tchatChanID = main.config.getLong("tchatChanelID");
        prefix = main.config.getString("prefix");
        dbConn = main.config.getString("postgresConnString");
        plugin = main;
        this.db = new DBConnection(dbConn);
        S = new Select(db.connect());
        D = new Delete(db.connect());
    }

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        String[] args = event.getMessage().getContentRaw().split("\\s+");
        if (event.getAuthor().isBot()) return;
        if (event.getChannel().getIdLong() == tchatChanID) return;
        if (args[0].equalsIgnoreCase(prefix + "resetUser")) {
            if (event.getGuild().getMember(event.getAuthor()).hasPermission(Permission.BAN_MEMBERS)) {
                if (event.getMessage().getMentionedUsers().size() == 1) {
                    ResultSet member;
                    try {
                        member = S.getMemberFromDiscordID(event.getMessage().getMentionedUsers().get(0).getId());
                        if (member.getDate("acceptedate") == null) {
                            if (member.getString("rolename")!= null) {
                                if (member.getString("rolename").equals("Refuser")) {
                                    Member discordMember = event.getGuild().getMemberById(event.getMessage().getMentionedUsers().get(0).getId());
                                    //envois d'un MP au membres
                                    event.getMessage().getMentionedUsers().get(0).openPrivateChannel().flatMap(channel -> channel.sendMessage(":information_source: **Le staff a décider que tu avait le droit à une nouvelle oportunitée de candidatée**\nRDV sur https://litopia.fr/nous-rejoindre")).queue();
                                    //supression de tous les roles du membres
                                    for (Role r : discordMember.getRoles()) {
                                        discordMember.getGuild().removeRoleFromMember(discordMember, r).queue();
                                    }
                                    //Ajout du role en attente de candidature au membre
                                    discordMember.getGuild().addRoleToMember(discordMember, discordMember.getGuild().getRoleById("482612660962197544")).queue();

                                    //supression du membre de la BDD
                                    try {
                                        D.deleteCandidatureFromDiscordID(discordMember.getId());
                                    }catch (SQLException e){
                                        e.printStackTrace();
                                    }
                                    //envois du message de confirmation à l'admin
                                    event.getChannel().sendMessage("**:white_check_mark: le membre à maintenant le droit à une deuxième chance**").queue();
                                } else {
                                    event.getChannel().sendMessage("**:warning: <@" + event.getMessage().getMentionedUsers().get(0).getId() + "> doit être refusée pour avoirs le droit à une deuxiéme chance.**").queue();
                                }
                            } else {
                                event.getChannel().sendMessage("**:warning: <@" + event.getMessage().getMentionedUsers().get(0).getId() + "> doit être refusée pour avoirs le droit à une deuxiéme chance.**").queue();
                            }
                        } else {
                            event.getChannel().sendMessage("**:warning: <@" + event.getMessage().getMentionedUsers().get(0).getId() + "> à déjà était accépter il ne peut donc pas refaire sa candidature**").queue();
                        }
                    } catch (SQLException e) {
                        event.getChannel().sendMessage("**:warning: <@" + event.getMessage().getMentionedUsers().get(0).getId() + "> n'est pas dans la BDD**\n`" + e.getMessage() + "`").queue();
                    }
                } else {
                    event.getChannel().sendMessage("**:warning: Veuillez mentioner la personne qui doit refaire sa candidature**").queue();
                }
            } else {
                event.getChannel().sendMessage("**:warning: Vous n'avez pas la permission d'executer la commande**").queue();
            }
        }
    }
}
