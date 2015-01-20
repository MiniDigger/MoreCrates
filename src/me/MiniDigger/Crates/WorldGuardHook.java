package me.MiniDigger.Crates;

import static com.sk89q.worldguard.bukkit.BukkitUtil.toVector;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;

public class WorldGuardHook {

	private WorldGuardPlugin worldGuard;
	private boolean protectCrates = false;

	public boolean enableHook() {
		protectCrates = Crates.getInstance().getConfig()
				.getBoolean("worldguard-support");
		if (protectCrates) {
			Crates.getInstance().getLogger()
					.info("Searching for WorldGuard...");
			Plugin plugin = Crates.getInstance().getServer().getPluginManager()
					.getPlugin("WorldGuard");

			if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
				worldGuard = null;
				Crates.getInstance()
						.getLogger()
						.warning(
								"WorldGuard not found. Your Crates may be unprotected!");
				protectCrates = false;
			} else {
				worldGuard = (WorldGuardPlugin) plugin;
				Crates.getInstance()
						.getLogger()
						.info("Hooked into WorldGuard, Crate protection enabled!");
				protectCrates = true;
			}
		}
		return protectCrates;
	}

	public boolean shouldOpen(Player p, Location loc) {
		Vector v = toVector(loc);
		RegionManager regionManager = worldGuard.getRegionManager(p
				.getLocation().getWorld());
		ApplicableRegionSet set = regionManager.getApplicableRegions(v);
		LocalPlayer localPlayer = worldGuard.wrapPlayer(p);
		return set.allows(DefaultFlag.CHEST_ACCESS, localPlayer);
	}

	private static WorldGuardHook instance;

	public static WorldGuardHook getInstance() {
		if (instance == null) {
			instance = new WorldGuardHook();
		}
		return instance;
	}
}
