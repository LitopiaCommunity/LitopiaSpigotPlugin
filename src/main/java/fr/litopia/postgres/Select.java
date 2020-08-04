package fr.litopia.postgres;

import fr.litopia.bukkit.models.EntityData;
import fr.litopia.bukkit.models.MaterialData;
import fr.litopia.bukkit.models.PlayerStats;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

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
        pstmt.setString(3,materialData.getId());
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
        pstmt.setString(3,entityData.getId());
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

    public ResultSet getMemberData(String discordID) throws SQLException {
        String SQL = "Select * from members where iddiscord = (?) and acceptedate is not null;";
        PreparedStatement pstmt = this.conn.prepareStatement(SQL);
        pstmt.setString(1,discordID);
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

    public ResultSet getAllMembers() throws SQLException {
        String SQL = "Select * from members where acceptedate is not null";
        PreparedStatement pstmt = this.conn.prepareStatement(SQL);
        ResultSet rs = pstmt.executeQuery();
        return rs;
    }

    public ResultSet getMemberFromDiscordID(String discordID) throws SQLException {
        String SQL = "Select * from members where iddiscord = (?);";
        PreparedStatement pstmt = this.conn.prepareStatement(SQL);
        pstmt.setString(1,discordID);
        ResultSet rs = pstmt.executeQuery();
        rs.next();
        return rs;
    }

    public ResultSet getLeaderBoardScore() throws SQLException {
        String SQL = "Select minecraftnickname, totalscore from members where totalscore IS NOT NULL order by totalscore desc LIMIT 20;";
        PreparedStatement pstmt = this.conn.prepareStatement(SQL);
        ResultSet rs =  pstmt.executeQuery();
        return rs;
    }

    public  ResultSet getLeaderBoardTime() throws SQLException{
        String SQL = "Select minecraftnickname, playtime from members where playtimetick IS NOT NULL order by playtimetick desc LIMIT 20";
        PreparedStatement pstmt = this.conn.prepareStatement(SQL);
        ResultSet rs =  pstmt.executeQuery();
        return rs;
    }

    public  ResultSet getLeaderBoardDeath() throws SQLException{
        String SQL = "Select minecraftnickname, numberdeath from members where numberdeath IS NOT NULL order by numberdeath LIMIT 20";
        PreparedStatement pstmt = this.conn.prepareStatement(SQL);
        ResultSet rs =  pstmt.executeQuery();
        return rs;
    }

    public  ResultSet getLeaderBoardDeathReverse() throws SQLException{
        String SQL = "Select minecraftnickname, numberdeath from members where numberdeath IS NOT NULL order by numberdeath desc LIMIT 20";
        PreparedStatement pstmt = this.conn.prepareStatement(SQL);
        ResultSet rs =  pstmt.executeQuery();
        return rs;
    }

    public  ResultSet getLeaderBoardAdvancements() throws SQLException{
        String SQL = "Select minecraftnickname, totaladvancement from members where totaladvancement IS NOT NULL order by totaladvancement desc LIMIT 20";
        PreparedStatement pstmt = this.conn.prepareStatement(SQL);
        ResultSet rs =  pstmt.executeQuery();
        return rs;
    }

    public  ResultSet getLeaderBoardWalkDistance() throws SQLException{
        String SQL = "Select minecraftnickname, totalparcourdistance from members where totalparcourdistance IS NOT NULL order by totalparcourdistance desc LIMIT 20";
        PreparedStatement pstmt = this.conn.prepareStatement(SQL);
        ResultSet rs =  pstmt.executeQuery();
        return rs;
    }

    public  ResultSet getLeaderBoardTransportDistance() throws SQLException{
        String SQL = "Select minecraftnickname, totalparcourdistancetransportation from members where totalparcourdistancetransportation IS NOT NULL order by totalparcourdistancetransportation desc LIMIT 20";
        PreparedStatement pstmt = this.conn.prepareStatement(SQL);
        ResultSet rs =  pstmt.executeQuery();
        return rs;
    }

    public  ResultSet getLeaderBoardMobsKill() throws SQLException{
        String SQL = "Select minecraftnickname,totalkillentity from members where totalkillentity IS NOT NULL order by totalkillentity desc LIMIT 20";
        PreparedStatement pstmt = this.conn.prepareStatement(SQL);
        ResultSet rs =  pstmt.executeQuery();
        return rs;
    }

    public  ResultSet getLeaderBoardPlayersKill() throws SQLException {
        String SQL = "Select minecraftnickname, totalplayerkill from members where totalplayerkill IS NOT NULL order by totalplayerkill desc LIMIT 20";
        PreparedStatement pstmt = this.conn.prepareStatement(SQL);
        ResultSet rs =  pstmt.executeQuery();
        return rs;
    }

    public  ResultSet getLeaderBoardJumps() throws SQLException {
        String SQL = "Select minecraftnickname, numberjump from members where numberjump IS NOT NULL order by numberjump desc LIMIT 20";
        PreparedStatement pstmt = this.conn.prepareStatement(SQL);
        ResultSet rs =  pstmt.executeQuery();
        return rs;
    }

    public int countMembers() throws SQLException{
        String SQL = "select count(*) from members where acceptedate IS NOT NULL";
        PreparedStatement pstmt = this.conn.prepareStatement(SQL);
        ResultSet rs =  pstmt.executeQuery();
        rs.next();
        return rs.getInt(1);
    }

    public ResultSet getInactifListMembers() throws SQLException{
        String SQL = "select minecraftnickname, discordnickname from members where lastupdate <= now()-interval '7 days'";
        PreparedStatement pstmt = this.conn.prepareStatement(SQL);
        ResultSet rs =  pstmt.executeQuery();
        return rs;
    }

    public int countActiveMembers() throws SQLException{
        String SQL = "select count(*) from members where  lastupdate BETWEEN now()-interval '7 days' and now();";
        PreparedStatement pstmt = this.conn.prepareStatement(SQL);
        ResultSet rs =  pstmt.executeQuery();
        rs.next();
        return rs.getInt(1);
    }

    public ResultSet getActifMembers() throws SQLException{
        String SQL = "select minecraftnickname,discordnickname from members where lastupdate BETWEEN now()-interval '7 days' and now();";
        PreparedStatement pstmt = this.conn.prepareStatement(SQL);
        ResultSet rs =  pstmt.executeQuery();
        return rs;
    }

    public int countRejectMembers() throws SQLException{
        String SQL = "select count(*) from members where rolename = 'Refuser'";
        PreparedStatement pstmt = this.conn.prepareStatement(SQL);
        ResultSet rs =  pstmt.executeQuery();
        rs.next();
        return rs.getInt(1);
    }

    public ResultSet getRejectMembers() throws SQLException{
        String SQL = "select minecraftnickname,discordnickname from members where rolename = 'Refuser';";
        PreparedStatement pstmt = this.conn.prepareStatement(SQL);
        ResultSet rs =  pstmt.executeQuery();
        return rs;
    }

    public int countAwaitVotesMembers() throws SQLException{
        String SQL = "select count(*) from members where acceptedate is null and rolename is null";
        PreparedStatement pstmt = this.conn.prepareStatement(SQL);
        ResultSet rs =  pstmt.executeQuery();
        rs.next();
        return rs.getInt(1);
    }


    public ResultSet getAwaitVotesMembers() throws SQLException{
        String SQL = "select minecraftnickname,discordnickname from members where acceptedate is null and rolename is null;";
        PreparedStatement pstmt = this.conn.prepareStatement(SQL);
        ResultSet rs =  pstmt.executeQuery();
        return rs;
    }
}
