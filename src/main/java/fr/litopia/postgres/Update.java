package fr.litopia.postgres;

import fr.litopia.bukkit.Main;
import fr.litopia.bukkit.models.EntityData;
import fr.litopia.bukkit.models.MaterialData;
import fr.litopia.bukkit.models.PlayerStats;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Update {
    private Connection conn;

    public Update(Connection con){
        this.conn = con;
    }


    public void updateMemberStats(PlayerStats playerStats) throws SQLException {
        String SQL = "update members" +
                " set minecraftnickname = (?)," +
                " playTime = (?)," +
                " timeSinceLastDeath = (?)," +
                " numberDeath = (?)," +
                " numberJump = (?)," +
                " totalMobKill = (?)," +
                " totalPlayerKill = (?)," +
                " totalParcourDistance = (?)" +
                " where idDiscord = (?) and minecraftUUID = (?) and acceptedate is not null" ;
        PreparedStatement pstmt = this.conn.prepareStatement(SQL);
        pstmt.setString(1,playerStats.getUsername());
        pstmt.setString(2,playerStats.getTimePlayedInString());
        pstmt.setString(3,playerStats.getTimeSinceLastDeathInString());
        pstmt.setInt(4,playerStats.getTotalDeath());
        pstmt.setInt(5,playerStats.getTotalJump());
        pstmt.setInt(6,playerStats.getTotalMobKill());
        pstmt.setInt(7,playerStats.getTotalPlayerKill());
        pstmt.setFloat(8,playerStats.getTotalDistanceFloat());
        pstmt.setString(9,playerStats.getDiscordID());
        pstmt.setString(10,playerStats.getPlayerUUID());
        pstmt.executeUpdate();
    }

    public void updateItemStats(MaterialData materialData, PlayerStats playerStats) throws SQLException {
        String SQL = "update itemstat" +
                " set numberCraft = (?)," +
                " numberUsed = (?)," +
                " numberMine = (?)," +
                " numberBroken = (?)" +
                " where idDiscord = (?) and minecraftUUID = (?) and item = (?)";
        PreparedStatement pstmt = this.conn.prepareStatement(SQL);
        pstmt.setInt(1,materialData.getCraftStat());
        pstmt.setInt(2,materialData.getUseStat());
        pstmt.setInt(3,materialData.getMinestat());
        pstmt.setInt(4,materialData.getBrokenStat());
        pstmt.setString(5,playerStats.getDiscordID());
        pstmt.setString(6,playerStats.getPlayerUUID());
        pstmt.setString(7,materialData.getName());
        pstmt.executeUpdate();
    }
    public void updateMobStats(EntityData entityData, PlayerStats playerStats) throws SQLException {
        String SQL = "update mobstat" +
                " set killed = (?)," +
                " murder = (?)" +
                " where idDiscord = (?) and minecraftUUID = (?) and mob = (?)";
        PreparedStatement pstmt = this.conn.prepareStatement(SQL);
        pstmt.setInt(1,entityData.getKillEntity());
        pstmt.setInt(2,entityData.getEntityKilledBy());
        pstmt.setString(3,playerStats.getDiscordID());
        pstmt.setString(4,playerStats.getPlayerUUID());
        pstmt.setString(5,entityData.getName());
        pstmt.executeUpdate();
    }
}
