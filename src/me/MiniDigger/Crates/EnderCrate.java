package me.MiniDigger.Crates;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class EnderCrate {

	private UUID uuid;
	private Inventory inv;

	private void createInv(Player player) {
		inv = Bukkit.createInventory(
				player,
				Crates.getInstance().getConfig().getInt("endercrate.size") * 9,
				Crates.getInstance().getConfig().getString("endercrate.display-name")
			);
	}
	
	public EnderCrate(UUID uuid) {
		this.uuid = uuid;
		createInv(null);
	}
	
	public EnderCrate(Player p) {
		uuid = p.getUniqueId();
		createInv(p);
	}

	private EnderCrate() {
		createInv(null);
	}

	public void open(Player p) {
		try {
			if (!WorldGuardHook.getInstance().shouldOpen(p, p.getLocation())) {
				return;
			}
		} catch (Exception ex) {
			Crates.getInstance()
					.getLogger()
					.warning(
							"Failed to check WorldGuard flags for player"
									+ p.getName() + "! " + ex.getMessage());
		}

		if (!Crates.getInstance().getConfig()
				.getBoolean("use-perms-for-opening")) {
			p.openInventory(getInv());
		} else if (p.hasPermission("endercrate.open")) {
			p.openInventory(getInv());
		} else {
			Crates.getInstance().getPrefix().then("You don't have the ")
					.color(ChatColor.RED).then("permission ")
					.color(ChatColor.RED).tooltip("endercrate.open")
					.then(" to open this crate!").color(ChatColor.RED);
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
