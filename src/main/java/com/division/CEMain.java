package com.division;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.division.listener.*;
import com.division.packet.GlowPacketAdapter;
import org.bukkit.plugin.java.JavaPlugin;

public class CEMain extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("CrackShotEnhancer Enabled");
        getServer().getPluginManager().registerEvents(new ProjectileEvent(), this);
        getServer().getPluginManager().registerEvents(new ScopeEvent(this), this);
        getServer().getPluginManager().registerEvents(new WeaponExplosionEvent(), this);
        getServer().getPluginManager().registerEvents(new WeaponShotEvent(), this);
        getServer().getPluginManager().registerEvents(new ReloadEvent(), this);
        ProtocolLibrary.getProtocolManager().addPacketListener(new GlowPacketAdapter(this, PacketType.Play.Server.ENTITY_METADATA));
    }

    @Override
    public void onDisable() {
        getLogger().info("CrackShotEnhancer Disabled");
    }
}
