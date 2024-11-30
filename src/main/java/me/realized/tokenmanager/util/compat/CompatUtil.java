package me.realized.tokenmanager.util.compat;

import me.realized.tokenmanager.util.NumberUtil;
import org.bukkit.Bukkit;

public final class CompatUtil {

    private static final long SUB_VERSION;

    static {
        final String bukkitVersion = Bukkit.getBukkitVersion();

        final String[] versionParts = bukkitVersion.split("-")[0].split("\\.");

        SUB_VERSION = versionParts.length > 1 ? Long.parseLong(versionParts[1]) : 0;


        //final String packageName = Bukkit.getServer().getClass().getPackage().getName();
        //SUB_VERSION = NumberUtil.parseLong(packageName.substring(packageName.lastIndexOf('.') + 1).split("_")[1]).orElse(0);
    }

    private CompatUtil() {}

    public static boolean isPre1_17() {
        return SUB_VERSION < 17;
    }

    public static boolean isPre1_14() {
        return SUB_VERSION < 14;
    }

    public static boolean isPre1_13() {
        return SUB_VERSION < 13;
    }

    public static boolean isPre1_12() {
        return SUB_VERSION < 12;
    }

    public static boolean isPre1_9() {
        return SUB_VERSION < 9;
    }
}