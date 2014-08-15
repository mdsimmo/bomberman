package io.github.mdsimmo.bomberman.commands.arena;

import io.github.mdsimmo.bomberman.Bomberman;
import io.github.mdsimmo.bomberman.Game;
import io.github.mdsimmo.bomberman.PlayerRep;
import io.github.mdsimmo.bomberman.commands.Command;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EditArena extends Command {

	public EditArena(Command parent) {
		super(parent);
	}

	@Override
	public String name() {
		return "edit";
	}

	@Override
	public List<String> options(CommandSender sender, List<String> args) {
		if (args.size() == 1)
			return Game.allGames();
		else
			return null;
	}

	@Override
	public boolean run(CommandSender sender, List<String> args) {
		if (args.size() > 1)
			return false;
		
		if (sender instanceof Player == false) {
			Bomberman.sendMessage(sender, "You must be a player");
			return true;
		}
		Player player = (Player)sender;
		PlayerRep rep = PlayerRep.findPlayerRep(player);
		if (rep == null) {
			sender.sendMessage("You're not registed with a game");
			return true;
		}
		if (args.size() == 0) {
			if (!rep.startEditMode()) {
				Bomberman.sendMessage(sender, "Couldn't start edit mode");
			} else {
				Bomberman.sendMessage(sender, "Edit mode started");
			}
		} else {
			switch (args.get(0).toLowerCase()) {
			case "save":
				rep.commitChanges(true);
				Bomberman.sendMessage(sender, "Changes saved");
				break;
			case "discard":
				rep.commitChanges(false);
				Bomberman.sendMessage(sender, "Changes removed");
				break;
			default:
				return false;
			}
		}
		return true;
	}

	@Override
	public String description() {
		return "Edit a game's arena. This will effect all games using the same arena.";
	}

	@Override
	public String usage(CommandSender sender) {
		return "\n"
				+ "/" + path() + "- start edit mode\n"
				+ "/" + path() + "[save/discard] - commit changes";
	}

	@Override
	public Permission permission() {
		return Permission.ARENA_EDITING;
	}

}
