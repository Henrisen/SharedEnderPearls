package cloud.thehsi.sharedenderpearls.Crafting;

import cloud.thehsi.sharedenderpearls.Main;
import cloud.thehsi.sharedenderpearls.ToolBelt;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class HeadToPearl implements Listener {
    Plugin plugin;
    Main main;
    ToolBelt toolBelt;
    ShapedRecipe recipe;

    public HeadToPearl(Plugin plugin, Main main) {
        this.plugin = plugin;
        this.main = main;
        this.toolBelt = new ToolBelt(plugin);
        ItemStack customItem = new ItemStack(Material.ENDER_PEARL, 4); // Change this to your desired item

        NamespacedKey key = new NamespacedKey(this.plugin, "head_to_pearl");

        // Create a new ShapedRecipe with the custom item
        recipe = new ShapedRecipe(key, customItem);

        // Define the recipe pattern (shape) and ingredients
        recipe.shape("0P0", "PHP", "0P0");
        recipe.setIngredient('0', Material.AIR);
        recipe.setIngredient('H', Material.PLAYER_HEAD);
        recipe.setIngredient('P', Material.ENDER_PEARL);

        // Add the recipe to the server
        if (main.settings.get("canBeCrafted")) this.plugin.getServer().addRecipe(recipe);
        Bukkit.getScheduler().runTaskTimer(this.plugin, () -> Bukkit.getScheduler().runTask(this.plugin, () -> {
            if (!main.settings.get("canBeCrafted")) this.plugin.getServer().removeRecipe(key);
            if (this.plugin.getServer().getRecipe(key) != null) return;
            if (main.settings.get("canBeCrafted")) this.plugin.getServer().addRecipe(recipe);
        }), 5, 5);
    }

    @EventHandler
    public void onCraft(CraftItemEvent event) {
        if ((((ShapedRecipe) event.getRecipe()).getKey().getKey().equals("head_to_pearl"))) {
            for (ItemStack item : event.getInventory().getMatrix()) {
                if (item == null) continue;
                if (item.getType() != Material.PLAYER_HEAD) continue;
                SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
                if (skullMeta == null) continue;
                if (skullMeta.getOwningPlayer() == null) continue;
                if (skullMeta.getOwningPlayer().getPlayer() == null) continue;
                if (event.getCurrentItem() == null) continue;
                ItemMeta itemMeta = event.getCurrentItem().getItemMeta();
                ItemStack itemStack = new ItemStack(Material.ENDER_PEARL);
                if (itemMeta == null) continue;
                itemMeta.getPersistentDataContainer().set(toolBelt.key("bind"), PersistentDataType.STRING, skullMeta.getOwningPlayer().getPlayer().getName());
                itemMeta.setDisplayName(ChatColor.DARK_AQUA + "Bound Ender Pearl");
                List<String> l = new ArrayList<>();
                l.add(ChatColor.GOLD + "Ender Pearl bound to: " + skullMeta.getOwningPlayer().getPlayer().getDisplayName());
                itemMeta.setLore(l);
                itemStack.setItemMeta(itemMeta);
                itemStack.addUnsafeEnchantment(Enchantment.LOYALTY, 1);
                itemStack.setAmount(4);
                event.setCurrentItem(itemStack);
                if (main.settings.get("keepHeadAfterCrafting")) item.setAmount(item.getAmount()+1);
            }
        }
    }
}
