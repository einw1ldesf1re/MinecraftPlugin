package de.leon.luemmelplugin.commands;

import de.leon.luemmelplugin.LuemmelPlugin;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class vote implements CommandExecutor{

    private static int y = 0;
    private static int n = 0;
    private static int vote_time = 45; //seconds
    private static int vote_time_counter = vote_time;
    private static boolean isvote = false;
    private BukkitRunnable runnable;
    private static List<Player> voted_players = new ArrayList<>();


    //setings for percentuage
    private static boolean display_percent = true;
    private static int display_percent_length = 30;
    private static String sym = "┃";


    private final JavaPlugin plugin;

    public vote(JavaPlugin plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if(sender instanceof ConsoleCommandSender){
            sender.sendMessage("§8[§6Lümmel§8] §7: §cOnly players can run this command");
        }

        Player p = (Player) sender;

        if(args.length == 1) {

            if (args[0].equals("yes")) {
                if (isvote) {
                    if(!voted_players.contains(sender)) {
                        y++;
                        voted_players.add((Player) sender);
                        sender.sendMessage("§8[§6Lümmel§8] §7: §7You have voted for \"§aYes§7\"");
                    }else{
                        sender.sendMessage("§8[§6Lümmel§8] §7: §cYou have already voted");
                    }
                } else {
                    sender.sendMessage("§8[§6Lümmel§8] §7: §cNo voting available at the moment");
                }
            }

            else if (args[0].equals("no")) {
                if (isvote) {
                    if(!voted_players.contains(sender)) {
                        n++;
                        voted_players.add((Player) sender);
                        sender.sendMessage("§8[§6Lümmel§8] §7: §7You have voted for \"§cNo§7\"");
                    }else{
                        sender.sendMessage("§8[§6Lümmel§8] §7: §cYou have already voted");
                    }
                } else {
                    sender.sendMessage("§8[§6Lümmel§8] §7: §cNo voting available at the moment");
                }
            }


            else if (args[0].equals("day")) {
                if (sender.hasPermission("luemmel.vote.day") || sender.hasPermission("luemmel.vote.*")) {
                    if (!isvote) {

                        TextComponent agree = new TextComponent("§8[§a✔§8]");
                        agree.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Click to vote for \"yes\"").create()));
                        agree.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/vote yes"));

                        TextComponent spacer1 = new TextComponent("              ");
                        TextComponent spacer2 = new TextComponent("      §7|      ");

                        TextComponent disagree = new TextComponent("§8[§c✘§8]");
                        disagree.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Click to vote for \"no\"").create()));
                        disagree.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/vote no"));

                        for (Player player : Bukkit.getOnlinePlayers()) {
                            player.sendMessage("");
                            player.sendMessage("");
                            player.sendMessage("§8§m⋯⋯⋯⋯⋯⋯⋯⋯⋯§8{ §6 Vote §8}§m⋯⋯⋯⋯⋯⋯⋯⋯⋯");
                            player.sendMessage("§7author: "+sender.getName());
                            player.sendMessage("§7reason: §eTime set Day");
                            player.spigot().sendMessage(spacer1, agree, spacer2, disagree);
                            player.sendMessage("§8§m⋯⋯⋯⋯⋯⋯⋯⋯⋯⋯⋯⋯⋯⋯⋯⋯⋯⋯⋯⋯⋯⋯⋯⋯⋯");
                            player.sendMessage("");
                        }
                        isvote = true;
                        vote_time_counter = vote_time;

                        runnable = new BukkitRunnable() {
                            @Override
                            public void run() {
                                Bukkit.getOnlinePlayers().forEach(player -> {

                                    double max = y+n; //100%
                                    double percent_y = y/(max/100); //yes in percent
                                    double percent_n = n/(max/100); //no in percent

                                    double repeat_amount_y = (percent_y*((double) display_percent_length /100));
                                    double repeat_amount_n = (percent_n*((double) display_percent_length /100));
                                    if(y+n > 0) {
                                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§6Vote §8(§7Day§8) §7: §a" + sym.repeat((int) repeat_amount_y) + "§c" + sym.repeat((int) repeat_amount_n) + " §7: §8" + formatCounter(vote_time_counter)));
                                    }else{
                                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§6Vote §8(§7Day§8) §7: §f" + sym.repeat(display_percent_length) + " §7: §8" + formatCounter(vote_time_counter)));
                                    }
                                });
                                vote_time_counter--;
                            }
                        };
                        runnable.runTaskTimer(plugin, 0 , 20);

                        y++;                                        //let Creator vote instandly for yes
                        voted_players.add((Player) sender);         //add Creator to Players already voted
                        awaitSeconds("day", p.getLocation().getWorld());

                        return true;
                    } else {
                        sender.sendMessage("§8[§6Lümmel§8] §7: §cThere is already a voting");
                        return true;
                    }
                }
                sender.sendMessage("§8[§6Lümmel§8] §7: §cYou don't have Permissions");
                return true;
            }else if(args[0].equals("night")){
                if (sender.hasPermission("luemmel.vote.night") || sender.hasPermission("luemmel.vote.*")) {
                    if (!isvote) {

                        TextComponent agree = new TextComponent("§8[§a✔§8]");
                        agree.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Click to vote for \"yes\"").create()));
                        agree.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/vote yes"));

                        TextComponent spacer1 = new TextComponent("              ");
                        TextComponent spacer2 = new TextComponent("      §7|      ");

                        TextComponent disagree = new TextComponent("§8[§c✘§8]");
                        disagree.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Click to vote for \"no\"").create()));
                        disagree.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/vote no"));

                        for (Player player : Bukkit.getOnlinePlayers()) {
                            player.sendMessage("");
                            player.sendMessage("");
                            player.sendMessage("§8§m⋯⋯⋯⋯⋯⋯⋯⋯⋯§8{ §6 Vote §8}§m⋯⋯⋯⋯⋯⋯⋯⋯⋯");
                            player.sendMessage("§7author: "+sender.getName());
                            player.sendMessage("§7reason: §eTime set Night");
                            player.spigot().sendMessage(spacer1, agree, spacer2, disagree);
                            player.sendMessage("§8§m⋯⋯⋯⋯⋯⋯⋯⋯⋯⋯⋯⋯⋯⋯⋯⋯⋯⋯⋯⋯⋯⋯⋯⋯⋯");
                            player.sendMessage("");
                        }
                        isvote = true;
                        vote_time_counter = vote_time;

                        runnable = new BukkitRunnable() {
                            @Override
                            public void run() {
                                Bukkit.getOnlinePlayers().forEach(player -> {

                                    double max = y+n; //100%
                                    double percent_y = y/(max/100); //yes in percent
                                    double percent_n = n/(max/100); //no in percent

                                    double repeat_amount_y = (percent_y*((double) display_percent_length /100));
                                    double repeat_amount_n = (percent_n*((double) display_percent_length /100));
                                    if(y+n > 0) {
                                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§6Vote §8(§7Night§8) §7: §a" + sym.repeat((int) repeat_amount_y) + "§c" + sym.repeat((int) repeat_amount_n) + " §7: §8" + formatCounter(vote_time_counter)));
                                    }else{
                                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§6Vote §8(§7Night§8) §7: §f" + sym.repeat(display_percent_length) + " §7: §8" + formatCounter(vote_time_counter)));
                                    }
                                });
                                vote_time_counter--;
                            }
                        };
                        runnable.runTaskTimer(plugin, 0 , 20);

                        y++;                                        //let Creator vote instandly for yes
                        voted_players.add((Player) sender);         //add Creator to Players already voted
                        awaitSeconds("night", p.getLocation().getWorld());

                        return true;
                    } else {
                        sender.sendMessage("§8[§6Lümmel§8] §7: §cThere is already a voting");
                        return true;
                    }
                }
                sender.sendMessage("§8[§6Lümmel§8] §7: §cYou don't have Permissions");
                return true;
            }else if(args[0].equals("sun")){
                if (sender.hasPermission("luemmel.vote.sun") || sender.hasPermission("luemmel.vote.*")) {
                    if (!isvote) {

                        TextComponent agree = new TextComponent("§8[§a✔§8]");
                        agree.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Click to vote for \"yes\"").create()));
                        agree.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/vote yes"));

                        TextComponent spacer1 = new TextComponent("              ");
                        TextComponent spacer2 = new TextComponent("      §7|      ");

                        TextComponent disagree = new TextComponent("§8[§c✘§8]");
                        disagree.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Click to vote for \"no\"").create()));
                        disagree.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/vote no"));

                        for (Player player : Bukkit.getOnlinePlayers()) {
                            player.sendMessage("");
                            player.sendMessage("");
                            player.sendMessage("§8§m⋯⋯⋯⋯⋯⋯⋯⋯⋯§8{ §6 Vote §8}§m⋯⋯⋯⋯⋯⋯⋯⋯⋯");
                            player.sendMessage("§7author: "+sender.getName());
                            player.sendMessage("§7reason: §eWeather set Sun");
                            player.spigot().sendMessage(spacer1, agree, spacer2, disagree);
                            player.sendMessage("§8§m⋯⋯⋯⋯⋯⋯⋯⋯⋯⋯⋯⋯⋯⋯⋯⋯⋯⋯⋯⋯⋯⋯⋯⋯⋯");
                            player.sendMessage("");
                        }
                        isvote = true;
                        vote_time_counter = vote_time;

                        runnable = new BukkitRunnable() {
                            @Override
                            public void run() {
                                Bukkit.getOnlinePlayers().forEach(player -> {

                                    double max = y+n; //100%
                                    double percent_y = y/(max/100); //yes in percent
                                    double percent_n = n/(max/100); //no in percent

                                    double repeat_amount_y = (percent_y*((double) display_percent_length /100));
                                    double repeat_amount_n = (percent_n*((double) display_percent_length /100));
                                    if(y+n > 0) {
                                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§6Vote §8(§7Sun§8) §7: §a" + sym.repeat((int) repeat_amount_y) + "§c" + sym.repeat((int) repeat_amount_n) + " §7: §8" + formatCounter(vote_time_counter)));
                                    }else{
                                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§6Vote §8(§7Sun§8) §7: §f" + sym.repeat(display_percent_length) + " §7: §8" + formatCounter(vote_time_counter)));
                                    }
                                });
                                vote_time_counter--;
                            }
                        };
                        runnable.runTaskTimer(plugin, 0 , 20);

                        y++;                                        //let Creator vote instandly for yes
                        voted_players.add((Player) sender);         //add Creator to Players already voted
                        awaitSeconds("sun", p.getLocation().getWorld());

                        return true;
                    } else {
                        sender.sendMessage("§8[§6Lümmel§8] §7: §cThere is already a voting");
                        return true;
                    }
                }
                sender.sendMessage("§8[§6Lümmel§8] §7: §cYou don't have Permissions");
                return true;
            }else{
                sender.sendMessage("§8[§6Lümmel§8] §7: /vote <yes, no, day, night, sun>");
            }

        }else{
            sender.sendMessage("§8[§6Lümmel§8] §7: /vote <yes, no, day, night, sun>");
        }

        return true;
    }

    private String formatCounter(int duration){
        String string = "";
        int minutes = 0;
        int seconds = 0;

        if(duration / 60 >= 1){
            minutes = duration / 60;
            duration = duration - ((duration / 60) * 60);
        }

        if(duration >= 1){
            seconds = duration;
        }

        if(minutes <= 9){
            string = string + "0" + minutes + ":";
        }else{
            string = string + minutes + ":";
        }

        if(seconds <= 9){
            string = string + "0" + seconds;
        }else{
            string = string + seconds;
        }

        return string;
    }

    public void awaitSeconds(String argument, World w) {

        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                if (isvote) {
                    if (argument.equals("day")) {
                        if (n >= y) {
                            for (Player player : Bukkit.getOnlinePlayers()) {
                                player.sendMessage("§8[§6Lümmel§8] §7: §cWorld time has not set to \"day\"");
                            }
                        } else if (n < y && (y >= (Bukkit.getOnlinePlayers().size() * 0.5))) {

                            for (Player player : Bukkit.getOnlinePlayers()) {
                                player.sendMessage("§8[§6Lümmel§8] §7: §aWorld time has set to \"day\"");
                            }
                            try {
                                w.setFullTime(1000);

                            } catch (Exception ex) {
                                System.out.println("Exception: " + ex.getMessage());
                                ex.printStackTrace();
                            }

                        }
                    }else if(argument.equals("night")){
                        if (n >= y) {
                            for (Player player : Bukkit.getOnlinePlayers()) {
                                player.sendMessage("§8[§6Lümmel§8] §7: §cWorld time has not set to \"night\"");
                            }
                        } else if (n < y && (y >= (Bukkit.getOnlinePlayers().size() * 0.5))) {

                            for (Player player : Bukkit.getOnlinePlayers()) {
                                player.sendMessage("§8[§6Lümmel§8] §7: §aWorld time has set to \"night\"");
                            }
                            try {
                                w.setFullTime(13000);

                            } catch (Exception ex) {
                                System.out.println("Exception: " + ex.getMessage());
                                ex.printStackTrace();
                            }

                        }
                    }else if(argument.equals("sun")){
                        if (n >= y) {
                            for (Player player : Bukkit.getOnlinePlayers()) {
                                player.sendMessage("§8[§6Lümmel§8] §7: §cWorld weather has not set to \"sun\"");
                            }
                        } else if (n < y && (y >= (Bukkit.getOnlinePlayers().size() * 0.5))) {

                            for (Player player : Bukkit.getOnlinePlayers()) {
                                player.sendMessage("§8[§6Lümmel§8] §7: §aWorld weather has set to \"sun\"");
                            }
                            try {
                                w.setStorm(false);
                                w.setThundering(false);

                            } catch (Exception ex) {
                                System.out.println("Exception: " + ex.getMessage());
                                ex.printStackTrace();
                            }

                        }
                    }
                    isvote = false;
                    voted_players.clear();
                    runnable.cancel();
                }
                }
            },  (vote_time+1) * 20);

    }

}
