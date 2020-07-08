package fr.litopia.bot.commands;

import fr.litopia.bot.models.SimplePlayerData;
import fr.litopia.bot.models.SimplePlayersCollection;
import fr.litopia.bukkit.Main;
import fr.litopia.postgres.DBConnection;
import fr.litopia.postgres.Select;
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
                    SimplePlayersCollection globalPlayersCollection = new SimplePlayersCollection(plugin);
                    sendRankGlobalPointMessage(event, globalPlayersCollection,"Leaderbord global des meilleurs","Point");
                } catch (Exception e) {
                    event.getChannel().sendMessage("**:warning: Une erreur inconue et survenue** \n`" + e.getMessage() + "`").queue();
                    e.printStackTrace();
                }
            } else {
                switch (args[1]) {
                    case "jumps":
                        try {
                            SimplePlayersCollection globalPlayersCollection = new SimplePlayersCollection(plugin);
                            sendRankJumpMessage(event, globalPlayersCollection,"Leaderbord des lapins ou des chat","saut");
                        } catch (Exception e) {
                            event.getChannel().sendMessage("**:warning: Une erreur inconue et survenue** \n`" + e.getMessage() + "`").queue();
                            e.printStackTrace();
                        }
                        break;
                    case "death":
                        try {
                            SimplePlayersCollection globalPlayersCollection = new SimplePlayersCollection(plugin);
                            sendRankDeathMessage(event, globalPlayersCollection,"Leaderbord des joueur les moin mort","mort");
                        } catch (Exception e) {
                            event.getChannel().sendMessage("**:warning: Une erreur inconue et survenue** \n`" + e.getMessage() + "`").queue();
                            e.printStackTrace();
                        }
                        break;
                    case "mobs":
                        try {
                            SimplePlayersCollection globalPlayersCollection = new SimplePlayersCollection(plugin);
                            sendRankMobsKillMessage(event, globalPlayersCollection,"Leaderbord des joueur les plus sanginaire","mob tuer");
                        } catch (Exception e) {
                            event.getChannel().sendMessage("**:warning: Une erreur inconue et survenue** \n`" + e.getMessage() + "`").queue();
                            e.printStackTrace();
                        }
                        break;
                    case "players":
                        try {
                            SimplePlayersCollection globalPlayersCollection = new SimplePlayersCollection(plugin);
                            sendRankPlayerKillMessage(event, globalPlayersCollection,"Leaderbord des killer en séris","joueur tuer");
                        } catch (Exception e) {
                            event.getChannel().sendMessage("**:warning: Une erreur inconue et survenue** \n`" + e.getMessage() + "`").queue();
                            e.printStackTrace();
                        }
                        break;
                    case "distance":
                        try {
                            SimplePlayersCollection globalPlayersCollection = new SimplePlayersCollection(plugin);
                            sendRankDistanceMessage(event, globalPlayersCollection,"Leaderbord des voyageurs","km");
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
        eb.addField("**"+prefix+"leaderboard jumps**","Affiche le leaderboard du nombres de jump",true);
        eb.addField("**"+prefix+"leaderboard death**","Affiche le leaderboard du nombres de mort",true);
        eb.addField("**"+prefix+"leaderboard mobs**","Affiche le leaderboard du nombres de mobs tuer",true);
        eb.addField("**"+prefix+"leaderboard players**","Affiche le leaderboard du nombres de joueur tuer",true);
        eb.addField("**"+prefix+"leaderboard distance**","Affiche le leaderboard des nomade",true);
        event.getChannel().sendMessage(eb.build()).queue();
    }

    private void sendRankJumpMessage(GuildMessageReceivedEvent event, SimplePlayersCollection globalPlayersCollection,String Title, String keyname) {
        globalPlayersCollection.sortByJump();
        EmbedBuilder eb = new EmbedBuilder();
        StringBuilder s = new StringBuilder("```\n");
        int i = 0;
        for (SimplePlayerData playerdata : globalPlayersCollection.getGlobalPlayersData()) {
            i = i + 1;
            s.append(i).append(" - ").append(playerdata.getTotalJump()).append(" ").append(keyname).append(" ").append(playerdata.getMinecraftUsername()).append("\n");
            if (i == 30) break;
        }
        s.append("```");
        eb.addField(Title, s.toString(), false);
        event.getChannel().sendMessage(eb.build()).queue();
    }

    private void sendRankPlayerKillMessage(GuildMessageReceivedEvent event, SimplePlayersCollection globalPlayersCollection,String Title, String keyname) {
        globalPlayersCollection.sortByPlayerKill();
        EmbedBuilder eb = new EmbedBuilder();
        StringBuilder s = new StringBuilder("```\n");
        int i = 0;
        for (SimplePlayerData playerdata : globalPlayersCollection.getGlobalPlayersData()) {
            i = i + 1;
            s.append(i).append(" - ").append(playerdata.getTotalPlayerKill()).append(" ").append(keyname).append(" ").append(playerdata.getMinecraftUsername()).append("\n");
            if (i == 30) break;
        }
        s.append("```");
        eb.addField(Title, s.toString(), false);
        event.getChannel().sendMessage(eb.build()).queue();
    }

    private void sendRankDeathMessage(GuildMessageReceivedEvent event, SimplePlayersCollection globalPlayersCollection,String Title, String keyname) {
        globalPlayersCollection.sortByNumberDeath();
        EmbedBuilder eb = new EmbedBuilder();
        StringBuilder s = new StringBuilder("```\n");
        int i = 0;
        for (SimplePlayerData playerdata : globalPlayersCollection.getGlobalPlayersData()) {
            i = i + 1;
            s.append(i).append(" - ").append(playerdata.getTotalDeath()).append(" ").append(keyname).append(" ").append(playerdata.getMinecraftUsername()).append("\n");
            if (i == 30) break;
        }
        s.append("```");
        eb.addField(Title, s.toString(), false);
        event.getChannel().sendMessage(eb.build()).queue();
    }

    private void sendRankMobsKillMessage(GuildMessageReceivedEvent event, SimplePlayersCollection globalPlayersCollection,String Title, String keyname) {
        globalPlayersCollection.sortByMobkill();
        EmbedBuilder eb = new EmbedBuilder();
        StringBuilder s = new StringBuilder("```\n");
        int i = 0;
        for (SimplePlayerData playerdata : globalPlayersCollection.getGlobalPlayersData()) {
            i = i + 1;
            s.append(i).append(" - ").append(playerdata.getTotalMobKill()).append(" ").append(keyname).append(" ").append(playerdata.getMinecraftUsername()).append("\n");
            if (i == 30) break;
        }
        s.append("```");
        eb.addField(Title, s.toString(), false);
        event.getChannel().sendMessage(eb.build()).queue();
    }

    private void sendRankDistanceMessage(GuildMessageReceivedEvent event, SimplePlayersCollection globalPlayersCollection,String Title, String keyname) {
        globalPlayersCollection.sortByParcourDistance();
        EmbedBuilder eb = new EmbedBuilder();
        StringBuilder s = new StringBuilder("```\n");
        int i = 0;
        for (SimplePlayerData playerdata : globalPlayersCollection.getGlobalPlayersData()) {
            i = i + 1;
            s.append(i).append(" - ").append(playerdata.getTotalParcourDistance()).append(" ").append(keyname).append(" ").append(playerdata.getMinecraftUsername()).append("\n");
            if (i == 30) break;
        }
        s.append("```");
        eb.addField(Title, s.toString(), false);
        event.getChannel().sendMessage(eb.build()).queue();
    }

    private void sendRankGlobalPointMessage(GuildMessageReceivedEvent event, SimplePlayersCollection globalPlayersCollection,String Title, String keyname) {
        globalPlayersCollection.sortByGlobalRankingScore();
        EmbedBuilder eb = new EmbedBuilder();
        StringBuilder s = new StringBuilder("```\n");
        int i = 0;
        for (SimplePlayerData playerdata : globalPlayersCollection.getGlobalPlayersData()) {
            i = i + 1;
            s.append(i).append(" - ").append(playerdata.getRankingScore()).append(" ").append(keyname).append(" ").append(playerdata.getMinecraftUsername()).append("\n");
            if (i == 30) break;
        }
        s.append("```");
        eb.addField(Title, s.toString(), false);
        event.getChannel().sendMessage(eb.build()).queue();
    }
}
