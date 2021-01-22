package com.division.listener;

import com.division.CEMain;
import com.division.enums.PenetrationType;
import com.division.events.WeaponPenetrationDamageEvent;
import com.division.events.WeaponPenetrationEvent;
import com.division.hook.CSConfigHook;
import com.division.hook.CrackShotAPI;
import com.shampaggon.crackshot.CSDirector;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class ProjectileEvent implements Listener {

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        ProjectileSource source = projectile.getShooter();
        if (source instanceof Player) {
            Player p = (Player) source;
            ItemStack item = p.getInventory().getItemInMainHand();
            CrackShotAPI api = CrackShotAPI.getInstance();
            if (item != null && api.getWeaponTitle(item) != null && (event.getHitBlock() != null || event.getHitEntity() != null)) {
                boolean entityPenetrate;
                boolean wallPenetrate;
                boolean actC4;
                int penetrateRange;
                int maxPenetrate;
                Location start;
                Vector next;
                PenetrationType type;
                String title = api.getWeaponTitle(item);
                CSConfigHook hook = CSConfigHook.getInstance();
                entityPenetrate = hook.getBoolean(title, ".Shooting.Entity_Penetration");
                wallPenetrate = hook.getBoolean(title, ".Shooting.Wall_Penetration");
                actC4 = hook.getBoolean(title, ".Shooting.Active_C4");
                penetrateRange = hook.getInt(title, ".Shooting.Penetration_Range");
                maxPenetrate = hook.getInt(title, ".Shooting.Max_Penetration");
                if (maxPenetrate == 0)
                    maxPenetrate = 999;
                if (entityPenetrate || wallPenetrate || actC4) {
                    if (event.getHitBlock() != null) {
                        start = fixLocation(event.getHitBlock().getLocation());
                        type = PenetrationType.BLOCK;
                    }
                    else {
                        start = event.getHitEntity().getLocation();
                        type = PenetrationType.ENTITY;
                    }
                    if (actC4) //to-do event 호출하기
                        activeC4(start, p); //c4 총알 폭파 구현
                    if (((entityPenetrate && type == PenetrationType.ENTITY) || (wallPenetrate && type == PenetrationType.BLOCK)) && penetrateRange > 0) {
                        WeaponPenetrationEvent penetrateEvent = new WeaponPenetrationEvent(p, start, type, penetrateRange, maxPenetrate);
                        Bukkit.getPluginManager().callEvent(penetrateEvent);
                        if (!penetrateEvent.isCancelled() && penetrateEvent.getRange() != 0) {
                            next = start.toVector().subtract(p.getLocation().toVector()).normalize();
                            //블럭 반복을 통한 엔티티 수집 및 관통 구현
                            BlockIterator iterator = new BlockIterator(p.getWorld(), start.toVector(), next, 1, penetrateEvent.getRange());
                            ArrayList<LivingEntity> entities = new ArrayList<>();
                            while (iterator.hasNext()) {
                                Block block = iterator.next();
                                for (Entity entity : p.getWorld().getNearbyEntities(block.getLocation(), 0.4, 0.4, 0.4)) {
                                    if (entity instanceof LivingEntity && checkRange(entity, p, next)) {
                                        LivingEntity victim = (LivingEntity) entity;
                                        if (!entities.contains(victim) && entities.size() <= penetrateEvent.getMaxPenetration()) {
                                            entities.add(victim);

                                        }
                                    }
                                }
                            }
                            for (LivingEntity entity : entities) {
                                int victimDmg = hook.getInt(title, ".Shooting.Projectile_Damage");
                                int chance = hook.getInt(title, ".Critical_Hits.Chance");
                                if (hook.getBoolean(title, ".Critical_Hits.Enable")) {
                                    int random = ThreadLocalRandom.current().nextInt(1, 101);
                                    if (chance >= random)
                                        victimDmg += hook.getInt(title, ".Critical_Hits.Bonus_Damage");
                                }
                                WeaponPenetrationDamageEvent damageEvent = new WeaponPenetrationDamageEvent(p, entity, type, victimDmg);
                                Bukkit.getPluginManager().callEvent(damageEvent);
                                if (!damageEvent.isCancelled()) {
                                    entity.damage(damageEvent.getDamage(), p);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void activeC4(Location target, Player p) {
        CSDirector director = CrackShotAPI.getInstance().getHandle();
        HashMap<String, String> list = new HashMap<>(); //이름 - 폭발물 이름 형식 저장
        for (Entity entity : p.getWorld().getNearbyEntities(target, 1.5, 1.5, 1.5)) {
            if (entity instanceof Item) {
                Map<String, Map<String, ArrayDeque<Item>>> bombs = director.itembombs;
                for (String name : bombs.keySet()) {
                    for (String bombName : bombs.get(name).keySet()) {
                        for (Item item : bombs.get(name).get(bombName)) {
                            if (item.getLocation().equals(entity.getLocation()))
                                list.put(name, bombName);
                        }
                    }
                }
            }
        }
        for (String key : list.keySet()) {
            Player t = Bukkit.getPlayer(key);
            ItemStack stack = CrackShotAPI.getInstance().generateWeapon(list.get(key));
            CrackShotAPI.getInstance().fixCSError(stack);
            director.detonateC4(t, stack, list.get(key), "itembomb");
        }
    }

    public boolean checkRange(Entity entity, Player shooter, Vector shoot) {
        Vector current = entity.getLocation().toVector().subtract(shooter.getLocation().toVector()).normalize();
        return !(current.dot(shoot) <= 0.996);
    }

    public Location fixLocation(Location value) {
        value.subtract(new Vector(0, 1, 0));
        value.setX(Math.round(value.getX()) + 0.5);
        value.setY(Math.round(value.getY()));
        value.setZ(Math.round(value.getZ()) + 0.5);
        return value;
    }


}
