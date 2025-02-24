package dev.bronzylobster.starRPMail.commands;

import dev.bronzylobster.starRPMail.StarRPMail;
import dev.bronzylobster.starRPMail.Utils;
import dev.bronzylobster.starrpcore.Commands.AbstractCommand;
import dev.bronzylobster.starrpcore.Utils.MessageManager;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;


import java.util.HashMap;
import java.util.Map;

public class MailCommand extends AbstractCommand {
    FileConfiguration config = StarRPMail.getInstance().getConfig();
    MessageManager messageManager = new MessageManager(StarRPMail.getInstance());

    public MailCommand() {
        super("mail", StarRPMail.getInstance());
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        if (strings.length == 0) {
            commandSender.sendMessage("Usage: /mail <player>");
            return;
        }
        String player = strings[0];

        if (!Bukkit.getOfflinePlayer(player).isOnline()) {
            commandSender.sendMessage(messageManager.messageToComponent(config.getString("messages.player-offline")));
            return;
        }

        Player p = Bukkit.getPlayer(player);
        Player sender = (Player) commandSender;

        assert p != null;
        Location recieverLocation = p.getLocation();
        double distance = sender.getLocation().distance(recieverLocation);
        long baseDelay = 20L;
        double scale = config.getLong("scale");
        double scalingFactor = config.getLong("scaling-factor");
        double delay = baseDelay + scalingFactor * (1 - Math.exp(-distance / scale));

        if (StarRPMail.sends.containsKey(p)) {
            commandSender.sendMessage(messageManager.messageToComponent(config.getString("messages.unavailable")));
            return;
        }

        Inventory Sinv = sender.getInventory();
        if (sender.getItemInHand() == null){
            commandSender.sendMessage(messageManager.messageToComponent(config.getString("messages.no-item")));
            return;
        }

        ItemStack item = sender.getItemInHand();
        Sinv.remove(item);
        sender.updateInventory();
        Map<String, String> placeholders = new HashMap<>();
        String itemName = LegacyComponentSerializer.legacyAmpersand().serialize(item.effectiveName());
        placeholders.put("item", itemName);
        String playerName = LegacyComponentSerializer.legacyAmpersand().serialize(p.displayName());
        placeholders.put("player", playerName);
        commandSender.sendMessage(messageManager.messageToComponent(messageManager.getPlaceholders(p, "mail-send", placeholders)));
        StarRPMail.sends.put(p, item);

        new BukkitRunnable() {
            @Override
            public void run() {
                Inventory Pinv = p.getInventory();
                if (Utils.isFullPlayerInventory(p)) {
                    placeholders.put("player", LegacyComponentSerializer.legacyAmpersand().serialize(sender.displayName()));
                    p.sendMessage(messageManager.messageToComponent(messageManager.getPlaceholders(sender, "mail-receive", placeholders)));

                    Component msg = LegacyComponentSerializer.legacyAmpersand().deserialize(config.getString("messages.inventory-full"));
                    p.sendMessage(msg);
                } else {
                    Pinv.addItem(item);
                    placeholders.put("player", LegacyComponentSerializer.legacyAmpersand().serialize(sender.displayName()));
                    p.sendMessage(messageManager.messageToComponent(messageManager.getPlaceholders(sender, "mail-receive", placeholders)));
                    StarRPMail.sends.remove(p);
                }
                String soundkey = config.getString(config.getString("sound"));
                if (soundkey != null) {
                    p.playSound(Sound.sound(Key.key(soundkey), Sound.Source.AMBIENT, 1f, 1f));
                }
            }
        }.runTaskLater(StarRPMail.getInstance(), Math.round(delay));


    }
}
