package cloud.thehsi.sharedenderpearls;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;

public class ToolBelt {
    private final Plugin plugin;

    public ToolBelt(Plugin plugin) {
        this.plugin = plugin;
    }

    public NamespacedKey key(@SuppressWarnings("SameParameterValue") String s_key) {
        assert plugin != null;
        return new NamespacedKey(plugin, s_key);
    }

    public TextComponent sendAsPlugin(String text) {
        String msg = ChatColor.DARK_AQUA.toString() +
                ChatColor.BOLD +
                "[SEP] " +
                ChatColor.RESET +
                text +
                ChatColor.RESET;
        return new TextComponent(msg);
    }
}
