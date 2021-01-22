package com.division.hook;

import com.shampaggon.crackshot.CSDirector;

public class CSConfigHook {

    private static CSConfigHook instance;

    static {
        instance = new CSConfigHook();
    }

    private CSConfigHook() {
    }

    public static CSConfigHook getInstance() {
        return instance;
    }

    public boolean getBoolean(String weaponTitle, String extra) {
        CSDirector director = CrackShotAPI.getInstance().getHandle();
        if (weaponTitle.contains("_")) {
            //getBoolean("M26_XX",".Shooting.Projectile_Speed") -> M26.Shooting.Projectile_Speed
            String[] list = weaponTitle.split("_"); //Item_Information: Similar_Use 여부 확인
            if (director.getBoolean(list[0] + ".Item_Information.Similar_Use"))
                return director.getBoolean(list[0] + extra);
        }
        else
            return director.getBoolean(weaponTitle + extra);
        return false;
    }

    public int getInt(String weaponTitle, String extra) {
        CSDirector director = CrackShotAPI.getInstance().getHandle();
        if (weaponTitle.contains("_")) {
            //getBoolean("M26",".Shooting.Projectile_Speed") -> M26.Shooting.Projectile_Speed
            String[] list = weaponTitle.split("_"); //Item_Information: Similar_Use 여부 확인
            if (director.getBoolean(list[0] + ".Item_Information.Similar_Use"))
                return director.getInt(list[0] + extra);
        }
        else
            return director.getInt(weaponTitle + extra);
        return 0;
    }
}
