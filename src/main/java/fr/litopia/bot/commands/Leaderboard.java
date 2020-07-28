package fr.litopia.bot.commands;

import fr.litopia.bot.models.LeaderboardData;
import fr.litopia.bukkit.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Leaderboard extends ListenerAdapter {
    private static long tchatChanID;
    private static String prefix;
    private static Main plugin;

    public Leaderboard(Main main) {
        tchatChanID = main.config.getLong("tchatChanelID");
        prefix = main.config.getString("prefix");
        plugin = main;
    }

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        String[] args = event.getMessage().getContentRaw().split("\\s+");
        if (event.getAuthor().isBot()) return;
        if (event.getChannel().getIdLong() == tchatChanID) return;
        if (args[0].equalsIgnoreCase(prefix + "leaderboard")||args[0].equalsIgnoreCase(prefix + "lb")) {

            //Si il n'y a pas d'argument
            if (args.length == 1) {
                try {
                    sendRankGlobalPointMessage(event, "Leaderbord global des meilleurs");
                } catch (Exception e) {
                    event.getChannel().sendMessage("**:warning: Une erreur inconue et survenue** \n`" + e.getMessage() + "`").queue();
                    e.printStackTrace();
                }
            } else {
                switch (args[1]) {
                    case "jumps":
                        try {
                            sendRankJumpMessage(event, "Leaderbord des lapins ou des chat");
                        } catch (Exception e) {
                            event.getChannel().sendMessage("**:warning: Une erreur inconue et survenue** \n`" + e.getMessage() + "`").queue();
                            e.printStackTrace();
                        }
                        break;
                    case "death":
                        if (args.length == 2) {
                            try {
                                sendRankDeathMessage(event, "Leaderbord des joueur les moin mort");
                            } catch (Exception e) {
                                event.getChannel().sendMessage("**:warning: Une erreur inconue et survenue** \n`" + e.getMessage() + "`").queue();
                                e.printStackTrace();
                            }
                        }
                        else if (args[2].equals("reverse")) {
                            try {
                                sendRankDeathReverseMessage(event, "Leaderbord des joueur les plus mort");
                            } catch (Exception e) {
                                event.getChannel().sendMessage("**:warning: Une erreur inconue et survenue** \n`" + e.getMessage() + "`").queue();
                                e.printStackTrace();
                            }
                        }
                        break;
                    case "mobs":
                        try {
                            sendRankMobsKillMessage(event,"Leaderbord des joueur les plus sanginaire");
                        } catch (Exception e) {
                            event.getChannel().sendMessage("**:warning: Une erreur inconue et survenue** \n`" + e.getMessage() + "`").queue();
                            e.printStackTrace();
                        }
                        break;
                    case "players":
                        try {
                            sendRankPlayerKillMessage(event,"Leaderbord des killer en séris");
                        } catch (Exception e) {
                            event.getChannel().sendMessage("**:warning: Une erreur inconue et survenue** \n`" + e.getMessage() + "`").queue();
                            e.printStackTrace();
                        }
                        break;
                    case "walk":
                        try {
                            sendRankWalkMessage(event,"Leaderbord des voyageurs");
                        } catch (Exception e) {
                            event.getChannel().sendMessage("**:warning: Une erreur inconue et survenue** \n`" + e.getMessage() + "`").queue();
                            e.printStackTrace();
                        }
                        break;
                    case "vehicle":
                        try {
                            sendRankTransportation(event,"Leaderbord des conducteur");
                        } catch (Exception e) {
                            event.getChannel().sendMessage("**:warning: Une erreur inconue et survenue** \n`" + e.getMessage() + "`").queue();
                            e.printStackTrace();
                        }
                        break;
                    case "playtime":
                        try {
                            sendRankGlobalPlayTimeMessage(event,"Leaderbord du temps de jeux");
                        } catch (Exception e) {
                            event.getChannel().sendMessage("**:warning: Une erreur inconue et survenue** \n`" + e.getMessage() + "`").queue();
                            e.printStackTrace();
                        }
                        break;
                    case "advancement":
                        try {
                            sendRankGlobalAdvancement(event,"Leaderbord des advencement");
                        } catch (Exception e) {
                            event.getChannel().sendMessage("**:warning: Une erreur inconue et survenue** \n`" + e.getMessage() + "`").queue();
                            e.printStackTrace();
                        }
                        break;
                    default:
                        try{
                            sendHelpMessage(event);
                        }catch (Exception e) {
                            event.getChannel().sendMessage("**:warning: Une erreur inconue et survenue** \n`" + e.getMessage() + "`").queue();
                            e.printStackTrace();
                        }
                        break;
                }
            }
        }
    }

    private void sendHelpMessage(GuildMessageReceivedEvent event) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Help !");
        eb.appendDescription("Cette commande vous permet de conaitre les statistique des joueurs de litopia");
        eb.addField("**"+prefix+"leaderboard**","Affiche le leaderboard global",true);
        eb.addField("**"+prefix+"leaderboard playtime**","Affiche le leaderboard du temps de jeu",true);
        eb.addField("**"+prefix+"leaderboard advancement**","Affiche le leaderboard des advancement",true);
        eb.addField("**"+prefix+"leaderboard jumps**","Affiche le leaderboard du nombres de jump",true);
        eb.addField("**"+prefix+"leaderboard death**","Affiche le leaderboard du nombres de mort",true);
        eb.addField("**"+prefix+"leaderboard mobs**","Affiche le leaderboard du nombres de mobs tuer",true);
        eb.addField("**"+prefix+"leaderboard players**","Affiche le leaderboard du nombres de joueur tuer",true);
        eb.addField("**"+prefix+"leaderboard walk**","Affiche le leaderboard des nomade",true);
        eb.addField("**"+prefix+"leaderboard vehicle**","Affiche le leaderboard des conduteur",true);
        event.getChannel().sendMessage(eb.build()).queue();
    }

    private void sendRankGlobalAdvancement(GuildMessageReceivedEvent event, String Title) throws Exception {
        EmbedBuilder eb = new EmbedBuilder();
        eb.addField(Title, LeaderboardData.getlbAdvancement(plugin), false);
        event.getChannel().sendMessage(eb.build()).queue();
    }

    private void sendRankTransportation(GuildMessageReceivedEvent event, String Title) throws Exception {
        EmbedBuilder eb = new EmbedBuilder();
        eb.addField(Title, LeaderboardData.getlbTransportation(plugin), false);
        event.getChannel().sendMessage(eb.build()).queue();
    }

    private void sendRankJumpMessage(GuildMessageReceivedEvent event,String Title) throws Exception {
        EmbedBuilder eb = new EmbedBuilder();
        eb.addField(Title, LeaderboardData.getlbJump(plugin), false);
        event.getChannel().sendMessage(eb.build()).queue();
    }

    private void sendRankPlayerKillMessage(GuildMessageReceivedEvent event,String Title) throws Exception {
        EmbedBuilder eb = new EmbedBuilder();
        eb.addField(Title, LeaderboardData.getlbPlayerKills(plugin), false);
        event.getChannel().sendMessage(eb.build()).queue();
    }

    private void sendRankDeathMessage(GuildMessageReceivedEvent event,String Title) throws Exception {
        EmbedBuilder eb = new EmbedBuilder();
        eb.addField(Title, LeaderboardData.getlbDeath(plugin), false);
        event.getChannel().sendMessage(eb.build()).queue();
    }

    private void sendRankDeathReverseMessage(GuildMessageReceivedEvent event,String Title) throws Exception {
        EmbedBuilder eb = new EmbedBuilder();
        eb.addField(Title, LeaderboardData.getlbDeathReverse(plugin), false);
        event.getChannel().sendMessage(eb.build()).queue();
    }

    private void sendRankMobsKillMessage(GuildMessageReceivedEvent event,String Title) throws Exception {
        EmbedBuilder eb = new EmbedBuilder();
        eb.addField(Title, LeaderboardData.getlbMobsKill(plugin), false);
        event.getChannel().sendMessage(eb.build()).queue();
    }

    private void sendRankWalkMessage(GuildMessageReceivedEvent event, String Title) throws Exception{
        EmbedBuilder eb = new EmbedBuilder();
        eb.addField(Title, LeaderboardData.getlbWalk(plugin), false);
        event.getChannel().sendMessage(eb.build()).queue();

    }

    private void sendRankGlobalPointMessage(GuildMessageReceivedEvent event,String Title) throws Exception {
        EmbedBuilder eb = new EmbedBuilder();
        eb.addField(Title, LeaderboardData.getlbScore(plugin), false);
        event.getChannel().sendMessage(eb.build()).queue();
    }

    private void sendRankGlobalPlayTimeMessage(GuildMessageReceivedEvent event,String Title)throws Exception{
        EmbedBuilder eb = new EmbedBuilder();
        eb.addField(Title, LeaderboardData.getlbTime(plugin), false);
        event.getChannel().sendMessage(eb.build()).queue();
    }

    private void sendlbTransportation(GuildMessageReceivedEvent event,String Title) throws Exception {
        EmbedBuilder eb = new EmbedBuilder();
        eb.addField(Title, LeaderboardData.getlbTransportation(plugin), false);
        event.getChannel().sendMessage(eb.build()).queue();
    }

    private void sendlbAdvancement(GuildMessageReceivedEvent event,String Title) throws Exception {
        EmbedBuilder eb = new EmbedBuilder();
        eb.addField(Title, LeaderboardData.getlbAdvancement(plugin), false);
        event.getChannel().sendMessage(eb.build()).queue();
    }
}
