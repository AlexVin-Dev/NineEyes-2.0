package ru.velfan;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import java.io.File;

import java.util.ResourceBundle;

public final class NineEyes extends JavaPlugin implements CommandExecutor {

    private ResourceBundle lang;

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        String langCode = getConfig().getString("language", "en_US");
        File langDir = new File(getDataFolder(), "lang");
        if (!langDir.exists()) {
            langDir.mkdirs();
        }
        saveResource("lang/en_US.properties", false);
        saveResource("lang/ru_RU.properties", false);
        lang = ResourceBundle.getBundle("lang/" + langCode);
        getCommand("nightvision").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("nightvision")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (args.length > 0) {
                    player = getServer().getPlayer(args[0]);
                    if (player == null) {
                        sender.sendMessage(lang.getString("player_not_found"));
                        return true;
                    }
                }
                if (player.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
                    player.removePotionEffect(PotionEffectType.NIGHT_VISION);
                    player.sendMessage(lang.getString("night_vision_disabled"));
                } else {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 6000, 1));
                    player.sendMessage(lang.getString("night_vision_enabled"));
                }
                return true;
            } else {
                sender.sendMessage(lang.getString("command_only_for_players"));
                return true;
            }
        }
        return false;
    }
}
