package de.leon.luemmelplugin.commands;

import de.leon.luemmelplugin.LuemmelPlugin;
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
    private static int vote_time = 10; //seconds
    private static boolean isvote = false;
    private static List<Player> voted_players = new ArrayList<>();


    //setings for percentuage
    private static boolean display_percent = true;
    private static int display_percent_length = 30;
    private static String sym = "┃";

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

                        //percentage //////////////////////////////////////////////
                        double max = y+n; //100%
                        double percent_y = y/(max/100); //yes in percent
                        double percent_n = n/(max/100); //no in percent

                        Bukkit.getLogger().info(percent_y + " | "+ percent_n);

                        double repeat_amount_y = (percent_y*((double) display_percent_length /100));
                        double repeat_amount_n = (percent_n*((double) display_percent_length /100));

                        //Bukkit.getLogger().info(repeat_amount_y + " | "+ repeat_amount_n);

                        //Bukkit.getLogger().info(sym.repeat((int) repeat_amount_y) + sym.repeat((int) repeat_amount_n));

                        //////////////////////////////////////////////////////////

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

                        for (Player player : Bukkit.getOnlinePlayers()) {
                            player.spigot().sendMessage(agree);
                        }

                        isvote = true;
                        awaitSeconds("day", p.getLocation().getWorld());

                        //Bukkit.getLogger().info("Vote Day ausgeführt!!!!!!!!!!!!!!!!");

                        return true;
                    } else {
                        sender.sendMessage("§8[§6Lümmel§8] §7: §cThere is already a voting");
                        return true;
                    }
                }
                sender.sendMessage("§8[§6Lümmel§8] §7: §cYou don't have Permissions");
                return true;
            }else if(args[0].equals("sun")){
                p.getLocation().getWorld().setTime(1000);
            }

        }else{
            sender.sendMessage("§8[§6Lümmel§8] §7: /vote <yes, no, day>");
        }

        return true;
    }

    private final JavaPlugin plugin;

    public vote(JavaPlugin plugin){
        this.plugin = plugin;
    }

    public void awaitSeconds(String argument, World w) {

        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                if (isvote) {
                    if (argument.equals("day")) {
                        if (n >= y) {
                            for (Player player : Bukkit.getOnlinePlayers()) {
                                player.sendMessage("§8[§6Lümmel§8] §7: §cWorld time was not set to \"day\"");
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
                    }
                    isvote = false;
                    voted_players.clear();
                }
                }
            },  vote_time * 20);

    }
}
