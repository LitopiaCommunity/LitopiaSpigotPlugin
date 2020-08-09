package fr.litopia.tools;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

public class HTTPRequest {


    public static JSONObject get(String inputURL) throws IOException {
        InputStream result = null;
        boolean error = false;
        URL urlToRead = new URL(inputURL);
        HttpURLConnection conn = (HttpURLConnection) urlToRead.openConnection();
        try {
            conn.setRequestMethod("GET");
            result = conn.getInputStream();
        } catch (UnknownHostException e) {
            error = true;
            result = null;
            System.out.println("[LitopiaServices] : Erreur lors de la connexion à l'API");
        } catch (Exception ex) {
            ex.printStackTrace();
            error = true;
            result = conn.getErrorStream();
        }
        if (result != null) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(result));
            StringBuilder data = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                data.append(line);
            }
            rd.close();
            JSONObject jObject = new JSONObject(data.toString());
            return jObject;
        }
        return null;
    }
}
