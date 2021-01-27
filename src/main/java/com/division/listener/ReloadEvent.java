package com.division.listener;

import com.division.hook.CSConfigHook;
import com.shampaggon.crackshot.events.WeaponDamageEntityEvent;
import com.shampaggon.crackshot.events.WeaponReloadEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class ReloadEvent implements Listener {

    @EventHandler (priority = EventPriority.LOWEST)
    public void onReload(WeaponReloadEvent event) {
        ItemStack stack = event.getPlayer().getInventory().getItemInMainHand();
        CSConfigHook hook = CSConfigHook.getInstance();
        int time = hook.getInt(event.getWeaponTitle(), ".Reload.Tactical_Reload");
        if (time != 0 && stack.getItemMeta().getDisplayName() != null && !stack.getItemMeta().getDisplayName().contains("«0»"))
            event.setReloadDuration(time);
    }

}
