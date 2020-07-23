package fr.litopia.bukkit.models;

import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class EntityData {
    private EntityType entityType;
    private String name;
    private Player player;
    private int killEntity;
    private int entityKilledBy;

    public EntityData(EntityType ett, String name){
        this.entityType=ett;
        this.name=name;
    }

    public EntityData(String ettName){
        this.entityType = EntityType.fromName(ettName);
        this.name = ettName;
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
