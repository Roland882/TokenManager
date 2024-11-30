// Decompiled with: CFR 0.152
// Class Version: 8
package me.realized.tokenmanager.shop;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalLong;
import me.realized.tokenmanager.TokenManagerPlugin;
import me.realized.tokenmanager.config.Config;
import me.realized.tokenmanager.shop.Shop;
import me.realized.tokenmanager.shop.Slot;
import me.realized.tokenmanager.util.Log;
import me.realized.tokenmanager.util.NumberUtil;
import me.realized.tokenmanager.util.Reloadable;
import me.realized.tokenmanager.util.StringUtil;
import me.realized.tokenmanager.util.compat.Items;
import me.realized.tokenmanager.util.config.AbstractConfiguration;
import me.realized.tokenmanager.util.inventory.GUIBuilder;
import me.realized.tokenmanager.util.inventory.ItemBuilder;
import me.realized.tokenmanager.util.inventory.ItemUtil;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ShopConfig
        extends AbstractConfiguration<TokenManagerPlugin>
        implements Reloadable {
    private final Map<String, Shop> shopSamples = new LinkedHashMap<String, Shop>();
    private Inventory confirmGuiSample;

    public ShopConfig(TokenManagerPlugin plugin) {
        super(plugin, "shops");
    }

    @Override
    protected void loadValues(FileConfiguration configuration) {
        ConfigurationSection section = configuration.getConfigurationSection("shops");
        if (section == null) {
            return;
        }
        for (String name : section.getKeys(false)) {
            Shop shop;
            ConfigurationSection shopSection = section.getConfigurationSection(name);
            name = name.toLowerCase();
            try {
                shop = new Shop(name, shopSection.getString("title", "&cShop title was not specified."), shopSection.getInt("rows", 1), shopSection.getBoolean("auto-close", false), shopSection.getBoolean("use-permission", false), shopSection.getBoolean("confirm-purchase", false));
            }
            catch (IllegalArgumentException ex) {
                Log.error(this, "Failed to initialize shop '" + name + "': " + ex.getMessage());
                continue;
            }
            ConfigurationSection itemsSection = shopSection.getConfigurationSection("items");
            if (itemsSection != null) {
                for (String num : itemsSection.getKeys(false)) {
                    ItemStack displayed;
                    OptionalLong target = NumberUtil.parseLong(num);
                    if (!target.isPresent()) {
                        Log.error(this, "Failed to load slot '" + num + "' of shop '" + name + "': '" + num + "' is not a valid number.");
                        continue;
                    }
                    long slot = target.getAsLong();
                    if (slot < 0L || slot >= (long)shop.getInventory().getSize()) {
                        Log.error(this, "Failed to load slot '" + num + "' of shop '" + name + "': '" + slot + "' is over the shop size.");
                        continue;
                    }
                    ConfigurationSection slotSection = itemsSection.getConfigurationSection(num);
                    try {
                        displayed = ItemUtil.loadFromString(slotSection.getString("displayed"));
                    }
                    catch (Exception ex) {
                        shop.getInventory().setItem((int)slot, ItemBuilder.of(Material.REDSTONE_BLOCK).name("&4&m------------------").lore("&cThere was an error", "&cwhile loading this", "&citem, please contact", "&can administrator.", "&4&m------------------").build());
                        Log.error(this, "Failed to load displayed item for slot '" + num + "' of shop '" + name + "': " + ex.getMessage());
                        continue;
                    }
                    Object value = slotSection.get("message");
                    shop.setSlot((int)slot, displayed, new Slot((TokenManagerPlugin)this.plugin, shop, (int)slot, slotSection.getInt("cost", 1000000), slotSection.getInt("empty-slots-required", 0), displayed, value != null ? (value instanceof List ? StringUtil.fromList((List)value) : value.toString()) : null, slotSection.getString("subshop"), slotSection.getStringList("commands"), slotSection.getBoolean("use-permission", false), slotSection.getBoolean("confirm-purchase", false)));
                }
            }
            if (this.register(name, shop)) continue;
            Log.error(this, "Failed to load shop '" + name + "': Shop already exists.");
        }
        Config config = ((TokenManagerPlugin)this.plugin).getConfiguration();
        this.confirmGuiSample = GUIBuilder.of(StringUtil.color(config.getConfirmPurchaseTitle()), 3).pattern(GUIBuilder.Pattern.of("AAABBBCCC", "AAABBBCCC", "AAABBBCCC").specify('A', Items.GREEN_PANE.clone()).specify('B', Items.GRAY_PANE.clone()).specify('C', Items.RED_PANE.clone())).set(10, ItemUtil.loadFromString(config.getConfirmPurchaseConfirm(), error -> Log.error(this, "Failed to load confirm-button: " + error))).set(16, ItemUtil.loadFromString(config.getConfirmPurchaseCancel(), error -> Log.error(this, "Failed to load cancel-button: " + error))).build();
    }

    @Override
    public void handleUnload() {
        this.shopSamples.clear();
        ((TokenManagerPlugin)this.plugin).getShopManager().clearCache();
    }

    public Optional<Shop> getShop(String name) {
        return Optional.ofNullable(this.shopSamples.get(name));
    }

    public Collection<Shop> getShops() {
        return this.shopSamples.values();
    }

    public boolean register(String name, Shop shop) {
        if (this.shopSamples.containsKey(name)) {
            return false;
        }
        this.shopSamples.put(name, shop);
        return true;
    }

    public Inventory getConfirmGuiSample() {
        return this.confirmGuiSample;
    }
}
