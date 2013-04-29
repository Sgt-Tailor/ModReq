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
import modreq.Status;
import modreq.Ticket;
import modreq.korik.SubCommandExecutor;
import modreq.korik.Utils;
import modreq.managers.TicketHandler;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CheckCommand extends SubCommandExecutor {
    private ModReq plugin;
    private TicketHandler tickets;

    public CheckCommand(ModReq instance) {
	plugin = instance;

    }

    @command(maximumArgsLength = 1, permissions = { "modreq.check" }, usage = "/check <page>", description = "shows open tickets")
    public void Integer(CommandSender sender, String[] args) {
	tickets = plugin.getTicketHandler();
	int page = Integer.parseInt(args[0]);
	if (sender instanceof Player) {
	    tickets.sendPlayerPage(page, Status.OPEN, (Player) sender);
	} else {
	    sender.sendMessage("This command can only be ran as a player");
	}

    }

    @command
    public void Null(CommandSender sender, String[] args) {
	String[] page1 = Utils.addInFront(args, "1");
	Integer(sender, page1);
	return;
    }

    @command(minimumArgsLength = 1, maximumArgsLength = 1, usage = "/check id <id>")
    public void id(CommandSender sender, String[] args) {
	if (sender instanceof Player) {
	    tickets = plugin.getTicketHandler();
	    try {
		int id = Integer.parseInt(args[0]);
		if (id > 0 && id <= tickets.getTicketCount()) {
		    Ticket t = tickets.getTicketById(id);
		    t.sendMessageToPlayer((Player) sender);
		}
	    } catch (Exception e) {
		e.printStackTrace();
		sender.sendMessage(ChatColor.RED + args[0] + " is not a number");
	    }
	} else {
	    sender.sendMessage("This command can only be ran as a player");
	}
    }

    @command(minimumArgsLength = 0, maximumArgsLength = 1, usage = "/check closed <page>")
    public void closed(CommandSender sender, String[] args) {
	tickets = plugin.getTicketHandler();
	int page = 1;
	if (args.length == 1) {
	    page = java.lang.Integer.parseInt(args[0]);
	}
	if (sender instanceof Player) {
	    tickets.sendPlayerPage(page, Status.CLOSED, (Player) sender);
	} else {
	    sender.sendMessage("This command can only be ran as a player");
	}
    }

    @command(minimumArgsLength = 0, maximumArgsLength = 1, usage = "/check claimed <page>")
    public void claimed(CommandSender sender, String[] args) {
	tickets = plugin.getTicketHandler();
	int page = 1;
	if (args.length == 1) {
	    page = java.lang.Integer.parseInt(args[0]);
	}
	if (sender instanceof Player) {
	    tickets.sendPlayerPage(page, Status.CLAIMED, (Player) sender);
	} else {
	    sender.sendMessage("This command can only be ran as a player");
	}
    }
    @command(minimumArgsLength = 0, maximumArgsLength = 1, usage = "/check claimed <page>")
    public void pending(CommandSender sender, String[] args) {
	tickets = plugin.getTicketHandler();
	int page = 1;
	if (args.length == 1) {
	    page = java.lang.Integer.parseInt(args[0]);
	}
	if (sender instanceof Player) {
	    tickets.sendPlayerPage(page, Status.PENDING, (Player) sender);
	} else {
	    sender.sendMessage("This command can only be ran as a player");
	}
    }

}
