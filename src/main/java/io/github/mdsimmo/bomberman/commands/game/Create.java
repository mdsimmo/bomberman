package io.github.mdsimmo.bomberman.commands.game;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.SessionOwner;
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
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Create extends Cmd {

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
                return List.of("default", "schema", "wand");
            case 3:
                switch (args.get(1).toLowerCase()) {
                    case "default":
                        return List.of("purple"); // TODO auto-generate list of default schematics
                    case "schema":
                        WorldEdit worldEdit = WorldEdit.getInstance();
                        String root = worldEdit.getConfiguration().saveDir;
                        File schemas = worldEdit.getWorkingDirectoryFile(root);
                        String schemasPath = schemas.getPath();
                        List<File> allFiles = allFiles(schemas);
                        return allFiles.stream()
                                .map(File::getPath)
                                .map(path -> {
                                    if (!path.toLowerCase().startsWith(schemas.getPath().toLowerCase()))
                                        throw new RuntimeException("SubFile isn't in parent? " +
                                                "\nSchemas: '" + schemasPath.toLowerCase() + "'" +
                                                "\nPath: '" + path + "'");
                                    return path.substring(schemasPath.length());
                                }).collect(Collectors.toList());
                    default:
                        return null;
                }
            case 4:
                if (args.get(1).equalsIgnoreCase("schema")) {
                    return List.of("skipair");
                } else {
                    return null;
                }
            default:
                return null;
        }
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
                    if (args.size() < 2)
                        args.add("default");
                    switch (args.get(1).toLowerCase()) {
                        case "wand":
                            if (args.size() == 2) {
                                makeFromSelection(gameName, sender);
                                return true;
                            } else {
                                return false;
                            }
                        case "schema":
                            boolean skipAir = args.stream().skip(3).anyMatch("skipAir"::equalsIgnoreCase);
                            if (args.size() >= 3) {
                                makeFromFile(gameName, args.get(2), (Player) sender, skipAir);
                                return true;
                            } else {
                                return false;
                            }
                        case "default":
                            if (args.size() < 3) {
                                args.add("purple");
                                args.add("skipAir");
                            }
                            boolean skipAir_ = args.stream().skip(3).anyMatch("skipAir"::equalsIgnoreCase);
                            if (args.size() >= 3) {
                                makeFromDefault(gameName, args.get(2), (Player) sender, skipAir_);
                                return true;
                            } else {
                                return false;
                            }
                        default:
                            sender.sendMessage("'" + args.get(1) + "'");
                            return false;
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

    private void makeFromDefault(String gameName, String schemaName, Player player, boolean skipAir) {
        try {
            URL schemaURL = ClassLoader.getSystemResource(schemaName);
            File schemaFile = new File(schemaURL.toURI());
            if (schemaFile.exists())
                Game.Companion.BuildGameFromSchema(gameName, player.getLocation(), schemaFile, skipAir, player);
            else
                context(Text.CREATE_SCHEMA_NOT_FOUND.with("schema", Message.of(schemaName))).sendTo(player);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private void makeFromFile(String gameName, String schemaName, Player player, boolean skipAir) {
        WorldEdit we = WorldEdit.getInstance();
        File saveDir = we.getWorkingDirectoryFile(we.getConfiguration().saveDir);
        var matches = saveDir.listFiles(dir -> {
            System.out.println(dir);
            return dir.getPath().contains(schemaName);
        });
        Arrays.stream(matches == null ? new File[] {} : matches)
                // The minimum length path will be the closest match
                .min(Comparator.comparing(it -> it.getName().length()))
                .ifPresentOrElse(
                        file ->
                            Game.Companion.BuildGameFromSchema(gameName, player.getLocation(), file, skipAir, player),
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
