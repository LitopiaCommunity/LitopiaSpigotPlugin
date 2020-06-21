package fr.litopia.bukkit.models;

import fr.litopia.bukkit.Main;
import fr.litopia.postgres.DBConnection;
import fr.litopia.postgres.Insert;
import fr.litopia.postgres.Select;
import fr.litopia.postgres.Update;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

public class PlayerStats {
    private Player player;
    private String Username;
    private ArrayList<MaterialData> itemStatCollection;
    private ArrayList<EntityData> EntityCollection;
    private int TotalPlayerKill;
    private int TotalMobKill;
    private int TotalJump;
    private int TotalDeath;
    private Main plugin;
    private String discordID = "";

    public PlayerStats(Player p, Main plugin) throws Exception {
        this.player = p;
        this.plugin = plugin;
        this.itemStatCollection = new ArrayList<MaterialData>();
        for (MaterialData md:this.plugin.getEveryMaterial()) {
            md.setPlayer(p);
            this.itemStatCollection.add(md);
        }

        this.EntityCollection = new ArrayList<EntityData>();
        for (EntityData ett:this.plugin.getEveryEntity()) {
            ett.setPlayer(p);
            this.EntityCollection.add(ett);
        }

        this.TotalMobKill = this.player.getStatistic(Statistic.MOB_KILLS);
        this.TotalPlayerKill = this.player.getStatistic(Statistic.PLAYER_KILLS);
        this.TotalJump = this.player.getStatistic(Statistic.JUMP);
        this.TotalDeath = this.player.getStatistic(Statistic.DEATHS);
        this.Username = this.player.getDisplayName();
        //Bukkit.advancementIterator().forEachRemaining(advancement -> System.out.println(advancement.getCriteria()));
    }

    public ArrayList<MaterialData> getItemStatCollection() {
        return itemStatCollection;
    }

    public ArrayList<EntityData> getEntityCollection() {
        return EntityCollection;
    }

    public String getUsername(){return Username;}

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

    public String getDiscordID() throws SQLException {
        if (this.discordID=="") {
            String dbConn = this.plugin.config.getString("postgresConnString");
            DBConnection db = new DBConnection(dbConn);
            Select S = new Select(db.connect());
            String discordID = S.getDiscordID(player.getUniqueId().toString());
            this.discordID = discordID;
            return discordID;
        }else {
            return this.discordID;
        }
    }

    public String getPlayerUUID(){
        return player.getUniqueId().toString().replace("-","");
    }

    public String getTimeSinceLastDeathInString(){
        int time = player.getStatistic(Statistic.TIME_SINCE_DEATH);
        return getConvertTick(time);
    }

    public String getTimePlayedInString(){
        int time = player.getStatistic(Statistic.PLAY_ONE_MINUTE);
        return getConvertTick(time);
    }

    public String getTotalDistanceString(){
        long totalDistance = totalDistanceInCm();
        return convertCentimeterToString(totalDistance);
    }

    public float getTotalDistanceFloat(){
        long totalDistance = totalDistanceInCm();
        return convertCentimeterToFloat(totalDistance);
    }

    private long totalDistanceInCm() {
        long totalDistance;
        totalDistance = player.getStatistic(Statistic.WALK_ONE_CM);
        totalDistance += player.getStatistic(Statistic.WALK_UNDER_WATER_ONE_CM);
        totalDistance += player.getStatistic(Statistic.WALK_UNDER_WATER_ONE_CM);
        totalDistance += player.getStatistic(Statistic.SPRINT_ONE_CM);
        totalDistance += player.getStatistic(Statistic.SPRINT_ONE_CM);
        totalDistance += player.getStatistic(Statistic.CLIMB_ONE_CM);
        totalDistance += player.getStatistic(Statistic.FALL_ONE_CM);
        totalDistance += player.getStatistic(Statistic.CROUCH_ONE_CM);
        totalDistance += player.getStatistic(Statistic.SWIM_ONE_CM);
        return totalDistance;
    }

    private String convertCentimeterToString(long distance){
        distance = distance/100;
        if (distance<1000){
            return String.valueOf(distance)+" m";
        }else {
            return String.valueOf((float)distance/1000)+" Km";
        }
    }

    private float convertCentimeterToFloat(long distance){
        distance = distance/100;
        return (float)distance/1000;
    }

    private String getConvertTick(int tick) {
        long d = toUnit(tick, TimeUnit.DAYS);
        long h = toUnit(tick,TimeUnit.HOURS);
        long m = toUnit(tick,TimeUnit.MINUTES);
        long s = tick/20;
        s = s-m*60;
        m = m-h*60;
        h = h-d*24;

        if (d==0 && h==0 && m==0){
            return String.valueOf(s) + "s";
        }
        if (d==0 && h==0){
            return String.valueOf(m) + "m " + String.valueOf(s) + "s";
        }
        if (d==0) {
            return String.valueOf(h) + "h " + String.valueOf(m) + "m " + String.valueOf(s) + "s";
        }
        return String.valueOf(d) + "J " + String.valueOf(h) + "h " + String.valueOf(m) + "m " + String.valueOf(s) + "s";
    }

    public static long toUnit(long ticks, TimeUnit unit) {
        return unit.convert(ticks /20, TimeUnit.SECONDS);
    }


    public void save() throws SQLException {
        String dbConn = this.plugin.config.getString("postgresConnString");
        DBConnection db = new DBConnection(dbConn);
        Update u = new Update(db.connect());
        u.updateMemberStats(this);
        this.saveMaterialStat();
        this.saveEntityStat();
    }

    private void saveMaterialStat() throws SQLException {
        String dbConn = this.plugin.config.getString("postgresConnString");
        DBConnection db = new DBConnection(dbConn);
        Connection conn = db.connect();
        Select S = new Select(conn);
        Update U = new Update(conn);
        Insert I = new Insert(conn);

        for (MaterialData materialData:this.itemStatCollection) {
            if (materialData.getUseStat()!=0||materialData.getCraftStat()!=0||materialData.getBrokenStat()!=0||materialData.getMinestat()!=0){
                if(S.checkItemStat(this,materialData)==0){
                    I.insertItemStats(materialData,this);
                }else {
                    U.updateItemStats(materialData,this);
                }
            }
        }
    }

    private void saveEntityStat() throws SQLException{
        String dbConn = this.plugin.config.getString("postgresConnString");
        DBConnection db = new DBConnection(dbConn);
        Connection conn = db.connect();
        Select S = new Select(conn);
        Update U = new Update(conn);
        Insert I = new Insert(conn);

        for (EntityData entityData:this.EntityCollection) {
            if (entityData.getKillEntity()!=0||entityData.getEntityKilledBy()!=0){
                if(S.checkMobStat(this,entityData)==0){
                    I.insertMobStats(entityData,this);
                }else {
                    U.updateMobStats(entityData,this);
                }
            }
        }
    }




}
