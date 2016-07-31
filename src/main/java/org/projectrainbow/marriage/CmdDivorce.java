package org.projectrainbow.marriage;

import PluginReference.ChatColor;
import PluginReference.MC_Command;
import PluginReference.MC_Player;
import PluginReference.RainbowUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CmdDivorce implements MC_Command {

    public CmdDivorce() {
    }

    public String getCommandName() {
        return "divorce";
    }

    @Override
    public List<String> getAliases() {
        return null;
    }

    @Override
    public String getHelpLine(MC_Player plr) {
        return ChatColor.AQUA + "/divorce" + ChatColor.WHITE
                + " --- Divorce Someone!";
    }

    @Override
    public void handleCommand(MC_Player plr, String[] args) {
        if (plr == null) {
            System.out.println("--- Only for players!");
            return;
        }

        if (args.length <= 0) {
            this.SendUsage(plr);
        } else {
            MarryManager.HandleDivorce(plr, args[0]);
        }
    }

    @Override
    public boolean hasPermissionToUse(MC_Player plr) {
        return plr == null || plr.hasPermission("rainbow.divorce");
    }

    @Override
    public List<String> getTabCompletionList(MC_Player plr, String[] args) {
        String prefix = args[args.length - 1].toLowerCase(Locale.ROOT);
        List<String> result = new ArrayList<String>();
        for (MC_Player player : RainbowUtils.getServer().getPlayers()) {
            if (player.getName().toLowerCase(Locale.ROOT).startsWith(prefix)) {
                result.add(player.getName());
            }
        }
        return result;
    }

    public void SendUsage(MC_Player cs) {
        cs.sendMessage(
                ChatColor.GOLD + "/divorce " + ChatColor.YELLOW + "PlayerName");
        String spouseName = MarryManager.GetSpouse(cs.getName());

        if (spouseName != null) {
            cs.sendMessage(
                    ChatColor.GREEN + "You are married to " + ChatColor.YELLOW
                            + spouseName);
        } else {
            cs.sendMessage(
                    ChatColor.GREEN + "You are single and ready to mingle!");
        }

    }
}
