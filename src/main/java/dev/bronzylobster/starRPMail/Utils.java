package dev.bronzylobster.starRPMail;

import org.bukkit.entity.Player;

public class Utils {

    public static boolean isFullPlayerInventory(Player player) {
         if (player.getInventory().firstEmpty() != -1) {
             return false;
         } else {
             return true;
         }
    }
}
