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

import java.util.*;

public class Sep implements CommandExecutor, TabCompleter {
    Plugin plugin;
    Main main;
    List<String> options;

    public Sep(Plugin plugin, Main main) {
        this.plugin = plugin;
        this.main = main;
        options = new ArrayList<>(main.settings.keySet());
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        List<String> args = new ArrayList<>();
        switch (strings.length) {
            case 1 -> {
                if (main.settings.get("enabled")) {
                    args.add("*");
                    args.addAll(options);
                }else {
                    args.add("enabled");
                }
            }
            case 2 -> {
                if (options.contains(strings[0])) {
                    args.add("true");
                    args.add("false");
                }
            }
            default -> args.add("");
        }
        return args;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        ToolBelt tb = new ToolBelt(plugin);
        TextComponent msg;
        switch (strings.length) {
            case 2 -> {
                if (options.contains(strings[0])) {
                    if (Objects.equals(strings[1], "true")) {
                        main.settings.replace(strings[0], true);
                    } else if (Objects.equals(strings[1], "false")) {
                        main.settings.replace(strings[0], false);
                    } else {
                        msg = tb.sendAsPlugin(ChatColor.RED + "Cannot set '" + strings[0] + "' to " + strings[1]);
                        commandSender.spigot().sendMessage(msg);
                        return false;
                    }
                    msg = tb.sendAsPlugin("Rule '" + strings[0] + "' is now " + main.settings.get(strings[0]));
                    commandSender.spigot().sendMessage(msg);
                    return true;
                } else {
                    msg = tb.sendAsPlugin(ChatColor.RED + "Rule '" + strings[0] + "' not found");
                    commandSender.spigot().sendMessage(msg);
                    return false;
                }
            }
            case 1 -> {
                if (options.contains(strings[0])) {
                    msg = tb.sendAsPlugin(ChatColor.YELLOW + strings[0] + ChatColor.WHITE + ": " + (main.settings.get(strings[0]) ? ChatColor.GREEN : ChatColor.RED) + main.settings.get(strings[0]));
                    commandSender.spigot().sendMessage(msg);
                    return true;
                } else if (Objects.equals(strings[0], "*")) {
                    msg = tb.sendAsPlugin("All Rules:");
                    commandSender.spigot().sendMessage(msg);
                    for (Map.Entry<String, Boolean> rule : main.settings.entrySet()) {
                        msg = tb.sendAsPlugin(ChatColor.YELLOW + rule.getKey() + ChatColor.WHITE + ": " + (rule.getValue() ? ChatColor.GREEN : ChatColor.RED) + rule.getValue());
                        commandSender.spigot().sendMessage(msg);
                    }
                }else {
                    msg = tb.sendAsPlugin(ChatColor.RED + "Rule '" + strings[0] + "' not found");
                    commandSender.spigot().sendMessage(msg);
                    return false;
                }
            }
            default -> commandSender.sendMessage(ChatColor.RED + "Usage: /sep <rule> [value]");
        }
        return false;
    }
}
