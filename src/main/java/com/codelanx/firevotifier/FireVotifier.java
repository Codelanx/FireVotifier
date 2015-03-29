/*
 * Copyright (C) 2015 Codelanx
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.codelanx.firevotifier;

import com.vexsoftware.votifier.Votifier;
import com.vexsoftware.votifier.model.Vote;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Handles firing fake vote events to Votifier
 *
 * @since 1.0.0
 * @author 1Rogue
 * @version 1.0.0
 */
public class FireVotifier extends JavaPlugin {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            this.sendMessage(sender, "Usage: /fakevote <username> <service>");
        }
        Vote v = this.buildVote(args);
        Votifier.getInstance().getListeners().forEach(l -> {
            try {
                l.voteMade(v);
            } catch (Exception ex) {
                this.sendMessage(sender, "Uncaught exception from listener '%s', from Plugin '%s'", l.getClass().getSimpleName(), this.getProvider(l.getClass()));
            }
        });
        return true;
    }

    /**
     * Builds a fake vote to fire
     * 
     * @since 1.0.0
     * @version 1.0.0
     * 
     * @param args The command arguments to build a vote (length of 2 minimum)
     * @return The new {@link Vote} object to pass to listeners
     */
    public Vote buildVote(String... args) {
        Vote vote = new Vote();
        vote.setServiceName(args[1]);
        vote.setUsername(args[0]);
        vote.setAddress("127.0.0.1");
        vote.setTimeStamp(""); //Todo
        return vote;
    }

    //sends a formatted message to a target
    private void sendMessage(CommandSender target, String message, Object... args) {
        target.sendMessage(ChatColor.translateAlternateColorCodes('&', "[&cFireVotifier&f] " + String.format(message, args)));
    }

    //Gets the name of the providing plugin for the listener class
    private String getProvider(Class<?> clazz) {
        if (clazz == null) {
            //silent treatment
            return "<unknown>";
        }
        JavaPlugin provider;
        try {
            provider = JavaPlugin.getProvidingPlugin(clazz);
        } catch (IllegalArgumentException ex) {
            return "<unknown>";
        }
        return provider.getName();
    }

}
