package fr.litopia.bot.models;

import fr.litopia.bukkit.Main;
import fr.litopia.bukkit.models.EntityData;
import fr.litopia.bukkit.models.MaterialData;
import fr.litopia.bukkit.models.PlayerStats;
import fr.litopia.postgres.DBConnection;
import fr.litopia.postgres.Select;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.UUID;

public class GlobalPlayerData {
    private final String dbConn;
    private final Player player;
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
    private ArrayList<GlobalEntityData> EntityStats;
    private ArrayList<GlobalItemData> ItemStats;
    private Connection con;

    public GlobalPlayerData(Main plugin, String minecraftUUID, String DiscordID) throws Exception {
        this.discordID = DiscordID;
        this.minecraftUUID = UUID.fromString(minecraftUUID.replaceAll("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})","$1-$2-$3-$4-$5"));
        this.plugin = plugin;
        this.dbConn = this.plugin.config.getString("postgresConnString");
        this.player = Bukkit.getPlayer(this.minecraftUUID);

        this.EntityStats = new ArrayList<GlobalEntityData>();
        this.ItemStats = new ArrayList<GlobalItemData>();
        if (player == null){
            Bukkit.getOfflinePlayer(this.minecraftUUID);
            setDataFromPlayer();
        }else{
            setDataFromPlayer();
        }
    }

    public GlobalPlayerData(Main plugin, String MinecraftUsername) throws Exception {
        this.plugin = plugin;
        this.dbConn = this.plugin.config.getString("postgresConnString");
        DBConnection db = new DBConnection(dbConn);
        Select S = new Select(db.connect());
        this.discordID = S.getDiscordIDFromMCNickname(MinecraftUsername);
        this.minecraftUUID = UUID.fromString(S.getMinecraftUUIDFromMinecraftNickname(MinecraftUsername));
        this.player = Bukkit.getPlayer(this.minecraftUUID);

        this.EntityStats = new ArrayList<GlobalEntityData>();
        this.ItemStats = new ArrayList<GlobalItemData>();

        if (player == null) {
            Bukkit.getOfflinePlayer(this.minecraftUUID);
        }
        setDataFromPlayer();
    }

    public GlobalPlayerData(String discordID,Main plugin) throws Exception {
        this.discordID = discordID;
        this.plugin = plugin;
        this.dbConn = this.plugin.config.getString("postgresConnString");
        DBConnection db = new DBConnection(dbConn);
        Select S = new Select(db.connect());
        System.out.println(this.discordID);
        this.minecraftUUID = UUID.fromString(S.getMinecraftUUID(this.discordID));
        System.out.println(this.minecraftUUID);
        this.player = Bukkit.getPlayer(this.minecraftUUID);

        this.EntityStats = new ArrayList<GlobalEntityData>();
        this.ItemStats = new ArrayList<GlobalItemData>();

        if (this.player==null){
            this.minecraftUsername = S.getMinecraftNickname(this.discordID,this.minecraftUUID.toString());
            setDataFromDataBase();
        }else {
            setDataFromPlayer();
        }
    }

    private void setDataFromDataBase() throws SQLException {
        DBConnection db = new DBConnection(dbConn);
        Select S = new Select(db.connect());
        ResultSet rs = S.getMemberData(this.discordID,this.minecraftUUID.toString());
        this.minecraftUsername = rs.getString("minecraftnickname");
        this.TotalPlayerKill = rs.getInt("totalplayerkill");
        this.TotalMobKill =rs.getInt("totalmobkill");
        this.TotalJump = rs.getInt("numberjump");
        this.TotalDeath = rs.getInt("numberdeath");
        this.TimePlayed = rs.getString("playtime");
        this.TimeSinceLastDeath = rs.getString("timesincelastdeath");
        this.TotalParcourDistance = rs.getFloat("totalparcourdistance");
        generateMobStats(S.getMemberMobStats(this.discordID,this.minecraftUUID.toString()));
        generateItemStats(S.getMemberItemStats(this.discordID,this.minecraftUUID.toString()));
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
        this.TotalParcourDistance = PS.getTotalDistance();
        generateMobStats(PS);
        generateItemStats(PS);
    }

    private void generateItemStats(PlayerStats PS){
        for (MaterialData materialData: PS.getItemStats()) {
            this.ItemStats.add(new GlobalItemData(materialData.getName(),materialData.getMineStat(),materialData.getBrokenStat(),materialData.getCraftStat(),materialData.getUseStat()));
        }
    }

    private void generateItemStats(ResultSet RS) throws SQLException {
        while (RS.next()) {
            this.ItemStats.add(new GlobalItemData(
                    RS.getString("item"),
                    RS.getInt("numbermine"),
                    RS.getInt("numberbroken"),
                    RS.getInt("numbercraft"),
                    RS.getInt("numberused")
            ));
        }
    }

    private void generateMobStats(PlayerStats PS){
        for (EntityData entityData:PS.getEntityStats()) {
            this.EntityStats.add(new GlobalEntityData(entityData.getName(),entityData.getKillEntity(),entityData.getEntityKilledBy()));
        }
    }

    private void generateMobStats(ResultSet RS) throws SQLException {
        while (RS.next()) {
            this.EntityStats.add(new GlobalEntityData(
                    RS.getString("mob"),
                    RS.getInt("killed"),
                    RS.getInt("murder")
            ));
        }
    }

    private void sortByMined(){
        Comparator<GlobalItemData> compareById = new Comparator<GlobalItemData>() {
            @Override
            public int compare(GlobalItemData o1, GlobalItemData o2) {
                return o1.getMineStat() - o2.getMineStat();
            }
        };
        this.ItemStats.sort(compareById);
        Collections.reverse(this.ItemStats);
    }

    private void sortByBroken(){
        Comparator<GlobalItemData> compareById = new Comparator<GlobalItemData>() {
            @Override
            public int compare(GlobalItemData o1, GlobalItemData o2) {
                return o1.getBrokenStat() - o2.getBrokenStat();
            }
        };
        this.ItemStats.sort(compareById);
        Collections.reverse(this.ItemStats);
    }

    private void sortByCrafted(){
        Comparator<GlobalItemData> compareById = new Comparator<GlobalItemData>() {
            @Override
            public int compare(GlobalItemData o1, GlobalItemData o2) {
                return o1.getCraftStat() - o2.getCraftStat();
            }
        };
        this.ItemStats.sort(compareById);
        Collections.reverse(this.ItemStats);
    }

    private void sortByUsed(){
        Comparator<GlobalItemData> compareById = new Comparator<GlobalItemData>() {
            @Override
            public int compare(GlobalItemData o1, GlobalItemData o2) {
                return o1.getUseStat() - o2.getUseStat();
            }
        };
        this.ItemStats.sort(compareById);
        Collections.reverse(this.ItemStats);
    }

    private void sortByMurderEntity(){
        Comparator<GlobalEntityData> compareById = new Comparator<GlobalEntityData>() {
            @Override
            public int compare(GlobalEntityData o1, GlobalEntityData o2) {
                return o1.getMurder() - o2.getMurder();
            }
        };
        this.EntityStats.sort(compareById);
        Collections.reverse(this.EntityStats);
    }

    private void sortByEntityKilled(){
        Comparator<GlobalEntityData> compareById = new Comparator<GlobalEntityData>() {
            @Override
            public int compare(GlobalEntityData o1, GlobalEntityData o2) {
                return o1.getKilled() - o2.getKilled();
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
            if (EntityStats.get(i).getKilled()!=0)
            message.append(EntityStats.get(i).getKilled()).append(" ").append(EntityStats.get(i).getName()).append("\n");
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
            if (EntityStats.get(i).getMurder()!=0)
                message.append(EntityStats.get(i).getMurder()).append(" ").append(EntityStats.get(i).getName()).append("\n");
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

    public UUID getMinecraftUUID() {
        return minecraftUUID;
    }

    public String getDiscordID() {
        return discordID;
    }

    public String getMinecraftUsername() {
        return minecraftUsername;
    }

    public ArrayList<GlobalEntityData> getEntityStats() {
        return EntityStats;
    }

    public ArrayList<GlobalItemData> getItemStats() {
        return ItemStats;
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

    public String getTotalParcourDistance() {
        if (this.TotalParcourDistance<1){
            this.TotalParcourDistance = this.TotalParcourDistance*10;
            return String.valueOf(this.TotalParcourDistance)+"m";
        }
        return String.valueOf(this.TotalParcourDistance)+"Km";
    }

}
