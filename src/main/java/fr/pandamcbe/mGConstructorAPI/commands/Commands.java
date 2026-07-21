package fr.pandamcbe.mGConstructorAPI.commands;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class Commands implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (cmd.getName().equalsIgnoreCase("report")) {
                player.sendMessage("§b§lMG-Land §3§l> §6§lVoulez-vous §e§lreport §d§l§oquelqu'un §6§l?");

                TextComponent component = new TextComponent(TextComponent.fromLegacyText("§a§l§nCliquez ici"));
                // Add a click event to the component.
                component.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://docs.google.com/forms/d/e/1FAIpQLSfFTdxrUsv0d9dzn69yCkE6MxgdgMegjUoU21dyY93KmHpQAA/viewform?usp=pp_url"));

                // Send it!
                player.spigot().sendMessage(component);
                return true;

            }
//            if (label.equalsIgnoreCase("arenas")) {
//                if (args.length == 0) {
//                    return true;
//                }
//                if(args[0].equalsIgnoreCase("create")) {
//                    if (args.length < 3) {
//                        player.sendMessage("§e§lTapez la commande §f§l/arenas create (nom arène) || addLoc (nom arène) [coordonnées]");
//                        return true;
//                    }
//                    String arenaName = args[1];
//                    Arena arena = new Arena(arenaName);
//
//                    OneShot.getInstance().arenaConfig.set("arenas. " + arenaName);
//                    arenaFile.saveArenaConfig();
//                    OneShot.getInstance().arenaManager.getArenas().add(arena);
//                    player.sendMessage("Vous venez de créer l'arène §a§l" + arenaName + " !");
//                }
//
//                if(args[0].equalsIgnoreCase("addLoc")) {
//
//                }
//
//            }
//
//            return true;

        }
        return false;
    }
}