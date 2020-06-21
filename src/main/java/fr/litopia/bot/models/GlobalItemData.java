package fr.litopia.bot.models;

import org.apache.commons.lang.WordUtils;
import org.jetbrains.annotations.Contract;

public class GlobalItemData {
    private String name;
    private int mineStat;
    private int brokenStat;
    private int craftStat;
    private int useStat;


    public GlobalItemData(String id, int mineStat, int brokenStat, int craftStat, int useStat) {
        this.name = id;
        this.mineStat = mineStat;
        this.brokenStat = brokenStat;
        this.craftStat = craftStat;
        this.useStat = useStat;
    }


    public String getID() {
        return name;
    }

    public String getName(){
        return WordUtils.capitalize(name.toLowerCase()).replace("_"," ");
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
