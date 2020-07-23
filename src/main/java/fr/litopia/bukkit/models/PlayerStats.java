package fr.litopia.bukkit.models;

import com.sun.source.doctree.StartElementTree;
import fr.litopia.bukkit.Main;
import fr.litopia.postgres.DBConnection;
import fr.litopia.postgres.Insert;
import fr.litopia.postgres.Select;
import fr.litopia.postgres.Update;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class PlayerStats {
    private int totalScore;
    private Player player;
    private String Username;
    private String minecraftUUID;
    private ArrayList<MaterialData> ItemStats;
    private ArrayList<EntityData> EntityStats;
    private int totalPlayerKill;
    private int totalMobKill;
    private int totalJump;
    private int totalDeath;
    private int timePlayed;
    private int timeSinceLastDeath;
    private int totalAdvancement;
    private float damageTaken;
    private float damageDealt;
    private float damageBlockedByShield;
    private int raidWon;
    private int tradedWithVillager;
    private int animalBred;
    private int fishCaught;
    private int mineStat;
    private int craftStat;
    private int useStat;
    private int killEntity;
    private int entityKilledBy;
    private long totalDistanceInCm;
    private long totalTransportationDistanceInCm;
    private Main plugin;
    private String discordID = "";

    public PlayerStats(Player p, Main plugin) throws Exception {
        this.player = p;
        this.plugin = plugin;
        this.ItemStats = new ArrayList<MaterialData>();

        this.minecraftUUID = this.player.getUniqueId().toString();
        //Inisialisation des variable de compte global des stats d'items
        this.mineStat = 0;
        this.craftStat = 0;
        this.useStat = 0;
        //Ajout des stat de chaque item au joueur
        for (MaterialData md:this.plugin.getEveryMaterial()) {
            md.setPlayer(p);
            this.ItemStats.add(md);
            this.mineStat += md.getMineStat();
            this.craftStat += md.getCraftStat();
            this.useStat += md.getUseStat();
        }

        //inisialisation des variable d'entiter tuer
        this.killEntity = 0;
        this.entityKilledBy = 0;
        this.EntityStats = new ArrayList<EntityData>();
        for (EntityData ett:this.plugin.getEveryEntity()) {
            ett.setPlayer(p);
            this.EntityStats.add(ett);
            this.killEntity += ett.getKillEntity();
            this.entityKilledBy += ett.getEntityKilledBy();
        }

        this.totalMobKill = this.player.getStatistic(Statistic.MOB_KILLS);
        this.totalPlayerKill = this.player.getStatistic(Statistic.PLAYER_KILLS);
        this.totalJump = this.player.getStatistic(Statistic.JUMP);
        this.totalDeath = this.player.getStatistic(Statistic.DEATHS);
        this.timePlayed = this.player.getStatistic(Statistic.PLAY_ONE_MINUTE);
        this.totalAdvancement = this.player.getScoreboard().getObjective("bac_advancements").getScore(this.player).getScore();
        this.damageTaken = this.player.getStatistic(Statistic.DAMAGE_TAKEN);
        this.damageDealt = this.player.getStatistic(Statistic.DAMAGE_DEALT);
        this.damageBlockedByShield = this.player.getStatistic(Statistic.DAMAGE_BLOCKED_BY_SHIELD);
        this.raidWon = this.player.getStatistic(Statistic.RAID_WIN);
        this.tradedWithVillager = this.player.getStatistic(Statistic.TRADED_WITH_VILLAGER);
        this.animalBred = this.player.getStatistic(Statistic.ANIMALS_BRED);
        this.fishCaught = this.player.getStatistic(Statistic.FISH_CAUGHT);
        this.totalScore = 0;

        this.totalDistanceInCm = totalDistanceInCm();
        this.totalTransportationDistanceInCm = totalTransportationDistanceInCm();
        this.Username = this.player.getDisplayName();

        this.timeSinceLastDeath = player.getStatistic(Statistic.TIME_SINCE_DEATH);
        //Bukkit.advancementIterator().forEachRemaining(advancement -> System.out.println(advancement.getCriteria()));
    }

    public PlayerStats(String discordID, Main plugin) throws Exception{
        this.player = null;
        this.plugin = plugin;
        this.discordID = discordID;

        DBConnection db = new DBConnection(this.plugin.config.getString("postgresConnString"));
        Select select = new Select(db.connect());
        ResultSet res = select.getMemberData(discordID);

        this.minecraftUUID = res.getString("minecraftUUID").replaceAll("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})","$1-$2-$3-$4-$5");

        this.mineStat = res.getInt("totalMineStat");
        this.craftStat = res.getInt("totalCraftStat");
        this.useStat = res.getInt("totalUseStat");

        this.killEntity = res.getInt("totalKillEntity");
        this.entityKilledBy = res.getInt("totalEntityKilledBy");

        this.totalMobKill = res.getInt("totalMobKill");
        this.totalPlayerKill = res.getInt("totalPlayerKill");
        this.totalJump =  res.getInt("numberJump");
        this.totalDeath = res.getInt("numberDeath");
        this.timePlayed = res.getInt("playTimeTick");
        this.totalAdvancement = res.getInt("totalAdvancement");
        this.damageTaken = res.getFloat("damageTaken");
        this.damageDealt = res.getFloat("damageDealt");
        this.damageBlockedByShield = res.getFloat("damageBlockedByShield");
        this.raidWon = res.getInt("raidWon");
        this.tradedWithVillager = res.getInt("tradedWithVillager");
        this.animalBred = res.getInt("animalBred");
        this.fishCaught = res.getInt("fishCaught");
        this.totalScore = res.getInt("totalScore");


        res = select.getMemberItemStats(this.discordID,this.minecraftUUID);
        while (res.next()){
            MaterialData materialData = new MaterialData(res.getString("item"));
            materialData.setStat(res.getInt("numberBroken"),res.getInt("numberCraft"),res.getInt("numberUsed"),res.getInt("numberMine"));
            this.ItemStats.add(materialData);
        }


        res = select.getMemberItemStats(this.discordID,this.minecraftUUID);
        while (res.next()){
            EntityData entityData = new EntityData(res.getString("mob"));
            entityData.setStat(res.getInt("killed"),res.getInt("murder"));
            this.EntityStats.add(entityData);
        }

    }

    public ArrayList<MaterialData> getItemStats() {
        return ItemStats;
    }

    public ArrayList<EntityData> getEntityStats() {
        return EntityStats;
    }

    public String getUsername(){return Username;}

    public int getMineStat() {
        return mineStat;
    }

    public int getCraftStat() {
        return craftStat;
    }

    public int getUseStat() {
        return useStat;
    }

    public int getKillEntity() {
        return killEntity;
    }

    public int getEntityKilledBy() {
        return entityKilledBy;
    }

    public int getTimePlayed() {
        return timePlayed;
    }

    public int getTimeSinceLastDeath(){return timeSinceLastDeath;}

    public int getTotalAdvancement() {
        return totalAdvancement;
    }

    public float getDamageTaken() {
        return damageTaken;
    }

    public float getDamageDealt() {
        return damageDealt;
    }

    public float getDamageBlockedByShield() {
        return damageBlockedByShield;
    }

    public int getRaidWon() {
        return raidWon;
    }

    public int getTradedWithVillager() {
        return tradedWithVillager;
    }

    public int getAnimalBred() {
        return animalBred;
    }

    public int getFishCaught() {
        return fishCaught;
    }

    public int getTotalScore() {
        return this.totalScore;
    }

    public int getTotalPlayerKill() {
        return totalPlayerKill;
    }

    public int getTotalMobKill() {
        return totalMobKill;
    }

    public int getTotalJump() {
        return totalJump;
    }

    public int getTotalDeath() {
        return totalDeath;
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
        return this.minecraftUUID;
    }

    public String getTimeSinceLastDeathInString(){
        return getConvertTick(this.timeSinceLastDeath);
    }

    public String getTimePlayedInString(){
        return getConvertTick(this.timePlayed);
    }

    public String getTotalDistanceTransportationString(){
        long totalDistance = totalTransportationDistanceInCm();
        return convertCentimeterToString(totalDistance);
    }

    public float getTotalDistanceTransportationFloat(){
        long totalDistance = totalTransportationDistanceInCm();
        return convertCentimeterToFloat(totalDistance);
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

    private long totalTransportationDistanceInCm(){
        long totalDistance;
        totalDistance = player.getStatistic(Statistic.AVIATE_ONE_CM);
        totalDistance += player.getStatistic(Statistic.BOAT_ONE_CM);
        totalDistance += player.getStatistic(Statistic.HORSE_ONE_CM);
        totalDistance += player.getStatistic(Statistic.MINECART_ONE_CM);
        totalDistance += player.getStatistic(Statistic.PIG_ONE_CM);
        totalDistance += player.getStatistic(Statistic.STRIDER_ONE_CM);
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

        for (MaterialData materialData:this.ItemStats) {
            if (materialData.getUseStat()!=0||materialData.getCraftStat()!=0||materialData.getBrokenStat()!=0||materialData.getMineStat()!=0){
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

        for (EntityData entityData:this.EntityStats) {
            if (entityData.getKillEntity()!=0||entityData.getEntityKilledBy()!=0){
                if(S.checkMobStat(this,entityData)==0){
                    I.insertMobStats(entityData,this);
                }else {
                    U.updateMobStats(entityData,this);
                }
            }
        }
    }

    private void sortByMined(){
        Comparator<MaterialData> compareById = new Comparator<MaterialData>() {
            @Override
            public int compare(MaterialData o1, MaterialData o2) {
                return o1.getMineStat() - o2.getMineStat();
            }
        };
        this.ItemStats.sort(compareById);
        Collections.reverse(this.ItemStats);
    }

    private void sortByBroken(){
        Comparator<MaterialData> compareById = new Comparator<MaterialData>() {
            @Override
            public int compare(MaterialData o1, MaterialData o2) {
                return o1.getBrokenStat() - o2.getBrokenStat();
            }
        };
        this.ItemStats.sort(compareById);
        Collections.reverse(this.ItemStats);
    }

    private void sortByCrafted(){
        Comparator<MaterialData> compareById = new Comparator<MaterialData>() {
            @Override
            public int compare(MaterialData o1, MaterialData o2) {
                return o1.getCraftStat() - o2.getCraftStat();
            }
        };
        this.ItemStats.sort(compareById);
        Collections.reverse(this.ItemStats);
    }

    private void sortByUsed(){
        Comparator<MaterialData> compareById = new Comparator<MaterialData>() {
            @Override
            public int compare(MaterialData o1, MaterialData o2) {
                return o1.getUseStat() - o2.getUseStat();
            }
        };
        this.ItemStats.sort(compareById);
        Collections.reverse(this.ItemStats);
    }

    private void sortByMurderEntity(){
        Comparator<EntityData> compareById = new Comparator<EntityData>() {
            @Override
            public int compare(EntityData o1, EntityData o2) {
                return o1.getKillEntity() - o2.getKillEntity();
            }
        };
        this.EntityStats.sort(compareById);
        Collections.reverse(this.EntityStats);
    }

    private void sortByEntityKilled(){
        Comparator<EntityData> compareById = new Comparator<EntityData>() {
            @Override
            public int compare(EntityData o1, EntityData o2) {
                return o1.getEntityKilledBy() - o2.getEntityKilledBy();
            }
        };
        this.EntityStats.sort(compareById);
        Collections.reverse(this.EntityStats);
    }

    public String getMobsKilledMessage(){
        sortByEntityKilled();
        StringBuilder message = new StringBuilder();
        int maxIndex;
        if (EntityStats.size()>10){
            maxIndex = 10;
        }else{
            maxIndex =EntityStats.size();
        }
        for (int i = 0; i < maxIndex ; i++) {
            if (EntityStats.get(i).getEntityKilledBy()!=0)
                message.append(EntityStats.get(i).getEntityKilledBy()).append(" ").append(EntityStats.get(i).getName()).append("\n");
        }
        return message.toString();
    }

    public String getMurderMobsMessage(){
        sortByMurderEntity();
        StringBuilder message = new StringBuilder();
        int maxIndex;
        if (EntityStats.size()>10){
            maxIndex = 10;
        }else{
            maxIndex =EntityStats.size();
        }
        for (int i = 0; i <maxIndex ; i++) {
            if (EntityStats.get(i).getKillEntity()!=0)
                message.append(EntityStats.get(i).getKillEntity()).append(" ").append(EntityStats.get(i).getName()).append("\n");
        }
        return message.toString();
    }

    public String getCraftedItems(){
        sortByCrafted();
        StringBuilder message = new StringBuilder();
        int maxIndex;
        if (ItemStats.size()>10){
            maxIndex = 10;
        }else{
            maxIndex =ItemStats.size();
        }
        for (int i = 0; i <maxIndex ; i++) {
            if (ItemStats.get(i).getCraftStat()!=0)
                message.append(ItemStats.get(i).getCraftStat()).append(" ").append(ItemStats.get(i).getName()).append("\n");
        }
        return message.toString();
    }

    public String getBrokenItems(){
        sortByBroken();
        StringBuilder message = new StringBuilder();
        int maxIndex;
        if (ItemStats.size()>10){
            maxIndex = 10;
        }else{
            maxIndex =ItemStats.size();
        }
        for (int i = 0; i <maxIndex ; i++) {
            if (ItemStats.get(i).getBrokenStat()!=0)
                message.append(ItemStats.get(i).getBrokenStat()).append(" ").append(ItemStats.get(i).getName()).append("\n");
        }
        return message.toString();
    }

    public String getUsedItems(){
        sortByUsed();
        StringBuilder message = new StringBuilder();
        int maxIndex;
        if (ItemStats.size()>10){
            maxIndex = 10;
        }else{
            maxIndex =ItemStats.size();
        }
        for (int i = 0; i <maxIndex ; i++) {
            if (ItemStats.get(i).getUseStat()!=0)
                message.append(ItemStats.get(i).getUseStat()).append(" ").append(ItemStats.get(i).getName()).append("\n");
        }
        return message.toString();
    }

    public String getMinedItems(){
        sortByMined();
        StringBuilder message = new StringBuilder();
        int maxIndex;
        if (ItemStats.size()>10){
            maxIndex = 10;
        }else{
            maxIndex =ItemStats.size();
        }
        for (int i = 0; i <maxIndex ; i++) {
            if (ItemStats.get(i).getMineStat()!=0)
                message.append(ItemStats.get(i).getMineStat()).append(" ").append(ItemStats.get(i).getName()).append("\n");
        }
        return message.toString();
    }
}
