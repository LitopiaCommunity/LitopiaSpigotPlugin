package fr.litopia.bot.models;

import fr.litopia.bukkit.Main;
import fr.litopia.bukkit.models.PlayerStats;
import fr.litopia.postgres.DBConnection;
import fr.litopia.postgres.Select;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class LeaderboardData {
    Main pl;

    public static String getlbScore(Main plugin) throws Exception {
        DBConnection db = new DBConnection((String) plugin.getConfig().get("postgresConnString"));
        Select S = new Select(db.connect());
        ResultSet rs = S.getLeaderBoardScore();
        ArrayList<String> playerList = new ArrayList<>();
        ArrayList<String> scoreList = new ArrayList<>();
        while (rs.next()){
            playerList.add(rs.getString("minecraftnickname"));
            Player pl = Bukkit.getPlayer(rs.getString("minecraftnickname"));
            if (pl == null) {
                scoreList.add(String.valueOf(rs.getInt("totalscore")));
            }else {
                PlayerStats playerStats = new PlayerStats(pl,plugin);
                scoreList.add(String.valueOf(playerStats.getTotalScore()));
            }
        }
        return lbComposer(playerList,scoreList,"Points");
    }

    public static String getlbDeath(Main plugin)throws Exception{
        DBConnection db = new DBConnection((String) plugin.getConfig().get("postgresConnString"));
        Select S = new Select(db.connect());
        ResultSet rs = S.getLeaderBoardDeath();
        ArrayList<String> playerList = new ArrayList<>();
        ArrayList<String> deathScore = new ArrayList<>();
        while (rs.next()){
            playerList.add(rs.getString("minecraftnickname"));
            Player pl = Bukkit.getPlayer(rs.getString("minecraftnickname"));
            if (pl == null) {
                deathScore.add(String.valueOf(rs.getInt("numberdeath")));
            }else {
                PlayerStats playerStats = new PlayerStats(pl,plugin);
                deathScore.add(String.valueOf(playerStats.getTotalDeath()));
            }
        }
        return lbComposer(playerList,deathScore,"Mort");
    }

    public static String getlbDeathReverse(Main plugin)throws Exception{
        DBConnection db = new DBConnection((String) plugin.getConfig().get("postgresConnString"));
        Select S = new Select(db.connect());
        ResultSet rs = S.getLeaderBoardDeathReverse();
        ArrayList<String> playerList = new ArrayList<>();
        ArrayList<String> deathScore = new ArrayList<>();
        while (rs.next()){
            playerList.add(rs.getString("minecraftnickname"));
            Player pl = Bukkit.getPlayer(rs.getString("minecraftnickname"));
            if (pl == null) {
                deathScore.add(String.valueOf(rs.getInt("numberdeath")));
            }else {
                PlayerStats playerStats = new PlayerStats(pl,plugin);
                deathScore.add(String.valueOf(playerStats.getTotalDeath()));
            }
        }
        return lbComposer(playerList,deathScore,"Mort");
    }

    public static String getlbTime(Main plugin)throws Exception{
        DBConnection db = new DBConnection((String) plugin.getConfig().get("postgresConnString"));
        Select S = new Select(db.connect());
        ResultSet rs = S.getLeaderBoardTime();
        ArrayList<String> playerList = new ArrayList<>();
        ArrayList<String> timeList = new ArrayList<>();
        while (rs.next()){
            playerList.add(rs.getString("minecraftnickname"));
            Player pl = Bukkit.getPlayer(rs.getString("minecraftnickname"));
            if (pl == null) {
                timeList.add(rs.getString("playtime"));
            }else {
                PlayerStats playerStats = new PlayerStats(pl,plugin);
                timeList.add(playerStats.getTimePlayedInString());
            }
        }
        return lbComposer(playerList,timeList,"");
    }

    public static String getlbMobsKill(Main plugin) throws Exception {
        DBConnection db = new DBConnection((String) plugin.getConfig().get("postgresConnString"));
        Select S = new Select(db.connect());
        ResultSet rs = S.getLeaderBoardMobsKill();
        ArrayList<String> playerList = new ArrayList<>();
        ArrayList<String> mobList = new ArrayList<>();
        while (rs.next()){
            playerList.add(rs.getString("minecraftnickname"));
            Player pl = Bukkit.getPlayer(rs.getString("minecraftnickname"));
            if (pl == null) {
                mobList.add(String.valueOf(rs.getInt("totalkillentity")));
            }else {
                PlayerStats playerStats = new PlayerStats(pl,plugin);
                mobList.add(String.valueOf(playerStats.getTotalMobKill()));
            }
        }
        return lbComposer(playerList,mobList,"mobs tuer");
    }

    public static String getlbWalk(Main plugin) throws Exception {
        DBConnection db = new DBConnection((String) plugin.getConfig().get("postgresConnString"));
        Select S = new Select(db.connect());
        ResultSet rs = S.getLeaderBoardWalkDistance();
        ArrayList<String> playerList = new ArrayList<>();
        ArrayList<String> walkList = new ArrayList<>();
        while (rs.next()){
            playerList.add(rs.getString("minecraftnickname"));
            Player pl = Bukkit.getPlayer(rs.getString("minecraftnickname"));
            if (pl == null) {
                walkList.add(PlayerStats.convertMeterToString(rs.getInt("totalparcourdistance")));
            }else {
                PlayerStats playerStats = new PlayerStats(pl,plugin);
                walkList.add(String.valueOf(playerStats.getTotalDistanceString()));
            }
        }
        return lbComposer(playerList,walkList,"a pied");
    }

    public static String getlbTransportation(Main plugin) throws Exception{
        DBConnection db = new DBConnection((String) plugin.getConfig().get("postgresConnString"));
        Select S = new Select(db.connect());
        ResultSet rs = S.getLeaderBoardTransportDistance();
        ArrayList<String> playerList = new ArrayList<>();
        ArrayList<String> walkList = new ArrayList<>();
        while (rs.next()){
            playerList.add(rs.getString("minecraftnickname"));
            Player pl = Bukkit.getPlayer(rs.getString("minecraftnickname"));
            if (pl == null) {
                walkList.add(PlayerStats.convertMeterToString(rs.getInt("totalparcourdistancetransportation")));
            }else {
                PlayerStats playerStats = new PlayerStats(pl,plugin);
                walkList.add(playerStats.getTotalDistanceTransportationString());
            }
        }
        return lbComposer(playerList,walkList,"en transport");
    }

    public static String getlbPlayerKills(Main plugin) throws Exception {
        DBConnection db = new DBConnection((String) plugin.getConfig().get("postgresConnString"));
        Select S = new Select(db.connect());
        ResultSet rs = S.getLeaderBoardPlayersKill();
        ArrayList<String> playerList = new ArrayList<>();
        ArrayList<String> playerKillList = new ArrayList<>();
        while (rs.next()){
            playerList.add(rs.getString("minecraftnickname"));
            Player pl = Bukkit.getPlayer(rs.getString("minecraftnickname"));
            if (pl == null) {
                playerKillList.add(String.valueOf(rs.getInt("totalplayerkill")));
            }else {
                PlayerStats playerStats = new PlayerStats(pl,plugin);
                playerKillList.add(String.valueOf(playerStats.getTotalPlayerKill()));
            }
        }
        return lbComposer(playerList,playerKillList,"joueurs tuer");
    }

    public static String getlbJump(Main plugin) throws Exception {
        DBConnection db = new DBConnection((String) plugin.getConfig().get("postgresConnString"));
        Select S = new Select(db.connect());
        ResultSet rs = S.getLeaderBoardJumps();
        ArrayList<String> playerList = new ArrayList<>();
        ArrayList<String> playerKillList = new ArrayList<>();
        while (rs.next()){
            playerList.add(rs.getString("minecraftnickname"));
            Player pl = Bukkit.getPlayer(rs.getString("minecraftnickname"));
            if (pl == null) {
                playerKillList.add(String.valueOf(rs.getInt("numberjump")));
            }else {
                PlayerStats playerStats = new PlayerStats(pl,plugin);
                playerKillList.add(String.valueOf(playerStats.getTotalJump()));
            }
        }
        return lbComposer(playerList,playerKillList,"sauts");
    }

    public static String getlbAdvancement(Main plugin) throws Exception {
        DBConnection db = new DBConnection((String) plugin.getConfig().get("postgresConnString"));
        Select S = new Select(db.connect());
        ResultSet rs = S.getLeaderBoardAdvancements();
        ArrayList<String> playerList = new ArrayList<>();
        ArrayList<String> advancementList = new ArrayList<>();
        while (rs.next()){
            playerList.add(rs.getString("minecraftnickname"));
            Player pl = Bukkit.getPlayer(rs.getString("minecraftnickname"));
            if (pl == null) {
                advancementList.add(String.valueOf(rs.getInt("totaladvancement")));
            }else {
                PlayerStats playerStats = new PlayerStats(pl,plugin);
                advancementList.add(String.valueOf(playerStats.getTotalAdvancement()));
            }
        }
        return lbComposer(playerList,advancementList,"advancementq");
    }

    private static String lbComposer(ArrayList<String> playerList, ArrayList<String> ScoreList, String keyname){
        StringBuilder s = new StringBuilder("```\n");
        for (int i = 0; i<playerList.size();i++){
            s.append(i+1).append(" - ").append(playerList.get(i)).append(" ").append(ScoreList.get(i)).append(" ").append(keyname).append("\n");
        }
        s.append("```");
        return s.toString();
    }


}
