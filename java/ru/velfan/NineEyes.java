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
        saveResource("lang/en_US.properties", true);
        saveResource("lang/ru_RU.properties", true);
        lang = ResourceBundle.getBundle("lang/" + langCode);
        getCommand("nightvision").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!cmd.getName().equalsIgnoreCase("nightvision")) {
            return false;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(lang.getString("command_only_for_players"));
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("nineeyes.nightvision")) {
            player.sendMessage(lang.getString("no_permission"));
            return true;
        }

        if (args.length > 0) {
            Player targetPlayer = getServer().getPlayer(args[0]);
            if (targetPlayer == null) {
                sender.sendMessage(lang.getString("player_not_found"));
                return true;
            }
            player = targetPlayer;
        }

        if (player.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
            player.removePotionEffect(PotionEffectType.NIGHT_VISION);
            player.sendMessage(lang.getString("night_vision_disabled"));
        } else {
            int nightVisionDuration = getConfig().getInt("night_vision_duration", 6000);
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, nightVisionDuration, 1));
            player.sendMessage(lang.getString("night_vision_enabled"));
        }

        return true;
    }
}
