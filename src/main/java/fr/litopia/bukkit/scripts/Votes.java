package fr.litopia.bukkit.scripts;

import fr.litopia.bukkit.Main;
import fr.litopia.tools.HTTPRequest;
import fr.litopia.tools.PlayerTools;
import fr.litopia.tools.postgres.DBConnection;
import fr.litopia.tools.postgres.Select;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;

public class Votes {


    public static void check(Player player, Main pl) throws IOException, SQLException {
        DBConnection db = new DBConnection(pl.config.getString("postgresConnString"));
        Select S = new Select(db.connect());
        Timestamp lastUpdate = S.getLastUpdate(player.getDisplayName());

        JSONObject data = HTTPRequest.get("https://www.liste-serveur.fr/api/hasVoted/4ce84376ef137c9be173b92845da9284/"+player.getDisplayName());
        if (data.getBoolean("hasVoted")){
            Timestamp lastVote = new Timestamp(data.getLong("lastVote")*1000);
            if (lastUpdate.before(lastVote)){
                if (PlayerTools.hasAvaliableSlot(player)) {
                    ItemStack itemStack = new ItemStack(Material.DIAMOND, 1);
                    player.getInventory().addItem(itemStack);
                    player.sendTitle(ChatColor.GREEN+"Merci pour ton vote",ChatColor.BLUE+"Tu reçois 1 diamond",20,150,20);
                }
            }
        }

    }
}
