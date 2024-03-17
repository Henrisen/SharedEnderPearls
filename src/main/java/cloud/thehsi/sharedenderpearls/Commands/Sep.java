package cloud.thehsi.sharedenderpearls.Commands;

import cloud.thehsi.sharedenderpearls.Main;
import cloud.thehsi.sharedenderpearls.ToolBelt;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
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
        List<String> options = new ArrayList<>();
        options.add("enabled");
        options.add("canStealPearls");
        options.add("disablePostTeleportFallDamage");
        switch (strings.length) {
            case 1:
                args.addAll(options);
                break;
            case 2:
                if (options.contains(strings[0])) {
                    args.add("true");
                    args.add("false");
                }
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
            ToolBelt tb = new ToolBelt(plugin);
            TextComponent msg;
            switch (strings[0]) {
                case "canStealPearls":
                    if (Objects.equals(strings[1], "true")) {
                        main.settings.replace("canStealPearls", true);
                    } else if (Objects.equals(strings[1], "false")) {
                        main.settings.replace("canStealPearls", false);
                    } else {
                        msg = tb.sendAsPlugin(ChatColor.RED + "Cannot set 'canStealPearls' to " + strings[1]);
                        commandSender.spigot().sendMessage(msg);
                        break;
                    }
                    msg = tb.sendAsPlugin("Rule 'enabled' is now " + main.settings.get("canStealPearls"));
                    commandSender.spigot().sendMessage(msg);
                    break;
                case "enabled":
                    if (Objects.equals(strings[1], "true")) {
                        main.settings.replace("enabled", true);
                    } else if (Objects.equals(strings[1], "false")) {
                        main.settings.replace("enabled", false);
                    } else {
                        msg = tb.sendAsPlugin(ChatColor.RED + "Cannot set 'enabled' to " + strings[1]);
                        commandSender.spigot().sendMessage(msg);
                        break;
                    }
                    msg = tb.sendAsPlugin("Rule 'enabled' is now " + main.settings.get("enabled"));
                    commandSender.spigot().sendMessage(msg);
                    break;
                case "disablePostTeleportFallDamage":
                    if (Objects.equals(strings[1], "true")) {
                        main.settings.replace("disablePostTeleportFallDamage", true);
                    } else if (Objects.equals(strings[1], "false")) {
                        main.settings.replace("disablePostTeleportFallDamage", false);
                    } else {
                        msg = tb.sendAsPlugin(ChatColor.RED + "Cannot set 'disablePostTeleportFallDamage' to " + strings[1]);
                        commandSender.spigot().sendMessage(msg);
                        break;
                    }
                    msg = tb.sendAsPlugin("Rule 'disablePostTeleportFallDamage' is now " + main.settings.get("disablePostTeleportFallDamage"));
                    commandSender.spigot().sendMessage(msg);
                    break;
                default:
                    msg = tb.sendAsPlugin(ChatColor.RED + "Rule '" + strings[0] + "' not found");
                    commandSender.spigot().sendMessage(msg);
            }
        }else if (strings.length == 1) {
            ToolBelt tb = new ToolBelt(plugin);
            TextComponent msg;
            switch (strings[0]) {
                case "canStealPearls":
                    msg = tb.sendAsPlugin("Value of 'canStealPearls' is " + main.settings.get("canStealPearls"));
                    commandSender.spigot().sendMessage(msg);
                    break;
                case "enabled":
                    msg = tb.sendAsPlugin("Value of 'enabled' is " + main.settings.get("enabled"));
                    commandSender.spigot().sendMessage(msg);
                    break;
                case "disablePostTeleportFallDamage":
                    msg = tb.sendAsPlugin("Value of 'disablePostTeleportFallDamage' is " + main.settings.get("disablePostTeleportFallDamage"));
                    commandSender.spigot().sendMessage(msg);
                    break;
                default:
                    msg = tb.sendAsPlugin(ChatColor.RED + "Rule '" + strings[0] + "' not found");
                    commandSender.spigot().sendMessage(msg);
            }
        }else {
            commandSender.sendMessage(ChatColor.RED + "Usage: /sep <rule> <value>");
        }
        return true;
    }
}
