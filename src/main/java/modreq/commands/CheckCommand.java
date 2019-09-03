/*
 Modreq Minecraft/Bukkit server ticket system
 Copyright (C) 2013 Sven Wiltink, korikisulda

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
import modreq.Status;
import modreq.Ticket;
import modreq.korik.SubCommandExecutor;
import modreq.repository.TicketRepository;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CheckCommand extends SubCommandExecutor {

    private ModReq plugin;
    private TicketRepository tickets;

    public CheckCommand(ModReq instance) {
        plugin = instance;
    }

    @Override
    public void onInvalidCommand(CommandSender sender, String[] args, String command) {
        tickets = plugin.getTicketRepository();
        int page = 1;
        try {
            page = Integer.parseInt(command);
        } catch (Exception e) {
            Message.sendToPlayer(MessageType.ERROR_NUMBER, (Player) sender);
            return;
        }

        tickets.sendPlayerPage(page, Status.OPEN, (Player) sender);
    }

    @command
    public void Null(CommandSender sender, String[] args) {
        onInvalidCommand(sender, null, "1");
    }

    @command(minimumArgsLength = 1, maximumArgsLength = 1, usage = "/check id <id>")
    public void id(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            tickets = plugin.getTicketRepository();
            int id;
            try {
                id = Integer.parseInt(args[0]);
            } catch (Exception e) {
                Message.sendToPlayer(MessageType.ERROR_NUMBER, (Player) sender, args[0]);
                return;
            }

            Ticket t = tickets.getTicketById(id);
            if (t != null) {
                t.sendMessageToPlayer((Player) sender);
            } else {
                Message.sendToPlayer(MessageType.ERROR_TICKET_EXIST, (Player) sender, args[0]);
            }
        } else {
            sender.sendMessage("This command can only be ran as a player");
        }
    }

    @command(minimumArgsLength = 0, maximumArgsLength = 1, usage = "/check closed <page>")
    public void closed(CommandSender sender, String[] args) {
        tickets = plugin.getTicketRepository();
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
        tickets = plugin.getTicketRepository();
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
        tickets = plugin.getTicketRepository();
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
