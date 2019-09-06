/*
 	KorikUtils - configuration and subcommand utils for Bukkit/Minecraft
    Copyright (C) 2013 korikisulda

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
package modreq.korik;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;


public abstract class SubCommandExecutor implements CommandExecutor, TabCompleter {

    private static Map<String, command> subCommands = new HashMap<>();

    public SubCommandExecutor() {
        for (Method m : this.getClass().getMethods()) {
            for (Annotation a : m.getAnnotations()) {
                if (a instanceof command) {
                    if (m.getName().equals("Null")) {
                        continue;
                    }
                    subCommands.put(m.getName().toLowerCase(), (command) a);
                }
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> result = new ArrayList<>();
        if (args.length == 1 ) {
            subCommands.forEach((subCommand, annotation) -> {
                if (subCommand.contains(args[0]) && hasPerms(sender, annotation.permissions())) {
                    result.add(subCommand);
                }
            });
        }

        return result;
    }

    /*
     * For passing from other subcommandexecutor classes
     */
    public void onCommand(CommandSender sender, String[] args) {

        onCommand(sender, "", "", args);
    }

    /*
     *
     */

    @command
    public void Null(CommandSender sender, String[] args) {
        sender.sendMessage(ChatColor.RED + "It is not possible to use this command with no arguments.");
        sender.sendMessage(ChatColor.RED + "Use the 'help' subcommand for a list of available subcommands.");
    }


    public void onInvalidCommand(CommandSender sender, String[] arguments, String commandName) {
        sender.sendMessage(ChatColor.RED + "The subcommand '" + commandName + "' does not exist.");
    }

    public void onConsoleExecutePlayerOnlyCommand(CommandSender sender, String[] args, String commandName) {
        sender.sendMessage(ChatColor.RED + "This command must be executed as a player.");
    }

    public boolean onCommand(CommandSender sender, Command command,
                             String label, String[] args) {
        return onCommand(sender, command.getName(), label, args);
    }

    public boolean onCommand(CommandSender sender, String command,
                             String label, String[] args) {
        ArrayList<String> arguments = new ArrayList<String>();
        String c = "";
        try {
            c = args[0];
            boolean b = false;
            for (String s : args) {
                if (!b) {
                    b = true;
                    continue;
                }
                arguments.add(s);
            }
        } catch (Exception e) {
        }

        onSubCommand(sender, arguments.toArray(new String[arguments.size()]), c);
        return true;
    }

    public void onSubCommand(CommandSender sender, String[] arguments, String commandName) {
        if (commandName == "") commandName = "Null";
        try {
            for (Method m : this.getClass().getMethods()) {
                if (m.getName().equalsIgnoreCase(commandName)) {
                    for (Annotation a : m.getAnnotations()) {
                        if (a instanceof command) {
                            command c = (command) a;
                            if (arguments == null) arguments = c.defaultArguments();
                            if (!(sender instanceof Player) && c.playerOnly()) {
                                onConsoleExecutePlayerOnlyCommand(sender, arguments, commandName);
                            } else if (arguments.length > c.maximumArgsLength()) {
                                sender.sendMessage(c.usage());
                            } else if (arguments.length < c.minimumArgsLength()) {
                                sender.sendMessage(c.usage());
                            } else if (!hasPerms(sender, c.permissions()) && !getAltPerm(sender, m.getName())) {
                                sender.sendMessage(ChatColor.RED + "You do not have permission do do that!");
                            } else {
                                //well..... if they're here...
                                m.invoke(this, new Object[]{sender, arguments});
                            }
                            return;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            sender.sendMessage(ChatColor.RED + "Internal error!");
            return;
        }
        onInvalidCommand(sender, arguments, commandName);
    }

    public boolean getAltPerm(CommandSender sender, String scom) {
        return false;
    }

    private boolean hasPerms(CommandSender sender, String[] perms) {
        for (String p : perms) {
            if (!sender.hasPermission(p)) return false;
        }
        return true;
    }

    @command(
            maximumArgsLength = 1,
            usage = "[command]",
            description = "displays help"
    )

    public void help(CommandSender sender, String[] args) {
        if (args.length == 1) {
            for (Method m : this.getClass().getMethods()) {
                for (Annotation a : m.getAnnotations()) {
                    if (a instanceof command && m.getName().equalsIgnoreCase(args[0])) {
                        command c = (command) a;
                        if (!hasPerms(sender, c.permissions())) {
                            return;
                        }
                        sender.sendMessage("[" + ChatColor.GREEN +
                                    m.getName() + ChatColor.GRAY + " subcommand Summary]");
                        sender.sendMessage(ChatColor.GRAY + m.getName() + " " + c.usage() + ChatColor.GRAY + " - " + c.description());
                        sender.sendMessage(ChatColor.GRAY + "Permissions: " + (c.permissions().length == 0 ? ChatColor.GREEN + "none" : Utils.join(c.permissions(), ",", 0)));
                        return;
                    }
                }
            }
            sender.sendMessage(ChatColor.RED + "Unknown subcommand.");
            return;
        }

        for (Method m : this.getClass().getMethods()) {
            for (Annotation a : m.getAnnotations()) {
                if (a instanceof command) {
                    command c = (command) a;
                    String subCommandName = m.getName() + " ";
                    if (m.getName().equals("Null")) {
                        subCommandName = "";
                    }

                    if (subCommandName.length() == 0 && c.usage().length() == 0) {
                        continue;
                    }

                    if (!hasPerms(sender, c.permissions())) {
                        continue;
                    }
                    sender.sendMessage(ChatColor.GREEN + subCommandName + c.usage() + ChatColor.GRAY + " - " + c.description());
                }
            }
        }

    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface command {
        String[] permissions() default {};

        String[] defaultArguments() default {};

        int minimumArgsLength() default 0;

        int maximumArgsLength() default 100;

        String usage() default "";

        String description() default "";

        boolean playerOnly() default false;
    }
}
