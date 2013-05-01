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

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ModhelpCommand extends SubCommandExecutor {

    private ModReq plugin;

    public ModhelpCommand(ModReq instance) {
        plugin = instance;
    }

    @command
    public void Null(CommandSender sender, String[] args) {

        sender.sendMessage(ChatColor.YELLOW + "----ModReq-Help-Message----");
        if (sender.hasPermission("modreq.request")) {
            sender.sendMessage(ChatColor.GOLD + "/modreq <message> " + ChatColor.WHITE + plugin.Messages.getString("info.modreq"));
        }
        if (sender.hasPermission("modreq.check")) {
            sender.sendMessage(ChatColor.GOLD + "/check <page> " + ChatColor.WHITE + plugin.Messages.getString("info.check"));
        }
        if (sender.hasPermission("modreq.check")) {
            sender.sendMessage(ChatColor.GOLD + "/check claimed <page> " + ChatColor.WHITE + plugin.Messages.getString("info.check"));
        }
        if (sender.hasPermission("modreq.check")) {
            sender.sendMessage(ChatColor.GOLD + "/check closed <page> " + ChatColor.WHITE + plugin.Messages.getString("info.check"));
        }
        if (sender.hasPermission("modreq.check.pending")) {
            sender.sendMessage(ChatColor.GOLD + "/check pending <page> " + ChatColor.WHITE + plugin.Messages.getString("info.check"));
        }
        if (sender.hasPermission("modreq.check")) {
            sender.sendMessage(ChatColor.GOLD + "/check id <id> " + ChatColor.WHITE + plugin.Messages.getString("info.ticket"));
        }
        if (sender.hasPermission("modreq.check")) {
            sender.sendMessage(ChatColor.GOLD + "/ticket " + ChatColor.WHITE + plugin.Messages.getString("info.ticket"));
        }
        if (sender.hasPermission("modreq.check")) {
            sender.sendMessage(ChatColor.GOLD + "/ticket setpending <id>" + ChatColor.WHITE + plugin.Messages.getString("info.pending"));
        }
        if (sender.hasPermission("modreq.tp-id")) {
            sender.sendMessage(ChatColor.GOLD + "/tp-id <id> " + ChatColor.WHITE + plugin.Messages.getString("info.tp-id"));
        }
        if (sender.hasPermission("modreq.claim")) {
            sender.sendMessage(ChatColor.GOLD + "/claim <id> " + ChatColor.WHITE + plugin.Messages.getString("info.claim"));
        }
        if (sender.hasPermission("modreq.check")) {
            sender.sendMessage(ChatColor.GOLD + "/comment <id> <comment> " + ChatColor.WHITE + plugin.Messages.getString("info.comment"));
        }
        if (sender.hasPermission("modreq.reopen")) {
            sender.sendMessage(ChatColor.GOLD + "/re-open <number> (message) " + ChatColor.WHITE + plugin.Messages.getString("info.re-open"));
        }
        if (sender.hasPermission("modreq.status")) {
            sender.sendMessage(ChatColor.GOLD + "/status (number) " + ChatColor.WHITE + plugin.Messages.getString("info.status"));
        }
        if (sender.hasPermission("modreq.close")) {
            sender.sendMessage(ChatColor.GOLD + "/done <number> (message) " + ChatColor.WHITE + plugin.Messages.getString("info.done"));
        }
        if (sender.hasPermission("modreq.close")) {
            sender.sendMessage(ChatColor.GOLD + "/close <number> (message) " + ChatColor.WHITE + plugin.Messages.getString("info.done"));
        }
        if (sender.hasPermission("modreq.mods")) {
            sender.sendMessage(ChatColor.GOLD + "/mods " + ChatColor.WHITE + plugin.Messages.getString("info.mods"));
        }
        sender.sendMessage(ChatColor.GOLD + "/modhelp " + ChatColor.WHITE + plugin.Messages.getString("info.modhelp"));
        return;

    }
}
