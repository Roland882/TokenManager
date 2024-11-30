package me.realized.tokenmanager.util;

import java.util.List;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class Placeholders {

    public static void replace(Player player, final ItemStack item, final Object value, final String... placeholders) {
        if (!item.hasItemMeta()) {
            return;
        }

        final ItemMeta meta = item.getItemMeta();

        if (meta.hasLore()) {
            final List<String> lore = meta.getLore();
            lore.replaceAll(line -> {
                for (final String placeholder : placeholders) {
                    line = value instanceof Number ? replace(line, (Number) value, placeholder) : line.replace("%" + placeholder + "%", String.valueOf(value));
                }

                if (player != null) {
                    line = PlaceholderAPI.setPlaceholders(player, line.replace("@", "_"));
                }
                return line;
            });
            meta.setLore(lore);
        }

        if (meta.hasDisplayName()) {
            String displayName = meta.getDisplayName();

            for (final String placeholder : placeholders) {
                displayName =
                        value instanceof Number ? replace(displayName, (Number) value, placeholder) : displayName.replace("%" + placeholder + "%", String.valueOf(value));
            }

            if (player != null) {
                displayName = PlaceholderAPI.setPlaceholders(player, displayName.replace("@", "_"));
            }
            meta.setDisplayName(displayName);
        }

        item.setItemMeta(meta);
    }

    public static String replace(String line, final Number value, final String... placeholders) {
        for (final String key : placeholders) {
            line = line
                    .replace("%" + key + "%", String.valueOf(value))
                    .replace("%" + key + "_raw%", String.valueOf(value))
                    .replace("%" + key + "_commas%", NumberUtil.withCommas(value.longValue()))
                    .replace("%" + key + "_formatted%", NumberUtil.withSuffix(value.longValue()));
        }
        return line;
    }

    private Placeholders() {}
}