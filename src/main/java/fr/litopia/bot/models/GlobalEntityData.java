package fr.litopia.bot.models;

import org.apache.commons.lang.WordUtils;

public class GlobalEntityData {
    private String name;
    private int killed;
    private int murder;

    public GlobalEntityData(String id, int killed, int murder){
        this.name = id;
        this.killed = killed;
        this.murder = murder;
    }

    public String getID() {
        return name;
    }

    public String getName(){
        return WordUtils.capitalize(this.name.toLowerCase()).replace("_"," ");
    }

    public int getKilled() {
        return killed;
    }

    public int getMurder() {
        return murder;
    }
}
