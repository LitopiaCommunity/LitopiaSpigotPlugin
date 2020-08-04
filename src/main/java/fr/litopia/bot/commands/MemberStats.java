package fr.litopia.bot.commands;

import fr.litopia.bukkit.Main;
import fr.litopia.postgres.DBConnection;
import fr.litopia.postgres.Select;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MemberStats extends ListenerAdapter {
    private static long tchatChanID;
    private static String prefix;
    private static String dbConn;
    private static Main plugin;
    private DBConnection db;
    private Select S;

    public MemberStats(Main main) {
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
        if (args[0].equalsIgnoreCase(prefix + "membres")) {
            if (args.length == 1){
                try {
                    sendBaseStatut(event);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            switch (args[1]){
                case "inactif":
                    try {
                        event.getChannel().sendMessage(ListBuilder(S.getInactifListMembers())).queue();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    break;
                case "actif":
                    try {
                        event.getChannel().sendMessage(ListBuilder(S.getActifMembers())).queue();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    break;
                case "attente":
                    try {
                        event.getChannel().sendMessage(ListBuilder(S.getAwaitVotesMembers())).queue();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    break;
                case "refuser":
                    try {
                        event.getChannel().sendMessage(ListBuilder(S.getRejectMembers())).queue();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    break;
                case "help":
                    sendHelpMessage(event);
                    break;
                default:
                    event.getChannel().sendMessage("Une erreur dans votre premier argument").queue();
                    break;
            }
        }
    }

    public void sendBaseStatut(GuildMessageReceivedEvent event) throws SQLException {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Status des members de Litopia !");
        eb.appendDescription("Cette commande vous permet de conaitre les info des joueurs de Litopia");
        eb.addField("Membres de Litopia",String.valueOf(S.countMembers())+" membres",true);
        eb.addField("Membres Actif",String.valueOf(S.countActiveMembers())+" membres actif",true);
        eb.addField("Attente de votes",String.valueOf(S.countAwaitVotesMembers())+" candidats en attente",true);
        eb.addField("Candidats refuser",String.valueOf(S.countRejectMembers())+" candidats refuser",true);
        eb.setFooter("Pour plus de d'info "+prefix+"membres help");
        event.getChannel().sendMessage(eb.build()).queue();
    }

    private void sendHelpMessage(GuildMessageReceivedEvent event) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Help !");
        eb.appendDescription("Cette commande vous permet de conaitre les statistique des joueurs de litopia");
        eb.addField("**"+prefix+"membres**","Affiche le nombres de membres et leurs status",true);
        eb.addField("**"+prefix+"membres actif**","Affiche la liste des Litopien inactif",true);
        eb.addField("**"+prefix+"membres inactif**","Affiche la liste des Litopien actif",true);
        eb.addField("**"+prefix+"membres attente**","Affiche la liste des candidature en attente",true);
        eb.addField("**"+prefix+"membres refuser**","Affiche la liste des candidat refusée",true);
        event.getChannel().sendMessage(eb.build()).queue();
    }

    private String ListBuilder(ResultSet rs) throws SQLException {
        StringBuilder returnList = new StringBuilder();
        returnList.append("```").append("Minecraft Nickname").append(" - ").append("Discord Nickname").append('\n');
        while (rs.next()){
            returnList.append(rs.getString(1)).append(" - ").append(rs.getString(2)).append('\n');
        }
        returnList.append("```");
        return returnList.toString();
    }


}
