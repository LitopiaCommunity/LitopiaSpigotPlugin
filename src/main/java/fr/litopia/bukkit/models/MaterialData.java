package fr.litopia.bukkit.models;

import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

import javax.naming.Name;

public class MaterialData {
    private Material mat;
    private String name;
    private Player player;
    private int mineStat;
    private int brokenStat;
    private int craftStat;
    private int useStat;

    public MaterialData(Material material, String name){
        this.mat = material;
        this.name = name;
    }

    public Material getMat() {
        return mat;
    }

    public String getName() {
        return name;
    }

    public void setPlayer(Player p){
        this.player = p;

        this.brokenStat = this.player.getStatistic(Statistic.BREAK_ITEM,this.mat);
        this.craftStat = this.player.getStatistic(Statistic.CRAFT_ITEM,this.mat);
        this.useStat = this.player.getStatistic(Statistic.USE_ITEM,this.mat);
        this.mineStat = this.player.getStatistic(Statistic.MINE_BLOCK,this.mat);
    }

    public Player getPlayer() {
        return player;
    }

    public int getMinestat() {
        return mineStat;
    }

    public int getBrokenStat() {
        return brokenStat;
    }

    public int getCraftStat() {
        return craftStat;
    }

    public int getUseStat() {
        return useStat;
    }
}
