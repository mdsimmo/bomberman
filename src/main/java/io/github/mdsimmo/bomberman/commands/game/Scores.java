package io.github.mdsimmo.bomberman.commands.game;

import io.github.mdsimmo.bomberman.Bomberman;
import io.github.mdsimmo.bomberman.Game;
import io.github.mdsimmo.bomberman.commands.Command;

import java.util.List;

import org.bukkit.command.CommandSender;

public class Scores extends GameCommand {

	public Scores(Command parent) {
		super(parent);
	}

	@Override
	public List<String> shortOptions(CommandSender sender, List<String> args) {
		return null;
	}

	@Override
	public boolean runShort(CommandSender sender, List<String> args, Game game) {
		Bomberman.sendMessage(sender, game.scoreDisplay());
		return false;
	}

	@Override
	public boolean firstIsGame(List<String> args) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String description() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String usage(CommandSender sender) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Permission permission() {
		// TODO Auto-generated method stub
		return null;
	}

}
