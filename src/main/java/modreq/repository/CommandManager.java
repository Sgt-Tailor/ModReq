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
package modreq.repository;

import modreq.ModReq;
import modreq.commands.*;

public class CommandManager {

    private TicketCommand newTicket;
    private ModReq plugin;
    private ModsCommand mods;
    private ReloadCommand reload;

    public CommandManager(ModReq instance) {
        plugin = instance;
        mods = new ModsCommand(plugin);
        reload = new ReloadCommand(plugin);
        newTicket = new TicketCommand(plugin, plugin.getTicketRepository());

    }

    public void initCommands() {
        plugin.getCommand("mods").setExecutor(mods);
        plugin.getCommand("modreload").setExecutor(reload);
        plugin.getCommand("ticket").setExecutor(newTicket);
    }
}
