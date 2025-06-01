package io.github.thebusybiscuit.coloredenderchests;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.IntStream;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.EnderChest;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.implementation.handlers.SimpleBlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.utils.ColoredMaterial;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;

import org.bukkit.plugin.Plugin;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class ColoredEnderChest extends SlimefunItem implements Listener {

    private static final Map<UUID, Map<String, Inventory>> playerChests = new HashMap<>();
    private static File DATA_FILE;
    private static boolean loaded = false;
    private final int size, c1, c2, c3;
    private final Plugin plugin;

    public ColoredEnderChest(ColoredEnderChests plugin, int size, int c1, int c2, int c3) {
        super(
                plugin.itemGroup,
                getColoredStack(size, c1, c2, c3),
                RecipeType.ANCIENT_ALTAR,
                (size == 27) ? new ItemStack[] {
                        new ItemStack(ColoredMaterial.WOOL.get(c1)), new ItemStack(ColoredMaterial.WOOL.get(c2)), new ItemStack(ColoredMaterial.WOOL.get(c3)),
                        new ItemStack(Material.OBSIDIAN), new ItemStack(Material.CHEST), new ItemStack(Material.OBSIDIAN),
                        SlimefunItems.ENDER_RUNE.item(), new ItemStack(Material.OBSIDIAN), SlimefunItems.ENDER_RUNE.item(), }
                        : new ItemStack[] {
                        new ItemStack(ColoredMaterial.WOOL.get(c1)), new ItemStack(ColoredMaterial.WOOL.get(c2)), new ItemStack(ColoredMaterial.WOOL.get(c3)),
                        SlimefunItems.WITHER_PROOF_OBSIDIAN.item(), getSmallerEnderChest(c1, c2, c3), SlimefunItems.WITHER_PROOF_OBSIDIAN.item(),
                        SlimefunItems.ENDER_RUNE.item(), SlimefunItems.GOLD_24K.item(), SlimefunItems.ENDER_RUNE.item(),
                });
        this.size = size;
        this.c1 = c1;
        this.c2 = c2;
        this.c3 = c3;
        this.plugin = plugin;

        int[] slots = IntStream.range(0, size).toArray();

        if (DATA_FILE == null) {
            DATA_FILE = new File(plugin.getDataFolder(), "enderchests.json");
        }

        if (!loaded) {
            Bukkit.getPluginManager().registerEvents(this, plugin);
            loadInventories();
            loaded = true;
        }

        addItemHandler(onBlockBreak());
        addItemHandler(onBlockPlace(c1, c2, c3));

        new BlockMenuPreset(getId(), "&eEnder Chest", true) {

            @Override
            public void init() {
                setSize(size);

                addMenuOpeningHandler(p -> {
                    p.playSound(p.getLocation(), Sound.BLOCK_ENDER_CHEST_OPEN, 1.8F, 1.6F);
                    Inventory inv = getInventoryFor(p.getUniqueId(), c1, c2, c3, size);
                    p.openInventory(inv);
                });
                addMenuCloseHandler(p -> p.playSound(p.getLocation(), Sound.BLOCK_ENDER_CHEST_CLOSE, 1.8F, 1.6F));
            }

            @Override
            public int[] getSlotsAccessedByItemTransport(ItemTransportFlow arg0) {
                return slots;
            }

            @Override
            public boolean canOpen(Block b, Player p) {
                String data = BlockStorage.getLocationInfo(b.getLocation(), "yaw");
                int yaw = 0;

                if (data != null) {
                    yaw = Integer.parseInt(data);
                } else if (b.getType() == Material.ENDER_CHEST) {
                    EnderChest chest = (EnderChest) b.getBlockData();

                    switch (chest.getFacing()) {
                        case NORTH:
                            yaw = 180;
                            break;
                        case SOUTH:
                            yaw = 0;
                            break;
                        case WEST:
                            yaw = 90;
                            break;
                        case EAST:
                            yaw = -90;
                            break;
                        default:
                            break;
                    }

                    BlockStorage.addBlockInfo(b, "yaw", String.valueOf(yaw));
                }

                ColorIndicator.updateIndicator(b, c1, c2, c3, yaw + 45);
                return true;
            }
        };
    }

    /**
     * Gera o SlimefunItemStack com a lore colorida para cada cor.
     */
    private static SlimefunItemStack getColoredStack(int size, int c1, int c2, int c3) {
        String id = "COLORED_ENDER_CHEST_" + (size == 27 ? "SMALL" : "BIG") + "_" + c1 + "_" + c2 + "_" + c3;
        String displayName = "&eArmazenamentos Ender &7(" + (size == 27 ? "Pequeno" : "Grande") + ")";
        List<String> lore = Arrays.asList(
                "",
                "&7Tamanho: &e" + size,
                "",
                getColoredLore(c1),
                getColoredLore(c2),
                getColoredLore(c3)
        );
        return new SlimefunItemStack(id, Material.ENDER_CHEST, displayName, lore.toArray(new String[0]));
    }

    /**
     * Retorna o nome da cor em português e com o ChatColor correspondente.
     */
    private static String getColoredLore(int color) {
        switch (color) {
            case 0: return ChatColor.WHITE + "Branco";
            case 1: return ChatColor.GOLD + "Laranja";
            case 2: return ChatColor.LIGHT_PURPLE + "Magenta";
            case 3: return ChatColor.AQUA + "Azul Claro";
            case 4: return ChatColor.YELLOW + "Amarelo";
            case 5: return ChatColor.GREEN + "Verde Limão";
            case 6: return ChatColor.LIGHT_PURPLE + "Rosa";
            case 7: return ChatColor.DARK_GRAY + "Cinza Escuro";
            case 8: return ChatColor.GRAY + "Cinza Claro";
            case 9: return ChatColor.DARK_AQUA + "Ciano";
            case 10: return ChatColor.DARK_PURPLE + "Roxo";
            case 11: return ChatColor.BLUE + "Azul";
            case 12: return ChatColor.GOLD + "Marrom";
            case 13: return ChatColor.DARK_GREEN + "Verde";
            case 14: return ChatColor.RED + "Vermelho";
            case 15: return ChatColor.BLACK + "Preto";
            default: return ChatColor.WHITE + "Desconhecido";
        }
    }

    private static String getChestKey(int c1, int c2, int c3, int size) {
        return c1 + "-" + c2 + "-" + c3 + "-" + size;
    }

    private static Inventory getInventoryFor(UUID uuid, int c1, int c2, int c3, int size) {
        String key = getChestKey(c1, c2, c3, size);
        playerChests.putIfAbsent(uuid, new HashMap<>());
        Map<String, Inventory> chests = playerChests.get(uuid);

        if (!chests.containsKey(key)) {
            Inventory inv = Bukkit.createInventory(null, size, "Colored Ender Chest");
            chests.put(key, inv);
        }
        return chests.get(key);
    }

    public static void saveInventories() {
        JSONObject root = new JSONObject();
        for (UUID uuid : playerChests.keySet()) {
            Map<String, Inventory> chests = playerChests.get(uuid);
            JSONObject chestsJson = new JSONObject();
            for (String key : chests.keySet()) {
                Inventory inv = chests.get(key);
                chestsJson.put(key, ItemStackSerializationUtil.itemStackArrayToBase64(inv.getContents()));
            }
            root.put(uuid.toString(), chestsJson);
        }

        try {
            DATA_FILE.getParentFile().mkdirs();
            try (FileWriter writer = new FileWriter(DATA_FILE)) {
                writer.write(root.toJSONString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadInventories() {
        if (!DATA_FILE.exists()) return;
        try (FileReader reader = new FileReader(DATA_FILE)) {
            JSONObject root = (JSONObject) new JSONParser().parse(reader);
            for (Object uuidO : root.keySet()) {
                UUID uuid = UUID.fromString((String) uuidO);
                JSONObject chestsJson = (JSONObject) root.get(uuidO);
                Map<String, Inventory> chests = new HashMap<>();
                for (Object keyO : chestsJson.keySet()) {
                    String key = (String) keyO;
                    String base64 = (String) chestsJson.get(keyO);
                    ItemStack[] contents = ItemStackSerializationUtil.itemStackArrayFromBase64(base64);
                    Inventory inv = Bukkit.createInventory(null, getSizeFromKey(key), "Colored Ender Chest");
                    inv.setContents(contents);
                    chests.put(key, inv);
                }
                playerChests.put(uuid, chests);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int getSizeFromKey(String key) {
        String[] split = key.split("-");
        return Integer.parseInt(split[3]);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().getType() == InventoryType.CHEST && event.getPlayer() instanceof Player) {
            saveInventories();
        }
    }

    private BlockBreakHandler onBlockBreak() {
        return new SimpleBlockBreakHandler() {
            @Override
            public void onBlockBreak(Block b) {
                ColorIndicator.removeIndicator(b);
            }
        };
    }

    private BlockPlaceHandler onBlockPlace(int c1, int c2, int c3) {
        return new BlockPlaceHandler(false) {
            @Override
            public void onPlayerPlace(BlockPlaceEvent e) {
                int yaw = 0;
                EnderChest chest = (EnderChest) e.getBlock().getBlockData();
                switch (chest.getFacing()) {
                    case NORTH:
                        yaw = 180;
                        break;
                    case SOUTH:
                        yaw = 0;
                        break;
                    case WEST:
                        yaw = 90;
                        break;
                    case EAST:
                        yaw = -90;
                        break;
                    default:
                        break;
                }
                BlockStorage.addBlockInfo(e.getBlock(), "yaw", String.valueOf(yaw));
                ColorIndicator.updateIndicator(e.getBlock(), c1, c2, c3, yaw + 45);
            }
        };
    }

    private static ItemStack getSmallerEnderChest(int c1, int c2, int c3) {
        SlimefunItem enderChest = SlimefunItem.getById("COLORED_ENDER_CHEST_SMALL_" + c1 + "_" + c2 + "_" + c3);
        return enderChest != null ? enderChest.getItem() : null;
    }
}