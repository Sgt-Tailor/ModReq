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
package modreq.managers;

import modreq.ModReq;
import modreq.commands.CheckCommand;
import modreq.commands.ClearticketsCommand;
import modreq.commands.CommentCommand;
import modreq.commands.DoneCommand;
import modreq.commands.ModhelpCommand;
import modreq.commands.ModreqCommand;
import modreq.commands.ModsCommand;
import modreq.commands.ReloadCommand;
import modreq.commands.ReopenCommand;
import modreq.commands.StatusCommand;
import modreq.commands.TicketCommand;
import modreq.commands.TpIdCommand;
import modreq.commands.UpdatemodreqCommand;
import modreq.commands.claimCommand;

public class CommandManager {

    private ModReq plugin;
    private CheckCommand check;
    private claimCommand claim;
    private DoneCommand done;
    private StatusCommand status;
    private ModreqCommand modreq;
    private TpIdCommand tpid;
    private ReopenCommand reopen;
    private ModsCommand mods;
    private ModhelpCommand modhelp;
    private TicketCommand ticket;
    private UpdatemodreqCommand update;
    private ClearticketsCommand clear;
    private ReloadCommand reload;
    private CommentCommand comment;

    public CommandManager(ModReq instance) {
        plugin = instance;
        check = new CheckCommand(plugin);
        claim = new claimCommand(plugin);
        done = new DoneCommand(plugin);
        status = new StatusCommand(plugin);
        modreq = new ModreqCommand(plugin);
        tpid = new TpIdCommand(plugin);
        reopen = new ReopenCommand(plugin);
        mods = new ModsCommand(plugin);
        modhelp = new ModhelpCommand(plugin);
        ticket = new TicketCommand(plugin);
        update = new UpdatemodreqCommand(plugin);
        clear = new ClearticketsCommand(plugin);
        reload = new ReloadCommand(plugin);
        comment = new CommentCommand();

    }

    public void initCommands() {
        plugin.getCommand("check").setExecutor(check);
        plugin.getCommand("claim").setExecutor(claim);
        plugin.getCommand("done").setExecutor(done);
        plugin.getCommand("status").setExecutor(status);
        plugin.getCommand("modreq").setExecutor(modreq);
        plugin.getCommand("tp-id").setExecutor(tpid);
        plugin.getCommand("re-open").setExecutor(reopen);
        plugin.getCommand("mods").setExecutor(mods);
        plugin.getCommand("modhelp").setExecutor(modhelp);
        plugin.getCommand("ticket").setExecutor(ticket);
        plugin.getCommand("updatemodreq").setExecutor(update);
        plugin.getCommand("cleartickets").setExecutor(clear);
        plugin.getCommand("modreload").setExecutor(reload);
        plugin.getCommand("comment").setExecutor(comment);
    }
}
