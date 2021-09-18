package io.github.mdsimmo.bomberman.commands.game

import io.github.mdsimmo.bomberman.commands.Cmd
import io.github.mdsimmo.bomberman.commands.GameCommand
import io.github.mdsimmo.bomberman.commands.Permission
import io.github.mdsimmo.bomberman.commands.Permissions
import io.github.mdsimmo.bomberman.game.Game
import io.github.mdsimmo.bomberman.messaging.Contexted
import io.github.mdsimmo.bomberman.messaging.Message
import io.github.mdsimmo.bomberman.messaging.Text
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.max
import kotlin.math.min

class Configure(parent: Cmd) : GameCommand(parent) {
    override fun name(): Message {
        return context(Text.CONFIGURE_NAME).format()
    }

    override fun gameOptions(args: List<String>): List<String> {
        return emptyList()
    }

    override fun permission(): Permission {
        return Permissions.CONFIGURE
    }

    override fun extra(): Message {
        return context(Text.CONFIGURE_EXTRA).format()
    }

    override fun example(): Message {
        return context(Text.CONFIGURE_EXAMPLE).format()
    }

    override fun description(): Message {
        return context(Text.CONFIGURE_DESCRIPTION).format()
    }

    override fun usage(): Message {
        return context(Text.CONFIGURE_USAGE).format()
    }

    override fun gameRun(sender: CommandSender, args: List<String>, flags: Map<String, String>, game: Game): Boolean {
        if (args.isNotEmpty())
            return false
        if (sender !is Player) {
            context(Text.MUST_BE_PLAYER).sendTo(sender)
            return true
        }
        if (sender.gameMode != GameMode.CREATIVE) {
            context(Text.CONFIGURE_PROMPT_CREATIVE).sendTo(sender)
            return true
        }

        showMainMenu(sender, game)
        return true
    }

    private fun showMainMenu(player: Player, game: Game) {
        GuiBuilder.show(player, Text.CONFIGURE_TITLE_MAIN.format().toString(), arrayOf(
                "         ",
                " s b l i ",
                "         "),
                onInit = { index ->
                    when (index.section) {
                        's' -> GuiBuilder.ItemSlot(Material.REDSTONE).unMovable()
                                .displayName(Text.CONFIGURE_TITLE_GENERAL.format().toString())
                        'b' -> GuiBuilder.ItemSlot(Material.DIRT).unMovable()
                                .displayName(Text.CONFIGURE_TITLE_BLOCKS.format().toString())
                        'l' -> GuiBuilder.ItemSlot(Material.GOLDEN_APPLE).unMovable()
                                .displayName(Text.CONFIGURE_TITLE_LOOT.format().toString())
                        'i' -> GuiBuilder.ItemSlot(Material.CHEST).unMovable()
                                .displayName(Text.CONFIGURE_TITLE_INVENTORY.format().toString())
                        else -> GuiBuilder.blank
                    }
                },
                onClick = { index, _, _->
                    when (index.section) {
                        's' -> showGeneralConfig(player, game)
                        'b' -> showBlockSettings(player, game)
                        'l' -> showLootSettings(player, game)
                        'i' -> showInventorySettings(player, game)
                    }
                }
        )
    }

    private fun showGeneralConfig(player: Player, game: Game) {
        val settings = game.settings
        GuiBuilder.show(player, Text.CONFIGURE_TITLE_GENERAL.format().toString(), arrayOf(
                "  ^^^^   ",
                "< lfbitg ",
                "  vvvv   "),
                onInit = { index ->
                    index.inventory.maxStackSize = 100000
                    when (index.section) {
                        '<' -> GuiBuilder.ItemSlot(Material.PAPER)
                                .unMovable().displayName(stringify(Text.CONFIGURE_BACK))
                        'l' -> GuiBuilder.ItemSlot(setQty(ItemStack(Material.PLAYER_HEAD), settings.lives))
                                .unMovable().displayName(stringify(Text.CONFIGURE_LIVES))
                        'f' -> GuiBuilder.ItemSlot(setQty(ItemStack(Material.TNT), settings.fuseTicks))
                                .unMovable().displayName(stringify(Text.CONFIGURE_FUSE_TICKS))
                        'b' -> GuiBuilder.ItemSlot(setQty(ItemStack(Material.FLINT_AND_STEEL), settings.fireTicks))
                                .unMovable().displayName(stringify(Text.CONFIGURE_FIRE_TICKS))
                        'i' -> GuiBuilder.ItemSlot(setQty(ItemStack(Material.MILK_BUCKET), settings.immunityTicks))
                                .unMovable().displayName(stringify(Text.CONFIGURE_IMMUNITY_TICKS))
                        't' -> GuiBuilder.ItemSlot(settings.bombItem)
                                .unMovable().displayName(stringify(Text.CONFIGURE_TNT_BLOCK))
                        'g' -> GuiBuilder.ItemSlot(settings.powerItem)
                                .unMovable().displayName(stringify(Text.CONFIGURE_FIRE_ITEM))
                        '^' -> GuiBuilder.ItemSlot(Material.STONE_BUTTON)
                                .unMovable().displayName("+")
                        'v' -> GuiBuilder.ItemSlot(Material.STONE_BUTTON)
                                .unMovable().displayName("-")
                        else -> GuiBuilder.blank
                    }
                },
                onClick = {index, slot, cursor ->
                    index.inventory.maxStackSize = 100000
                    when (index.section) {
                        '<' -> showMainMenu(player, game)
                        '^' -> {
                            val item = index.inventory.getItem(index.invIndex + 9)!!
                            setQty(item, item.amount + 1)
                        }
                        'v' -> {
                            val item = index.inventory.getItem(index.invIndex - 9)!!
                            setQty(item, max(item.amount - 1, 1))
                        }
                        't', 'g' -> {
                            if (cursor != null && cursor.amount != 0) {
                                slot!!.type = cursor.type
                            }
                        }
                    }
                },
                onClose = {inventoryIterator ->
                    for ((index, item) in inventoryIterator) {
                        when(index.section) {
                            'l' -> settings.lives = item!!.amount
                            'f' -> settings.fuseTicks = item!!.amount
                            'b' -> settings.fireTicks = item!!.amount
                            'i' -> settings.immunityTicks = item!!.amount
                            't' -> settings.bombItem = item!!.type
                            'g' -> settings.powerItem = item!!.type
                        }
                    }
                    Game.saveGame(game)
                }
        )
    }

    private fun showBlockSettings(player: Player, game: Game) {
        showBlockSettings(player, game, 0, stringify(Text.CONFIGURE_DESTRUCTIBLE_DESC),
                game.settings.destructible) { game.settings.destructible = it }
    }

    private fun showBlockSettings(player: Player, game: Game, selected: Int, description: String, types: Set<Material>, result: (Set<Material>) -> Unit) {
        val typesList = types.filter { it.isItem }.filter { it.isBlock }.toList()
        val settings = game.settings
        GuiBuilder.show(player, Text.CONFIGURE_TITLE_BLOCKS.format().toString(), arrayOf(
                "<d s p n ",
                " c cEc c ",
                "iiiiiiiii",
                "iiiiiiiii"),
                onInit = { index ->
                    when (index.section) {
                        'E' -> GuiBuilder.ItemSlot(Material.EMERALD)
                                .unMovable().displayName(description)
                        '<' -> GuiBuilder.ItemSlot(Material.PAPER)
                                .unMovable().displayName(stringify(Text.CONFIGURE_BACK))
                        'd' -> GuiBuilder.ItemSlot(Material.DIRT)
                                .unMovable().displayName(stringify(Text.CONFIGURE_DESTRUCTIBLE))
                        's' -> GuiBuilder.ItemSlot(Material.OBSIDIAN)
                                .unMovable().displayName(stringify(Text.CONFIGURE_INDESTRUCTIBLE))
                        'p' -> GuiBuilder.ItemSlot(Material.OXEYE_DAISY)
                                .unMovable().displayName(stringify(Text.CONFIGURE_PASS_DESTROY))
                        'n' -> GuiBuilder.ItemSlot(Material.SIGN)
                                .unMovable().displayName(stringify(Text.CONFIGURE_PASS_KEEP))
                        'c' -> {
                            if (index.secIndex == selected) {
                                GuiBuilder.ItemSlot(Material.YELLOW_STAINED_GLASS_PANE)
                                        .unMovable().displayName(" ")
                            } else {
                                GuiBuilder.blank
                            }
                        }
                        'i' -> {
                            val mat = typesList.getOrNull(index.secIndex)
                            if (mat == null) {
                                GuiBuilder.ItemSlot(null)
                            } else {
                                GuiBuilder.ItemSlot(mat)
                            }
                        }
                        else -> GuiBuilder.blank
                    }
                },
                onClick = { index, _, _->
                    when (index.section) {
                        '<' -> showMainMenu(player, game)
                        'd' -> if (selected != 0) showBlockSettings(player, game, 0, stringify(Text.CONFIGURE_DESTRUCTIBLE_DESC),
                                settings.destructible) { settings.destructible = it}
                        's' -> if (selected != 1) showBlockSettings(player, game, 1, stringify(Text.CONFIGURE_INDESTRUCTIBLE_DESC),
                                settings.indestructible) {settings.indestructible = it}
                        'p' -> if (selected != 2) showBlockSettings(player, game, 2, stringify(Text.CONFIGURE_PASS_DESTROY_DESC),
                                settings.passDestroy) {settings.passDestroy = it}
                        'n' -> if (selected != 3) showBlockSettings(player, game, 3, stringify(Text.CONFIGURE_PASS_KEEP_DESC),
                                settings.passKeep) {settings.passKeep = it}
                    }
                },
                onClose = {
                    val blocks = it.mapNotNull { (index, stack) ->
                        if (index.section == 'i') {
                            stack?.type
                        } else {
                            null
                        }
                    }.flatMap { mat ->
                        expandSimilarMaterials(mat)
                    }.toSet()
                    result(blocks)
                    Game.saveGame(game)
                }
        )
    }

    private fun expandSimilarMaterials(mat: Material): Sequence<Material> {
        val wallVariant = mat.key.key
                .replace("sign", "wall_sign")
                .replace("banner", "wall_banner")
                .replace("fan", "wall_fan")
                .replace("torch", "wall_torch")
                .replace("head", "wall_head")
                .replace("skull", "skull_head")
        val wallType = Material.matchMaterial(wallVariant) ?: return sequenceOf(mat)
        return sequenceOf(mat, wallType)
    }

    private fun showInventorySettings(player: Player, game: Game) {
        GuiBuilder.show(player, stringify(Text.CONFIGURE_TITLE_INVENTORY), arrayOf(
                "< HCLBS  ",
                "  aaaas  ",
                "iiiiiiiii",
                "iiiiiiiii",
                "iiiiiiiii",
                "hhhhhhhhh"),
                onInit = { index ->
                    val initialItems = ArrayList(game.settings.initialItems)
                    while (initialItems.size < 9*4+5) {
                        initialItems.add(null)
                    }
                    when (index.section) {
                        '<' -> GuiBuilder.ItemSlot(Material.PAPER).unMovable().displayName(stringify(Text.CONFIGURE_BACK))
                        'a' -> GuiBuilder.ItemSlot(initialItems[(3-index.secIndex)+9*4])
                        's' -> GuiBuilder.ItemSlot(initialItems[(9*4+4)])
                        'h' -> GuiBuilder.ItemSlot(initialItems[index.secIndex])
                        'i' -> GuiBuilder.ItemSlot(initialItems[index.secIndex+9])
                        'H' -> GuiBuilder.ItemSlot(Material.IRON_HELMET).unMovable().hideAttributes()
                        'C' -> GuiBuilder.ItemSlot(Material.IRON_CHESTPLATE).unMovable().hideAttributes()
                        'L' -> GuiBuilder.ItemSlot(Material.IRON_LEGGINGS).unMovable().hideAttributes()
                        'B' -> GuiBuilder.ItemSlot(Material.IRON_BOOTS).unMovable().hideAttributes()
                        'S' -> GuiBuilder.ItemSlot(Material.SHIELD).unMovable().hideAttributes()
                        else -> GuiBuilder.blank
                    }
                },
                onClick = {index, _, _->
                    when (index.section) {
                        '<' -> showMainMenu(player, game)
                    }
                },
                onClose = {inventoryIterator ->
                    val closingItems = Array<ItemStack?>(9*4+5) { null }
                    for ((index, item) in inventoryIterator) {
                        when(index.section) {
                            'a' -> closingItems[9*4+(3-index.secIndex)] = item
                            's' -> closingItems[9*4+4] = item
                            'h' -> closingItems[index.secIndex] = item
                            'i' -> closingItems[9+index.secIndex] = item
                        }
                    }
                    game.settings.initialItems = closingItems.asList()
                    Game.saveGame(game)
                }
        )
    }

    private fun showLootSettings(player: Player, game: Game) {
        val gameLoot = game.settings.blockLoot
        // manipulate the loot into a format that can be shown visually
        // Need to group blocks with duplicate loot by flipping key/values
        val lootBlock = mutableMapOf<Map<ItemStack, Int>, MutableSet<Material>>()
        gameLoot.forEach { (mat, loot) ->
            lootBlock.getOrPut(loot) { mutableSetOf() }.add(mat)
        }
        // Flip the key/values back
        val loot = lootBlock
                .map { (loot, matList) ->
                    Pair(matList.toList(), loot.toList())
                }.toMutableList()

        showLootSettings(player, game, 0, loot)
    }

    private fun showLootSettings(player: Player, game: Game, slot: Int, loot: MutableList<Pair<List<Material>, List<Pair<ItemStack, Int>>>>) {
        // split out stacks with weight over 64
        val (mats, items) = loot.getOrElse(slot) {
            Pair(arrayListOf(), arrayListOf())
        }.let { (mats, items) ->
            Pair(
                    mats.filter { it.isBlock && it.isItem },
                    items.flatMap {(stack, weight) ->
                        var brokenWeight = weight
                        val list = mutableListOf<Pair<ItemStack, Int>>()
                        do {
                            list.add(Pair(stack, min(brokenWeight, 64)))
                            brokenWeight -= 64
                        } while (brokenWeight > 0)
                        list
                    }
            )
        }

        GuiBuilder.show(player, stringify(Text.CONFIGURE_TITLE_LOOT), arrayOf(
                "<SSSSSSSS",
                "Kkkkkkkkk",
                "         ",
                "Vvvvvvvvv",
                "Wwwwwwwww"),
                onInit = { index ->
                    when (index.section) {
                        '<' -> GuiBuilder.ItemSlot(Material.PAPER)
                                .unMovable().displayName(stringify(Text.CONFIGURE_BACK))
                        'S' -> {
                            val icon = when {
                                // selected
                                index.secIndex == slot -> Material.YELLOW_CONCRETE
                                // nothing added
                                index.secIndex >= loot.size
                                        || loot[index.secIndex].let { (mats, items) ->
                                    mats.isEmpty() && items.isEmpty()
                                }  -> Material.GRAY_CONCRETE
                                // something present
                                else -> Material.WHITE_CONCRETE
                            }
                            GuiBuilder.ItemSlot(icon)
                                    .unMovable().displayName(stringify(Text.CONFIGURE_LOOT_SLOT.with("slot", index.secIndex)))
                        }
                        'K' -> GuiBuilder.ItemSlot(Material.EMERALD)
                                .unMovable().displayName(stringify(Text.CONFIGURE_LOOT_BLOCK.with("slot", slot)))
                        'k' -> GuiBuilder.ItemSlot(mats.getOrNull(index.secIndex)?.let { ItemStack(it) })
                        'V' -> GuiBuilder.ItemSlot(Material.EMERALD)
                                .unMovable().displayName(stringify(Text.CONFIGURE_LOOT_ITEM.with("slot", slot)))
                        'v' -> GuiBuilder.ItemSlot(items.getOrNull(index.secIndex)?.first)
                        'W' -> GuiBuilder.ItemSlot(Material.EMERALD)
                                .unMovable().displayName(stringify(Text.CONFIGURE_LOOT_WEIGHT.with("slot", slot)))
                        'w' -> GuiBuilder.ItemSlot(Material.GOLD_NUGGET, items.getOrNull(index.secIndex)?.second ?: 0)
                        else -> GuiBuilder.blank
                    }
                },
                onClick = { index, _, _ ->
                    when (index.section) {
                        '<' -> showMainMenu(player, game)
                        'S' -> {
                            // Trying to reload the same slot causes ghost bugs
                            // (because closing previous screen happens AFTER loading new screen)
                            showLootSettings(player, game, index.secIndex, loot)
                            // TODO the colored tiles are one cycle behind because of the close-load order
                        }
                    }
                },
                onClose = {inventoryIterator ->
                    val matsSaved = mutableListOf<Material>()

                    // Count the weightings of each item
                    val itemsSaved = HashMap<ItemStack?, AtomicInteger>()
                    for ((index, item) in inventoryIterator) {
                        when (index.section) {
                            'k' -> item?.let { matsSaved.add(it.type) }
                            'v' -> {
                                val weight = index.inventory.getItem(index.invIndex+9)?.amount ?: 0
                                itemsSaved.getOrPut(item) { AtomicInteger(0) }.addAndGet(weight)
                            }
                        }
                    }
                    // Override the current list for the next view
                    // (but first pad the list out to make sure the item slot doesn't move around while editing)
                    while (loot.size <=  slot) {
                        loot.add(Pair(listOf(), listOf()))
                    }
                    loot[slot] = Pair(matsSaved, itemsSaved.mapNotNull { (stack, weight) ->
                        if (weight.get() == 0 && stack == null) {
                            // This filters the empty row sum
                            null
                        } else {
                            Pair(stack ?: ItemStack(Material.AIR, 0), weight.get())
                        }
                    }.toList())

                    // Map to standard form to save
                    game.settings.blockLoot = loot.flatMap {(mats, itemWeights) ->
                        mats.flatMap { expandSimilarMaterials(it).toList() }
                                .map { Pair(it, itemWeights.toMap()) }
                    }.toMap()
                    Game.saveGame(game)
                }
        )
    }

    private fun setQty(itemStack: ItemStack, amount: Int = itemStack.amount) : ItemStack {
        itemStack.amount = amount
        val meta = itemStack.itemMeta!!
        meta.lore = listOf(amount.toString())
        itemStack.itemMeta = meta
        return itemStack
    }

    private fun stringify(text: Contexted): String {
        return text.format().toString()
    }
}