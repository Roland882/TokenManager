// Decompiled with: CFR 0.152
// Class Version: 8
package me.realized.tokenmanager.shop.gui;

import me.realized.tokenmanager.TokenManagerPlugin;
import me.realized.tokenmanager.shop.Shop;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public abstract class BaseGui {
    protected final TokenManagerPlugin plugin;
    protected final Shop shop;
    protected final Inventory inventory;

    protected BaseGui(TokenManagerPlugin plugin, Shop shop, Inventory inventory) {
        this.plugin = plugin;
        this.shop = shop;
        this.inventory = inventory;
    }

    public boolean isGui(Inventory inventory) {
        return this.inventory.equals(inventory);
    }

    public void open(Player player) {
        player.openInventory(this.inventory);
    }

    public abstract void refresh(Player var1, boolean var2);

    public abstract boolean handle(Player var1, int var2);

    public Shop getShop() {
        return this.shop;
    }
}
