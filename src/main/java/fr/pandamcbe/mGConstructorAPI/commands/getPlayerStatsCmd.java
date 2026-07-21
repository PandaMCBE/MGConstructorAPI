//package fr.pandamcbe.mGConstructorAPI.commands;
//
//import fr.pandamcbe.commons.ProfilAccount;
//import fr.pandamcbe.commons.StatsAccount;
//import fr.pandamcbe.mgapi.Main;
//import fr.pandamcbe.mgapi.serverutilapi.enums_strings.AllNames;
//import org.bukkit.Bukkit;
//import org.bukkit.command.Command;
//import org.bukkit.command.CommandSender;
//import org.bukkit.entity.Player;
//
//import java.util.Map;
//import java.util.UUID;
//
//public class getPlayerStatsCmd {
//    private boolean verifyName(String serverName){
//        for(String s : Main.getInstance().serverNames){
//            if(!s.equalsIgnoreCase(AllNames.LOBBY.getName())) {
//                if(serverName.equalsIgnoreCase(s))
//                    return true;
//            }
//        }
//        return false;
//    }
//
//    public getPlayerStatsCmd(CommandSender sender, Command cmd, String[] args, Map<UUID, Map<String, Object>> mainMap, Player player) {
//
//        if (sender.hasPermission("pandafun.commands.stats")) {
//            if (cmd.getName().equalsIgnoreCase("stats")) {
//
//                if (args.length == 2) {
//                    final String serverName = args[0];
//                    if (verifyName(serverName)) {
//                        StatsAccount statsAccount = (StatsAccount) Main.getInstance().manipulation.getAccount(mainMap, player.getUniqueId(), AllNames.statsPojo(serverName));
//
//                        player.sendMessage("§b§lMG-Land> §r§f" + args[1] + " §fa actuellement au " + serverName + ":\n§e" + statsAccount.getCoins() + " §6MGCoins\n§d" + statsAccount.getElos() + " §dMGElos\n§c" + statsAccount.getKills() + " §cMGKills\n§a" + statsAccount.getKillstreaks() + " §aMGKillStreaks\n§b" + statsAccount.getWins() + " §bMGWins");
//                    } else {
//                        player.sendMessage("§cNom de serveur incorrècte, veuillez réessayer.");
//                    }
//                } else if (args.length < 2) {
//                    player.sendMessage("§b§lMG-Land §r§c/stats [serverName] [playerName] [stat(coins, elos...)] [add/remove] [amount]");
//                }
//                if (args.length >= 4) {
//                    final String serverName = args[0];
//                    if (verifyName(serverName)) {
//                        Player target = Bukkit.getPlayer(args[1]);
//                        if (target != null) {
//                            StatsAccount statsAccount = (StatsAccount) Main.getInstance().manipulation.getAccount(mainMap, player.getUniqueId(), AllNames.statsPojo(serverName));
//                            editCoins(args, player, target, statsAccount, mainMap);
//                            editElos(args, player, target, statsAccount);
//                            editKills(args, player, target, statsAccount);
//                            editWins(args, player, target, statsAccount);
//                            editKillStreaks(args, player, target, statsAccount);
//                        }
//                    }else {
//                        player.sendMessage("§cNom de serveur incorrècte, veuillez réessayer.");
//                    }
//                }
//            }
//        }
//    }
//
//    public void editCoins(String[] args, Player player, Player target, StatsAccount statsAccount, Map<UUID, Map<String, Object>> mainMap){
//        if (args[2].equalsIgnoreCase("coins")) {
//            if (args[3].equalsIgnoreCase("add")) {
//                if (args.length == 5) {
//
//                        int amount = Integer.parseInt(args[4]);
//                        statsAccount.setCoins(statsAccount.getCoins() + amount);
//                        if(Bukkit.getServer().getServerName().equalsIgnoreCase(AllNames.LOBBY.getName())) {
//                            ProfilAccount profilAccount = (ProfilAccount) Main.getInstance().manipulation.getAccount(mainMap, player.getUniqueId(), AllNames.PROFILPOJO.getName());
//                            profilAccount.setCoins(profilAccount.getCoins() + amount);
//                        }
//
//                        player.sendMessage("§b§lMG-Land> §r§f" + target.getName() + " §fa reçu §e" + amount + " §6MGCoins");
//
//
//                }
//                // /coins remove <le nombre de coins> <le joueur>
//            } else if (args[3].equalsIgnoreCase("remove")) {
//                if (args.length == 5) {
//
//                        int amount = Integer.parseInt(args[4]);
//                        statsAccount.setCoins(statsAccount.getCoins() - amount);
//                        if(Bukkit.getServer().getServerName().equalsIgnoreCase(AllNames.LOBBY.getName())) {
//                            ProfilAccount profilAccount = (ProfilAccount) Main.getInstance().manipulation.getAccount(mainMap, player.getUniqueId(), AllNames.PROFILPOJO.getName());
//                            profilAccount.setCoins(profilAccount.getCoins() - amount);
//                        }
//                        player.sendMessage("§b§lMG-Land>: §r§f" + target.getName() + " §fa perdu §e" + amount + " §6MGCoins");
//
//
//                }
//            }
//        }
//    }
//
//    public void editElos(String[] args, Player player, Player target, StatsAccount statsAccount) {
//        if (args[2].equalsIgnoreCase("elos")) {
//            if (args[3].equalsIgnoreCase("add")) {
//                if (args.length == 5) {
//
//                        int amount = Integer.parseInt(args[4]);
//                        statsAccount.setElos(statsAccount.getElos() + amount);
//                        player.sendMessage("§b§lMG-Land> §r§f" + target.getName() + " §fa reçu §e" + amount + " §dMGElos");
//
//
//                }
//
//            } else if (args[3].equalsIgnoreCase("remove")) {
//                if (args.length == 5) {
//                        int amount = Integer.parseInt(args[4]);
//                        statsAccount.setElos(statsAccount.getElos() - amount);
//                        player.sendMessage("§b§lMG-Land>: §r§f" + target.getName() + " §fa perdu §e" + amount + " §dMGElos");
//                }
//            }
//        }
//    }
//
//    public void editKills(String[] args, Player player, Player target, StatsAccount statsAccount) {
//        if (args[2].equalsIgnoreCase("kills")) {
//            if (args[3].equalsIgnoreCase("add")) {
//                if (args.length == 5) {
//                        int amount = Integer.parseInt(args[4]);
//                        statsAccount.setKills(statsAccount.getKills() + amount);
//                        player.sendMessage("§b§lMG-Land> §r§f" + target.getName() + " §fa reçu §e" + amount + " §cMGKills");
//
//
//                }
//
//            } else if (args[3].equalsIgnoreCase("remove")) {
//                if (args.length == 5) {
//                        int amount = Integer.parseInt(args[4]);
//                        statsAccount.setKills(statsAccount.getKills() - amount);
//                        player.sendMessage("§b§lMG-Land>: §r§f" + target.getName() + " §fa perdu §e" + amount + " §cMGKills");
//
//                }
//            }
//        }
//    }
//
//    public void editWins(String[] args, Player player, Player target, StatsAccount statsAccount){
//        if (args[2].equalsIgnoreCase("wins")) {
//            if (args[3].equalsIgnoreCase("add")) {
//                if (args.length == 5) {
//                        int amount = Integer.parseInt(args[4]);
//                        statsAccount.setWins(statsAccount.getWins() + amount);
//                        player.sendMessage("§b§lMG-Land> §r§f" + target.getName() + " §fa reçu §e" + amount + " §bMGWins");
//
//                }
//
//            } else if (args[3].equalsIgnoreCase("remove")) {
//                if (args.length == 5) {
//                        int amount = Integer.parseInt(args[4]);
//                        statsAccount.setWins(statsAccount.getWins() - amount);
//                        player.sendMessage("§b§lMG-Land>: §r§f" + target.getName() + " §fa perdu §e" + amount + " §bMGWins");
//
//                }
//            }
//        }
//    }
//
//    public void editKillStreaks(String[] args, Player player, Player target, StatsAccount statsAccount){
//        if (args[2].equalsIgnoreCase("killstreaks")) {
//            if (args[3].equalsIgnoreCase("add")) {
//                if (args.length == 5) {
//                        int amount = Integer.parseInt(args[4]);
//                        statsAccount.setKillstreaks(statsAccount.getKillstreaks() + amount);
//                        player.sendMessage("§b§lMG-Land> §r§f" + target.getName() + " §fa reçu §e" + amount + " §bMGKillStreak");
//
//
//                }
//
//            } else if (args[3].equalsIgnoreCase("remove")) {
//                if (args.length == 5) {
//                        int amount = Integer.parseInt(args[4]);
//                        statsAccount.setKillstreaks(statsAccount.getKillstreaks() - amount);
//                        player.sendMessage("§b§lMG-Land>: §r§f" + target.getName() + " §fa perdu §e" + amount + " §bMGKillStreak");
//
//                }
//            }
//        }
//    }
//}