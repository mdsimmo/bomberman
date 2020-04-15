package io.github.mdsimmo.bomberman.commands.game;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.SessionOwner;
import io.github.mdsimmo.bomberman.Bomberman;
import io.github.mdsimmo.bomberman.commands.Cmd;
import io.github.mdsimmo.bomberman.game.Game;
import io.github.mdsimmo.bomberman.game.GameRegestry;
import io.github.mdsimmo.bomberman.messaging.Message;
import io.github.mdsimmo.bomberman.messaging.Text;
import io.github.mdsimmo.bomberman.utils.Box;
import io.github.mdsimmo.bomberman.utils.WorldEditUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Create extends Cmd {

    private static Bomberman bm = Bomberman.instance;
    private static WorldEdit we = WorldEdit.getInstance();

    public Create(Cmd parent) {
        super(parent);
    }

    @Override
    public Message name() {
        return context(Text.CREATE_NAME).format();
    }

    @Override
    public List<String> options(CommandSender sender, List<String> args) {
        switch (args.size()) {
            case 1:
                return GameRegestry.allGames().stream().map(Game::getName).collect(Collectors.toList());
            case 2:
                return List.of("worldedit", "bomberman", "wand");
            case 3:
                File root = root(args.get(1));
                if (root == null)
                    return null;

                List<File> allFiles = allFiles(root);
                return allFiles.stream()
                        .map(File::getPath)
                        .map(path -> path.substring(root.getPath().length()+1)).collect(Collectors.toList());
            case 4:
                return List.of("skipAir");
            default:
                return null;
        }
    }

    private static File root(String plugin) {
        if (plugin.equalsIgnoreCase("bomberman") || plugin.equalsIgnoreCase("bm")) {
            return bm.schematics();
        }
        if (plugin.equalsIgnoreCase("worldedit") || plugin.equalsIgnoreCase("we"))
            return we.getWorkingDirectoryFile(we.getConfiguration().saveDir);
        return null;
    }

    private static List<File> allFiles(File root) {
        File[] files = root.listFiles();
        if (files == null) return List.of();
        List<File> fileList = new ArrayList<>();
        for (File f : files) {
            if (f.isDirectory()) {
                List<File> subFiles = allFiles(f);
                if (subFiles == null) continue; // empty subdir
                fileList.addAll(subFiles);
            } else {
                fileList.add(f);
            }
        }
        return fileList;
    }

    @Override
    public boolean run(CommandSender sender, List<String> args) {
        if (args.size() < 1)
            return false;
        if (!(sender instanceof Player)) {
            context(Text.MUST_BE_PLAYER).sendTo(sender);
            return true;
        }
        var gameName = args.get(0);
        return GameRegestry.byName(gameName)
                .map(game -> {
                    context(Text.CREATE_GAME_EXISTS)
                            .with("game", game)
                            .sendTo(sender);
                    return true;
                })
                .orElseGet(() -> {
                    if (args.size() < 2) {
                        args.add("bm");
                        args.add("purple");
                        args.add("skipair");
                    }
                    if ("wand".equals(args.get(1).toLowerCase())) {
                        if (args.size() == 2) {
                            makeFromSelection(gameName, sender);
                            return true;
                        } else {
                            return false;
                        }
                    } else {
                        boolean skipAir = args.stream().skip(3).anyMatch("skipair"::equalsIgnoreCase);
                        if (args.size() >= 3) {
                            File saveDir = root(args.get(1));
                            if (saveDir == null)
                                return false;
                            makeFromFile(gameName, args.get(2), saveDir, (Player) sender, skipAir);
                            return true;
                        } else {
                            return false;
                        }
                    }
                });
    }

    private void makeFromSelection(String gameName, CommandSender sender) {
        SessionOwner owner = BukkitAdapter.adapt(sender);
        LocalSession session = WorldEdit.getInstance().getSessionManager().getIfPresent(owner);
        if (session == null || session.getSelectionWorld() == null) {
            context(Text.CREATE_NEED_SELECTION).sendTo(sender);
        } else {
            try {
                Region region = session.getSelection(session.getSelectionWorld());
                Box box = WorldEditUtils.selectionBounds(region);
                var game = Game.Companion.BuildGameFromRegion(gameName, box);
                Text.CREATE_SUCCESS.with("game", game).sendTo(sender);
            } catch (IncompleteRegionException e) {
                // FIXME can selection occur in world other than selection?
                throw new RuntimeException("Selection World different to selection", e);
            }
        }
    }

    private void makeFromFile(String gameName, String schemaName, File saveDir, Player player, boolean skipAir) {
        var matches = saveDir.listFiles(dir -> dir.getPath().contains(schemaName));
        Arrays.stream(matches == null ? new File[] {} : matches)
                // The minimum length path will be the closest match
                .min(Comparator.comparing(it -> it.getName().length()))
                .ifPresentOrElse(
                        file ->
                            Game.Companion.BuildGameFromSchema(gameName, player.getLocation(), file, skipAir),
                        () ->
                            context(Text.CREATE_SCHEMA_NOT_FOUND.with("schema", Message.of(schemaName))).sendTo(player)
                );
    }

    @Override
    public Permission permission() {
        return Permission.GAME_DICTATE;
    }

    @Override
    public Message example() {
        return context(Text.CONVERT_EXAMPLE).format(); }

    @Override
    public Message extra() {
        return context(Text.CREATE_EXTRA).format();
    }

    @Override
    public Message description() {
        return context(Text.CREATE_DESCRIPTION).format();
    }

    @Override
    public Message usage() {
        return context(Text.CREATE_USAGE).format();
    }

}
