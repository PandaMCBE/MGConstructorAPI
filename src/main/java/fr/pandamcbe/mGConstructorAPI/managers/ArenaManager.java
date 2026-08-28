package fr.pandamcbe.mGConstructorAPI.managers;

import fr.pandamcbe.mGConstructorAPI.MGConstructorAPI;
import fr.pandamcbe.mGConstructorAPI.Team;
import fr.pandamcbe.mGConstructorAPI.arenas.Arena;
import fr.pandamcbe.mGConstructorAPI.arenas.State;
import fr.pandamcbe.mGConstructorAPI.arenas.TeamArena;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class ArenaManager extends OptionalsHooksManager {

//    private final WaitingRoom waitingRoomManager = MGConstructorAPI.getInstance().getWaitingRoomManager();
    private final Team team = MGConstructorAPI.getInstance().getTeam();

    public Arena getArenaByPlayer(UUID uuid, List<Arena> arenaList) {
        if(uuid != null) {
            for (Arena arena : arenaList) {
                if (arena.getAlluuids().contains(uuid)) {
                    return arena;
                }
            }
        }
        return null;
    }

    public Arena getNextArena(List<Arena> arenaList, String mgmode) { //Récupérer une arène non démarrée
        for (Arena arena : arenaList) { //Pour chaque objet Arena qui sont dans la liste définie de toutes les arènes existantes
            if (arena.getMode().equals(mgmode)) {
                if (arena.getState().equals(State.WAITING) ||
                        arena.getState().equals(State.FINISHED)) { //Si l'arène n'a pas une liste d'ensemble de joueurs vide et si l'arène n'est pas démarrée. En gros, c'est pour repérer des arènes où des joueurs sont encore en attente, donc pas démarrée mais contient quand même des personnes dans sa liste.
                    return arena; //retourner l'arène qui respecte ces conditions et arrêter la boucle
                }
            }
        }
        return null; //retourner null par défaut si aucune arène n'est disponible
    }

    public String getArena(Arena arena){
        if(arena == null) { //Si la recherche d'arène libre n'a rien retourné -> null
            return "§cIndisponible"; //Retourner un message indiquant l'indisponibilité
        }else {
            return arena.getName(); //Sinon, afficher le nom de l'arène sélectionnée
        }
    }

    public void lookForArena(List<Arena> arenaList, String mgmode, Player player){
        for (Arena arena : arenaList) { //Pour chaque objet Arena qui sont dans la liste définie de toutes les arènes existantes
            if (arena.getMode().equals(mgmode)) {
                if (arena.getState().equals(State.WAITING) || arena.getState().equals(State.FINISHED)) { //Si l'arène n'a pas une liste d'ensemble de joueurs vide et si l'arène n'est pas démarrée. En gros, c'est pour repérer des arènes où des joueurs sont encore en attente, donc pas démarrée mais contient quand même des personnes dans sa liste.
                    joinArena(arenaList, arena, player);
                } else {
                    arena.waitingRoom.waiting(arenaList, arena, player);
                }
            }
        }
    }

    public void joinArena(List<Arena> arenaList, Arena nextArena, Player p) {
        UUID uuid = p.getUniqueId();

        if (nextArena != null) {

            if(!nextArena.getAlluuids().contains(uuid) && nextArena.getAlluuids().size() < nextArena.getMaxPlayersAllowed()) {
                nextArena.getAlluuids().add(uuid);
                nextArena.waitingRoom.waiting(arenaList, nextArena, p);
            }

            //Comment être sûr que les joueurs d'un duel soient toujours mis en relation même après avoir été placé en attente ?
            //Envisager le retrait de joinArena de ArenaManager pour le placer dans MGPlayerManager et MGDuelManager qui devraient
            //d'ailleurs trouver un meilleur nom.
            team.addPlayer((TeamArena) nextArena, p);

            if (nextArena.getAlluuids().size() >= nextArena.getMinimumPlayers()) { //Si la liste de file d'attente depuis la classe Main est supérieur ou égale 2. C'est pour lancer une partie même n'étant peu.
                if(nextArena.getAlluuids().size() == nextArena.getMaxPlayersAllowed()){
                    if (!nextArena.getState().equals(State.PLAYING)) {
                        nextArena.setState(State.PLAYING);
                    }
                }
                startCountDown(nextArena);
            }
        }
    }

//    public void startArena(Arena nextArena) {
//
//    }

    public void breakingPlacedBlocks(Arena arena){
        if(!arena.getBlocks().isEmpty()) {
            for (Location loc : arena.getBlocks().keySet()) {
                loc.getBlock().setType(Material.AIR);
            }
        }
    }

    public void placeBrokenBlocks(Arena arena){
        if(!arena.getBlocks().isEmpty()) { //Si la map obtenue par le getter getBlocks n'est pas vide
            for (Location loc : arena.getBlocks().keySet()) { //Pour toutes les locations par toutes les clés de type location de la map "blocks"
                Material material = arena.getBlocks().get(loc); //Initialisation d'un objet Material à une valeur d'une clé de type location de la map "blocks". En gros obtenir le type de block correspondant à la location qui est retournée par la boucle For de la map blocks.
                loc.getBlock().setType(material); //Définition du type de block qu'est la location récupérée par la boucle parmi les clés de la map "blocks" par celui qui lui est associé en valeur
            } //En gros, tous les blocs qui ont été cassés et placés dans la map par leur coordonées et leur type sont replacées sur les locations qui correspondent à chaque clé.
        }
    }
}