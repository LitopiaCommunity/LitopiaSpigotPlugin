package fr.litopia.bukkit.models;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

import javax.naming.Name;

public class MaterialData {
    private Material mat;
    private String name;
    private String id;
    private Player player;
    private int mineStat;
    private int brokenStat;
    private int craftStat;
    private int useStat;

    public MaterialData(Material material, String name){
        this.mat = material;
        this.name = WordUtils.capitalize(name.toLowerCase()).replace("_"," ");
        this.id = name;
    }

    public MaterialData(String name){
        this.mat = Material.matchMaterial(name);
        this.name = WordUtils.capitalize(name.toLowerCase()).replace("_"," ");
        this.id = name;
    }

    public Material getMat() {
        return mat;
    }

    public String getName() {
        return name;
    }

    public String getId(){
        return id;
    }

    public void setPlayer(Player p){
        this.player = p;

        this.brokenStat = this.player.getStatistic(Statistic.BREAK_ITEM,this.mat);
        this.craftStat = this.player.getStatistic(Statistic.CRAFT_ITEM,this.mat);
        this.useStat = this.player.getStatistic(Statistic.USE_ITEM,this.mat);
        this.mineStat = this.player.getStatistic(Statistic.MINE_BLOCK,this.mat);
    }

    public void setStat(int BREAK_ITEM, int CRAFT_ITEM, int USE_ITEM, int MINE_BLOCK){
        this.player = null;
        this.brokenStat = BREAK_ITEM;
        this.craftStat = CRAFT_ITEM;
        this.useStat = USE_ITEM;
        this.mineStat = MINE_BLOCK;
    }

    public Player getPlayer() {
        return player;
    }

    public int getMineStat() {
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
