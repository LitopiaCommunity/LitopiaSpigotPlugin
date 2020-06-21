package fr.litopia.postgres;

import fr.litopia.bukkit.models.EntityData;
import fr.litopia.bukkit.models.MaterialData;
import fr.litopia.bukkit.models.PlayerStats;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Select {
    private Connection conn;

    public Select(Connection con){
        this.conn = con;
    }

    public String getMinecraftUUID(String discordID) throws SQLException {
        String SQL = "Select minecraftUUID from members where iddiscord = (?) and acceptedate is not null;";
        PreparedStatement pstmt = this.conn.prepareStatement(SQL);
        pstmt.setString(1,discordID);
        ResultSet rs = pstmt.executeQuery();
        rs.next();
        return rs.getString("minecraftuuid").replaceAll("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})","$1-$2-$3-$4-$5");
    }

    public String getMinecraftUUIDFromMinecraftNickname(String nickname) throws SQLException {
        String SQL = "Select minecraftUUID from members where lower(minecraftnickname) = lower((?)) and acceptedate is not null;";
        PreparedStatement pstmt = this.conn.prepareStatement(SQL);
        pstmt.setString(1,nickname);
        ResultSet rs = pstmt.executeQuery();
        rs.next();
        return rs.getString("minecraftuuid").replaceAll("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})","$1-$2-$3-$4-$5");
    }

    public String getDiscordID(String mcUUID) throws SQLException {
        String SQL = "Select iddiscord from members where minecraftUUID = (?) and acceptedate is not null;";
        PreparedStatement pstmt = this.conn.prepareStatement(SQL);
        pstmt.setString(1,mcUUID.replaceAll("-",""));
        ResultSet rs = pstmt.executeQuery();
        rs.next();
        return rs.getString("iddiscord");
    }
    
    public String getMcNicknameFomDiscordID(String discordID) throws SQLException {
        String SQL = "Select minecraftnickname from members where iddiscord = (?) and acceptedate is null;";
        PreparedStatement pstmt = this.conn.prepareStatement(SQL);
        pstmt.setString(1,discordID);
        ResultSet rs = pstmt.executeQuery();
        rs.next();
        return rs.getString("minecraftnickname");
    }

    public String getDiscordIDFromMCNickname(String mcNickname) throws SQLException {
        String SQL = "Select iddiscord from members where LOWER(minecraftnickname) = LOWER((?)) and acceptedate is not null;";
        PreparedStatement pstmt = this.conn.prepareStatement(SQL);
        pstmt.setString(1,mcNickname);
        ResultSet rs = pstmt.executeQuery();
        rs.next();
        return rs.getString("iddiscord");
    }

    public String getMinecraftNickname(String discordID, String minecraftUUID) throws SQLException {
        String SQL = "Select minecraftnickname from members where iddiscord = (?) and minecraftuuid = (?) and acceptedate is not null;";
        PreparedStatement pstmt = this.conn.prepareStatement(SQL);
        pstmt.setString(1,discordID);
        pstmt.setString(2,minecraftUUID.replaceAll("-",""));
        ResultSet rs = pstmt.executeQuery();
        rs.next();
        return rs.getString("minecraftnickname");
    }

    public int checkItemStat(PlayerStats playerStats, MaterialData materialData) throws SQLException {
        String SQL = "Select count(*) from itemstat" +
                " where idDiscord = (?) and minecraftUUID = (?) and item = (?)";
        PreparedStatement pstmt = this.conn.prepareStatement(SQL);
        pstmt.setString(1,playerStats.getDiscordID());
        pstmt.setString(2,playerStats.getPlayerUUID());
        pstmt.setString(3,materialData.getName());
        ResultSet rs = pstmt.executeQuery();
        rs.next();
        return rs.getInt("count");
    }

    public int checkMobStat(PlayerStats playerStats, EntityData entityData) throws SQLException {
        String SQL = "Select count(*) from mobstat" +
                " where idDiscord = (?) and minecraftUUID = (?) and mob = (?)";
        PreparedStatement pstmt = this.conn.prepareStatement(SQL);
        pstmt.setString(1,playerStats.getDiscordID());
        pstmt.setString(2,playerStats.getPlayerUUID());
        pstmt.setString(3,entityData.getName());
        ResultSet rs = pstmt.executeQuery();
        rs.next();
        return rs.getInt("count");
    }

    public ResultSet getMemberData(String discordID, String minecraftUUID) throws SQLException {
        String SQL = "Select * from members where iddiscord = (?) and minecraftuuid = (?) and acceptedate is not null;";
        PreparedStatement pstmt = this.conn.prepareStatement(SQL);
        pstmt.setString(1,discordID);
        pstmt.setString(2,minecraftUUID.replaceAll("-",""));
        ResultSet rs = pstmt.executeQuery();
        rs.next();
        return rs;
    }

    public ResultSet getMemberItemStats(String discordID, String minecraftUUID) throws SQLException{
        String SQL = "Select * from itemstat where iddiscord = (?) and minecraftuuid = (?);";
        PreparedStatement pstmt = this.conn.prepareStatement(SQL);
        pstmt.setString(1,discordID);
        pstmt.setString(2,minecraftUUID.replaceAll("-",""));
        ResultSet rs = pstmt.executeQuery();
        rs.next();
        return rs;
    }

    public ResultSet getMemberMobStats(String discordID, String minecraftUUID) throws SQLException{
        String SQL = "Select * from mobstat where iddiscord = (?) and minecraftuuid = (?);";
        PreparedStatement pstmt = this.conn.prepareStatement(SQL);
        pstmt.setString(1,discordID);
        pstmt.setString(2,minecraftUUID.replaceAll("-",""));
        ResultSet rs = pstmt.executeQuery();
        rs.next();
        return rs;
    }
}
