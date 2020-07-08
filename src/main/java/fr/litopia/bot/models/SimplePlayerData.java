package fr.litopia.bot.models;

import fr.litopia.bukkit.Main;
import fr.litopia.bukkit.models.PlayerStats;
import fr.litopia.postgres.DBConnection;
import fr.litopia.postgres.Select;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

public class SimplePlayerData {
    private final String dbConn;
    private Player player;
    private Main plugin;
    private UUID minecraftUUID;
    private String discordID = "";
    private String minecraftUsername;
    private int TotalPlayerKill;
    private int TotalMobKill;
    private int TotalJump;
    private int TotalDeath;
    private String TimePlayed;
    private String TimeSinceLastDeath;
    private float TotalParcourDistance;
    private Connection con;

    public SimplePlayerData(Main plugin,
                            String minecraftUUID,
                            String DiscordID,
                            String minecraftUsername,
                            int totalplayerkill,
                            int TotalMobKill,
                            int TotalJump,
                            int TotalDeath,
                            String TimePlayed,
                            String TimeSinceLastDeath,
                            float TotalParcourDistance) throws Exception {
        this.discordID = DiscordID;
        this.minecraftUUID = UUID.fromString(minecraftUUID.replaceAll("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})","$1-$2-$3-$4-$5"));
        this.plugin = plugin;
        this.dbConn = this.plugin.config.getString("postgresConnString");
        this.player = Bukkit.getPlayer(this.minecraftUUID);

        if (player == null){
            this.minecraftUsername = minecraftUsername;
            this.TotalPlayerKill = totalplayerkill;
            this.TotalMobKill =TotalMobKill;
            this.TotalJump = TotalJump;
            this.TotalDeath = TotalDeath;
            this.TimePlayed = TimePlayed;
            this.TimeSinceLastDeath = TimeSinceLastDeath;
            this.TotalParcourDistance = TotalParcourDistance;
        }else{
            setDataFromPlayer();
        }
    }

    private void setDataFromPlayer() throws Exception {
        PlayerStats PS = new PlayerStats(this.player,this.plugin);
        this.minecraftUsername = PS.getUsername();
        this.TotalPlayerKill = PS.getTotalPlayerKill();
        this.TotalMobKill = PS.getTotalMobKill();
        this.TotalJump = PS.getTotalJump();
        this.TotalDeath = PS.getTotalDeath();
        this.TimePlayed = PS.getTimePlayedInString();
        this.TimeSinceLastDeath = PS.getTimeSinceLastDeathInString();
        this.TotalParcourDistance = PS.getTotalDistanceFloat();
    }

    public String getMinecraftUsername() {
        return minecraftUsername;
    }

    public int getTotalPlayerKill() {
        return TotalPlayerKill;
    }

    public int getTotalMobKill() {
        return TotalMobKill;
    }

    public int getTotalJump() {
        return TotalJump;
    }

    public int getTotalDeath() {
        return TotalDeath;
    }

    public String getTimePlayed() {
        return TimePlayed;
    }

    public String getTimeSinceLastDeath() {
        return TimeSinceLastDeath;
    }

    public float getTotalParcourDistance() {
        return TotalParcourDistance;
    }

    public int getRankingScore(){
        return (int) (((this.TotalMobKill*10) + (TotalJump*2) - (this.TotalDeath*20) - (this.TotalPlayerKill*100) + (this.TotalParcourDistance*0.16))/100);
    }
}
