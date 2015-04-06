package io.github.mdsimmo.bomberman.commands.game.set;

import io.github.mdsimmo.bomberman.Game;
import io.github.mdsimmo.bomberman.PlayerRep;
import io.github.mdsimmo.bomberman.commands.Cmd;
import io.github.mdsimmo.bomberman.commands.GameCommand;
import io.github.mdsimmo.bomberman.messaging.Chat;
import io.github.mdsimmo.bomberman.messaging.Text;
import io.github.mdsimmo.bomberman.playerstates.GamePlayingState;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Handicap extends GameCommand {

	public Handicap(Cmd parent) {
		super(parent);
	}

	@Override
	public Text nameShort() {
		return Text.HANDICAP_NAME;
	}

	@Override
	public List<String> shortOptions(CommandSender sender, List<String> args) {
		if (args.size() == 1) {
			List<String> options = new ArrayList<>();
			for (Player p : Bukkit.getServer().getOnlinePlayers())
				options.add(p.getName());
			return options;
		} else {
			return null;
		}
		
	}

	@Override
	public boolean runShort(CommandSender sender, List<String> args, Game game) {
		if (args.size() != 2)
			return false;
		
		@SuppressWarnings("deprecation")
		PlayerRep rep = PlayerRep.getPlayerRep(Bukkit.getPlayer(args.get(0)));
		if (rep == null) {
			Chat.sendMessage(getMessage(Text.INVALID_PLAYER, sender).put( "player", args.get(0)));
			return true;
		}
		int handicap = 0;
		try {
			handicap = Integer.parseInt(args.get(1));
		} catch (NumberFormatException e) {
			Chat.sendMessage(getMessage(Text.INVALID_NUMBER, sender).put( "number", args.get(1)));
		}
		game.setHandicap(rep, handicap);
		if (handicap > 0)
			Chat.sendMessage(getMessage(Text.HANDICAP_HANDYCAPPED, sender).put( "game", game).put( "player", rep ));
		else if (handicap == 0)
			Chat.sendMessage(getMessage(Text.HANDICAP_REMOVED, sender).put( "game", game).put( "player", rep ));
		else
			Chat.sendMessage(getMessage(Text.HANDICAP_ADVANTAGE, sender).put( "game", game).put( "player", rep ));
		
		if (rep.isPlaying() && !((GamePlayingState)rep.getState()).getGame().isPlaying)
			game.initialise(rep);
		return true;
	}
	
	@Override
	public Permission permission() {
		return Permission.GAME_DICTATE;
	}

	@Override
	public Text extraShort() {
		return Text.HANDICAP_EXTRA;
	}

	@Override
	public Text exampleShort() {
		return Text.HANDICAP_EXAMPLE;
	}

	@Override
	public Text descriptionShort() {
		return Text.HANDICAP_DESCRIPTION;
	}

	@Override
	public Text usageShort() {
		return Text.HANDICAP_USAGE;
	}

}
