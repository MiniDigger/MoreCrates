package me.MiniDigger.Crates;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

@SerializableAs("EnderCrate")
public class EnderCrate implements ConfigurationSerializable {

	private UUID uuid;
	private Inventory inv;

	public EnderCrate(Player p) {
		uuid = p.getUniqueId();
		inv = Bukkit.createInventory(p, Crates.getInstance().getConfig()
				.getInt("endercrate.size") * 9, Crates.getInstance()
				.getConfig().getString("endercrate.display-name"));
	}

	public EnderCrate() {
		inv = Bukkit.createInventory(null, Crates.getInstance().getConfig()
				.getInt("endercrate.size") * 9, Crates.getInstance()
				.getConfig().getString("endercrate.display-name"));
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> result = new HashMap<>();

		result.put("uuid", uuid.toString());
		result.put("inv", Utils.InventoryToString(inv));

		return result;
	}

	public static EnderCrate deserialize(Map<String, Object> arg) {
		EnderCrate result = new EnderCrate();

		result.uuid = UUID.fromString((String) arg.get("uuid"));
		result.inv = Utils.StringToInventory((String) arg.get("inv"));

		return result;
	}

	public void open(Player p) {
		if (p.hasPermission("endercrate.open")) {
			p.openInventory(getInv());
		}
	}

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public Inventory getInv() {
		return inv;
	}

	public void setInv(Inventory inv) {
		this.inv = inv;
	}

}
