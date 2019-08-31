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

import modreq.ModReq;
import modreq.korik.SubCommandExecutor;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ModsCommand extends SubCommandExecutor {


    public ModsCommand(ModReq instance) {
    }

    @command
    public void Null(CommandSender sender, String[] args) {
        if (sender.hasPermission("modreq.mods")) {
            sender.sendMessage(ModReq.format(ModReq.getInstance().Messages.getString("headers-footers.mods.header"), "", "", ""));
            boolean first = true;
            StringBuilder online = new StringBuilder();
            for (Player op : Bukkit.getOnlinePlayers()) {
                if (op.hasPermission("modreq.check")) {
                    if (sender instanceof Player) {
                        if (((Player) sender).canSee(op)) {
                            if (first) {
                                online = new StringBuilder(op.getDisplayName());
                                first = false;
                            } else {
                                online.append(" ").append(op.getDisplayName());
                            }
                        }
                    } else {
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
                return;
            }
            sender.sendMessage(onlineString);
        }
    }
}
