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
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ModsCommand extends SubCommandExecutor {
    private ModReq plugin;

    public ModsCommand(ModReq instance) {
	plugin = instance;
    }

    @command
    public void Null(CommandSender sender, String[] args) {
	if (sender.hasPermission("modreq.mods")) {

	    sender.sendMessage(ChatColor.GOLD
		    + plugin.Messages.getString("list-of-mods",
			    "-------List-of-Online-Mods-------"));

	    Player[] op = Bukkit.getOnlinePlayers();
	    String online = "";
	    for (int i = 0; i < op.length; i++) {
		if (op[i].hasPermission("modreq.check")) {
		    if (sender instanceof Player) {
			if (((Player) sender).canSee(op[i])) {
			    if (i == 0) {
				online = op[i].getDisplayName();
			    } else {
				online = online + " " + op[i].getDisplayName();
			    }
			}

		    } else {
			if (i == 0) {
			    online = op[i].getDisplayName();
			} else {
			    online = online + " " + op[i].getDisplayName();
			}
		    }

		}
	    }
	    if (online.equals("")) {
		sender.sendMessage(ChatColor.GRAY
			+ plugin.Messages.getString("no-mods",
				"There are no mods online"));
		return;
	    }
	    sender.sendMessage(online);
	    return;
	} else {
	    sender.sendMessage(ChatColor.RED
		    + plugin.Messages.getString("no-permission",
			    "You don't have permissions to do this)"));
	    return;
	}
    }
}
