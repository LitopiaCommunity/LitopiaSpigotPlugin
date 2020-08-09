package fr.litopia.bukkit.listener;

import fr.litopia.bot.action.TchatActions;
import fr.litopia.bukkit.Main;
import fr.litopia.bukkit.models.PlayerStats;
import fr.litopia.bukkit.scripts.Votes;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class BukkitListener implements Listener {
    private final Main plugin;
    private final TchatActions tchatActions;

    public BukkitListener(Main main){
        this.plugin = main;
        this.tchatActions= new TchatActions(this.plugin);
    }

    @EventHandler
    private void onPlayerChat(AsyncPlayerChatEvent event){
        this.tchatActions.sendMessageToDiscord(event.getPlayer(),event.getMessage());
    }

    @EventHandler()
    public void onPlayerJoin(PlayerJoinEvent e){
        String msg = ":anchor: **"+e.getPlayer().getDisplayName()+" vient de rejoindre Litopia** ["+ Bukkit.getOnlinePlayers().size()+"/"+Bukkit.getMaxPlayers()+"]";
        this.tchatActions.sendBasicBotMessage(msg);
        try {
            Votes.check(e.getPlayer(),this.plugin);
        }catch (Exception err){
            err.printStackTrace();
        }
    }

    @EventHandler()
    public void onPlayerQuit(PlayerQuitEvent e) throws Exception {
        String msg = ":door: **"+e.getPlayer().getDisplayName()+" vient de quitter Litopia** ["+ (Bukkit.getOnlinePlayers().size()-1)+"/"+Bukkit.getMaxPlayers()+"]";
        this.tchatActions.sendBasicBotMessage(msg);

        PlayerStats PS = new PlayerStats(e.getPlayer(),this.plugin);
        PS.save();


    }

    @EventHandler()
    public void onJoin(PlayerDeathEvent e){
        String msg = ":skull: **"+e.getDeathMessage()+"**";
        this.tchatActions.sendBasicBotMessage(msg);
    }


}
