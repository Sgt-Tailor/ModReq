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
import modreq.managers.TicketHandler;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ClearticketsCommand extends SubCommandExecutor {

    private ModReq plugin;
    private TicketHandler tickets;

    public ClearticketsCommand(ModReq instance) {
        plugin = instance;
    }

    @command
    public void Null(CommandSender sender, String[] args) {
        tickets = plugin.getTicketHandler();
        if (sender.hasPermission("modreq.cleartickets")) {
            tickets.clearTickets();
            sender.sendMessage(ChatColor.GREEN
                    + "All tickets have been removed");
        }
    }
}
