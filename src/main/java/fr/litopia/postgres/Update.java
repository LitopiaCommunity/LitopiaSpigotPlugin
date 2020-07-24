package fr.litopia.postgres;

import fr.litopia.bukkit.models.EntityData;
import fr.litopia.bukkit.models.MaterialData;
import fr.litopia.bukkit.models.PlayerStats;

import java.sql.*;

public class Update {
    private Connection conn;

    public Update(Connection con){
        this.conn = con;
    }

    public void acceptMembers(String discordID) throws SQLException {
        String SQL = "update members" +
                " set acceptedate = (?)," +
                " roleName = (?)" +
                " where idDiscord = (?)";

        PreparedStatement pstmt = this.conn.prepareStatement(SQL);
        pstmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
        pstmt.setString(2,"Litopien");
        pstmt.setString(3,discordID);
        pstmt.executeUpdate();
    }

    public void rejectMembers(String discordID) throws SQLException {
        String SQL = "update members" +
                " set roleName = (?)" +
                " where idDiscord = (?)";

        PreparedStatement pstmt = this.conn.prepareStatement(SQL);
        pstmt.setString(1,"Refuser");
        pstmt.setString(2,discordID);
        pstmt.executeUpdate();
    }

    public void updateMemberStats(PlayerStats playerStats) throws SQLException {
        String SQL = "update members" +
                " set minecraftnickname = (?)," +
                " playTime = (?)," +
                " timeSinceLastDeath = (?)," +
                " playTimeTick = (?)," +
                " timeSinceLastDeathTick = (?)," +
                " numberDeath = (?)," +
                " numberJump = (?)," +
                " totalMobKill = (?)," +
                " totalPlayerKill = (?)," +
                " totalParcourDistance = (?)," +
                " totalParcourDistanceTransportation = (?)," +
                " totalAdvancement = (?)," +
                " damageTaken = (?)," +
                " damageDealt = (?)," +
                " damageBlockedByShield = (?)," +
                " raidWon = (?)," +
                " tradedWithVillager = (?)," +
                " animalBred = (?)," +
                " fishCaught = (?)," +
                " totalMineStat = (?)," +
                " totalCraftStat = (?)," +
                " totalUseStat = (?)," +
                " totalKillEntity = (?)," +
                " totalEntityKilledBy = (?)," +
                " totalScore = (?)," +
                " lastUpdate = now()" +
                " where idDiscord = (?) and minecraftUUID = (?) and acceptedate is not null" ;
        System.out.println(playerStats.getDiscordID());
        System.out.println(playerStats.getPlayerUUID());
        PreparedStatement pstmt = this.conn.prepareStatement(SQL);
        pstmt.setString(1,playerStats.getUsername());
        pstmt.setString(2,playerStats.getTimePlayedInString());
        pstmt.setString(3,playerStats.getTimeSinceLastDeathInString());
        pstmt.setInt(4,playerStats.getTimePlayed());
        pstmt.setInt(5,playerStats.getTimeSinceLastDeath());
        pstmt.setInt(6,playerStats.getTotalDeath());
        pstmt.setInt(7,playerStats.getTotalJump());
        pstmt.setInt(8,playerStats.getTotalMobKill());
        pstmt.setInt(9,playerStats.getTotalPlayerKill());
        pstmt.setFloat(10,playerStats.getTotalDistance());
        pstmt.setFloat(11,playerStats.getTotalDistanceTransportation());
        pstmt.setInt(12,playerStats.getTotalAdvancement());
        pstmt.setFloat(13,playerStats.getDamageTaken());
        pstmt.setFloat(14,playerStats.getDamageDealt());
        pstmt.setFloat(15,playerStats.getDamageBlockedByShield());
        pstmt.setInt(16,playerStats.getRaidWon());
        pstmt.setInt(17,playerStats.getTradedWithVillager());
        pstmt.setInt(18,playerStats.getAnimalBred());
        pstmt.setInt(19,playerStats.getFishCaught());
        pstmt.setInt(20,playerStats.getMineStat());
        pstmt.setInt(21,playerStats.getCraftStat());
        pstmt.setInt(22,playerStats.getUseStat());
        pstmt.setInt(23,playerStats.getKillEntity());
        pstmt.setInt(24,playerStats.getEntityKilledBy());
        pstmt.setInt(25,playerStats.getTotalScore());
        pstmt.setString(26,playerStats.getDiscordID());
        pstmt.setString(27,playerStats.getPlayerUUID());
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
        pstmt.setInt(3,materialData.getMineStat());
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
