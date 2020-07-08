package fr.litopia.bot.commands;

import fr.litopia.bukkit.Main;
import fr.litopia.postgres.DBConnection;
import fr.litopia.postgres.Select;
import fr.litopia.postgres.Update;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;

public class RejectCandidature extends ListenerAdapter {

    private static Select S;
    private static Update U;
    private static long tchatChanID;
    private static String prefix;
    private static Main plugin;
    private final DBConnection db;

    public RejectCandidature(Main main) {
        tchatChanID = main.config.getLong("tchatChanelID");
        prefix = main.config.getString("prefix");
        String dbConn = main.config.getString("postgresConnString");
        plugin = main;
        this.db = new DBConnection(dbConn);
        S = new Select(db.connect());
        U = new Update(db.connect());
    }

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        String[] args = event.getMessage().getContentRaw().split("\\s+");
        if (event.getAuthor().isBot()) return;
        if (event.getChannel().getIdLong() == tchatChanID) return;
        if (args[0].equalsIgnoreCase(prefix + "rejectUser")) {
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
                        //recupération du membre mentionée
                        Member member = event.getGuild().getMemberById(event.getMessage().getMentionedUsers().get(0).getId());
                        //Envois un message de refu
                        event.getMessage().getMentionedUsers().get(0).openPrivateChannel().flatMap(channel -> channel.sendMessage(":x: **Désoler mais tu est definitivement refusée de litopia**")).queue();
                        //supretion de tous c'est roles
                        for (Role r:member.getRoles()) {
                            member.getGuild().removeRoleFromMember(member,r).queue();
                        }
                        //Ajout du role refusée
                        member.getGuild().addRoleToMember(member,member.getGuild().getRoleById("482281139609010187")).queue();
                        //envois du message de validation au modo
                        event.getChannel().sendMessage("**:white_check_mark: le membre à bien était refuser**").queue();
                        //Mise à jour de la BDD
                        U.rejectMembers(member.getId());
                    } catch (Exception e) {
                        event.getChannel().sendMessage("**:warning: Une erreur inatendu c'est produite**\n`"+e.getMessage()+"`").queue();
                        e.printStackTrace();
                    }
                } else {
                    event.getChannel().sendMessage("**:warning: Veuillez mentioner la personne à ejecter de litopia**").queue();
                }
            }else{
                event.getChannel().sendMessage("**:warning: Vous n'avez pas la permission d'executer la commande**").queue();
            }
        }
    }
}
