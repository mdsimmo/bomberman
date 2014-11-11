package io.github.mdsimmo.bomberman.commands;

import io.github.mdsimmo.bomberman.PlayerRep;
import io.github.mdsimmo.bomberman.messaging.Chat;
import io.github.mdsimmo.bomberman.messaging.Language;
import io.github.mdsimmo.bomberman.messaging.Message;
import io.github.mdsimmo.bomberman.messaging.Text;
import io.github.mdsimmo.bomberman.utils.Utils;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LanguageCmd extends Command {

	public LanguageCmd(Command parent) {
		super(parent);
	}

	@Override
	public Text name() {
		return Text.LANGUAGE_NAME;
	}

	@Override
	public List<String> options(CommandSender sender, List<String> args) {
		List<String> langs = Language.allLanguages();
		if (!langs.contains("english"))
			langs.add("english");
		return langs;
	}

	@Override
	public boolean run(CommandSender sender, List<String> args) {
		if (args.size() != 1)
			return false;
		
		if (sender instanceof Player == false) {
			Chat.sendMessage(sender, getMessage(Text.MUST_BE_PLAYER, sender));
			return true;
		}
		
		Language lang = Language.getLanguage(args.get(0));
		if (lang == null) {
			if (args.get(0).equalsIgnoreCase("english")) {
				PlayerRep.getPlayerRep((Player)sender).setLanguage(null);
				Chat.sendMessage(sender, getMessage(Text.LANGUAGE_SUCCESS, sender, args.get(0)));
			} else
				Chat.sendMessage(sender, getMessage(Text.LANGUAGE_UNKNOWN, sender, args.get(0)));
		} else {
			PlayerRep.getPlayerRep((Player)sender).setLanguage(lang);
			Chat.sendMessage(sender, getMessage(Text.LANGUAGE_SUCCESS, sender, lang));
		}
		return true;
	}

	@Override
	public Message extra(CommandSender sender, List<String> args) {
		return getMessage(Text.LANGUAGE_EXTRA, sender);
	}

	@Override
	public Message example(CommandSender sender, List<String> args) {
		String lang = Utils.random(Language.allLanguages());
		lang = lang == null ? "mylang" : lang;
		return getMessage(Text.LANGUAGE_EXAMPLE, sender, lang);
	}

	@Override
	public Message description(CommandSender sender, List<String> args) {
		return getMessage(Text.LANGUAGE_DESCRIPTION, sender);
	}

	@Override
	public Message usage(CommandSender sender, List<String> args) {
		return getMessage(Text.LANGUAGE_USAGE, sender);
	}

	@Override
	public Permission permission() {
		return Permission.OBSERVER;
	}

}
