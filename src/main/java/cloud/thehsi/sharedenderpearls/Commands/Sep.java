package cloud.thehsi.sharedenderpearls.Commands;

import cloud.thehsi.sharedenderpearls.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Sep implements CommandExecutor, TabCompleter {
    Plugin plugin;
    Main main;

    public Sep(Plugin plugin, Main main) {
        this.plugin = plugin;
        this.main = main;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        List<String> args = new ArrayList<>();
        switch (strings.length) {
            case 1:
                args.add("enabled");
                args.add("canStealPearls");
                break;
            case 2:
                args.add("true");
                args.add("false");
                break;
            default:
                args.add("");
                break;
        }
        return args;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 2) {
            switch (strings[0]) {
                case "canStealPearls":
                    if (Objects.equals(strings[1], "true")) {
                        main.canStealPearls = true;
                    } else if (Objects.equals(strings[1], "false")) {
                        main.canStealPearls = false;
                    }
                    break;
                case "enabled":
                    if (Objects.equals(strings[1], "true")) {
                        main.enabled = true;
                    } else if (Objects.equals(strings[1], "false")) {
                        main.enabled = false;
                    }
                    break;
            }
        }
        return true;
    }
}
