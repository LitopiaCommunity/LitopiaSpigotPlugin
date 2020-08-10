package fr.litopia.bukkit;


import fr.litopia.bot.commands.*;
import fr.litopia.bot.event.TchatEvent;
import fr.litopia.bukkit.listener.BukkitListener;
import fr.litopia.bukkit.models.EntityData;
import fr.litopia.bukkit.models.MaterialData;
import fr.litopia.tools.postgres.DBConnection;
import fr.litopia.tools.postgres.Insert;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Message;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;

import javax.security.auth.login.LoginException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class Main extends JavaPlugin {
    private JDA jda;
    private TchatEvent tchatEvent;
    public FileConfiguration config = getConfig();
    private ArrayList<MaterialData> EveryMaterial;
    private ArrayList<EntityData> EveryEntityType;


    @Override
    public void onLoad() {

        this.getItem();
        this.getMob();
        /*
        if (token.equalsIgnoreCase("null")){
            System.out.println("&cYou need token in config.yml");
            setEnabled(false);
        */

    }

    @Override
    public void onEnable() {

        config.addDefault("prefix","!");
        config.addDefault("token","");
        config.addDefault("tchatChanelID",0L);
        config.addDefault("postgresConnString","");
        config.addDefault("TchatWebhookURL","https://discordapp.com/api/webhooks/");
        config.options().copyDefaults(true);
        saveConfig();

        if (config.getString("token").equals("")){
            System.out.println("[Litopia Services] ERROR Veuillez indiquer un token");
            return;
        }
        if (config.getLong("tchatChanelID")==(0L)){
            System.out.println("[LitopiaServices] ERROR Veuillez indiquer l'id du salon discord de tchat");
            return;
        }
        if (config.getString("postgresConnString").equals("")){
            System.out.println("[LitopiaServices] ERROR Veuillez indiquer un l'uri de connexion à postgres");
            return;
        }
        if (config.getString("prefix").equals("")){
            System.out.println("[LitopiaServices] ERROR Veuillez indiquer un prefix au bot discord");
            return;
        }
        if (config.getString("TchatWebhookURL").equals("https://discordapp.com/api/webhooks/")){
            System.out.println("[LitopiaServices] ERROR Veuillez compléter l'uri du bot");
            return;
        }
        if (config.getString("TchatWebhookURL").equals("")){
            System.out.println("[LitopiaServices] ERROR Veuillez indiquer l'url du webhook");
            return;
        }

        try {
            this.jda = new JDABuilder(AccountType.BOT).setToken(config.getString("token")).addEventListeners(new TchatEvent(this)).build();
            this.tchatEvent = new TchatEvent(this);
            this.jda.addEventListener(new Stats(this));
            this.jda.addEventListener(new AcceptCandidature(this));
            this.jda.addEventListener(new Leaderboard(this));
            this.jda.addEventListener(new RejectCandidature(this));
            this.jda.addEventListener(new ResetCandidature(this));
            this.jda.addEventListener(new MemberStats(this));
        } catch (LoginException e) {
            e.printStackTrace();
        }

        getServer().getPluginManager().registerEvents(new BukkitListener(this),this);
    }

        @Override
        public void onDisable() {
            this.jda.shutdown();
        }
/*
        @Override
        public void reloadConfig() {
            jda.shutdown();
        }
        */


    public JDA getJda() {
        return jda;
    }

    public void sendMessageToMinecraft(Message message){
        Bukkit.broadcastMessage(ChatColor.DARK_PURPLE+""+ ChatColor.BOLD + message.getAuthor().getName() + ChatColor.GRAY+" : " + ChatColor.WHITE + message.getContentRaw());
    }

    public void addToWhiteListe(String Nickname) throws CommandException, ExecutionException, InterruptedException {
        Bukkit.getScheduler().callSyncMethod( this, () -> Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),"whitelist add "+Nickname)).get();
        Bukkit.reloadWhitelist();
    }

    private void getItem(){
        String dbConn = this.config.getString("postgresConnString");
        DBConnection db = new DBConnection(dbConn);
        Insert i = new Insert(db.connect());
        this.EveryMaterial = new ArrayList<MaterialData>();
        for(Material mat : Material.values()){
            this.EveryMaterial.add(new MaterialData(mat,mat.name()));
            try {
                i.insertItem(mat.name());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void getMob(){
        String dbConn = this.config.getString("postgresConnString");
        DBConnection db = new DBConnection(dbConn);
        Insert i = new Insert(db.connect());
        this.EveryEntityType = new ArrayList<EntityData>();
        for(EntityType ett : EntityType.values()){
            if (ett.isSpawnable())
                this.EveryEntityType.add(new EntityData(ett,ett.name()));
            try {
                i.insertMob(ett.name());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public ArrayList<MaterialData> getEveryMaterial() {
        return EveryMaterial;
    }

    public ArrayList<EntityData> getEveryEntity(){
        return EveryEntityType;
    }
}
