package fr.litopia.bukkit.models;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class EntityData {
    private EntityType entityType;
    private String name;
    private String id;
    private Player player;
    private int killEntity;
    private int entityKilledBy;

    public EntityData(EntityType ett, String name){
        this.entityType=ett;
        this.name= WordUtils.capitalize(name.toLowerCase()).replace("_"," ");
        this.id=name;
    }

    public EntityData(String ettName){
        this.entityType = EntityType.fromName(ettName);
        this.name = WordUtils.capitalize(ettName.toLowerCase()).replace("_"," ");
        this.id=name;
    }

    public void setPlayer(Player player) throws Exception {
        this.player = player;
        this.killEntity = player.getStatistic(Statistic.KILL_ENTITY, this.entityType);
        this.entityKilledBy = player.getStatistic(Statistic.ENTITY_KILLED_BY, this.entityType);
    }

    public void setStat(int KILL_ENTITY, int ENTITY_KILLED_BY){
        this.player = null;
        this.killEntity = KILL_ENTITY;
        this.entityKilledBy = ENTITY_KILLED_BY;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public String getName() {
        return name;
    }

    public String getId(){
        return id;
    }

    public Player getPlayer() {
        return player;
    }

    public int getKillEntity() {
        return killEntity;
    }

    public int getEntityKilledBy() {
        return entityKilledBy;
    }
}
