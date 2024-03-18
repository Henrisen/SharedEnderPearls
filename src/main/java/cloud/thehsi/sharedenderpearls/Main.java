package cloud.thehsi.sharedenderpearls;

import cloud.thehsi.sharedenderpearls.Commands.Sep;
import cloud.thehsi.sharedenderpearls.Crafting.HeadToPearl;
import cloud.thehsi.sharedenderpearls.Listeners.Throw;
import cloud.thehsi.sharedenderpearls.Update.UpdateChecker;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.*;

public final class Main extends JavaPlugin {

    private final ToolBelt toolBelt = new ToolBelt(this);

    public Map<String, Boolean> settings = new LinkedHashMap<>();
    FileConfiguration config = getConfig();

    public String version = "v1.3.0";

    private void loadConfig() {
        for (String key : settings.keySet()) {
            config.addDefault(key, settings.get(key));
        }

        for (String key : settings.keySet()) {
            settings.replace(key, config.getBoolean(key));
        }

        config.options().copyDefaults(true);
        saveConfig();
    }

    private void setConfig() {
        for (String key : settings.keySet()) {
            config.set(key, settings.get(key));
        }

        saveConfig();
    }

    @SuppressWarnings("unused")
    @Override
    public void onEnable() {
        settings.put("canBeCrafted", false);
        settings.put("canStealPearls", false);
        settings.put("cannotStealFromOPs", true);
        settings.put("disableCooldown", false);
        settings.put("disablePearlDamage", true);
        settings.put("disablePostTeleportDamage", false);
        settings.put("disablePostTeleportFallDamage", false);
        settings.put("doSpawnEndermites", false);
        settings.put("dropPearls", false);
        settings.put("enabled", true);
        settings.put("gravity", true);
        settings.put("instant", false);
        settings.put("keepHeadAfterCrafting", false);

        loadConfig();

        PluginManager pl = getServer().getPluginManager();
        pl.registerEvents(new Throw(this, this), this);
        pl.registerEvents(new HeadToPearl(this, this), this);

        PluginCommand cmd = getCommand("sharedenderpearls");
        if (cmd != null) {
            Sep sep = new Sep(this, this);
            cmd.setExecutor(sep);
            cmd.setTabCompleter(sep);
        }

        UpdateChecker updateChecker = new UpdateChecker(version);
        try {
            if (updateChecker.check()) {
                StringBuilder sb = updateChecker.latest();
                TextComponent l = updateChecker.latest_player(sb);
                TextComponent lc = updateChecker.latest_console(sb);
                Bukkit.getConsoleSender().spigot().sendMessage(lc);
                for (OfflinePlayer op : Bukkit.getServer().getOperators()) {
                    if (!op.isOnline()) continue;
                    Player p = op.getPlayer();
                    assert p != null;
                    p.spigot().sendMessage(l);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Bukkit.getScheduler().runTaskTimer(this, () -> Bukkit.getScheduler().runTaskAsynchronously(Main.this, () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                Inventory inv = p.getInventory();
                for (int i = 0; i < inv.getSize(); i++) {
                    ItemStack item = inv.getItem(i);
                    if (item == null) continue;
                    if (item.getItemMeta() == null) continue;
                    if (item.getType() == Material.ENDER_PEARL && item.getItemMeta().getPersistentDataContainer().has(toolBelt.key("bind"))) {
                        ItemMeta itemMeta = item.getItemMeta();
                        assert itemMeta != null;
                        itemMeta.setDisplayName(ChatColor.DARK_AQUA + "Bound Ender Pearl");
                        List<String> lore = new ArrayList<>();
                        lore.add(ChatColor.GOLD + "Ender Pearl bound to: " + item.getItemMeta().getPersistentDataContainer().get(toolBelt.key("bind"), PersistentDataType.STRING));
                        itemMeta.setLore(lore);
                        item.setItemMeta(itemMeta);
                        p.getInventory().setItem(i, item);
                    }
                }
            }
        }), 0, 10);
    }

    @SuppressWarnings("unused")
    @Override
    public void onDisable() {
        setConfig();
    }
}
