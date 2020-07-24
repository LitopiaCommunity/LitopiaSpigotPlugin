package fr.litopia.bot.commands;

import fr.litopia.bot.models.GlobalPlayerData;
import fr.litopia.bot.models.SimplePlayerData;
import fr.litopia.bot.models.SimplePlayersCollection;
import fr.litopia.bukkit.Main;
import fr.litopia.bukkit.models.PlayerStats;
import fr.litopia.postgres.DBConnection;
import fr.litopia.postgres.Select;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Stats extends ListenerAdapter {
    private static final long MILLI_PER_TICK = 1 / 20;
    private static long tchatChanID;
    private static String prefix;
    private static String dbConn;
    private static Main plugin;
    private DBConnection db;
    private Select S;

    public Stats(Main main){
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
        if (args[0].equalsIgnoreCase(prefix + "stats")) {

            //Si il n'y a pas d'argument
            if (args.length == 1){
                //on récupére les donnée de l'autheur
                try {
                    PlayerStats playerStats = PlayerStats.playerStatsMagicBuilder(event.getAuthor().getId(), plugin,true);
                    sendBaseStatMessage(event,playerStats);
                    return;
                } catch (Exception e) {
                    event.getChannel().sendMessage("**:warning: Vous n'êtes pas enregistrer dans la BDD** \n`"+e.getMessage()+"`").queue();
                    return;
                }
            }else {
                switch (args[1].toLowerCase()) {

                    case "mobs":
                        if (args.length == 2) {
                            //on récupére les donnée de l'autheur
                            try {
                                PlayerStats StatPlayer = PlayerStats.playerStatsMagicBuilder(event.getAuthor().getId(), plugin,true);
                                sendMobMessage(event, StatPlayer);
                                return;
                            } catch (Exception e) {
                                event.getChannel().sendMessage("**:warning: Vous n'êtes pas enregistrer dans la BDD** \n`" + e.getMessage() + "`").queue();

                                return;
                            }
                        }else{
                            //Si un membre est mentioner dans le message
                            if (event.getMessage().getMentionedUsers().size() >= 1) {
                                //on récupérer les donnée de l'utilisateur mentioner
                                try {
                                    //on affiche le message des mobs
                                    PlayerStats StatPlayer = PlayerStats.playerStatsMagicBuilder(event.getMessage().getMentionedUsers().get(0).getId(), plugin,true);
                                    sendMobMessage(event, StatPlayer);
                                    return;
                                } catch (Exception e) {
                                    event.getChannel().sendMessage("**:warning: <@" + event.getMessage().getMentionedUsers().get(0).getId() + "> n'est pas enregistrer dans la BDD** \n`" + e.getMessage() + "`").queue();

                                    return;
                                }
                            }
                            //Sinon on récupére le deuxieme argument en tant que pseudo minecraft.
                            try {
                                PlayerStats StatPlayer = PlayerStats.playerStatsMagicBuilder(args[2],plugin,false);
                                sendMobMessage(event, StatPlayer);
                            } catch (Exception e) {
                                event.getChannel().sendMessage("**:warning: " + args[2] + " n'a pas était trouver dans la BDD** \n`" + e.getMessage() + "`").queue();

                            }
                        }
                        break;
                    case "items":
                        if (args.length == 2) {
                            //on récupére les donnée de l'autheur
                            try {
                                PlayerStats playerStats = PlayerStats.playerStatsMagicBuilder(event.getAuthor().getId(), plugin,true);
                                sendItemMessage(event, playerStats);
                                return;
                            } catch (Exception e) {
                                event.getChannel().sendMessage("**:warning: Vous n'êtes pas enregistrer dans la BDD** \n`" + e.getMessage() + "`").queue();

                                return;
                            }
                        }else{
                            //Si un membre est mentioner dans le message
                            if (event.getMessage().getMentionedUsers().size() >= 1) {
                                //on récupérer les donnée de l'utilisateur mentioner
                                try {
                                    //on affiche le message des mobs
                                    PlayerStats playerStats = PlayerStats.playerStatsMagicBuilder(event.getMessage().getMentionedUsers().get(0).getId(), plugin,true);
                                    sendItemMessage(event, playerStats);
                                    return;
                                } catch (Exception e) {
                                    event.getChannel().sendMessage("**:warning: <@" + event.getMessage().getMentionedUsers().get(0).getId() + "> n'est pas enregistrer dans la BDD** \n`" + e.getMessage() + "`").queue();

                                    return;
                                }
                            }
                            //Sinon on récupére le deuxieme argument en tant que pseudo minecraft.
                            try {
                                PlayerStats playerStats = PlayerStats.playerStatsMagicBuilder(args[2],plugin,false);
                                sendItemMessage(event, playerStats);
                            } catch (Exception e) {
                                event.getChannel().sendMessage("**:warning: " + args[2] + " n'a pas était trouver dans la BDD** \n`" + e.getMessage() + "`").queue();

                            }
                        }
                        break;
                    case "help":
                        sendHelpMessage(event);
                        break;
                    default:
                        //Si un membre est mentioner dans le message
                        if (event.getMessage().getMentionedUsers().size() >= 1) {
                            //on récupérer les donnée de l'utilisateur mentioner
                            try {
                                PlayerStats playerStats = PlayerStats.playerStatsMagicBuilder(event.getMessage().getMentionedUsers().get(0).getId(), plugin,true);
                                sendBaseStatMessage(event, playerStats);
                                return;
                            } catch (Exception e) {
                                event.getChannel().sendMessage("**:warning: <@" + event.getMessage().getMentionedUsers().get(0).getId() + "> n'est pas enregistrer dans la BDD** \n`" + e.getMessage() + "`").queue();
                                return;
                            }
                        }

                        //Sinon on récupére le premier argument en tant que pseudo minecraft.
                        try {
                            PlayerStats playerStats = PlayerStats.playerStatsMagicBuilder(args[1],plugin,false);
                            sendBaseStatMessage(event, playerStats);
                        } catch (Exception e) {
                            event.getChannel().sendMessage("**:warning: " + args[1] + " n'a pas était trouver dans la BDD** \n`" + e.getMessage() + "`").queue();
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
        eb.addField("**"+prefix+"stats**","Affiche vos statistique ou celle du joueur indiquer ou mentioner",true);
        eb.addField("**"+prefix+"stats items**","Affiche vos stats d'item ou celle du joueur indiquer ou mentioner",true);
        eb.addField("**"+prefix+"stats mobs**","Affiche votre tableau de chasse ou celui du joueur indiquer",true);
        event.getChannel().sendMessage(eb.build()).queue();
    }

    private void sendItemMessage(GuildMessageReceivedEvent event, PlayerStats player) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Statistique de "+player.getUsername());

        eb.appendDescription("Voici les item que "+player.getUsername()+" connais bien");

        if (!player.getMinedItems().equals("")) {
            eb.addField("Nombre d'items minée", player.getMinedItems(), true);
        }

        if(eb.getFields().size()==1 ){
            eb.addBlankField(true);
        }

        if (!player.getUsedItems().equals("")) {
            eb.addField("Nombre d'items utilisée", player.getUsedItems(), true);
        }

        if(eb.getFields().size()==1){
            eb.addBlankField(true);
        }

        if (!player.getBrokenItems().equals("")) {
            eb.addField("Nombre d'items cassé", player.getBrokenItems(), true);
        }

        if(eb.getFields().size()==1 || eb.getFields().size()==4){
            eb.addBlankField(true);
        }

        if (!player.getCraftedItems().equals("")){
            eb.addField("Nombre d'items crafter",player.getCraftedItems(),true);
        }

        eb.setThumbnail("https://crafatar.com/avatars/"+player.getPlayerUUID());
        event.getChannel().sendMessage(eb.build()).queue();
    }

    private void sendMobMessage(GuildMessageReceivedEvent event, PlayerStats player) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Statistique de "+player.getUsername());
        eb.appendDescription("Voici le tableau de chasse ou de chassez de "+player.getUsername());
        eb.addField("Nombre de mob tuer",String.valueOf(player.getTotalMobKill()),true);
        eb.addBlankField(true);
        eb.addField("Nombre de mort",String.valueOf(player.getTotalDeath()),true);
        eb.addField("Mob tuer",player.getMobsKilledMessage(),true);
        eb.addBlankField(true);
        eb.addField("Tuer par",player.getMurderMobsMessage(),true);
        eb.setThumbnail("https://crafatar.com/avatars/"+player.getPlayerUUID());
        event.getChannel().sendMessage(eb.build()).queue();
    }




    private void sendBaseStatMessage(GuildMessageReceivedEvent event, PlayerStats player){
        System.out.println(player.getUsername());
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Statistique de "+player.getUsername());
        eb.addField("Score",String.valueOf(player.getTotalScore()),true);
        eb.addField("Temps de jeux",player.getTimePlayedInString(),true);
        eb.addField("Dernière mort",player.getTimeSinceLastDeathInString(),true);
        eb.addField("Nombre de mort",String.valueOf(player.getTotalDeath()),true);
        eb.addField("Nombre de mob tuer",String.valueOf(player.getTotalMobKill()),true);
        eb.addField("Nombres de joueur tuer",String.valueOf(player.getTotalPlayerKill()),true);
        //System.out.println(player.getTotalDistanceTransportation());
        //System.out.println(player.getTotalDistanceTransportationString());
        eb.addField("Distance Parcourue à Pied",player.getTotalDistanceString(),true);
        eb.addField("Distance Parcourue en Transport",player.getTotalDistanceTransportationString(),true);
        eb.addField("Nombre total de saut",String.valueOf(player.getTotalJump()),true);
        eb.setThumbnail("https://crafatar.com/avatars/"+player.getPlayerUUID());
        event.getChannel().sendMessage(eb.build()).queue();
    }
}
