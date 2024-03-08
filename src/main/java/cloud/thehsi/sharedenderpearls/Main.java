package cloud.thehsi.sharedenderpearls;

import cloud.thehsi.sharedenderpearls.Commands.Sep;
import cloud.thehsi.sharedenderpearls.Listeners.Throw;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public final class Main extends JavaPlugin {

    private final ToolBelt toolBelt = new ToolBelt(this);
    public Boolean enabled = true;
    public Boolean canStealPearls = false;

    @Override
    public void onEnable() {
        PluginManager pl = getServer().getPluginManager();
        pl.registerEvents(new Throw(this, this), this);

        PluginCommand cmd = getCommand("sharedenderpearls");
        if (cmd != null) {
            Sep sep = new Sep(this, this);
            cmd.setExecutor(sep);
            cmd.setTabCompleter(sep);
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

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }


}
