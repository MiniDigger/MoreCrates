package me.MiniDigger.Crates;

import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;

public class Utils {
	/**
	 * https://forums.bukkit.org/threads/serialize-inventory-to-single-string-
	 * and-vice-versa.92094/
	 * 
	 * @param invInventory
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static String InventoryToString(Inventory invInventory) {
		String serialization = invInventory.getSize() + ";";
		for (int i = 0; i < invInventory.getSize(); i++) {
			ItemStack is = invInventory.getItem(i);
			if (is != null) {
				String serializedItemStack = new String();

				String isType = String.valueOf(is.getType().getId());
				serializedItemStack += "t@" + isType;

				if (is.getDurability() != 0) {
					String isDurability = String.valueOf(is.getDurability());
					serializedItemStack += ":d@" + isDurability;
				}

				if (is.getAmount() != 1) {
					String isAmount = String.valueOf(is.getAmount());
					serializedItemStack += ":a@" + isAmount;
				}

				Map<Enchantment, Integer> isEnch = is.getEnchantments();
				if (isEnch.size() > 0) {
					for (Entry<Enchantment, Integer> ench : isEnch.entrySet()) {
						serializedItemStack += ":e@" + ench.getKey().getId()
								+ "@" + ench.getValue();
					}
				}

				serialization += i + "#" + serializedItemStack + ";";
			}
		}
		return serialization;
	}

	@SuppressWarnings("deprecation")
	public static Inventory StringToInventory(String invString, String name) {
		String[] serializedBlocks = invString.split(";");
		String invInfo = serializedBlocks[0];
		Inventory deserializedInventory = Bukkit.getServer().createInventory(null, Integer.valueOf(invInfo), name);

		for (int i = 1; i < serializedBlocks.length; i++) {
			String[] serializedBlock = serializedBlocks[i].split("#");
			int stackPosition = Integer.valueOf(serializedBlock[0]);

			if (stackPosition >= deserializedInventory.getSize()) {
				continue;
			}

			ItemStack is = null;
			Boolean createdItemStack = false;

			String[] serializedItemStack = serializedBlock[1].split(":");
			for (String itemInfo : serializedItemStack) {
				String[] itemAttribute = itemInfo.split("@");
				if (itemAttribute[0].equals("t")) {
					is = new ItemStack(Material.getMaterial(Integer
							.valueOf(itemAttribute[1])));
					createdItemStack = true;
				} else if (itemAttribute[0].equals("d") && createdItemStack) {
					is.setDurability(Short.valueOf(itemAttribute[1]));
				} else if (itemAttribute[0].equals("a") && createdItemStack) {
					is.setAmount(Integer.valueOf(itemAttribute[1]));
				} else if (itemAttribute[0].equals("e") && createdItemStack) {
					is.addEnchantment(Enchantment.getById(Integer
							.valueOf(itemAttribute[1])), Integer
							.valueOf(itemAttribute[2]));
				}
			}
			deserializedInventory.setItem(stackPosition, is);
		}

		return deserializedInventory;
	}

	public static Location StringToLocation(String locString) {
		String[] loc = locString.split(";");

		World world = Bukkit.getWorld(loc[0]);
		double x = Integer.parseInt(loc[1]);
		double y = Integer.parseInt(loc[2]);
		double z = Integer.parseInt(loc[3]);

		return new Location(world, x, y, z, 0, 0);
	}

	public static String LocationToString(Location loc) {
		String result = "";

		result += loc.getWorld().getName() + ";";
		result += (int) loc.getX() + ";";
		result += (int) loc.getY() + ";";
		result += (int) loc.getZ();

		return result;
	}
}
