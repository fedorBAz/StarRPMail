package dev.bronzylobster.starRPMail.commands;

import dev.bronzylobster.starRPMail.StarRPMail;
import dev.bronzylobster.starRPMail.Utils;
import dev.bronzylobster.starrpcore.Commands.AbstractCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class getitemCommand extends AbstractCommand {
    public getitemCommand() {
        super("getitem", StarRPMail.getInstance());
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        Player p = (Player) commandSender;
        if (Utils.isFullPlayerInventory(p)) {
            p.sendMessage("Your inventory is full");
        } else {
            p.getInventory().addItem(StarRPMail.sends.get(p));
            p.updateInventory();
            StarRPMail.sends.remove(p);
        }
    }
}
