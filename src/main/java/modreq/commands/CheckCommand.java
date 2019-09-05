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

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.Squid;

import java.sql.SQLException;

public class CheckCommand extends SubCommandExecutor {

    private ModReq plugin;
    private TicketRepository ticketRepository;

    public CheckCommand(ModReq instance) {
        plugin = instance;
        ticketRepository = instance.getTicketRepository();
    }

    @Override
    public void onInvalidCommand(CommandSender sender, String[] args, String command) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("You can only run this command as a player");
            return;
        }
        Player player = (Player) sender;
        int page;
        try {
            page = Integer.parseInt(command);
        } catch (Exception e) {
            Message.sendToPlayer(MessageType.ERROR_NUMBER, player);
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                ticketRepository.sendPlayerPage(page, Status.OPEN, player);
            } catch (SQLException e) {
                e.printStackTrace();
                Message.sendToPlayer(MessageType.ERROR_GENERIC, player);
            }
        });
    }

    @command
    public void Null(CommandSender sender, String[] args) {
        onInvalidCommand(sender, null, "1");
    }

    @command(minimumArgsLength = 1, maximumArgsLength = 1, usage = "/check id <id>")
    public void id(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("You can only run this command as a player");
            return;
        }
        int id;
        Player player = (Player) sender;
        try {
            id = Integer.parseInt(args[0]);
        } catch (Exception e) {
            Message.sendToPlayer(MessageType.ERROR_NUMBER, player, args[0]);
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            Ticket t;
            try {
                t = ticketRepository.getTicketById(id);
            } catch (SQLException e) {
                e.printStackTrace();
                Message.sendToPlayer(MessageType.ERROR_GENERIC, player);
                return;
            }

            if (t != null) {
                t.sendMessageToPlayer(player);
            } else {
                Message.sendToPlayer(MessageType.ERROR_TICKET_EXIST, player, args[0]);
            }
        });
    }

    @command(minimumArgsLength = 0, maximumArgsLength = 1, usage = "/check closed <page>")
    public void closed(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("You can only run this command as a player");
            return;
        }

        Player player = (Player) sender;
        int page;
        try {
            page = Integer.parseInt(args[0]);
        } catch (Exception e) {
            Message.sendToPlayer(MessageType.ERROR_NUMBER, player, args[0]);
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                ticketRepository.sendPlayerPage(page, Status.CLOSED, player);
            } catch (SQLException e) {
                e.printStackTrace();
                Message.sendToPlayer(MessageType.ERROR_GENERIC, player);
            }
        });
    }

    @command(minimumArgsLength = 0, maximumArgsLength = 1, usage = "/check claimed <page>")
    public void claimed(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be ran as a player");
            return;
        }

        int page = 1;
        Player player = (Player) sender;
        try {
            if (args.length >= 1) {
                page = Integer.parseInt(args[0]);
            }
        } catch (NumberFormatException e) {
            Message.sendToPlayer(MessageType.ERROR_NUMBER, player, args[0]);
            return;
        }

        int finalPage = page;
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                ticketRepository.sendPlayerPage(finalPage, Status.CLOSED, player);
            } catch (SQLException e) {
                e.printStackTrace();
                Message.sendToPlayer(MessageType.ERROR_GENERIC, player);
            }
        });
    }

    @command(minimumArgsLength = 0, maximumArgsLength = 1, usage = "/check claimed <page>")
    public void pending(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("You can only run this command as a player");
            return;
        }

        Player player = (Player) sender;
        int page = 1;
        try {
            if (args.length >= 1) {
                page = Integer.parseInt(args[0]);
            }
        } catch (NumberFormatException e) {
            Message.sendToPlayer(MessageType.ERROR_NUMBER, player, args[0]);
            return;
        }

        int finalPage = page;
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                ticketRepository.sendPlayerPage(finalPage, Status.CLOSED, player);
            } catch (SQLException e) {
                e.printStackTrace();
                Message.sendToPlayer(MessageType.ERROR_GENERIC, player);
            }
        });
    }
}
