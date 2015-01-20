package saharnooby.plugins.MoreCrates;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

import me.MiniDigger.Crates.Crate;
import me.MiniDigger.Crates.Crates;
import me.MiniDigger.Crates.EnderCrate;
import me.MiniDigger.Crates.Utils;

import org.bukkit.Location;

public class CrateFiles {

	/**
	 * Save specified crate into file.
	 * @param crate Crate
	 */
	public static void saveCrate(Crate crate) {
		if (crate == null)
			return;
		
		String loc = Utils.LocationToString(crate.getLoc());
		String inv = Utils.InventoryToString(crate.getInv());
		
		File dir = new File(Crates.getInstance().getDataFolder(), "crates");
		if (!dir.exists()) dir.mkdir();
		
		File crateFile = new File(dir, loc);
		if (crateFile.exists()) crateFile.delete();
		try {
			crateFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		PrintWriter out;
		try {
			out = new PrintWriter(crateFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}

		try {
			out.print(inv);
		} finally {
			out.close();
		}
	}
	
	/**
	 * Load crate on specified location.
	 * @param location Location of the crate.
	 * @return Loaded crate with items, or empty crate if file is not exists.
	 */
	public static Crate loadCrate(Location location) {
		String loc = Utils.LocationToString(location);
		
		File dir = new File(Crates.getInstance().getDataFolder(), "crates");
		if (!dir.exists())
			return null;
		
		File crateFile = new File(dir, loc);
		if (!crateFile.exists())
			return null;
		
		String inv;
		try {
			BufferedReader in = new BufferedReader(new FileReader(crateFile));
			try {
				inv = in.readLine();
				if (inv == null)
					return null;
			} finally {
				in.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		Crate crate = new Crate(location);
		crate.setInv(Utils.StringToInventory(inv, Crates.getInstance().getConfig().getString("crate.display-name")));
		return crate;
	}
	
	/**
	 * @see CrateFiles#saveCrate
	 */
	public static void saveEnderCrate(EnderCrate crate) {
		if (crate == null)
			return;
		
		String uuid = crate.getUuid().toString();
		String inv = Utils.InventoryToString(crate.getInv());
		
		File dir = new File(Crates.getInstance().getDataFolder(), "enderCrates");
		if (!dir.exists()) dir.mkdir();
		
		File crateFile = new File(dir, uuid);
		if (crateFile.exists()) crateFile.delete();
		try {
			crateFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		PrintWriter out;
		try {
			out = new PrintWriter(crateFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}

		try {
			out.print(inv);
		} finally {
			out.close();
		}
	}
	
	/**
	 * @see CrateFiles#loadCrate
	 */
	public static EnderCrate loadEnderCrate(UUID uuid) {
		String uuidString = uuid.toString();
		
		File dir = new File(Crates.getInstance().getDataFolder(), "enderCrates");
		if (!dir.exists())
			return null;
		
		File crateFile = new File(dir, uuidString);
		if (!crateFile.exists())
			return null;
		
		String inv;
		try {
			BufferedReader in = new BufferedReader(new FileReader(crateFile));
			try {
				inv = in.readLine();
				if (inv == null)
					return null;
			} finally {
				in.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		EnderCrate crate = new EnderCrate(uuid);
		crate.setInv(Utils.StringToInventory(inv, Crates.getInstance().getConfig().getString("endercrate.display-name")));
		return crate;
	}

	/**
	 * Remove crate file.
	 * @param crate Crate.
	 */
	public static void removeCrate(Crate crate) {
		if (crate == null)
			return;
		
		String loc = Utils.LocationToString(crate.getLoc());
		String inv = Utils.InventoryToString(crate.getInv());
		
		File dir = new File(Crates.getInstance().getDataFolder(), "crates");
		if (!dir.exists())
			return;
		
		File crateFile = new File(dir, loc);
		if (crateFile.exists())
			crateFile.delete();
	}
	
	/**
	 * @see CrateFiles#removeCrate(Crate)
	 */
	public static void removeEnderCrate(EnderCrate crate) {
		if (crate == null)
			return;
		
		String uuid = crate.getUuid().toString();
		String inv = Utils.InventoryToString(crate.getInv());
		
		File dir = new File(Crates.getInstance().getDataFolder(), "enderCrates");
		if (!dir.exists())
			return;
		
		File crateFile = new File(dir, uuid);
		if (crateFile.exists())
			crateFile.delete();
	}
}
