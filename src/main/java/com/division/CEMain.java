package com.division;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.division.listener.WeaponExplosionEvent;
import com.division.listener.ScopeEvent;
import com.division.packet.GlowPacketAdapter;
import org.bukkit.plugin.java.JavaPlugin;
import com.division.listener.ProjectileEvent;

public class CEMain extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("CrackShotEnhancer Enabled");
        getServer().getPluginManager().registerEvents(new ProjectileEvent(), this);
        getServer().getPluginManager().registerEvents(new ScopeEvent(this), this);
        getServer().getPluginManager().registerEvents(new WeaponExplosionEvent(), this);
        ProtocolLibrary.getProtocolManager().addPacketListener(new GlowPacketAdapter(this, PacketType.Play.Server.ENTITY_METADATA));
    }

    @Override
    public void onDisable() {
        getLogger().info("CrackShotEnhancer Disabled");
    }
}
