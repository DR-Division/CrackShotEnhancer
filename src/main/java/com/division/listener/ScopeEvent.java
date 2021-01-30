package com.division.listener;

import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import com.division.CEMain;
import com.division.hook.CSConfigHook;
import com.division.hook.CrackShotAPI;
import com.shampaggon.crackshot.events.WeaponScopeEvent;
import external.com.comphenix.packetwrapper.WrapperPlayServerEntityMetadata;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class ScopeEvent implements Listener {

    private final CEMain Plugin;
    public static ArrayList<UUID> scopeList = new ArrayList<>();

    public ScopeEvent(CEMain Plugin) {
        this.Plugin = Plugin;
    }

    @EventHandler (ignoreCancelled = true)
    public void onScope(WeaponScopeEvent event) {
        CSConfigHook hook = CSConfigHook.getInstance();
        if (hook.getBoolean(event.getWeaponTitle(), ".Scope.Thermal_Scope"))
            setGlow(event.getPlayer(), event.isZoomIn());
        if (hook.getBoolean(event.getWeaponTitle(), ".Scope.Extra_Zoom"))
            setExtraScope(event.getPlayer(), event.isZoomIn());

    }

    //버그 방지
    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (CrackShotAPI.getInstance().getWeaponTitle(event.getPlayer().getInventory().getItemInMainHand()) != null) {
            scopeList.remove(event.getPlayer().getUniqueId());
            setExtraScope(event.getPlayer(), false);
        }
    }

    private void setGlow(Player p, boolean value) {
        int distance = Bukkit.getViewDistance();
        if (value) {
            if (!scopeList.contains(p.getUniqueId()))
                scopeList.add(p.getUniqueId());
            for (Entity entity : p.getWorld().getNearbyEntities(p.getLocation(), distance * distance, distance * distance, distance * distance)) {
                if (entity instanceof LivingEntity && entity != p) {
                    WrapperPlayServerEntityMetadata meta = new WrapperPlayServerEntityMetadata();
                    WrappedDataWatcher dataWatcher = WrappedDataWatcher.getEntityWatcher(entity);
                    List<WrappedWatchableObject> objects = dataWatcher.getWatchableObjects();
                    objects.get(0).setValue((byte) ((byte) objects.get(0).getValue() | 0x40));
                    meta.setMetadata(objects);
                    meta.setEntityID(entity.getEntityId());
                    meta.sendPacket(p);
                }
            }
        }
        else {
            scopeList.remove(p.getUniqueId());
            for (Entity entity : p.getWorld().getNearbyEntities(p.getLocation(), distance * distance + 50, distance * distance + 50, distance * distance + 50)) {
                if (entity instanceof LivingEntity && entity != p) {
                    WrapperPlayServerEntityMetadata meta = new WrapperPlayServerEntityMetadata();
                    WrappedDataWatcher dataWatcher = WrappedDataWatcher.getEntityWatcher(entity);
                    List<WrappedWatchableObject> objects = dataWatcher.getWatchableObjects();
                    objects.get(0).setValue((byte) ((byte) objects.get(0).getValue() & ~0x40));
                    meta.setMetadata(objects);
                    meta.setEntityID(entity.getEntityId());
                    meta.sendPacket(p);
                }
            }
        }
    }

    private void setExtraScope(Player p, boolean value) {
        Bukkit.getScheduler().runTaskLater(Plugin, () -> {
            if (value) {
                p.removePotionEffect(PotionEffectType.SPEED);
                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 9999, 10, false, false), true);
                p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 9999, 200, false, false), false);
                p.setWalkSpeed(-0.2f);
            }
            else {
                p.removePotionEffect(PotionEffectType.SLOW);
                p.removePotionEffect(PotionEffectType.JUMP);
                p.setWalkSpeed(0.2f);
                CrackShotAPI.getInstance().getHandle().unscopePlayer(p, true);
            }
        }, 1L);

    }
}
