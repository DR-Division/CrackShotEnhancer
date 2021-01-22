package com.division.events;

import com.division.enums.PenetrationType;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class WeaponPenetrationEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private boolean isCancelled;
    private final Player shooter;
    private final Location location;
    private final PenetrationType type;
    private int range;
    private int maxPenetration;

    public WeaponPenetrationEvent(Player shooter,Location location, PenetrationType type, int range, int maxPenetrate) {
        this.shooter = shooter;
        this.type = type;
        this.location = location;
        this.range = range;
        this.isCancelled = false;
        this.maxPenetration = maxPenetrate;
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

    public Location getLocation() {
        return location;
    }

    public PenetrationType getType() {
        return type;
    }

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public int getMaxPenetration() {
        return maxPenetration;
    }

    public void setMaxPenetration(int maxPenetration) {
        this.maxPenetration = maxPenetration;
    }

}
