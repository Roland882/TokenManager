// Decompiled with: CFR 0.152
// Class Version: 8
package me.realized.tokenmanager.data;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.OptionalLong;
import java.util.UUID;
import java.util.function.Consumer;
import me.realized.tokenmanager.TokenManagerPlugin;
import me.realized.tokenmanager.command.commands.subcommands.OfflineCommand;
import me.realized.tokenmanager.data.database.Database;
import me.realized.tokenmanager.data.database.FileDatabase;
import me.realized.tokenmanager.data.database.MySQLDatabase;
import me.realized.tokenmanager.util.Loadable;
import me.realized.tokenmanager.util.Log;
import me.realized.tokenmanager.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitScheduler;

public class DataManager
        implements Loadable,
        Listener {
    private final TokenManagerPlugin plugin;
    private Database database;
    private List<Database.TopElement> topCache = new ArrayList<Database.TopElement>();
    private Integer topTask;
    private Integer updateInterval;
    private long lastUpdateMillis;
    private final Multimap<UUID, QueuedCommand> queuedCommands = LinkedHashMultimap.create();

    public DataManager(TokenManagerPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void handleLoad() throws Exception {
        this.database = this.plugin.getConfiguration().isMysqlEnabled() ? new MySQLDatabase(this.plugin) : new FileDatabase(this.plugin);
        boolean online = this.database.isOnlineMode();
        Log.info("===============================================");
        Log.info("TokenManager has detected your server as " + (online ? "online" : "offline") + " mode.");
        Log.info("DataManager will operate with " + (online ? "UUID" : "Username") + "s.");
        Log.info("If your server is NOT in " + (online ? "online" : "offline") + " mode, please manually set online-mode in TokenManager's config.yml.");
        Log.info("===============================================");
        this.database.setup();
        this.topTask = this.plugin.doSyncRepeat(() -> this.database.ordered(10, args -> this.plugin.doSync(() -> {
            this.lastUpdateMillis = System.currentTimeMillis();
            this.topCache = args;
        })), 0L, 1200L * (long)this.getUpdateInterval());
        Bukkit.getOnlinePlayers().forEach(player -> this.database.load((Player)player));
    }

    @Override
    public void handleUnload() throws Exception {
        BukkitScheduler scheduler;
        if (this.topTask != null && ((scheduler = Bukkit.getScheduler()).isCurrentlyRunning(this.topTask) || scheduler.isQueued(this.topTask))) {
            scheduler.cancelTask(this.topTask);
        }
        this.database.shutdown();
        this.database = null;
    }

    public OptionalLong get(Player player) {
        return this.database != null ? this.database.get(player) : OptionalLong.empty();
    }

    public void set(Player player, long amount) {
        if (this.database != null) {
            this.database.set(player, amount);
        }
    }

    public void get(String key, Consumer<OptionalLong> onLoad, Consumer<String> onError) {
        if (this.database != null) {
            this.database.get(key, onLoad, onError, false);
        }
    }

    public void set(String key, OfflineCommand.ModifyType type, long amount, long balance, boolean silent, Runnable onDone, Consumer<String> onError) {
        if (this.database != null) {
            this.database.set(key, type, amount, balance, silent, onDone, onError);
        }
    }

    public void transfer(CommandSender sender, Consumer<String> onError) {
        if (this.database != null) {
            this.database.transfer(sender, onError);
        }
    }

    public void queueCommand(Player player, OfflineCommand.ModifyType type, long amount, boolean silent) {
        this.queuedCommands.put(player.getUniqueId(), new QueuedCommand(type, amount, silent));
    }

    private int getUpdateInterval() {
        if (this.updateInterval != null) {
            return this.updateInterval;
        }
        this.updateInterval = this.plugin.getConfiguration().getBalanceTopUpdateInterval();
        return this.updateInterval < 1 ? 1 : this.updateInterval;
    }

    public String getNextUpdate() {
        return StringUtil.format((this.lastUpdateMillis + 60000L * (long)this.getUpdateInterval() - System.currentTimeMillis()) / 1000L);
    }

    @EventHandler
    public void on(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (this.database == null) {
            return;
        }
        this.database.load(player, balance -> {
            Collection<QueuedCommand> commands = this.queuedCommands.asMap().remove(player.getUniqueId());
            if (commands == null) {
                return balance;
            }
            long total = balance;
            for (QueuedCommand command : commands) {
                OfflineCommand.ModifyType type = command.type;
                long amount = command.amount;
                total = type.apply(total, amount);
                if (command.silent) continue;
                this.plugin.getLang().sendMessage(player, true, "COMMAND." + (type == OfflineCommand.ModifyType.ADD ? "add" : "remove"), "amount", amount);
            }
            return total;
        });
    }

    @EventHandler
    public void on(PlayerQuitEvent event) {
        if (this.database == null) {
            return;
        }
        Player player = event.getPlayer();
        this.queuedCommands.asMap().remove(player.getUniqueId());
        this.database.save(player);
    }

    public List<Database.TopElement> getTopCache() {
        return this.topCache;
    }

    private class QueuedCommand {
        private final OfflineCommand.ModifyType type;
        private final long amount;
        private final boolean silent;

        QueuedCommand(OfflineCommand.ModifyType type, long amount, boolean silent) {
            this.type = type;
            this.amount = amount;
            this.silent = silent;
        }
    }
}
