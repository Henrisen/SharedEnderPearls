package cloud.thehsi.sharedenderpearls;

import cloud.thehsi.sharedenderpearls.Listeners.Throw;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        PluginManager pl = getServer().getPluginManager();

        pl.registerEvents(new Throw(this), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
