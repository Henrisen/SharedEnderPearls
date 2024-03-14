package cloud.thehsi.sharedenderpearls.Update;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.http.HttpRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class UpdateChecker {
    private final String version;
    public UpdateChecker(String version) {
        this.version = version;
    }

    private int parseVersion(String version) {
        version = version.replace("v", "");
        String[] parts = version.split("\\."); // Split the version string by dots
        int result = 0;
        for (String part : parts) {
            result = result * 1000 + Integer.parseInt(part); // Convert each part to integer and combine
        }
        return result;
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
        return parseVersion(this.version) < parseVersion((String) jsonObject.get("tag_name"));
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
