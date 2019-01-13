package com.peaches.epicskyblock;

import com.peaches.epicskyblock.Inventories.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

class Command implements CommandExecutor {
    private static EpicSkyBlock plugin;

    public Command(EpicSkyBlock pl) {
        plugin = pl;
    }

    // Complete redo
    public boolean onCommand(CommandSender cs, org.bukkit.command.Command cmd, String label, String[] args) {
        if (args.length == 0) {
            for (String message : ConfigManager.getInstance().getConfig().getStringList("help")) {
                if (message.contains("%centered%")) {
                    plugin.sendCenteredMessage(cs, ChatColor.translateAlternateColorCodes('&', message.replace("%centered%", "")));
                } else {
                    cs.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                }
            }
            return true;
        }
        try {
            if (args[0].equalsIgnoreCase("givecrystals")) {
                Player p = Bukkit.getPlayer(args[1]);
                if (User.getbyPlayer(p) == null) {
                    User.users.add(new User(p.getName()));
                }
                User u = User.getbyPlayer(p);
                EpicSkyBlock.getSkyblock.sendTitle(p, "&e&lYou have recieved " + args[2] + " Island Crystals.", 20, 40, 20);
                if (u.getIsland() != null) {
                    Island island = u.getIsland();
                    island.addCrystals(Integer.parseInt(args[2]));
                    cs.sendMessage(ChatColor.translateAlternateColorCodes('&', EpicSkyBlock.getSkyblock.getConfig().getString("Options.Prefix") + "&eYou gave " + p.getName() + " " + args[2] + " Island Crystals."));
                    return true;
                }
                cs.sendMessage(ChatColor.translateAlternateColorCodes('&', EpicSkyBlock.getSkyblock.getConfig().getString("Options.Prefix") + "  &e" + p.getName() + " Does not have an island."));
                return true;
            }
        } catch (Exception e) {
            cs.sendMessage(ChatColor.translateAlternateColorCodes('&', EpicSkyBlock.getSkyblock.getConfig().getString("Options.Prefix") + "  &e/is givecrystals <Playername> <Amount>."));
            e.printStackTrace();
            return true;
        }
        try {
            if (args[0].equalsIgnoreCase("visit")) {
                Player p = Bukkit.getPlayer(args[1]);
                if (User.getbyPlayer(p) == null) {
                    User.users.add(new User(p.getName()));
                }
                User u = User.getbyPlayer(p);
                if (u.getIsland() != null) {
                    Island island = u.getIsland();
                    ((Player) cs).teleport(island.gethome());
                    return true;
                }
                cs.sendMessage(ChatColor.translateAlternateColorCodes('&', EpicSkyBlock.getSkyblock.getConfig().getString("Options.Prefix") + "  &e" + p.getName() + " Does not have an island."));
                return true;
            }
        } catch (Exception e) {
            cs.sendMessage(ChatColor.translateAlternateColorCodes('&', EpicSkyBlock.getSkyblock.getConfig().getString("Options.Prefix") + "  &e/is givecrystals <Playername> <Amount>."));
            e.printStackTrace();
            return true;
        }
        if (args[0].equalsIgnoreCase("recalculate")) {
            EpicSkyBlock.getSkyblock.calculateworth();
            cs.sendMessage(ChatColor.translateAlternateColorCodes('&', EpicSkyBlock.getSkyblock.getConfig().getString("Options.Prefix") + "  Recalculating Island Top."));
        }
        if (cs instanceof Player) {
            Player p = (Player) cs;
            if (args[0].equalsIgnoreCase("top")) {
                HashMap<String, Integer> worth = new HashMap<>();
                for (Island island : IslandManager.getIslands()) {
                    worth.put(island.getownername(), island.getLevel());
                }
                // Order HashMap
                EpicSkyBlock.getSkyblock.sendCenteredMessage(p, "&8&m----------------&7 &8< &eRichest Islands &8> &8&m----------------&7");
                int i = 1;
                Map<String, Integer> sorted = worth.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue())).collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));

                for (String name : sorted.keySet()) {
                    if (!name.equals("")) {
                        if (i == 10) return true;
                        p.sendMessage("#" + i + ". " + ChatColor.GRAY + name + " - " + ChatColor.YELLOW + "$" + sorted.get(name));
                        i++;
                    }
                }
                return true;
            }
            if (args[0].equalsIgnoreCase("regen")) {
                if (User.getbyPlayer(p) == null) {
                    User.users.add(new User(p.getName()));
                }
                if (User.getbyPlayer(p).getIsland() != null) {
                    User.getbyPlayer(p).getIsland().regen();
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', EpicSkyBlock.getSkyblock.getConfig().getString("Options.Prefix") + "  &eRegenerating Island..."));
                    return true;
                }
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', EpicSkyBlock.getSkyblock.getConfig().getString("Options.Prefix") + "  &eYou do not have an island."));
                return true;
            }
            if (args[0].equalsIgnoreCase("warps") || args[0].equalsIgnoreCase("warp")) {
                p.openInventory(WarpGUI.inv(User.getbyPlayer(p).getIsland()));
                return true;
            }
            if (args[0].equalsIgnoreCase("setwarp")) {
                if (User.getbyPlayer(p) == null) {
                    User.users.add(new User(p.getName()));
                }
                if (User.getbyPlayer(p).getIsland() != null) {
                    Island is = User.getbyPlayer(p).getIsland();
                    if (is.getWarp1() == null) {
                        is.setWarp1(p.getLocation());
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', EpicSkyBlock.getSkyblock.getConfig().getString("Options.Prefix") + "  &eWarp Set."));
                        return true;
                    }
                    if (is.getWarp2() == null && is.getWarpCount() > 1) {
                        is.setWarp2(p.getLocation());
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', EpicSkyBlock.getSkyblock.getConfig().getString("Options.Prefix") + "  &eWarp Set."));
                        return true;
                    }
                    if (is.getWarp3() == null && is.getWarpCount() > 1) {
                        is.setWarp3(p.getLocation());
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', EpicSkyBlock.getSkyblock.getConfig().getString("Options.Prefix") + "  &eWarp Set."));
                        return true;
                    }
                    if (is.getWarp4() == null && is.getWarpCount() > 2) {
                        is.setWarp4(p.getLocation());
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', EpicSkyBlock.getSkyblock.getConfig().getString("Options.Prefix") + "  &eWarp Set."));
                        return true;
                    }
                    if (is.getWarp5() == null && is.getWarpCount() > 2) {
                        is.setWarp5(p.getLocation());
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', EpicSkyBlock.getSkyblock.getConfig().getString("Options.Prefix") + "  &eWarp Set."));
                        return true;
                    }
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', EpicSkyBlock.getSkyblock.getConfig().getString("Options.Prefix") + "  &eYou have no warps left, do /is upgrade to get more."));
                    return true;
                }
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', EpicSkyBlock.getSkyblock.getConfig().getString("Options.Prefix") + "  &eYou dont have an island."));
                return true;
            }
            if (args[0].equalsIgnoreCase("crystals")) {
                if (User.getbyPlayer(p) == null) {
                    User.users.add(new User(p.getName()));
                }
                User u = User.getbyPlayer(p);
                if (u.getIsland() != null) {
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', EpicSkyBlock.getSkyblock.getConfig().getString("Options.Prefix") + "  &eYou have " + u.getIsland().getCrystals() + " Island Crystals."));
                    return true;
                }
            }

            if (args[0].equalsIgnoreCase("bypass")) {
                if (p.hasPermission("EpicSkyblock.bypass")) {
                    if (User.getbyPlayer(p) == null) {
                        User.users.add(new User(p.getName()));
                    }
                    User.getbyPlayer(p).setBypass(!User.getbyPlayer(p).getBypass());
                    if (User.getbyPlayer(p).getBypass()) {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', EpicSkyBlock.getSkyblock.getConfig().getString("Options.Prefix") + "  &eBypass Mode Enabled."));
                    } else {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', EpicSkyBlock.getSkyblock.getConfig().getString("Options.Prefix") + "  &eBypass Mode Disabled."));
                    }
                    return true;
                }
            }
            if (args[0].equalsIgnoreCase("fly")) {
                if (User.getbyPlayer(p) == null) {
                    User.users.add(new User(p.getName()));
                }
                if (User.getbyPlayer(p).getIsland() != null) {
                    if (User.getbyPlayer(p).getIsland().getFlyBoosterActive()) {
                        if (p.getAllowFlight()) {
                            p.setAllowFlight(false);
                            p.setFlying(false);
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', EpicSkyBlock.getSkyblock.getConfig().getString("Options.Prefix") + "  &eFly disabled."));
                        } else {
                            p.setAllowFlight(true);
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', EpicSkyBlock.getSkyblock.getConfig().getString("Options.Prefix") + "  &eFly enabled."));
                        }
                    } else {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', EpicSkyBlock.getSkyblock.getConfig().getString("Options.Prefix") + "  &eYou do not have permissions."));
                    }
                    return true;
                }
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', EpicSkyBlock.getSkyblock.getConfig().getString("Options.Prefix") + "  &eYou do not have an island."));
                return true;
            }
            if (args[0].equalsIgnoreCase("chat")) {
                if (User.getbyPlayer(p) == null) {
                    User.users.add(new User(p.getName()));
                }
                if (User.getbyPlayer(p).getIsland() != null) {
                    User.getbyPlayer(p).setChat(!User.getbyPlayer(p).getChat());
                    if (User.getbyPlayer(p).getChat()) {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', EpicSkyBlock.getSkyblock.getConfig().getString("Options.Prefix") + "  &eIsland chat has been enabled."));
                    } else {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', EpicSkyBlock.getSkyblock.getConfig().getString("Options.Prefix") + "  &eIsland chat has been disabled."));
                    }
                } else {
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', EpicSkyBlock.getSkyblock.getConfig().getString("Options.Prefix") + "  &eYou do not have an island."));
                }
                return true;
            }
            if (args[0].equalsIgnoreCase("upgrade") || args[0].equalsIgnoreCase("upgrades")) {
                if (User.getbyPlayer(p) == null) {
                    User.users.add(new User(p.getName()));
                }
                if (User.getbyPlayer(p).getIsland() != null) {
                    p.openInventory(UpgradesGUI.inv(User.getbyPlayer(p).getIsland()));
                    return true;
                }
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', EpicSkyBlock.getSkyblock.getConfig().getString("Options.Prefix") + "  &eYou do not have an island."));
                return true;
            }
            if (args[0].equalsIgnoreCase("boosters") || args[0].equalsIgnoreCase("booster")) {
                if (User.getbyPlayer(p) == null) {
                    User.users.add(new User(p.getName()));
                }
                if (User.getbyPlayer(p).getIsland() != null) {
                    p.openInventory(BoostersGUI.inv(User.getbyPlayer(p).getIsland()));
                    return true;
                }
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', EpicSkyBlock.getSkyblock.getConfig().getString("Options.Prefix") + "  &eYou do not have an island."));
                return true;
            }
            if (args[0].equalsIgnoreCase("missions") || args[0].equalsIgnoreCase("mission")) {
                if (User.getbyPlayer(p) == null) {
                    User.users.add(new User(p.getName()));
                }
                if (User.getbyPlayer(p).getIsland() != null) {
                    p.openInventory(MissionsGUI.inv(User.getbyPlayer(p).getIsland()));
                    return true;
                }
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', EpicSkyBlock.getSkyblock.getConfig().getString("Options.Prefix") + "  &eYou do not have an island."));
                return true;
            }
            if (args[0].equalsIgnoreCase("leave")) {
                if (User.getbyPlayer(p) == null) {
                    User.users.add(new User(p.getName()));
                }
                if (User.getbyPlayer(p).getIsland() != null) {
                    if (User.getbyPlayer(p).getIsland().getownername().equals(p.getName())) {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', EpicSkyBlock.getSkyblock.getConfig().getString("Options.Prefix") + "  &eTo leave your island you must transfer ownership to another player.."));
                        return true;
                    }
                    User.getbyPlayer(p).getIsland().removeUser(p.getName());
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', EpicSkyBlock.getSkyblock.getConfig().getString("Options.Prefix") + "  &eYou have left your island."));
                    return true;
                }
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', EpicSkyBlock.getSkyblock.getConfig().getString("Options.Prefix") + "  &eYou do not have an island."));
                return true;
            }
            if (args[0].equalsIgnoreCase("sethome")) {
                if (User.getbyPlayer(p) == null) {
                    User.users.add(new User(p.getName()));
                }
                if (User.getbyPlayer(p).getIsland() != null) {
                    if (User.getbyPlayer(p).getIsland().getownername().equals(p.getName())) {
                        if (IslandManager.getislandviablock(p.getLocation().getBlock()) == User.getbyPlayer(p).getIsland()) {
                            User.getbyPlayer(p).getIsland().setHome(p.getLocation());
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', EpicSkyBlock.getSkyblock.getConfig().getString("Options.Prefix") + "  &eIsland home set at your location.."));
                            return true;
                        }
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', EpicSkyBlock.getSkyblock.getConfig().getString("Options.Prefix") + "  &eYou must be on your island."));
                        return true;
                    }
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', EpicSkyBlock.getSkyblock.getConfig().getString("Options.Prefix") + "  &eOnly the island owner can do this."));
                    return true;
                }
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', EpicSkyBlock.getSkyblock.getConfig().getString("Options.Prefix") + "  &eYou do not have an island."));
                return true;
            }
            if (args[0].equalsIgnoreCase("delete")) {
                if (User.getbyPlayer(p) == null) {
                    User.users.add(new User(p.getName()));
                }
                IslandManager.deleteIsland(p);
                return true;
            }
            if (args[0].equalsIgnoreCase("home")) {
                if (User.getbyPlayer(p) == null) {
                    User.users.add(new User(p.getName()));
                }
                if (User.getbyPlayer(p).getIsland() == null) {
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', EpicSkyBlock.getSkyblock.getConfig().getString("Options.Prefix") + "  &eYou do not have an island."));
                    return true;
                } else {
                    p.teleport(User.getbyPlayer(p).getIsland().gethome());
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', EpicSkyBlock.getSkyblock.getConfig().getString("Options.Prefix") + "  &eTeleporting to island..."));
                    return true;
                }
            }
            if (args[0].equalsIgnoreCase("list")) {
                if (User.getbyPlayer(p) == null) {
                    User.users.add(new User(p.getName()));
                }
                User user = User.getbyPlayer(p);
                if (user.getIsland() != null) {
                    p.openInventory(Members.inv(user.getIsland()));
                    return true;
                } else {
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', EpicSkyBlock.getSkyblock.getConfig().getString("Options.Prefix") + "  &eYou do not have an island."));
                    return true;
                }
            }
            if (args[0].equalsIgnoreCase("create")) {
                if (User.getbyPlayer(p) == null) {
                    User.users.add(new User(p.getName()));
                }
                if (User.getbyPlayer(p).getIsland() == null) {
                    IslandManager.createIsland(p);
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', EpicSkyBlock.getSkyblock.getConfig().getString("Options.Prefix") + "  &eIsland Created."));
                    return true;
                } else {
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', EpicSkyBlock.getSkyblock.getConfig().getString("Options.Prefix") + "  &eYou already have an island."));
                    return true;
                }
            }
            if (args[0].equalsIgnoreCase("version")) {
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lPlugin Name : &eEpicSkyBlock"));
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lPlugin Version : &e" + plugin.getDescription().getVersion()));
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lPlugin Author : &ePeaches_MLG"));
                return true;
            }
            if (args[0].equalsIgnoreCase("reload")) {
                ConfigManager.getInstance().reloadConfig();
                EpicSkyBlock.getSkyblock.reloadConfig();
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', EpicSkyBlock.getSkyblock.getConfig().getString("Options.Prefix") + "  &ePlugin Reloaded."));
                return true;
            }
            try {
                if (args[0].equalsIgnoreCase("join")) {
                    Player player = Bukkit.getPlayer(args[1]);
                    if (User.getbyPlayer(p) == null) {
                        User.users.add(new User(p.getName()));
                    }
                    if (User.getbyPlayer(player) == null) {
                        User.users.add(new User(player.getName()));
                    }
                    if (player == null) {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', EpicSkyBlock.getSkyblock.getConfig().getString("Options.Prefix") + "  &ePlayer not found."));
                        return true;
                    }
                    if (User.getbyPlayer(player) == null) {
                        User.users.add(new User(player.getName()));
                    }
                    if (User.getbyPlayer(p).getIsland() != null) {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', EpicSkyBlock.getSkyblock.getConfig().getString("Options.Prefix") + "  &eYou are already apart of an island."));
                        return true;
                    }
                    if (User.getbyPlayer(player).getIsland() == null) {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', EpicSkyBlock.getSkyblock.getConfig().getString("Options.Prefix") + "  &eThat player is not apart of an island."));
                        return true;
                    }
                    if (User.getbyPlayer(p).getInvites().contains(User.getbyPlayer(player).getIsland().getownername())) {
                        if (User.getbyPlayer(player).getIsland().getPlayers().size() >= EpicSkyBlock.getSkyblock.getConfig().getInt("Upgrades.Members." + User.getbyPlayer(player).getIsland().getMemberCount())) {
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', EpicSkyBlock.getSkyblock.getConfig().getString("Options.Prefix") + "  &eThe maximum amount of players has already been reached."));
                            return true;
                        }
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', EpicSkyBlock.getSkyblock.getConfig().getString("Options.Prefix") + "  &eYou have joined an island."));
                        User.getbyPlayer(player).getIsland().addUser(p.getName());
                        p.teleport(User.getbyPlayer(p).getIsland().gethome());
                        for (String pla : User.getbyPlayer(player).getIsland().getPlayers()) {
                            Player i = Bukkit.getPlayer(pla);
                            if (i != null) {
                                i.sendMessage(ChatColor.translateAlternateColorCodes('&', EpicSkyBlock.getSkyblock.getConfig().getString("Options.Prefix") + "  &e" + p.getName() + " has joined your island."));
                            }

                        }
                        return true;
                    } else {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', EpicSkyBlock.getSkyblock.getConfig().getString("Options.Prefix") + "  &eYou do not have an invite for that island."));
                        return true;
                    }
                }
            } catch (Exception e) {
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', EpicSkyBlock.getSkyblock.getConfig().getString("Options.Prefix") + "  &e/is join <PlayerName>."));
                e.printStackTrace();
                return true;
            }
            try {
                if (args[0].equalsIgnoreCase("leader") || args[0].equalsIgnoreCase("owner")) {
                    if (User.getbyPlayer(p) == null) {
                        User.users.add(new User(p.getName()));
                    }
                    String player = args[1];
                    if (User.getbyPlayer(player) == null) {
                        User.users.add(new User(player));
                    }
                    if (User.getbyPlayer(p).getIsland() == null) {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', EpicSkyBlock.getSkyblock.getConfig().getString("Options.Prefix") + "  &eYou do not have an island."));
                        return true;
                    }
                    if (User.getbyPlayer(p).getIsland().getownername().equalsIgnoreCase(p.getName())) {
                        if (p.getName().equalsIgnoreCase(player)) {
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', EpicSkyBlock.getSkyblock.getConfig().getString("Options.Prefix") + "  &eYou cannot give yourself the leader role."));
                            return true;
                        }
                        if (User.getbyPlayer(p).getIsland().getPlayers().contains(player)) {
                            User.getbyPlayer(p).getIsland().setowner(player);
                            if (Bukkit.getPlayer(player) != null) {
                                Bukkit.getPlayer(player).sendMessage(ChatColor.translateAlternateColorCodes('&', EpicSkyBlock.getSkyblock.getConfig().getString("Options.Prefix") + "  &eYou have been given the owner role by " + p.getName()));
                            }
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', EpicSkyBlock.getSkyblock.getConfig().getString("Options.Prefix") + "  &eYou have transfered ownership to " + player));
                            return true;
                        } else {
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', EpicSkyBlock.getSkyblock.getConfig().getString("Options.Prefix") + "  &eThis player is not in your island."));
                            return true;
                        }
                    } else {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', EpicSkyBlock.getSkyblock.getConfig().getString("Options.Prefix") + "  &eOnly the island owner can transfer ownership."));
                        return true;
                    }
                }
            } catch (Exception e) {
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', EpicSkyBlock.getSkyblock.getConfig().getString("Options.Prefix") + "  &e/is leader <PlayerName>."));
                return true;
            }
            try {
                if (args[0].equalsIgnoreCase("kick")) {
                    if (User.getbyPlayer(p) == null) {
                        User.users.add(new User(p.getName()));
                    }
                    String player = args[1];
                    if (User.getbyPlayer(player) == null) {
                        User.users.add(new User(player));
                    }
                    if (User.getbyPlayer(p).getIsland() == null) {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', EpicSkyBlock.getSkyblock.getConfig().getString("Options.Prefix") + "  &eYou do not have an island."));
                        return true;
                    }
                    if (p.getName().equalsIgnoreCase(player)) {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', EpicSkyBlock.getSkyblock.getConfig().getString("Options.Prefix") + "  &eYou cannot kick yourself."));
                        return true;
                    }
                    if (User.getbyPlayer(p).getIsland().getownername().equalsIgnoreCase(player)) {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', EpicSkyBlock.getSkyblock.getConfig().getString("Options.Prefix") + "  &eYou cannot kick the owner."));
                        return true;
                    }
                    if (User.getbyPlayer(p).getIsland().getPlayers().contains(player)) {
                        User.getbyPlayer(player).getIsland().getPlayers().remove(player);
                        User.getbyPlayer(player).setIsland(null);
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', EpicSkyBlock.getSkyblock.getConfig().getString("Options.Prefix") + "  &eYou have kicked " + player));
                        if (Bukkit.getPlayer(player) != null) {
                            Bukkit.getPlayer(player).sendMessage(ChatColor.translateAlternateColorCodes('&', EpicSkyBlock.getSkyblock.getConfig().getString("Options.Prefix") + "  &eYou have beed kicked by " + p.getName()));
                        }
                        return true;
                    } else {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', EpicSkyBlock.getSkyblock.getConfig().getString("Options.Prefix") + "  &eThis player is not in your island."));
                        return true;
                    }
                }
            } catch (Exception e) {
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', EpicSkyBlock.getSkyblock.getConfig().getString("Options.Prefix") + "  &e/is kick <PlayerName>."));
                return true;
            }
            try {
                if (args[0].equalsIgnoreCase("deinvite") || args[0].equalsIgnoreCase("uninvite")) {
                    if (User.getbyPlayer(p) == null) {
                        User.users.add(new User(p.getName()));
                    }
                    Player player = Bukkit.getPlayer(args[1]);
                    if (player == null) {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', EpicSkyBlock.getSkyblock.getConfig().getString("Options.Prefix") + "  &ePlayer not found."));
                        return true;
                    }
                    if (User.getbyPlayer(player) == null) {
                        User.users.add(new User(player.getName()));
                    }
                    if (User.getbyPlayer(p).getIsland() == null) {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', EpicSkyBlock.getSkyblock.getConfig().getString("Options.Prefix") + "  &eYou do not have an island."));
                        return true;
                    }
                    if (p == player) {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', EpicSkyBlock.getSkyblock.getConfig().getString("Options.Prefix") + "  &eYou cannot invite yourself."));
                        return true;
                    }
                    if (User.getbyPlayer(player).getInvites().contains(User.getbyPlayer(p).getIsland().getownername())) {
                        User.getbyPlayer(player).getInvites().remove(User.getbyPlayer(p).getIsland().getownername());
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', EpicSkyBlock.getSkyblock.getConfig().getString("Options.Prefix") + "  &eInvite has been revoked from " + player.getName()));
                        return true;
                    } else {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', EpicSkyBlock.getSkyblock.getConfig().getString("Options.Prefix") + "  &eThis player has no active Invite to your island."));
                        return true;
                    }
                }
            } catch (Exception e) {
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', EpicSkyBlock.getSkyblock.getConfig().getString("Options.Prefix") + "  &e/is uninvite <PlayerName>."));
                return true;
            }
            try {
                if (args[0].equalsIgnoreCase("invite")) {
                    Player player = Bukkit.getPlayer(args[1]);
                    if (User.getbyPlayer(p) == null) {
                        User.users.add(new User(p.getName()));
                    }
                    if (User.getbyPlayer(player) == null) {
                        User.users.add(new User(player.getName()));
                    }
                    if (player == null) {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', EpicSkyBlock.getSkyblock.getConfig().getString("Options.Prefix") + "  &ePlayer not found."));
                        return true;
                    }
                    if (User.getbyPlayer(player) == null) {
                        User.users.add(new User(player.getName()));
                    }
                    if (User.getbyPlayer(p).getIsland() == null) {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', EpicSkyBlock.getSkyblock.getConfig().getString("Options.Prefix") + "  &eYou do not have an island."));
                        return true;
                    }
                    if (p == player) {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', EpicSkyBlock.getSkyblock.getConfig().getString("Options.Prefix") + "  &eYou cannot invite yourself."));
                        return true;
                    }
                    if (User.getbyPlayer(player).getInvites().contains(User.getbyPlayer(p).getIsland().getownername())) {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', EpicSkyBlock.getSkyblock.getConfig().getString("Options.Prefix") + "  &eThis player already has an active invite."));
                        return true;
                    }
                    User.getbyPlayer(player).getInvites().add(User.getbyPlayer(p).getIsland().getownername());
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', EpicSkyBlock.getSkyblock.getConfig().getString("Options.Prefix") + "  &eInvite sent to " + player.getName()));
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', EpicSkyBlock.getSkyblock.getConfig().getString("Options.Prefix") + "  &eYou have been invited to join " + p.getName() + "'s Island."));
                    return true;
                }
            } catch (Exception e) {
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', EpicSkyBlock.getSkyblock.getConfig().getString("Options.Prefix") + "  &e/is invite <PlayerName>."));
                return true;
            }
            for (String message : ConfigManager.getInstance().getConfig().getStringList("help")) {
                if (message.contains("%centered%")) {
                    plugin.sendCenteredMessage(p, ChatColor.translateAlternateColorCodes('&', message.replace("%centered%", "")));
                } else {
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                }
            }
        } else {
            cs.sendMessage("This command must be executed by a player");
            return true;
        }
        return false;
    }
}