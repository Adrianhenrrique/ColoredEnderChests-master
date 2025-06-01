# ColoredEnderChests

A Bukkit/Spigot/Paper plugin for [Slimefun](https://github.com/Slimefun/Slimefun4) that adds customizable, per-player, per-color Ender Chests with persistent storage and colorful, dynamic item lore.

---

## Features

- **Colored Ender Chests:**  
  Craft Ender Chests using three dye colors (all 16 Minecraft colors supported).  
  Each unique color combination creates a distinct chest.

- **Per-Player, Per-Color Inventory:**  
  Every player has their own private inventory for every color combination—safe and secure for all items!

- **Persistent Storage:**  
  Inventories are automatically saved locally in `enderchests.json` inside the plugin folder.  
  Your items are never lost, even after server restarts.

- **Colorful Lore:**  
  The item's lore dynamically shows the chosen colors, each one displayed in its respective chat color and in Portuguese.

- **Slimefun Integration:**  
  Full support for Slimefun recipes, menus, and custom handlers.

- **Safe Serialization:**  
  All inventories are serialized using Bukkit's safe mechanisms to prevent item loss or corrupt data.

---

## Example

A colored ender chest with Orange, White, and Lime:
```
Tamanho: 27

§eLaranja
§fBranco
§aVerde Limão
```

---

## Installation

1. **Requirements:**
   - [Slimefun 4](https://github.com/Slimefun/Slimefun4)
   - Bukkit/Spigot/Paper server (1.16+ recommended)

2. **How to install:**
   - Download the latest release from the [Releases page](../../releases).
   - Place the plugin `.jar` in your server’s `plugins` folder.
   - Restart your server.

3. **Configuration:**
   - No manual configuration is required. All player data is stored automatically in `plugins/ColoredEnderChests/enderchests.json`.

---

## Usage

- **Crafting:**  
  Use the Slimefun Ancient Altar with dyes and the required items to craft colored ender chests.
- **Access:**  
  Place and right-click your colored ender chest to access your private inventory for that color combination.
- **Persistence:**  
  All items are kept safe between restarts.

---

## Building

1. Clone the repository:
   ```bash
   git clone https://github.com/Adrianhenrrique/ColoredEnderChests-master.git
   ```
2. Build with Maven:
   ```bash
   mvn clean package
   ```
3. The compiled `.jar` will be in the `target/` folder.

---

## Contributing

Pull requests are welcome! For major changes, please open an issue first to discuss what you would like to change.

---

## License

MIT License

---

**Made for the Minecraft Slimefun Community!**