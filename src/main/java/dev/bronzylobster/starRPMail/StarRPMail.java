package dev.bronzylobster.starRPMail;

import dev.bronzylobster.starRPMail.commands.MailCommand;
import dev.bronzylobster.starRPMail.commands.getitemCommand;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public final class StarRPMail extends JavaPlugin {

    @Getter
    private static StarRPMail instance;
    public static Map<Player, ItemStack> sends;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        instance = this;
        sends = new HashMap<>();

        new MailCommand();
        new getitemCommand();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
