package fr.litopia.postgres;

import fr.litopia.bukkit.models.EntityData;
import fr.litopia.bukkit.models.MaterialData;
import fr.litopia.bukkit.models.PlayerStats;

import java.sql.*;

public class Insert {
    private Connection conn;

    public Insert(Connection con){
        this.conn = con;
    }

    public void insertItem(String item) throws SQLException {
        String SQL = "Select count(*) from item where item = (?);";
        PreparedStatement pstmt = this.conn.prepareStatement(SQL);
        pstmt.setString(1,item);
        ResultSet rs = pstmt.executeQuery();
        rs.next();
        if(rs.getInt("count")==0) {
            SQL = "insert into item values('" + item + "');";
            Statement stmt = this.conn.createStatement();
            stmt.executeUpdate(SQL);
        }
    }

    public void insertMob(String mob) throws SQLException {
        String SQL = "Select count(*) from mob where mob = (?);";
        PreparedStatement pstmt = this.conn.prepareStatement(SQL);
        pstmt.setString(1,mob);
        ResultSet rs = pstmt.executeQuery();
        rs.next();
        if(rs.getInt("count")==0) {
            SQL = "insert into mob values('" + mob + "');";
            Statement stmt = this.conn.createStatement();
            stmt.executeUpdate(SQL);
        }
    }

    public void insertItemStats(MaterialData materialData, PlayerStats playerStats) throws SQLException {
        String SQL = "insert into itemstat values((?),(?),(?),(?),(?),(?),(?))";
        PreparedStatement pstmt = this.conn.prepareStatement(SQL);
        pstmt.setString(1,playerStats.getDiscordID());
        pstmt.setString(2,playerStats.getPlayerUUID());
        pstmt.setString(3,materialData.getId());
        pstmt.setInt(4,materialData.getCraftStat());
        pstmt.setInt(5,materialData.getUseStat());
        pstmt.setInt(6,materialData.getMineStat());
        pstmt.setInt(7,materialData.getBrokenStat());
        pstmt.executeUpdate();
    }

    public void insertMobStats(EntityData entityData, PlayerStats playerStats) throws SQLException {
        String SQL = "insert into mobstat values((?),(?),(?),(?),(?))";
        PreparedStatement pstmt = this.conn.prepareStatement(SQL);
        pstmt.setString(1,playerStats.getDiscordID());
        pstmt.setString(2,playerStats.getPlayerUUID());
        pstmt.setString(3,entityData.getId());
        pstmt.setInt(4,entityData.getKillEntity());
        pstmt.setInt(5,entityData.getEntityKilledBy());
        pstmt.executeUpdate();
    }

    public  void insertMemberStat(String uuid, String idDiscord){
        String SQL = "insert into members values" ;
    }
}
