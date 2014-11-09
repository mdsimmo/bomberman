package io.github.mdsimmo.bomberman.commands.arena;

import io.github.mdsimmo.bomberman.Board;
import io.github.mdsimmo.bomberman.Game;
import io.github.mdsimmo.bomberman.PlayerRep;
import io.github.mdsimmo.bomberman.commands.Command;
import io.github.mdsimmo.bomberman.messaging.Chat;
import io.github.mdsimmo.bomberman.messaging.Message;
import io.github.mdsimmo.bomberman.messaging.Text;
import io.github.mdsimmo.bomberman.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EditArena extends Command {

	public EditArena(Command parent) {
		super(parent);
	}

	@Override
	public Text name() {
		return Text.EDIT_NAME;
	}

	@Override
	public List<String> options(CommandSender sender, List<String> args) {
		if (sender instanceof Player == false)
			return null;
		PlayerRep rep = PlayerRep.getPlayerRep((Player)sender);
		if (args.size() == 1) {
			if (rep.isEditting()) {
				List<String> list = new ArrayList<>();
				list.add(getMessage(Text.EDIT_SAVE, sender).toString());
				list.add(getMessage(Text.EDIT_DISCARD, sender).toString());
				list.add(getMessage(Text.EDIT_IGNORE, sender).toString());
				return list;
			} else {
				return Game.allGames();
			}
		} else
			return null;
	}

	@Override
	public boolean run(CommandSender sender, List<String> args) {
		if (args.size() > 1)
			return false;
		
		if (sender instanceof Player == false) {
			Chat.sendMessage(sender, getMessage(Text.MUST_BE_PLAYER, sender));
			return true;
		}
		Player player = (Player)sender;
		PlayerRep rep = PlayerRep.getPlayerRep(player);
		Game game = rep.getEditting();
		Board board = null;
		if (game != null)
			board = game.board;
			
		if (args.size() == 0) {
			if (rep.startEditMode()) {
				Chat.sendMessage(sender, getMessage(Text.EDIT_STARTED, sender, game, board));
			} else {
				if (rep.isEditting())
					Chat.sendMessage(sender, getMessage(Text.EDIT_ALREADY_STARTED, sender, game, board));
				else
					Chat.sendMessage(sender, getMessage(Text.EDIT_CANT_START, sender, game, board));
			}
		} else {
			String arg = args.get(0);
			String save = getMessage(Text.EDIT_SAVE, sender).toString();
			String discard = getMessage(Text.EDIT_DISCARD, sender).toString();
			String ignore = getMessage(Text.EDIT_IGNORE, sender).toString();
			if (save.equalsIgnoreCase(arg)) {
				if (rep.saveChanges())
					Chat.sendMessage(sender, getMessage(Text.EDIT_CHANGES_SAVED, sender, game, board));
				else
					Chat.sendMessage(sender, getMessage(Text.EDIT_PROMPT_START, sender, game, board));
			} else if (discard.equalsIgnoreCase(arg)) {
				if (rep.discardChanges(true))
					Chat.sendMessage(sender, getMessage(Text.EDIT_CANGES_REMOVED, sender, game, board));
				else {
					Chat.sendMessage(sender, getMessage(Text.EDIT_PROMPT_START, sender, game, board));
				}
			} else if (ignore.equalsIgnoreCase(arg)) {
				if (rep.discardChanges(false))
					Chat.sendMessage(sender, getMessage(Text.EDIT_MODE_QUIT, sender, game, board));
				else {
					Chat.sendMessage(sender, getMessage(Text.EDIT_PROMPT_START, sender, game, board));
				}
			} else {
				Game game2 = Game.findGame(args.get(0));
				if (game2 == null)
					return false;
				else {
					rep.setGameActive(game2);
					args.remove(0);
					return run(sender, args);
				}
			}
		}
		return true;
	}

	@Override
	public Message extra(CommandSender sender, List<String> args) {
		return getMessage(Text.EDIT_EXTRA, sender);
	}

	@Override
	public Message usage(CommandSender sender, List<String> args) {
		return getMessage(Text.EDIT_USAGE, sender);
	}

	@Override
	public Permission permission() {
		return Permission.ARENA_EDITING;
	}

	@Override
	public Message example(CommandSender sender, List<String> args) {
		String game = Utils.random(Game.allGames());
		if (game == null)
			game = "mygame";
		return getMessage(Text.EDIT_EXAMPLE, sender, game);
	}

	@Override
	public Message description(CommandSender sender, List<String> args) {
		return getMessage(Text.EDIT_DESCRIPTION, sender);
	}

}
