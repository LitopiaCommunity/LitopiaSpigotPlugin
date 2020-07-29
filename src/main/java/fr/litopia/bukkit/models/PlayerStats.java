package fr.litopia.bukkit.models;

import fr.litopia.bukkit.Main;
import fr.litopia.postgres.DBConnection;
import fr.litopia.postgres.Insert;
import fr.litopia.postgres.Select;
import fr.litopia.postgres.Update;
import org.bukkit.Bukkit;
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
    private long totalDistance;
    private long totalTransportationDistance;
    private Main plugin;
    private String discordID = "";

    public PlayerStats(Player p, Main plugin) throws Exception {
        this.player = p;
        this.plugin = plugin;


        this.minecraftUUID = this.player.getUniqueId().toString();
        //Inisialisation des variable de compte global des stats d'items
        this.mineStat = 0;
        this.craftStat = 0;
        this.useStat = 0;
        this.ItemStats = new ArrayList<MaterialData>();
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
        this.timeSinceLastDeath = this.player.getStatistic(Statistic.TIME_SINCE_DEATH);
        this.totalAdvancement = this.player.getScoreboard().getObjective("bac_advancements").getScore(this.player).getScore();
        this.damageTaken = this.player.getStatistic(Statistic.DAMAGE_TAKEN);
        this.damageDealt = this.player.getStatistic(Statistic.DAMAGE_DEALT);
        this.damageBlockedByShield = this.player.getStatistic(Statistic.DAMAGE_BLOCKED_BY_SHIELD);
        this.raidWon = this.player.getStatistic(Statistic.RAID_WIN);
        this.tradedWithVillager = this.player.getStatistic(Statistic.TRADED_WITH_VILLAGER);
        this.animalBred = this.player.getStatistic(Statistic.ANIMALS_BRED);
        this.fishCaught = this.player.getStatistic(Statistic.FISH_CAUGHT);

        this.totalDistance = convertCentimeterToMeter(totalDistanceInCm());
        this.totalTransportationDistance = convertCentimeterToMeter(totalTransportationDistanceInCm());
        this.Username = this.player.getDisplayName();

        this.totalScore = setTotalScore();
    }

    public static PlayerStats playerStatsFactory(String input, Main plugin, boolean fromDisocrd) throws Exception {
        DBConnection db = new DBConnection(plugin.config.getString("postgresConnString"));
        Select select = new Select(db.connect());
        Player pl = null;
        if (fromDisocrd){
            pl = Bukkit.getPlayer(UUID.fromString(select.getMinecraftUUID(input)));
        }else{
            pl = Bukkit.getPlayer(UUID.fromString(select.getMinecraftUUIDFromMinecraftNickname(input)));
            input = select.getDiscordIDFromMCNickname(input);
        }
        if (pl == null){
            return new PlayerStats(input,plugin);
        }else{
            return new PlayerStats(pl,plugin);
        }
    }

    public PlayerStats(String discordID, Main plugin) throws Exception{
        this.player = null;
        this.plugin = plugin;
        this.discordID = discordID;

        DBConnection db = new DBConnection(this.plugin.config.getString("postgresConnString"));
        Select select = new Select(db.connect());
        ResultSet res = select.getMemberData(discordID);
        this.minecraftUUID = res.getString("minecraftUUID");

        this.Username = res.getString("minecraftNickname");

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
        this.timeSinceLastDeath = res.getInt("timesincelastdeathtick");
        this.totalAdvancement = res.getInt("totalAdvancement");
        this.damageTaken = res.getFloat("damageTaken");
        this.damageDealt = res.getFloat("damageDealt");
        this.damageBlockedByShield = res.getFloat("damageBlockedByShield");
        this.raidWon = res.getInt("raidWon");
        this.tradedWithVillager = res.getInt("tradedWithVillager");
        this.animalBred = res.getInt("animalBred");
        this.fishCaught = res.getInt("fishCaught");
        this.totalScore = res.getInt("totalScore");
        this.totalDistance = (long)res.getFloat("totalParcourDistance");
        this.totalTransportationDistance = (long)res.getFloat("totalparcourdistancetransportation");

        intMatStat(select.getMemberItemStats(this.discordID,this.minecraftUUID));
        intMobStat(select.getMemberMobStats(this.discordID,this.minecraftUUID));
    }

    private void intMatStat(ResultSet allMatStats) throws SQLException {
        this.ItemStats = new ArrayList<MaterialData>();
        while (allMatStats.next()){
            MaterialData materialData = new MaterialData(allMatStats.getString("item"));
            materialData.setStat(
                    allMatStats.getInt("numberBroken"),
                    allMatStats.getInt("numberCraft"),
                    allMatStats.getInt("numberUsed"),
                    allMatStats.getInt("numberMine")
            );
            this.ItemStats.add(materialData);
        }
    }

    private void intMobStat(ResultSet allEntityStats) throws SQLException {
        this.EntityStats = new ArrayList<EntityData>();
        while (allEntityStats.next()){
            EntityData entityData = new EntityData(allEntityStats.getString("mob"));
            entityData.setStat(
                    allEntityStats.getInt("killed"),
                    allEntityStats.getInt("murder")
            );
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

    private int setTotalScore() {
        double
                mobKillScore,
                jumpScore,
                playerKilledScore,
                timePlayedScore,
                distanceScore,
                distanceTransortationScore,
                minedScore,
                craftScore,
                useScore,
                damageTakenScore,
                damageDealtScore,
                damageBlockedByShieldScore,
                killEntityScore,
                raidWonScore,
                tradedWithVillagersScore,
                animalBredScore,
                fishCaughtScore,
                sumScore;
        int deathScore,advancementScore;

        mobKillScore = Math.sqrt(35000)/(175*Math.sqrt(this.totalMobKill));
        jumpScore = Math.sqrt(this.totalJump)*(Math.sqrt(100000)/500);
        deathScore = 0;
        for (int i = 0; i<this.totalDeath;i++){
            deathScore -= Math.sqrt(i);
        }
        playerKilledScore = -((Math.sqrt(this.totalPlayerKill)*20)/Math.sqrt(5));
        timePlayedScore = Math.sqrt((double)this.timePlayed/20/60/60)*(Math.sqrt(240)/1.2);
        advancementScore = this.totalAdvancement;
        distanceScore = Math.sqrt(this.totalDistance)*(Math.sqrt(500000)/2500);
        distanceTransortationScore = Math.sqrt(this.totalTransportationDistance)*(Math.sqrt(750000)/3750);
        minedScore = Math.sqrt(this.mineStat)*(Math.sqrt(200000)/1000);
        craftScore = Math.sqrt(this.craftStat)*(Math.sqrt(50000)/250);
        useScore = Math.sqrt(this.useStat)*(Math.sqrt(200000)/1000);
        damageTakenScore = Math.sqrt(this.damageTaken)*(Math.sqrt(5000)/50);
        damageDealtScore = Math.sqrt(this.damageDealt)*(Math.sqrt(100000)/1000);
        damageBlockedByShieldScore = Math.sqrt(this.damageBlockedByShield)*(Math.sqrt(2000)/(40/3));
        killEntityScore = Math.sqrt(this.killEntity)*(Math.sqrt(20000)/100);
        raidWonScore = (Math.sqrt(this.raidWon)/(Math.log(this.raidWon+Math.exp(1)-1)+Math.sqrt(this.raidWon))*200);
        tradedWithVillagersScore = 50*((Math.sqrt(this.tradedWithVillager)/(Math.log(this.tradedWithVillager+Math.exp(1)-1)+Math.sqrt(this.tradedWithVillager/500))));
        animalBredScore = Math.sqrt(this.animalBred)*(Math.sqrt(500)/(10/3));
        fishCaughtScore = 25*((Math.sqrt(10*this.fishCaught)/(Math.log(this.fishCaught+Math.exp(1)+500)+Math.sqrt(1/(this.fishCaught+1)))));
        sumScore = (
                mobKillScore+
                jumpScore+
                deathScore+
                playerKilledScore+
                timePlayedScore+
                advancementScore+
                distanceScore+
                distanceTransortationScore+
                minedScore+
                craftScore+
                useScore+
                damageTakenScore+
                damageDealtScore+
                damageBlockedByShieldScore+
                killEntityScore+
                raidWonScore+
                tradedWithVillagersScore+
                animalBredScore+
                fishCaughtScore);
        return (int) ((sumScore*(1+this.timeSinceLastDeath/20/60/60/24)));
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
        return this.minecraftUUID.replaceAll("-","");
    }

    public String getTimeSinceLastDeathInString(){
        return getConvertTick(this.timeSinceLastDeath);
    }

    public String getTimePlayedInString(){
        return getConvertTick(this.timePlayed);
    }

    public String getTotalDistanceTransportationString(){
        return convertMeterToString(this.totalTransportationDistance);
    }

    public String getTotalDistanceString(){
        return convertMeterToString(this.totalDistance);
    }

    public long getTotalDistance(){
        return totalDistance;
    }

    public long getTotalDistanceTransportation(){
        return totalTransportationDistance;
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

    public static String convertMeterToString(long distance){
        if (distance<1000){
            return String.valueOf(distance)+" m";
        }else {
            return String.valueOf((float)distance/1000)+" Km";
        }
    }

    private long convertCentimeterToMeter(long distance){
        return distance/100;
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
