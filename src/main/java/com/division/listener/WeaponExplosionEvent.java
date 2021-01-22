package com.division.listener;

import com.division.events.WeaponDetonateEvent;
import com.division.events.WeaponPenetrationDamageEvent;
import com.division.events.WeaponPenetrationEvent;
import com.division.hook.CSConfigHook;
import com.division.hook.CrackShotAPI;
import com.shampaggon.crackshot.CSDirector;
import com.shampaggon.crackshot.events.WeaponExplodeEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;

public class WeaponExplosionEvent implements Listener {

    @EventHandler
    public void test(WeaponDetonateEvent event) {
        Bukkit.broadcastMessage("dfdf");
    }

    @EventHandler
    public void onProjectileExplode(WeaponExplodeEvent event) {
        if (CSConfigHook.getInstance().getBoolean(event.getWeaponTitle(), ".Shooting.Active_C4")) {
            CSDirector director = CrackShotAPI.getInstance().getHandle();
            int radius = director.getInt(event.getWeaponTitle() + ".Explosions.Explosion_Radius");
            activeC4(event.getLocation(), event.getPlayer(), radius);
        }
    }

    private void activeC4(Location target, Player p, int radius) {
        CSDirector director = CrackShotAPI.getInstance().getHandle();
        HashMap<String, String> list = new HashMap<>(); //이름 - 폭발물 이름 형식 저장
        for (Entity entity : p.getWorld().getNearbyEntities(target, radius, radius, radius)) {
            if (entity instanceof Item) {
                Map<String, Map<String, ArrayDeque<Item>>> bombs = director.itembombs;
                for (String name : bombs.keySet()) {
                    for (String bombName : bombs.get(name).keySet()) {
                        for (Item item : bombs.get(name).get(bombName)) {
                            if (item.getLocation().equals(entity.getLocation()) && !name.equalsIgnoreCase(p.getName()))
                                list.put(name, bombName);
                        }
                    }
                }
            }
        }
        if (list.size() != 0) {
            WeaponDetonateEvent event = new WeaponDetonateEvent(p, target, list);
            Bukkit.getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                for (String key : list.keySet()) {
                    Player t = Bukkit.getPlayer(key);
                    ItemStack stack = CrackShotAPI.getInstance().generateWeapon(list.get(key));
                    CrackShotAPI.getInstance().fixCSError(stack);
                    director.detonateC4(t, stack, list.get(key), "itembomb");
                }
            }
        }
    }
}
