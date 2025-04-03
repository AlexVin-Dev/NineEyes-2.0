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
        saveDefaultConfig();
        String langCode = getConfig().getString("language", "en_US");
        File langDir = new File(getDataFolder(), "lang");
        if (!langDir.exists()) langDir.mkdirs();

        saveResource("lang/en_US.properties", true);
        saveResource("lang/ru_RU.properties", true);
        lang = ResourceBundle.getBundle("lang/" + langCode);

        getCommand("nightvision").setExecutor(this);

        getLogger().info("NineEyes plugin enabled!"); // Стартовая запись в лог
    }

    @Override
    public void onDisable() {
        getLogger().info("NineEyes plugin disabled!"); // Финализационная запись в лог
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        try {
            if (!cmd.getName().equalsIgnoreCase("nightvision")) return false;

            if (!(sender instanceof Player)) {
                sender.sendMessage(lang.getString("command_only_for_players"));
                return true;
            }

            Player player = (Player) sender;

            if (!player.hasPermission("nineeyes.nightvision")) {
                player.sendMessage(lang.getString("no_permission"));
                return true;
            }

            Player target = player;
            if (args.length > 0) {
                target = getServer().getPlayer(args[0]);
                if (target == null) {
                    sender.sendMessage(lang.getString("player_not_found"));
                    return true;
                }
            }

            boolean isSelf = target.equals(player);
            String effectAction;

            if (target.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
                target.removePotionEffect(PotionEffectType.NIGHT_VISION);
                effectAction = "disabled";

                if (isSelf) {
                    player.sendMessage(lang.getString("night_vision_disabled"));
                } else {
                    player.sendMessage(lang.getString("night_vision_disabled_other").replace("%player%", target.getName()));
                }
            } else {
                int duration = getConfig().getInt("night_vision_duration", 6000);
                target.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, duration, 1));
                effectAction = "enabled";

                if (isSelf) {
                    player.sendMessage(lang.getString("night_vision_enabled"));
                } else {
                    player.sendMessage(lang.getString("night_vision_enabled_other").replace("%player%", target.getName()));
                }
            }

            // Логирование действия
            String logMessage = String.format(
                    "[NightVision] %s %s night vision for %s",
                    player.getName(),
                    effectAction,
                    isSelf ? "themselves" : target.getName()
            );
            getLogger().info(logMessage);

            return true;

        } catch (Exception e) {
            getLogger().severe("Error executing command: " + e.getMessage()); // Логирование ошибок
            e.printStackTrace();
            return false;
        }
    }
}