package org.projectrainbow.marriage;


import PluginReference.ChatColor;
import PluginReference.MC_Player;
import PluginReference.RainbowUtils;
import com.google.common.io.Files;
import joebkt._MarriageData;
import joebkt._MarriageStats;
import joebkt._SerializableLocation;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class MarryManager {

    public static Map<String, _MarriageData> marryMap = new ConcurrentHashMap();
    public static Map<String, _MarriageStats> marryStats = new ConcurrentHashMap();
    public static Map<String, String> marryPropose = new ConcurrentHashMap();
    public static _SerializableLocation churchLoc1 = null;
    public static _SerializableLocation churchLoc2 = null;
    public static String Filename = "Marriage.dat";

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

    public MarryManager() {
    }

    public static void LoadMarriageData() {
        try {
            File exc = new File("RainbowData" + File.separator + Filename);
            File oldFile = new File(Filename);

            if (oldFile.exists()) {
                Files.move(oldFile, exc);
            }

            if (!exc.exists()) {
                RainbowUtils.getServer().log(
                        ChatColor.YELLOW + "No Marriage File found. "
                                + ChatColor.GREEN + "I will start a new one!");
                return;
            }

            FileInputStream f = new FileInputStream(exc);
            ObjectInputStream s = new ObjectInputStream(f);

            marryMap = (ConcurrentHashMap) s.readObject();
            churchLoc1 = (_SerializableLocation) s.readObject();
            churchLoc2 = (_SerializableLocation) s.readObject();
            marryStats = (ConcurrentHashMap) s.readObject();
            RainbowUtils.getServer().log(
                    String.format("Loaded %d marriages.",
                            new Object[]{Integer.valueOf(marryMap.size() / 2)}));
            s.close();
        } catch (Throwable var4) {
            var4.printStackTrace();
            RainbowUtils.getServer().log(
                    ChatColor.RED + "LoadMarriageData: " + var4.toString());
            marryMap = new ConcurrentHashMap();
            marryStats = new ConcurrentHashMap();
            churchLoc1 = null;
            churchLoc2 = null;
        }

    }

    public static void SaveMarriageData() {
        try {
            long exc = System.currentTimeMillis();
            File file = new File("RainbowData" + File.separator + Filename);
            FileOutputStream f = new FileOutputStream(file);
            ObjectOutputStream s = new ObjectOutputStream(f);

            s.writeObject(marryMap);
            s.writeObject(churchLoc1);
            s.writeObject(churchLoc2);
            s.writeObject(marryStats);
            s.close();
            long msEnd = System.currentTimeMillis();
            String msg = ChatColor.YELLOW
                    + String.format("%-20s: %5d marriages.   Took %3d ms",
                    new Object[]{
                            "Marriages",
                            Integer.valueOf(marryMap.size() / 2), Long.valueOf(msEnd - exc)});

            RainbowUtils.getServer().log(msg);
        } catch (Throwable var8) {
            RainbowUtils.getServer().log(
                    ChatColor.RED + "SaveMarriageData: " + var8.toString());
        }

    }

    public static boolean IsInChurch(int x, int y, int z, int dimen) {
        if (churchLoc1 != null && churchLoc2 != null) {
            if (churchLoc1.dimension != dimen) {
                return false;
            } else {
                int minX = (int) Math.min(churchLoc1.x, churchLoc2.x);

                if (x < minX) {
                    return false;
                } else {
                    int maxX = (int) Math.max(churchLoc1.x, churchLoc2.x);

                    if (x > maxX) {
                        return false;
                    } else {
                        int minZ = (int) Math.min(churchLoc1.z, churchLoc2.z);

                        if (z < minZ) {
                            return false;
                        } else {
                            int maxZ = (int) Math.max(churchLoc1.z, churchLoc2.z);

                            if (z > maxZ) {
                                return false;
                            } else {
                                int minY = (int) Math.min(churchLoc1.y,
                                        churchLoc2.y);

                                if (y < minY) {
                                    return false;
                                } else {
                                    int maxY = (int) Math.max(churchLoc1.y,
                                            churchLoc2.y);

                                    return y <= maxY;
                                }
                            }
                        }
                    }
                }
            }
        } else {
            RainbowUtils.getServer().log(
                    ChatColor.RED
                            + "Warning: No church defined yet. No marriage possible.");
            RainbowUtils.getServer().log(
                    ChatColor.RED
                            + "In game do \'/church SelectChurch\' for more information.");
            return false;
        }
    }

    public static void ShowMarriageInfo(MC_Player p, String argTgtName) {
        String exactName = RainbowUtils.getServer().getPlayerExactName(argTgtName);

        if (exactName == null) {
            p.sendMessage(
                    ChatColor.RED + "No player known by name: "
                            + ChatColor.YELLOW + argTgtName);
        } else {
            _MarriageData md = (_MarriageData) marryMap.get(exactName);

            if (md == null) {
                p.sendMessage(
                        ChatColor.YELLOW + exactName + ChatColor.GREEN
                                + " is not married.");
            } else {
                p.sendMessage(
                        RainbowUtils.RainbowString(
                                "-------------------------------------"));
                p.sendMessage(
                        ChatColor.YELLOW + md.Person1 + ChatColor.GREEN
                                + " and " + ChatColor.YELLOW + md.Person2);
                p.sendMessage(
                        ChatColor.AQUA + "Married: " + ChatColor.WHITE
                                + dateFormat.format(md.msDateMarried));
                String strAttend = RainbowUtils.GetCommaList(md.Attendees);

                if (strAttend.length() <= 0) {
                    strAttend = "None";
                }

                p.sendMessage(
                        ChatColor.AQUA + "Attendees: " + ChatColor.GRAY
                                + strAttend);
            }
        }
    }

    public static String GetSpouse(String name) {
        _MarriageData md = (_MarriageData) marryMap.get(name);

        if (md == null) {
            return null;
        } else if (name.equalsIgnoreCase(md.Person1)) {
            return md.Person2;
        } else if (name.equalsIgnoreCase(md.Person2)) {
            return md.Person1;
        } else {
            RainbowUtils.getServer().log(
                    ChatColor.RED + "WARNING: Orphan Marriage Data for: " + name);
            return null;
        }
    }

    public static ArrayList<String> GetPlayersInChurch() {
        ArrayList list = new ArrayList();

        for (MC_Player p : RainbowUtils.getServer().getPlayers()) {
            if (IsInChurch((int) p.getLocation().x, (int) p.getLocation().y, (int) p.getLocation().z,
                    p.getLocation().dimension)) {
                list.add(p.getName());
            }
        }

        return list;
    }

    public static void HandleMarriage(String name1, String name2) {
        _MarriageData md = new _MarriageData();

        md.msDateMarried = Long.valueOf(System.currentTimeMillis());
        md.Person1 = name1;
        md.Person2 = name2;
        md.Attendees = GetPlayersInChurch();
        md.Attendees.remove(name1);
        md.Attendees.remove(name2);
        marryMap.put(name1, md);
        marryMap.put(name2, md);
        RainbowUtils.getServer().broadcastMessage(
                ChatColor.GREEN + "Congratulations! " + ChatColor.YELLOW + name1
                        + " has married " + name2 + ".");
        String strAttend = RainbowUtils.GetCommaList(md.Attendees);

        if (strAttend.length() > 0) {
            RainbowUtils.getServer().broadcastMessage(
                    ChatColor.AQUA + "Witnesses: " + ChatColor.GRAY + strAttend);
        }

    }

    public static void HandleDivorce(MC_Player p, String name2) {
        String name1 = p.getName();
        String spouse = GetSpouse(name1);

        if (spouse == null) {
            p.sendMessage(ChatColor.RED + "You are not married!");
        } else if (!spouse.equalsIgnoreCase(name2)) {
            p.sendMessage(
                    ChatColor.RED + "You are married to " + ChatColor.YELLOW
                            + spouse + ChatColor.RED + " not " + ChatColor.YELLOW
                            + name2);
        } else {
            marryMap.remove(name1);
            marryMap.remove(spouse);
            RainbowUtils.getServer().broadcastMessage(
                    ChatColor.RED + "Oh No! " + ChatColor.YELLOW + name1
                            + " has divorced " + spouse + ".");
        }
    }
}
