package cloud.thehsi.sharedenderpearls;

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
}
