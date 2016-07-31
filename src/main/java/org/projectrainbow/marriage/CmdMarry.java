package org.projectrainbow.marriage;


import PluginReference.ChatColor;
import PluginReference.MC_Command;
import PluginReference.MC_Player;
import PluginReference.RainbowUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class CmdMarry implements MC_Command {

    public String getCommandName() {
        return "marry";
    }

    @Override
    public List<String> getAliases() {
        return null;
    }

    @Override
    public String getHelpLine(MC_Player plr) {
        return ChatColor.AQUA + "/marry" + ChatColor.WHITE
                + " --- Marry Someone!";
    }

    @Override
    public void handleCommand(MC_Player plr, String[] args) {
        if (plr != null) {
            if (args.length <= 0) {
                this.SendUsage(plr);
            } else if (args.length == 1
                    && args[0].equalsIgnoreCase("selectChurch")
                    && plr.isOp()) {
                MarryListener.HandleSetChurchPosition(plr);
            } else if (args.length == 1 && args[0].equalsIgnoreCase("info")) {
                MarryManager.ShowMarriageInfo(plr, plr.getName());
            } else if (args.length == 2 && args[0].equalsIgnoreCase("info")) {
                MarryManager.ShowMarriageInfo(plr, args[1]);
            } else if (args.length == 1) {
                String spouseName = MarryManager.GetSpouse(plr.getName());

                if (spouseName != null) {
                    plr.sendMessage(
                            ChatColor.RED + "You are already married! "
                                    + ChatColor.AQUA + "Spouse: " + ChatColor.YELLOW
                                    + spouseName);
                } else {
                    String tgtName = args[0];
                    MC_Player pTgt = RainbowUtils.getServer().getOnlinePlayerByName(tgtName);

                    if (pTgt == null) {
                        plr.sendMessage(
                                ChatColor.RED + "No player online named: "
                                        + ChatColor.YELLOW + tgtName);
                    } else {
                        tgtName = pTgt.getName();
                        spouseName = MarryManager.GetSpouse(tgtName);
                        if (spouseName != null) {
                            plr.sendMessage(
                                    ChatColor.YELLOW + tgtName + ChatColor.RED
                                            + " is already married to "
                                            + ChatColor.YELLOW + spouseName);
                        } else if (tgtName.equalsIgnoreCase(plr.getName())) {
                            plr.sendMessage(
                                    ChatColor.RED + "You can\'t marry yourself!");
                        } else if (!MarryManager.IsInChurch((int) plr.getLocation().x,
                                (int) plr.getLocation().y, (int) plr.getLocation().z, plr.getLocation().dimension)) {
                            plr.sendMessage(
                                    ChatColor.RED
                                            + "You must be in church to marry!");
                        } else if (!MarryManager.IsInChurch((int) pTgt.getLocation().x,
                                (int) pTgt.getLocation().y, (int) pTgt.getLocation().z,
                                pTgt.getLocation().dimension)) {
                            plr.sendMessage(
                                    ChatColor.RED
                                            + "Your partner must also be in church!");
                        } else {
                            double dx = plr.getLocation().x - pTgt.getLocation().x;
                            double dz = plr.getLocation().z - pTgt.getLocation().z;
                            double dy = plr.getLocation().y - pTgt.getLocation().y;
                            double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);

                            if (dist >= 3.0D) {
                                plr.sendMessage(
                                        ChatColor.RED
                                                + "This is a wedding, stand closer together first!");
                            } else {
                                String tgtProposedTo = (String) MarryManager.marryPropose.get(
                                        tgtName);

                                if (tgtProposedTo != null
                                        && tgtProposedTo.equalsIgnoreCase(
                                        plr.getName())) {
                                    MarryManager.HandleMarriage(plr.getName(),
                                            tgtName);
                                    MarryManager.marryPropose.remove(
                                            plr.getName());
                                    MarryManager.marryPropose.remove(tgtName);
                                } else {
                                    MarryManager.marryPropose.put(plr.getName(),
                                            tgtName);
                                    pTgt.sendMessage(
                                            ChatColor.YELLOW + plr.getName()
                                                    + ChatColor.GREEN
                                                    + " proposes marriage to you!");
                                    plr.sendMessage(
                                            ChatColor.GREEN
                                                    + "You propose marriage to "
                                                    + ChatColor.YELLOW + tgtName);
                                }
                            }
                        }
                    }
                }
            } else {
                this.SendUsage(plr);
            }
        }
    }

    @Override
    public boolean hasPermissionToUse(MC_Player plr) {
        return plr == null || plr.hasPermission("rainbow.marry");
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
        cs.sendMessage(RainbowUtils.RainbowString("============ Marriage ============", "b"));
        cs.sendMessage(ChatColor.GOLD + "/marry " + ChatColor.YELLOW + "PlayerName");
        cs.sendMessage(ChatColor.GOLD + "/marry info " + ChatColor.YELLOW + "PlayerName");
        cs.sendMessage(ChatColor.GOLD + "/divorce " + ChatColor.YELLOW + "PlayerName");
        if (cs.isOp()) {
            cs.sendMessage(ChatColor.LIGHT_PURPLE + "[Admin] " + ChatColor.GOLD + "/marry SelectChurch " + ChatColor.GRAY + "- Configure church location");
        }

        String spouseName = MarryManager.GetSpouse(cs.getName());

        if (spouseName != null) {
            cs.sendMessage(ChatColor.GREEN + "You are married to " + ChatColor.YELLOW + spouseName);
        } else {
            cs.sendMessage(ChatColor.GREEN + "You are single and ready to mingle!");
        }
    }
}
