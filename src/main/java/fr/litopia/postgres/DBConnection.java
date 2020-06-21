package fr.litopia.postgres;

import java.sql.*;


public class DBConnection {
    private static String url;

    public DBConnection(String uri){
        url = uri;
    }

    public Connection connect() {

        Connection conn = null;
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(url);
            System.out.println("[LitopiaServices] Ouverture d'une connexion a la BDD");
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

        return conn;
    }
}
