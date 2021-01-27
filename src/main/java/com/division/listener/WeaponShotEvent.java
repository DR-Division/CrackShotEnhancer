package com.division.listener;

import com.division.events.WeaponRayTraceEvent;
import com.division.hook.CSConfigHook;
import com.division.util.Raytrace;
import com.shampaggon.crackshot.events.WeaponDamageEntityEvent;
import com.shampaggon.crackshot.events.WeaponShootEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

import java.util.concurrent.ThreadLocalRandom;

public class WeaponShotEvent implements Listener {

    @EventHandler
    public void onShoot(WeaponShootEvent event) {
        if (event.getProjectile() != null && CSConfigHook.getInstance().getBoolean(event.getWeaponTitle(), ".Shooting.Hit_Scan")) {
            //투사체가 존재하고, 옵션이 활성화 되어 있을때
            Player p = event.getPlayer();
            String weaponTitle = event.getWeaponTitle();
            CSConfigHook hook = CSConfigHook.getInstance();
            int maxDistance = hook.getInt(event.getWeaponTitle(), ".Shooting.Projectile_Speed");
            int maxPenetrate = hook.getInt(event.getWeaponTitle(), ".Shooting.Max_Penetration");
            if (maxDistance < 1)
                maxDistance = 1;
            WeaponRayTraceEvent rayTraceEvent = new WeaponRayTraceEvent(p, maxDistance, maxPenetrate);
            Bukkit.getPluginManager().callEvent(rayTraceEvent);
            if (rayTraceEvent.getRange() < 1)
                rayTraceEvent.setRange(1);
            if (!rayTraceEvent.isCancelled()) {
                int totalDmg = 0;
                event.getProjectile().remove();
                int damage = hook.getInt(weaponTitle, ".Shooting.Projectile_Damage");
                boolean head = hook.getBoolean(weaponTitle, ".Headshot.Enable");
                boolean crit = hook.getBoolean(weaponTitle, ".Critical_Hits.Enable");
                boolean back = hook.getBoolean(weaponTitle, ".Backstab.Enable");
                boolean isHead = false;
                boolean isCrit = false;
                boolean isBack = false;
                int headShot = hook.getInt(weaponTitle, ".Headshot.Bonus_Damage");
                int critDmg = hook.getInt(weaponTitle, ".Critical_Hits.Bonus_Damage");
                int critChance = hook.getInt(weaponTitle, ".Critical_Hits.Chance");
                int backDmg = hook.getInt(weaponTitle, ".Backstab.Bonus_Damage");
                for (Entity entity : Raytrace.getEntities(p, weaponTitle, rayTraceEvent.getRange(), rayTraceEvent.getMaxPenetration())) {
                    Vector direction = p.getLocation().getDirection();
                    totalDmg = damage;
                    if (head && checkHeadShot(entity, p, direction)) {
                        totalDmg += headShot;
                        isHead = true;
                    }
                    if (crit && checkCritical(critChance)) {
                        totalDmg += critDmg;
                        isCrit = true;
                    }
                    if (back && checkBack(entity, p)) {
                        totalDmg += backDmg;
                        isBack = true;
                    }
                    WeaponDamageEntityEvent damageEvent = new WeaponDamageEntityEvent(p, entity, p, weaponTitle, totalDmg, isHead, isBack, isCrit);
                    Bukkit.getPluginManager().callEvent(damageEvent);
                    if (!damageEvent.isCancelled()) {
                        LivingEntity livingEntity = (LivingEntity) entity;
                        livingEntity.damage(totalDmg, p);
                        livingEntity.setNoDamageTicks(0);
                    }
                }
            }
        }
    }
    public boolean checkHeadShot(Entity victim, Player shooter, Vector direction) {
        Vector center = victim.getLocation().clone().add(new Vector(0,victim.getHeight() - 0.25,0)).subtract(shooter.getEyeLocation()).toVector().normalize();
        Vector top = victim.getLocation().clone().add(new Vector(0,victim.getHeight(),0)).subtract(shooter.getEyeLocation()).toVector().normalize();
        Vector bot = victim.getLocation().clone().add(new Vector(0, victim.getHeight() - 0.5, 0)).subtract(shooter.getEyeLocation()).toVector().normalize();
        double accuracy = center.angle(top) + center.angle(bot);
        accuracy += accuracy * 1 / 5;
        return direction.angle(top) + direction.angle(bot) < accuracy;
    }

    public boolean checkCritical(int chance) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int randVal = random.nextInt(1, 101);
        return chance >= randVal;
    }

    public boolean checkBack(Entity victim, Player shooter) {
        Vector direction = victim.getLocation().clone().add(new Vector(0, victim.getHeight(), 0)).subtract(shooter.getLocation()).toVector().normalize();
        Vector vicDirection = victim.getLocation().getDirection();
        return direction.angle(vicDirection) <= 0.2;
    }
}
