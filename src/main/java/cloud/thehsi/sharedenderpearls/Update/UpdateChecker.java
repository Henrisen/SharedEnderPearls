package cloud.thehsi.sharedenderpearls.Update;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateChecker {
    private final String version;
    public UpdateChecker(String version) {
        this.version = version;
    }

    public static int[] parseVersion(String version) {
        int[] result = new int[3];
        String[] parts = version.substring(1).split("\\.");

        for (int i = 0; i < parts.length && i < 3; i++) {
            result[i] = Integer.parseInt(parts[i]);
        }

        return result;
    }

    public static boolean compareVersions(String version1, String version2) {
        int[] v1 = parseVersion(version1);
        int[] v2 = parseVersion(version2);

        // Compare major versions
        if (v1[0] > v2[0])
            return true;
        else if (v1[0] < v2[0])
            return false;

        // Compare minor versions
        if (v1[1] > v2[1])
            return true;
        else if (v1[1] < v2[1])
            return false;

        // Compare patch versions
        return v1[2] > v2[2];
    }

    public Boolean check() throws IOException {
        URL gh_api = new URL("https://api.github.com/repos/Henrisen/SharedEnderPearls/releases");
        HttpURLConnection connection = (HttpURLConnection) gh_api.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        connection.connect();
        if (connection.getResponseCode() != 200) return Boolean.FALSE;
        JSONArray jsonArray;
        BufferedReader br = new BufferedReader(new InputStreamReader((connection.getInputStream())));
        StringBuilder sb = new StringBuilder();
        String output;
        while ((output = br.readLine()) != null) {
            sb.append(output);
        }
        try {
            jsonArray = (JSONArray) new JSONParser().parse(sb.toString());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        JSONObject jsonObject = (JSONObject) jsonArray.get(0);
        Boolean old = compareVersions((String) jsonObject.get("tag_name"), this.version);
        if (!old && compareVersions(this.version, (String) jsonObject.get("tag_name"))) {
            String msg1 = ChatColor.DARK_AQUA.toString() +
                    ChatColor.BOLD +
                    "[SEP] " +
                    ChatColor.RESET +
                    ChatColor.GOLD +
                    "SharedEnderPearls [" +
                    ChatColor.BOLD +
                    this.version +
                    ChatColor.RESET +
                    ChatColor.GOLD +
                    "] is a dev build!" +
                    ChatColor.RESET;
            String msg2 = ChatColor.DARK_AQUA.toString() +
                    ChatColor.BOLD +
                    "[SEP] " +
                    ChatColor.RESET +
                    ChatColor.RED +
                    ChatColor.BOLD +
                    "Please do NOT Share the .jar file!" +
                    ChatColor.RESET;
            String msg3 = ChatColor.DARK_AQUA.toString() +
                    ChatColor.BOLD +
                    "[SEP] " +
                    ChatColor.RESET +
                    ChatColor.RED +
                    "DO NOT USE FOR PRODUCTION" +
                    ChatColor.RESET;
            TextComponent master = new TextComponent("");
            master.addExtra("\n");
            master.addExtra(msg1);
            master.addExtra("\n");
            master.addExtra(msg2);
            master.addExtra("\n");
            master.addExtra(msg3);
            master.addExtra("\n");
            Bukkit.getConsoleSender().spigot().sendMessage(master);
            for (OfflinePlayer op : Bukkit.getOperators()) {
                if (!op.isOnline()) continue;
                Player p = op.getPlayer();
                assert p != null;
                p.spigot().sendMessage(master);
            }
        }
        return old;
    }

    public StringBuilder latest() throws IOException {
        URL gh_api = new URL("https://api.github.com/repos/Henrisen/SharedEnderPearls/releases/latest");
        HttpURLConnection connection = (HttpURLConnection) gh_api.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        connection.connect();
        if (connection.getResponseCode() != 200) return new StringBuilder("NO INET");
        BufferedReader br = new BufferedReader(new InputStreamReader((connection.getInputStream())));
        StringBuilder sb = new StringBuilder();
        String output;
        while ((output = br.readLine()) != null) {
            sb.append(output);
        }
        return sb;
    }
    public TextComponent latest_player(StringBuilder sb) {
        JSONObject jsonObject;
        try {
            jsonObject = (JSONObject) new JSONParser().parse(sb.toString());
        } catch (ParseException e) {
            String msg1 = ChatColor.DARK_AQUA.toString() +
                    ChatColor.BOLD +
                    "[SEP] " +
                    ChatColor.RESET +
                    ChatColor.RED +
                    "Something went Wrong!" +
                    ChatColor.DARK_AQUA +
                    ChatColor.BOLD +
                    "[SEP] " +
                    ChatColor.RESET +
                    ChatColor.RED +
                    "Check Your Servers Internet!" +
                    ChatColor.RESET;
            return new TextComponent(msg1);
        }
        String ver = (String) jsonObject.get("tag_name");
        String url = (String) jsonObject.get("html_url");
        String msg1 = ChatColor.DARK_AQUA.toString() +
                ChatColor.BOLD +
                "[SEP] " +
                ChatColor.RESET +
                ChatColor.RED +
                "SharedEnderPearls [" +
                ChatColor.BOLD +
                this.version +
                ChatColor.RESET +
                ChatColor.RED +
                "] is out-of-date!" +
                ChatColor.RESET;
        String msg2 = ChatColor.DARK_AQUA.toString() +
                ChatColor.BOLD +
                "[SEP] " +
                ChatColor.RESET +
                ChatColor.RED +
                "Please Update to newest Version [" +
                ChatColor.BOLD +
                ver +
                ChatColor.RESET +
                ChatColor.RED +
                "] now!" +
                ChatColor.RESET;
        String msg3 = ChatColor.DARK_AQUA.toString() +
                ChatColor.BOLD +
                "[SEP] " +
                ChatColor.RESET +
                ChatColor.RED +
                "Download ";
        TextComponent master = new TextComponent("");
        master.addExtra("\n");
        master.addExtra(msg1);
        master.addExtra("\n");
        master.addExtra(msg2);
        master.addExtra("\n");
        master.addExtra(msg3);
        TextComponent message = new TextComponent( ChatColor.YELLOW.toString() + ChatColor.BOLD + "HERE" );
        message.setClickEvent( new ClickEvent( ClickEvent.Action.OPEN_URL, url ) );
        master.addExtra(message);
        master.addExtra("\n");
        return master;
    }

    public TextComponent latest_console(StringBuilder sb) {
        JSONObject jsonObject;
        try {
            jsonObject = (JSONObject) new JSONParser().parse(sb.toString());
        } catch (ParseException e) {
            String msg1 = ChatColor.DARK_AQUA.toString() +
                    ChatColor.BOLD +
                    "[SEP] " +
                    ChatColor.RESET +
                    ChatColor.RED +
                    "Something went Wrong!" +
                    ChatColor.DARK_AQUA +
                    ChatColor.BOLD +
                    "[SEP] " +
                    ChatColor.RESET +
                    ChatColor.RED +
                    "Check Your Servers Internet!" +
                    ChatColor.RESET;
            return new TextComponent(msg1);
        }
        String ver = (String) jsonObject.get("tag_name");
        String url = (String) jsonObject.get("html_url");
        return getTextComponent(ver, url);
    }

    private TextComponent getTextComponent(String ver, String url) {
        String msg1 = ChatColor.DARK_AQUA.toString() +
                ChatColor.BOLD +
                "[SEP] " +
                ChatColor.RESET +
                ChatColor.RED +
                "SharedEnderPearls [" +
                ChatColor.BOLD +
                this.version +
                ChatColor.RESET +
                ChatColor.RED +
                "] is out-of-date!" +
                ChatColor.RESET;
        String msg2 = ChatColor.DARK_AQUA.toString() +
                ChatColor.BOLD +
                "[SEP] " +
                ChatColor.RESET +
                ChatColor.RED +
                "Please Update to newest Version [" +
                ChatColor.BOLD +
                ver +
                ChatColor.RESET +
                ChatColor.RED +
                "] now!" +
                ChatColor.RESET;
        return getTextComponent(url, msg1, msg2);
    }

    private static TextComponent getTextComponent(String url, String msg1, String msg2) {
        String msg3 = ChatColor.DARK_AQUA.toString() +
                ChatColor.BOLD +
                "[SEP] " +
                ChatColor.RESET +
                ChatColor.RED +
                "Download at " +
                ChatColor.BOLD +
                url;
        TextComponent master = new TextComponent("");
        master.addExtra("\n");
        master.addExtra(msg1);
        master.addExtra("\n");
        master.addExtra(msg2);
        master.addExtra("\n");
        master.addExtra(msg3);
        master.addExtra("\n");
        return master;
    }
}
