/*
 KorikUtils - Minecraft/Bukkit utilities to make config and commands quicker
 Copyright (C) 2013 Korikisulda

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

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public abstract class SubCommandExecutor implements CommandExecutor {

    /*
     * For passing from other subcommandexecutor classes All credits for
     * Korikisulda for making this class :D
     */
    public void onCommand(CommandSender sender, String[] args) {

        onCommand(sender, "", "", args);
    }

    @Override
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

    public void onSubCommand(CommandSender sender, String[] arguments,
            String commandName) {
        if (commandName == "") {
            commandName = "Null";
        }
        try {
            for (Method m : this.getClass().getMethods()) {
                if (m.getName().equalsIgnoreCase(commandName)
                        || (isInteger(commandName) && m.getName().equals(
                        "Integer"))) {
                    if (m.getName().equals("Integer")) {
                        arguments = Utils.addInFront(arguments, commandName);
                    }
                    for (Annotation a : m.getAnnotations()) {
                        if (a instanceof command) {
                            command c = (command) a;
                            if (arguments == null) {
                                arguments = c.defaultArguments();
                            }
                            if (arguments.length > c.maximumArgsLength()) {
                                sender.sendMessage(c.usage());
                            } else if (arguments.length < c.minimumArgsLength()) {
                                sender.sendMessage(c.usage());
                            } else if (!hasPerms(sender, c.permissions())) {
                                sender.sendMessage(ChatColor.RED
                                        + "You do not have permission do do that!");
                                for (String p : c.permissions()) {
                                    sender.sendMessage("- " + p);
                                }
                            } else {
                                // well..... if they're here...
                                m.invoke(this,
                                        new Object[]{sender, arguments});
                            }
                            return;
                        }
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            sender.sendMessage(ChatColor.RED
                    + "Internal error! or you got the command wrong.");
            sender.sendMessage(ChatColor.RED + Utils.join(arguments, ",", 0)
                    + " | " + commandName);
            return;
        }

    }

    private boolean isInteger(String string) {
        try {
            @SuppressWarnings("unused")
            // it is used to check if it is a integer or not
            int i = Integer.parseInt(string);
        } catch (Exception e) {
            // it is not a number
            return false;
        }
        return true;
    }

    private boolean hasPerms(CommandSender sender, String[] perms) {
        for (String p : perms) {
            if (!sender.hasPermission(p)) {
                return false;
            }
        }
        return true;
    }

    @command(maximumArgsLength = 1, usage = "[command]", description = "displays help")
    public void help(CommandSender sender, String[] args) {
        if (args.length == 1) {
            for (Method m : this.getClass().getMethods()) {
                for (Annotation a : m.getAnnotations()) {
                    if (a instanceof command
                            && m.getName().equalsIgnoreCase(args[0])) {
                        command c = (command) a;
                        sender.sendMessage("["
                                + ((hasPerms(sender, c.permissions())) ? ChatColor.GREEN
                                : ChatColor.RED) + m.getName()
                                + ChatColor.GRAY + " subcommand Summary]");
                        sender.sendMessage(ChatColor.GRAY + m.getName() + " "
                                + c.usage() + ChatColor.GRAY + " - "
                                + c.description());
                        sender.sendMessage(ChatColor.GRAY
                                + "Permissions: "
                                + (c.permissions().length == 0 ? ChatColor.GREEN
                                + "none"
                                : Utils.join(c.permissions(), ",", 0)));

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
                    sender.sendMessage(((hasPerms(sender, c.permissions())) ? ChatColor.GREEN
                            : ChatColor.RED)
                            + m.getName()
                            + " "
                            + c.usage()
                            + ChatColor.GRAY
                            + " - " + c.description());
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
    }
}
