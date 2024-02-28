package cloud.thehsi.sharedenderpearls.Listeners;

import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;

public class Throw implements Listener {
    public Plugin plugin;
    public List<Player> teleportExclude = new ArrayList<>();

    public Throw(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onThrow(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        try {
            if (Objects.requireNonNull(event.getItem()).getType() == Material.ENDER_PEARL & (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) & p.isSneaking() & !Objects.requireNonNull(p.getInventory().getItemInMainHand().getItemMeta()).getPersistentDataContainer().has(key("bind"))) {
                ItemStack itemInMainHand = p.getInventory().getItemInMainHand();
                itemInMainHand.setAmount(itemInMainHand.getAmount() - 1);
                ItemStack itemStack = new ItemStack(Material.ENDER_PEARL, 1);
                ItemMeta itemMeta = itemStack.getItemMeta();
                assert itemMeta != null;
                itemMeta.getPersistentDataContainer().set(key("bind"), PersistentDataType.STRING, p.getName());
                itemMeta.setDisplayName(ChatColor.DARK_AQUA.toString() + "Bound Ender Pearl");
                List<String> l = new ArrayList<>();
                l.add(ChatColor.GOLD.toString() + "Ender Pearl bound to: " + p.getDisplayName());
                itemMeta.setLore(l);
                itemStack.setItemMeta(itemMeta);
                itemStack.addUnsafeEnchantment(Enchantment.LOYALTY, 1);
                Item i = p.getWorld().dropItem(p.getLocation(), itemStack);
                i.setPickupDelay(0);
                i.setThrower(p.getUniqueId());
                event.setCancelled(true);
            }
        } catch (NullPointerException e) {
            plugin.getLogger().log(Level.INFO, "Shared Ender Pearls encountered Null Pointer exception, Ignore This");
        }
    }

    @EventHandler
    public void onLand(ProjectileHitEvent event) {
        if (event.getEntity().getType() != EntityType.ENDER_PEARL) return;
        EnderPearl ep = (EnderPearl) event.getEntity();
        ItemStack itemStack = ep.getItem();
        if (!(event.getEntity().getShooter() instanceof Player)) return;
        Player p = (Player) event.getEntity().getShooter();
        if (Objects.requireNonNull(itemStack.getItemMeta()).getPersistentDataContainer().has(key("bind"))) {
            Player t = plugin.getServer().getPlayerExact(Objects.requireNonNull(itemStack.getItemMeta().getPersistentDataContainer().get(key("bind"), PersistentDataType.STRING)));
            teleportExclude.add(p);
            if (t == null) {
                itemStack.setAmount(1);
                p.setCooldown(Material.ENDER_PEARL, 5);
                if (p.getGameMode() != GameMode.CREATIVE) p.getInventory().addItem(itemStack);
                return;
            }
            Location l = event.getEntity().getLocation();
            l.setPitch(t.getLocation().getPitch());
            l.setYaw(t.getLocation().getYaw());
            t.setFallDistance(0f);
            t.teleport(l);
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL & teleportExclude.contains(event.getPlayer())) {
            teleportExclude.remove(event.getPlayer());
            event.setCancelled(true);
        }
    }

    private NamespacedKey key(String s_key) {
        return new NamespacedKey(plugin, s_key);
    }
}