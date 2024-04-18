package de.leon.luemmelplugin;

import de.leon.luemmelplugin.commands.vote;
import org.bukkit.plugin.java.JavaPlugin;

public final class LuemmelPlugin extends JavaPlugin {

    public String plugin_prefix = "§8[§6Lümmel§8] §7: ";

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("Plugin is online!");

        getCommand("vote").setExecutor(new vote(this));

        vote e = new vote(this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("Plugin is offline!");
    }
}
