package com.adrianwowk.wincommand;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class SpigotPlugin extends JavaPlugin implements TabExecutor {
    Server server;
    ConsoleCommandSender console;

    public SpigotPlugin() {
        this.server = Bukkit.getServer();
        this.console = this.server.getConsoleSender();
    }

    public void onEnable() {

        saveDefaultConfig();

        // Register command tab completer and executer

        getCommand("win").setTabCompleter(this);
        getCommand("win").setExecutor(this);

        getCommand("winreload").setExecutor(this);

        // Server Console Message
        console.sendMessage(getPrefix() + ChatColor.YELLOW + "Plugin Successfully Enabled");
    }

    public void onDisable() {
        console.sendMessage(getPrefix() + ChatColor.YELLOW + "Plugin Successfully Disabled");
    }

    public String getPrefix() {
        return translate("prefix");
    }

    public String translate(String path) {
        return ChatColor.translateAlternateColorCodes('&', this.getConfig().getString(path));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {


        if (command.getName().equalsIgnoreCase("win")) {
            if (!sender.hasPermission("wincommand.command")) {
                sender.sendMessage(getPrefix() + ChatColor.RED + "You do not have permission to use this command");
                return true;
            }

            if (args.length != 1) {
                sender.sendMessage(getPrefix() + "Invalid Command. Usage: /win <nick>");
                return true;
            } else {
                Player player = Bukkit.getPlayerExact(args[0]);

                if (player == null) {
                    sender.sendMessage(getPrefix() + "Target " + ChatColor.AQUA + args[0] + ChatColor.YELLOW + " was not found.");
                    return true;
                    // target not found
                } else {

                    player.sendTitle(translate("title"),
                            translate("winner-subtitle"),
                            getConfig().getInt("timings.fade-in"),
                            getConfig().getInt("timings.stay"),
                            getConfig().getInt("timings.fade-out"));

                    if (getConfig().getBoolean("sounds.play-sound"))
                        player.playSound(player.getLocation(), Sound.valueOf(getConfig().getString("sounds.winner-sound")), (float) getConfig().getDouble("sounds.volume"), (float) getConfig().getDouble("sounds.pitch"));
                    // do title
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if (p.getName().equals(player.getName()))
                            continue;
                        p.sendTitle("",
                                translate("subtitle").replace("%WINNER%", args[0]),
                                getConfig().getInt("timings.fade-in"),
                                getConfig().getInt("timings.stay"),
                                getConfig().getInt("timings.fade-out"));
                        if (getConfig().getBoolean("sounds.play-sound"))
                            p.playSound(p.getLocation(), Sound.valueOf(getConfig().getString("sounds.other-sound")), (float) getConfig().getDouble("sounds.volume"), (float) getConfig().getDouble("sounds.pitch"));
                    }

                }
            }

        } else if (command.getName().equalsIgnoreCase("winreload")) {
            if (!sender.hasPermission("wincommand.reload")) {
                sender.sendMessage(getPrefix() + ChatColor.RED + "You do not have permission to use this command");
                return true;
            } else {
                sender.sendMessage(getPrefix() + ChatColor.GREEN + "Reloaded Config.");
                reloadConfig();
            }
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> result = new ArrayList<>();
        if (cmd.getName().equalsIgnoreCase("win")) {
            if (sender.hasPermission("wincommand.command")) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    result.add(p.getName());
                }
            }
        }
        return result;
    }
}
