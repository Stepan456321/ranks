import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.ChatColor;
import org.bukkit.permissions.PermissionAttachment;

import java.util.HashMap;
import java.util.Map;

public class Main extends JavaPlugin implements Listener {

    private Map<String, PermissionAttachment> playerPermissions = new HashMap<>();
    private FileConfiguration config;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.config = this.getConfig();
        this.getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String rank = this.getPlayerRank(player);
        PermissionAttachment attachment = player.addAttachment(this);
        this.playerPermissions.put(player.getName(), attachment);
        this.setPermissions(player, rank);
    }

    private String getPlayerRank(Player player) {
        String rank = "default";
        for (String key : this.config.getConfigurationSection("ranks").getKeys(false)) {
            if (player.hasPermission("ranks." + key)) {
                rank = key;
            }
        }
        return rank;
    }

    private void setPermissions(Player player, String rank) {
        PermissionAttachment attachment = this.playerPermissions.get(player.getName());
        for (String permission : this.config.getStringList("ranks." + rank + ".permissions")) {
            attachment.setPermission(permission, true);
        }
        player.sendMessage(ChatColor.GREEN + "Вы получили привилегию " + rank);
    }

    @Override
    public void onDisable() {
        for (Map.Entry<String, PermissionAttachment> entry : this.playerPermissions.entrySet()) {
            entry.getValue().remove();
        }
        this.playerPermissions.clear();
    }
}
