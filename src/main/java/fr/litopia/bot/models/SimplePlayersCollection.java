package fr.litopia.bot.models;

import fr.litopia.bukkit.Main;
import fr.litopia.postgres.DBConnection;
import fr.litopia.postgres.Select;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SimplePlayersCollection {
    ArrayList<SimplePlayerData> globalPlayersData;

    public SimplePlayersCollection(Main plugin) throws Exception {
        this.globalPlayersData = new ArrayList<>();
        DBConnection Conn = new DBConnection(plugin.config.getString("postgresConnString"));
        Select S = new Select(Conn.connect());
        ResultSet players = S.getAllMembers();
        while (players.next()){
            SimplePlayerData playerData = new SimplePlayerData(
                    plugin,
                    players.getString("minecraftuuid"),
                    players.getString("iddiscord"),
                    players.getString("minecraftnickname"),
                    players.getInt("totalplayerkill"),
                    players.getInt("totalmobkill"),
                    players.getInt("numberjump"),
                    players.getInt("numberdeath"),
                    players.getString("playtime"),
                    players.getString("timesincelastdeath"),
                    players.getFloat("totalparcourdistance"));
            this.globalPlayersData.add(playerData);
        }
    }

    public ArrayList<SimplePlayerData> getGlobalPlayersData() {
        return this.globalPlayersData;
    }

    public void sortByPlayerKill(){
        Comparator<SimplePlayerData> compareById = new Comparator<SimplePlayerData>() {
            @Override
            public int compare(SimplePlayerData o1, SimplePlayerData o2) {
                return o1.getTotalPlayerKill() - o2.getTotalPlayerKill();
            }
        };
        this.globalPlayersData.sort(compareById);
        Collections.reverse(this.globalPlayersData);
    }

    public void sortByMobkill(){
        Comparator<SimplePlayerData> compareById = new Comparator<SimplePlayerData>() {
            @Override
            public int compare(SimplePlayerData o1, SimplePlayerData o2) {
                return o1.getTotalMobKill() - o2.getTotalMobKill();
            }
        };
        this.globalPlayersData.sort(compareById);
        Collections.reverse(this.globalPlayersData);
    }

    public void sortByJump(){
        Comparator<SimplePlayerData> compareById = new Comparator<SimplePlayerData>() {
            @Override
            public int compare(SimplePlayerData o1, SimplePlayerData o2) {
                return o1.getTotalJump() - o2.getTotalJump();
            }
        };
        this.globalPlayersData.sort(compareById);
        Collections.reverse(this.globalPlayersData);
    }

    public void sortByNumberDeath(){
        Comparator<SimplePlayerData> compareById = new Comparator<SimplePlayerData>() {
            @Override
            public int compare(SimplePlayerData o1, SimplePlayerData o2) {
                return o1.getTotalDeath() - o2.getTotalDeath();
            }
        };
        this.globalPlayersData.sort(compareById);
        //Collections.reverse(this.globalPlayersData);
    }

    public void sortByParcourDistance(){
        Comparator<SimplePlayerData> compareById = new Comparator<SimplePlayerData>() {
            @Override
            public int compare(SimplePlayerData o1, SimplePlayerData o2) {
                return (int)o1.getTotalParcourDistance() - (int)o2.getTotalParcourDistance();
            }
        };
        this.globalPlayersData.sort(compareById);
        Collections.reverse(this.globalPlayersData);
    }

    public void sortByGlobalRankingScore(){
        Comparator<SimplePlayerData> compareById = new Comparator<SimplePlayerData>() {
            @Override
            public int compare(SimplePlayerData o1, SimplePlayerData o2) {
                return (int) (o1.getRankingScore() - o2.getRankingScore());
            }
        };
        this.globalPlayersData.sort(compareById);
        Collections.reverse(this.globalPlayersData);
    }
}
