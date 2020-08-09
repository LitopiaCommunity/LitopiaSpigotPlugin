package fr.litopia.tools.postgres;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Delete {
    private Connection conn;

    public Delete(Connection con){
        this.conn = con;
    }

    public void deleteCandidatureFromDiscordID(String DiscordID) throws SQLException {
        String SQL = "Delete from members where iddiscord = (?)";
        PreparedStatement pstmt = this.conn.prepareStatement(SQL);
        pstmt.setString(1,DiscordID);
        ResultSet rs = pstmt.executeQuery();
    }
}
