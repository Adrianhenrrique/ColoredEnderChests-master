package io.github.thebusybiscuit.coloredenderchests;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.researches.Research;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.libraries.dough.config.Config;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.libraries.dough.updater.GitHubBuildsUpdater;

public class ColoredEnderChests extends JavaPlugin implements SlimefunAddon {

    protected Config cfg;
    protected Map<Integer, String> colors = new HashMap<>();
    protected ItemGroup itemGroup;

    @Override
    public void onEnable() {
        cfg = new Config(this);

        // Auto-Updater (opcional)
        if (cfg.getBoolean("options.auto-update") && getDescription().getVersion().startsWith("DEV - ")) {
            new GitHubBuildsUpdater(this, getFile(), "TheBusyBiscuit/ColoredEnderChests/master").start();
        }

        Research enderChestsResearch = new Research(new NamespacedKey(this, "colored_enderchests"), 2610, "Armazenamentos Ender", 20);
        Research bigEnderChestsResearch = new Research(new NamespacedKey(this, "big_colored_enderchests"), 2611, "Big Armazenamentos Ender", 30);

        enderChestsResearch.register();
        bigEnderChestsResearch.register();
//s
        colors.put(0, "&rBranco");
        colors.put(1, "&6Laranja");
        colors.put(2, "&dMagenta");
        colors.put(3, "&bAzul Claro");
        colors.put(4, "&eAmarelo");
        colors.put(5, "&aVerde Limão");
        colors.put(6, "&dRosa");
        colors.put(7, "&8Cinza Escuro");
        colors.put(8, "&7Cinza Claro");
        colors.put(9, "&3Ciano");
        colors.put(10, "&5Roxo");
        colors.put(11, "&9Azul");
        colors.put(12, "&6Marrom");
        colors.put(13, "&2Verde");
        colors.put(14, "&4Vermelho");
        colors.put(15, "&8Preto");

        itemGroup = new ItemGroup(
                new NamespacedKey(this, "colored_enderchests"),
                CustomItemStack.create(Material.ENDER_CHEST, "&5Armazenamento Ender"), 2);

        for (int c1 = 0; c1 < 16; c1++) {
            for (int c2 = 0; c2 < 16; c2++) {
                for (int c3 = 0; c3 < 16; c3++) {
                    registerEnderChest(enderChestsResearch, bigEnderChestsResearch, c1, c2, c3);
                }
            }
        }
    }

    private void registerEnderChest(Research smallResearch, Research bigResearch, final int c1, final int c2, final int c3) {
        if (cfg.getBoolean("small_chests")) {
            ColoredEnderChest item = new ColoredEnderChest(this, 27, c1, c2, c3);
            item.register(this);
            smallResearch.addItems(item);
        }

        if (cfg.getBoolean("big_chests")) {
            ColoredEnderChest item = new ColoredEnderChest(this, 54, c1, c2, c3);
            item.register(this);
            bigResearch.addItems(item);
        }
    }

    @Override
    public JavaPlugin getJavaPlugin() {
        return this;
    }

    @Override
    public void onDisable() {
        ColoredEnderChest.saveInventories();
    }

    @Override
    public String getBugTrackerURL() {
        return "https://github.com/TheBusyBiscuit/ColoredEnderChests/issues";
    }

    public ItemGroup getItemGroup() {
        return itemGroup;
    }
}