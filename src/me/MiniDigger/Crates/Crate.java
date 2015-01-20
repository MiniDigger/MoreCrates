package me.MiniDigger.Crates;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class Crate {

	private Location loc;
	private Inventory inv;

	private void createInv(Player player) {
		inv = Bukkit.createInventory(
				null,
				Crates.getInstance().getConfig().getInt("crate.size") * 9,
				Crates.getInstance().getConfig().getString("crate.display-name")
			);
	}
	
	public Crate(Location loc) {
		this.loc = loc;
		createInv(null);
	}

	private Crate() {
		createInv(null);
	}

	public void open(Player p) {
		if (!Crates.getInstance().getConfig()
				.getBoolean("use-perms-for-opening")) {
			p.openInventory(getInv());
		} else if (p.hasPermission("crate.open")) {
			p.openInventory(getInv());
		} else {
			Crates.getInstance().getPrefix().then("You don't have the ")
					.color(ChatColor.RED).then("permission ")
					.color(ChatColor.RED).tooltip("crate.open")
					.then(" to open this crate!").color(ChatColor.RED);
		}
	}

	public Inventory getInv() {
		return inv;
	}

	public void setInv(Inventory inv) {
		this.inv = inv;
	}

	public Location getLoc() {
		return loc;
	}

	public void setLoc(Location loc) {
		this.loc = loc;
	}
}
