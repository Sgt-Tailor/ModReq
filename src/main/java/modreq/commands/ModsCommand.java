/*
 Modreq Minecraft/Bukkit server ticket system
 Copyright (C) 2013 Sven Wiltink

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package modreq.commands;

import modreq.Message;
import modreq.MessageType;
import modreq.ModReq;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ModsCommand implements CommandExecutor {


    public ModsCommand(ModReq instance) {
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        boolean isPlayer = sender instanceof Player;
        if (!sender.hasPermission("modreq.mods")) {
            if (isPlayer) {
                Message.sendToPlayer(MessageType.ERROR_PERMISSION, (Player) sender);
            } else {
                sender.sendMessage("You don't have permissions to do this");
            }
            return true;
        }

        sender.sendMessage(ModReq.format(ModReq.getInstance().Messages.getString("headers-footers.mods.header"), "", "", ""));
        boolean first = true;
        StringBuilder online = new StringBuilder();

        Player player = null;
        if (isPlayer) {
            player = (Player) sender;
        }
        for (Player op : Bukkit.getOnlinePlayers()) {
            if (op.hasPermission("modreq.check")) {
                if (!isPlayer || player.canSee(op)) {
                    if (first) {
                        online = new StringBuilder(op.getDisplayName());
                        first = false;
                    } else {
                        online.append(" ").append(op.getDisplayName());
                    }
                }
            }
        }
        String onlineString = online.toString();
        if (onlineString.equals("")) {
            sender.sendMessage(ModReq.format(ModReq.getInstance().Messages.getString("error.nomods"), "", "", ""));
            return true;
        }
        sender.sendMessage(onlineString);
        return true;
    }
}
