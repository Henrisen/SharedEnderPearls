package cloud.thehsi.sharedenderpearls.Listeners;

import  cloud.thehsi.sharedenderpearls.Main;
import cloud.thehsi.sharedenderpearls.ToolBelt;
import org.bukkit.*;
import org.bukkit.damage.DamageType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.logging.Level;

public class Throw implements Listener {
    public Plugin plugin;
    private final List<Entity> onGround = new ArrayList<>();
    public Main main;
    private final ToolBelt toolBelt;
    public List<Player> teleportExclude = new ArrayList<>();

    public Throw(Plugin plugin, Main main) {
        this.plugin = plugin;
        this.main = main;
        this.toolBelt = new ToolBelt(plugin);

        Bukkit.getScheduler().runTaskTimer(this.plugin, () -> Bukkit.getScheduler().runTask(this.plugin, () -> {
            for (World world : Bukkit.getWorlds()) {
                for (EnderPearl ep : world.getEntitiesByClass(EnderPearl.class)) {
                    onEPMove(ep);
                }
            }
        }), 5, 5);
    }

    @EventHandler
    public void onPlayerClick(EntityDamageByEntityEvent event) {
        if (!main.settings.get("enabled")) return;
        if (!main.settings.get("canStealPearls")) return;
        if (event.getEntity() instanceof Player & event.getDamager() instanceof Player) {
            Player d = (Player) event.getDamager();
            Player p = (Player) event.getEntity();
            if (p.isOp() && main.settings.get("cannotStealFromOPs")) return;
            if (d.getInventory().getItemInMainHand().getItemMeta() == null) return;
            if (
                    d.getInventory().getItemInMainHand().getType() == Material.ENDER_PEARL &
                    !d.getInventory().getItemInMainHand().getItemMeta().getPersistentDataContainer().has(toolBelt.key("bind")) &
                    d.isSneaking()
            ) {
                ItemStack itemInMainHand = d.getInventory().getItemInMainHand();
                itemInMainHand.setAmount(itemInMainHand.getAmount() - 1);
                ItemStack itemStack = new ItemStack(Material.ENDER_PEARL, 1);
                ItemMeta itemMeta = itemStack.getItemMeta();
                assert itemMeta != null;
                itemMeta.getPersistentDataContainer().set(toolBelt.key("bind"), PersistentDataType.STRING, p.getName());
                itemMeta.setDisplayName(ChatColor.DARK_AQUA + "Bound Ender Pearl");
                List<String> l = new ArrayList<>();
                l.add(ChatColor.GOLD + "Ender Pearl bound to: " + p.getDisplayName());
                itemMeta.setLore(l);
                itemStack.setItemMeta(itemMeta);
                itemStack.addUnsafeEnchantment(Enchantment.LOYALTY, 1);
                Item i = d.getWorld().dropItem(d.getLocation(), itemStack);
                i.setPickupDelay(0);
                i.setThrower(d.getUniqueId());
                d.setCooldown(Material.ENDER_PEARL, 0);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onThrow(PlayerInteractEvent event) {
        if (!main.settings.get("enabled")) return;
        Player p = event.getPlayer();
        try {
            if (event.getItem() == null) return;
            if (p.getInventory().getItemInMainHand().getItemMeta() == null) return;
            if (
                    (event.getItem().getType() == Material.ENDER_PEARL) &
                            ((event.getAction() == Action.RIGHT_CLICK_AIR) || (event.getAction() == Action.RIGHT_CLICK_BLOCK)) &
                            p.getInventory().getItemInMainHand().getItemMeta().getPersistentDataContainer().has(toolBelt.key("bind")) &
                            main.settings.get("disableCooldown")
            ) {
                p.setCooldown(Material.ENDER_PEARL, 0);
            }
            if (
                    (event.getItem().getType() == Material.ENDER_PEARL) &
                            ((event.getAction() == Action.RIGHT_CLICK_AIR) || (event.getAction() == Action.RIGHT_CLICK_BLOCK)) &
                            p.isSneaking() &
                            !p.getInventory().getItemInMainHand().getItemMeta().getPersistentDataContainer().has(toolBelt.key("bind"))
            ) {
                ItemStack itemInMainHand = p.getInventory().getItemInMainHand();
                itemInMainHand.setAmount(itemInMainHand.getAmount() - 1);
                ItemStack itemStack = new ItemStack(Material.ENDER_PEARL, 1);
                ItemMeta itemMeta = itemStack.getItemMeta();
                assert itemMeta != null;
                itemMeta.getPersistentDataContainer().set(toolBelt.key("bind"), PersistentDataType.STRING, p.getName());
                itemMeta.setDisplayName(ChatColor.DARK_AQUA + "Bound Ender Pearl");
                List<String> l = new ArrayList<>();
                l.add(ChatColor.GOLD + "Ender Pearl bound to: " + p.getDisplayName());
                itemMeta.setLore(l);
                itemStack.setItemMeta(itemMeta);
                itemStack.addUnsafeEnchantment(Enchantment.LOYALTY, 1);
                if (!main.settings.get("dropPearls")) p.getInventory().addItem(itemStack);
                else {
                    Item i = p.getWorld().dropItem(p.getLocation(), itemStack);
                    i.setPickupDelay(0);
                    i.setThrower(p.getUniqueId());
                }
                p.setCooldown(Material.ENDER_PEARL, 0);
                event.setCancelled(true);
            }else if (
                    (event.getItem().getType() == Material.ENDER_PEARL) &
                            ((event.getAction() == Action.RIGHT_CLICK_AIR) || (event.getAction() == Action.RIGHT_CLICK_BLOCK)) &
                            p.getInventory().getItemInMainHand().getItemMeta().getPersistentDataContainer().has(toolBelt.key("bind"))
            ) {
                if (main.settings.get("doSpawnEndermites")) p.setCooldown(Material.ENDER_PEARL, 0);
            }
        } catch (NullPointerException e) {
            plugin.getLogger().log(Level.WARNING, e.getLocalizedMessage());
            plugin.getLogger().log(Level.INFO, "Shared Ender Pearls encountered Null Pointer exception, Ignore This");
        }
    }

    @EventHandler
    public void onLand(ProjectileHitEvent event) {
        if (!main.settings.get("enabled")) return;
        if (event.getEntity().getType() != EntityType.ENDER_PEARL) return;
        EnderPearl ep = (EnderPearl) event.getEntity();
        ItemStack itemStack = ep.getItem();
        if (!(ep.getShooter() instanceof Player p)) return;
        if (Objects.requireNonNull(itemStack.getItemMeta()).getPersistentDataContainer().has(toolBelt.key("bind"))) {
            Player t = plugin.getServer().getPlayerExact(Objects.requireNonNull(itemStack.getItemMeta().getPersistentDataContainer().get(toolBelt.key("bind"), PersistentDataType.STRING)));
            teleportExclude.add(p);
            if (t == null & p.isOnline()) {
                itemStack.setAmount(1);
                p.setCooldown(Material.ENDER_PEARL, 5);
                playerOfflineError(p, itemStack.getItemMeta().getPersistentDataContainer().get(toolBelt.key("bind"),PersistentDataType.STRING));
                if (p.getGameMode() != GameMode.CREATIVE) p.getInventory().addItem(itemStack);
                return;
            } else if (t == null) {
                itemStack.setAmount(1);
                ep.getWorld().dropItem(ep.getLocation(), itemStack);
                return;
            }
            Location l = event.getEntity().getLocation();
            l.setPitch(t.getLocation().getPitch());
            l.setYaw(t.getLocation().getYaw());
            t.setFallDistance(0f);
            t.teleport(l);
            if (main.settings.get("doSpawnEndermites") && l.getWorld() != null) {
                Random random = new Random();
                if (random.nextInt(10) == 0) {
                    l.getWorld().spawnEntity(l, EntityType.ENDERMITE);
                }
            }
            if (!main.settings.get("disablePearlDamage")) //noinspection UnstableApiUsage
                t.damage(5.0, org.bukkit.damage.DamageSource.builder(DamageType.FALL).build());
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        if (!main.settings.get("enabled")) return;
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL & teleportExclude.contains(event.getPlayer())) {
            teleportExclude.remove(event.getPlayer());
            event.setCancelled(true);
        }
        // Cancel Post TP Fall Damage
        if (main.settings.get("disablePostTeleportFallDamage")) {
            if (event.getCause() != PlayerTeleportEvent.TeleportCause.ENDER_PEARL) return;
            for (Entity e : event.getPlayer().getWorld().getNearbyEntities(event.getPlayer().getLocation(), 1, 1, 1, (entity) -> entity.getType() == EntityType.ENDER_PEARL)) {
                EnderPearl enderPearl = (EnderPearl) e;
                if (enderPearl.getItem().getItemMeta() == null) return;
                if (enderPearl.getItem().getItemMeta().getPersistentDataContainer().has(toolBelt.key("bind"))) {
                    event.getPlayer().setMetadata("enderPearlFalling", new FixedMetadataValue(plugin, true));
                    break;
                }
            }
        }

        // Cancel All Post TP Damage
        if (main.settings.get("disablePostTeleportDamage")) {
            if (event.getCause() != PlayerTeleportEvent.TeleportCause.ENDER_PEARL) return;
            for (Entity e : event.getPlayer().getWorld().getNearbyEntities(event.getPlayer().getLocation(), 1, 1, 1, (entity) -> entity.getType() == EntityType.ENDER_PEARL)) {
                EnderPearl enderPearl = (EnderPearl) e;
                if (enderPearl.getItem().getItemMeta() == null) return;
                if (enderPearl.getItem().getItemMeta().getPersistentDataContainer().has(toolBelt.key("bind"))) {
                    event.getPlayer().setNoDamageTicks(20 * 5);
                    break;
                }
            }
        }
    }

    @EventHandler
    public void onEPThrow(ProjectileLaunchEvent event) {
        if (!main.settings.get("enabled")) return;
        if (event.getEntity().getType() != EntityType.ENDER_PEARL) return;
        EnderPearl ep = (EnderPearl) event.getEntity();
        if (!(ep.getShooter() instanceof Player)) return;
        if (main.settings.get("instant")) {
            RayTraceResult r =((Player) ep.getShooter()).rayTraceBlocks(99, FluidCollisionMode.NEVER);
            if (r != null) {
                Location l = Objects.requireNonNull(r.getHitBlock()).getLocation();
                ep.teleport(l);
            }
        }
        if (!main.settings.get("gravity")) ep.setGravity(false);
        ep.setMetadata("velocity_x", new FixedMetadataValue(this.plugin, ep.getVelocity().getX()));
        ep.setMetadata("velocity_y", new FixedMetadataValue(this.plugin, ep.getVelocity().getY()));
        ep.setMetadata("velocity_z", new FixedMetadataValue(this.plugin, ep.getVelocity().getZ()));
        ItemStack itemStack = ep.getItem();
        if (!(event.getEntity().getShooter() instanceof Player p)) return;
        if (Objects.requireNonNull(itemStack.getItemMeta()).getPersistentDataContainer().has(toolBelt.key("bind"))) {
            Player t = plugin.getServer().getPlayerExact(Objects.requireNonNull(itemStack.getItemMeta().getPersistentDataContainer().get(toolBelt.key("bind"), PersistentDataType.STRING)));
            if (t == null & !p.isSneaking()) {
                p.setCooldown(Material.ENDER_PEARL, 5);
                event.getEntity().remove();
                playerOfflineError(p, itemStack.getItemMeta().getPersistentDataContainer().get(toolBelt.key("bind"),PersistentDataType.STRING));
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    private void inventoryOpen(InventoryOpenEvent event) {
        if (!main.settings.get("enabled")) return;
        Inventory inv = event.getInventory();
        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack item = inv.getItem(i);
            if (item == null) continue;
            if (item.getItemMeta() == null) continue;
            if (
                    item.getType() == Material.ENDER_PEARL &
                    item.getItemMeta().getPersistentDataContainer().has(toolBelt.key("bind"))
            ) {
                ItemMeta itemMeta = item.getItemMeta();
                assert itemMeta != null;
                itemMeta.setDisplayName(ChatColor.DARK_AQUA + "Bound Ender Pearl");
                List<String> l = new ArrayList<>();
                l.add(ChatColor.GOLD + "Ender Pearl bound to: " + item.getItemMeta().getPersistentDataContainer().get(toolBelt.key("bind"), PersistentDataType.STRING));
                itemMeta.setLore(l);
                item.setItemMeta(itemMeta);
                inv.setItem(i, item);
            }
        }
    }

    @EventHandler
    private void itemPickup(EntityPickupItemEvent event) {
        if (!main.settings.get("enabled")) return;
        if (!(event.getEntity() instanceof Player p)) return;
        Inventory inv = p.getInventory();
        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack item = inv.getItem(i);
            if (item == null) continue;
            if (item.getItemMeta() == null) continue;
            if (
                    item.getType() == Material.ENDER_PEARL &
                    item.getItemMeta().getPersistentDataContainer().has(toolBelt.key("bind"))
            ) {
                ItemMeta itemMeta = item.getItemMeta();
                assert itemMeta != null;
                itemMeta.setDisplayName(ChatColor.DARK_AQUA + "Bound Ender Pearl");
                List<String> l = new ArrayList<>();
                l.add(ChatColor.GOLD + "Ender Pearl bound to: " + item.getItemMeta().getPersistentDataContainer().get(toolBelt.key("bind"), PersistentDataType.STRING));
                itemMeta.setLore(l);
                item.setItemMeta(itemMeta);
                p.getInventory().setItem(i, item);
            }
        }
    }


    private void playerOfflineError(Player player, String target) {
        if (!main.settings.get("enabled")) return;
        String msg = ChatColor.DARK_AQUA.toString() +
                ChatColor.BOLD +
                "[SEP] " +
                ChatColor.RESET +
                ChatColor.RED +
                "Player [" +
                ChatColor.BOLD +
                target +
                ChatColor.RESET +
                ChatColor.RED +
                "] is not online!";
        player.playSound(player, Sound.ENTITY_ENDER_EYE_DEATH, SoundCategory.PLAYERS, 1, 1);
        player.sendMessage(msg);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!main.settings.get("enabled")) return;
        if (event.getEntity() instanceof Player player && event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            if (player.hasMetadata("enderPearlFalling") && player.getMetadata("enderPearlFalling").get(0).asBoolean()) {
                event.setCancelled(true);
                player.removeMetadata("enderPearlFalling", plugin);
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!main.settings.get("enabled")) return;
        Entity e = event.getPlayer();
        if (e.getFallDistance() == 0 && e.isOnGround() && e.getLocation().getBlock().getType() == Material.AIR) {
            if (onGround.contains(e)) {
                e.removeMetadata("enderPearlFalling", plugin);
                return;
            }
            onGround.add(e);
            return;
        }
        onGround.remove(e);
    }

    private void onEPMove(EnderPearl ep) {
        if (!main.settings.get("enabled")) return;
        Vector v = new Vector();
        if (!ep.hasMetadata("velocity_x")) return;
        if (!ep.hasMetadata("velocity_y")) return;
        if (!ep.hasMetadata("velocity_z")) return;
        if (ep.getMetadata("velocity_x").size() == 0) return;
        if (ep.getMetadata("velocity_y").size() == 0) return;
        if (ep.getMetadata("velocity_z").size() == 0) return;
        v.setX(ep.getMetadata("velocity_x").get(0).asDouble());
        v.setY(ep.getMetadata("velocity_y").get(0).asDouble());
        v.setZ(ep.getMetadata("velocity_z").get(0).asDouble());
        if (!main.settings.get("gravity")) ep.setVelocity(v);
        if (ep.getLocation().getY() > 1000) {
            if (main.settings.get("gravity")) return;
            ep.remove();
        }
    }

}