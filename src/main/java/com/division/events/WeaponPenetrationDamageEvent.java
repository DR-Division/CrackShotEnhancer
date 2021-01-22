package com.division.events;

import com.division.enums.PenetrationType;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class WeaponPenetrationDamageEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private boolean isCancelled;
    private final Player shooter;
    private final Entity victim;
    private final PenetrationType type;
    private int damage;

    public WeaponPenetrationDamageEvent(Player shooter, Entity victim, PenetrationType type, int damage) {
        this.shooter = shooter;
        this.victim = victim;
        this.type = type;
        this.damage = damage;
        this.isCancelled = false;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean isCancelled) {
        this.isCancelled = isCancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public Player getShooter() {
        return shooter;
    }

    public Entity getVictim() {
        return victim;
    }

    public PenetrationType getType() {
        return type;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

}
