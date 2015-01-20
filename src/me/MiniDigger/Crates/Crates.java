package me.MiniDigger.Crates;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import mkremins.fanciful.FancyMessage;
import net.gravitydevelopment.updater.Updater;
import net.gravitydevelopment.updater.Updater.UpdateType;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;
import org.mcstats.Metrics.Graph;
import org.mcstats.Metrics.Plotter;

import saharnooby.plugins.MoreCrates.CrateFiles;

public class Crates extends JavaPlugin implements Listener {

	public ItemStack crate;
	public ItemStack endercrate;
	private static Crates instance;
	private HashMap<UUID, Location> openCrates;
	private boolean worldguard_hooked = false;
	private final String consolPrefix = ChatColor.RED + "[" + ChatColor.GRAY
			+ "MoreCrates" + ChatColor.RED + "]";

	public FancyMessage getPrefix() {
		return new FancyMessage("[").color(ChatColor.RED).then("Crates")
				.color(ChatColor.GRAY).tooltip("Made by MiniDigger").then("] ")
				.color(ChatColor.RED);
	}

	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public void onEnable() {
		instance = this;
		saveDefaultConfig();
		getLogger().info("Registering...");
		getServer().getPluginManager().registerEvents(this, this);

		getLogger().info("Creating Crates...");
		crate = new ItemStack(Material.CHEST);
		ItemMeta meta = crate.getItemMeta();
		String ds = getConfig().getString("crate.display-name");
		meta.setDisplayName(ds);
		meta.setLore((List<String>) getConfig().getList("crate.lore"));
		crate.setItemMeta(meta);

		getLogger().info("Creating EnderCrates...");
		endercrate = new ItemStack(Material.TRAPPED_CHEST);
		meta = endercrate.getItemMeta();
		meta.setDisplayName(getConfig().getString("endercrate.display-name"));
		meta.setLore((List<String>) getConfig().getList("endercrate.lore"));
		endercrate.setItemMeta(meta);

		if (getConfig().getBoolean("enable-crafting")) {
			getLogger().info("Add Recipes...");
			try {
				ShapedRecipe cR = new ShapedRecipe(crate);
				cR.shape("PXP", "XCX", "PXP");
				cR.setIngredient('P', Material.getMaterial(33));
				cR.setIngredient('C', Material.CHEST);
				getServer().addRecipe(cR);
			} catch (Exception ex) {
				getLogger().info(
						"Failed to add Recipe for Crates: " + ex.getMessage()
								+ ", using default one!");
				ShapedRecipe cR = new ShapedRecipe(crate);
				cR.shape("PXP", "XCX", "PXP");
				cR.setIngredient('P', Material.getMaterial(33));
				cR.setIngredient('C', Material.CHEST);
				getServer().addRecipe(cR);
			}
			try {

			} catch (Exception ex) {
				getLogger().info(
						"Failed to add Recipe for EnderCrates: "
								+ ex.getMessage() + ", using default one!");
				ShapedRecipe ecR = new ShapedRecipe(endercrate);
				ecR.shape("PXP", "XCX", "PXP");
				ecR.setIngredient('P', Material.getMaterial(29));
				ecR.setIngredient('C', Material.ENDER_CHEST);
				getServer().addRecipe(ecR);
			}
		}

		if(Crates.getInstance().getServer().getPluginManager()
				.getPlugin("WorldGuard")!=null){
			WorldGuardHook.getInstance().enableHook();
		}

		openCrates = new HashMap<>();
		getLogger().info("Metrics...");
		Bukkit.getScheduler().runTaskAsynchronously(this, new Runnable() {

			@Override
			public void run() {
				try {
					Metrics metrics = new Metrics(Crates.getInstance());

					Graph g = metrics.createGraph("Crates Created");
					g.addPlotter(new Plotter("Crates") {

						@Override
						public int getValue() {
							return getConfig().getInt("crates.n");
						}
					});
					g.addPlotter(new Plotter("EnderCrates") {

						@Override
						public int getValue() {
							return getConfig().getInt("endercrates.n");
						}
					});

					metrics.start();

				} catch (IOException e) {
					getLogger().warning("Could not enable Metrcis!");
					e.printStackTrace();
					getLogger()
							.warning(
									"The Plugin will still work, but the statistics will no be updated.");
				}

			}
		});

		getLogger().info("Updater...");
		Bukkit.getScheduler().runTaskAsynchronously(this, new Runnable() {

			@Override
			public void run() {

				if (getConfig().getBoolean("look-for-updates")) {
					UpdateType type = UpdateType.NO_DOWNLOAD;
					if (getConfig().getBoolean("download-updates")) {
						type = UpdateType.DEFAULT;
					}
					Updater updater = new Updater(Crates.getInstance(), 79422,
							Crates.getInstance().getFile(), type, false);
					switch (updater.getResult()) {
					case DISABLED:
						getLogger()
								.warning(
										"You have disabled the updating system, so that my plugin can not look for updates ;(");
						break;
					case FAIL_APIKEY:
						getLogger()
								.warning(
										"You have changed something so that my updater broke!");
						break;
					case FAIL_BADID:
						getLogger()
								.warning(
										"I have failed to provide a valid api key for the updater! Please report this error!");
						break;
					case FAIL_DBO:
						getLogger().warning(
								"The updater could not contact dbo!");
						break;
					case FAIL_DOWNLOAD:
						getLogger().warning(
								"The updater failed to download the update!");
						break;
					case FAIL_NOVERSION:
						getLogger()
								.warning(
										"I failed to name my files on dbo right! Please report this error!");
						break;
					case NO_UPDATE:
						getLogger().info("The plugin is up-to-date!");
						break;
					case SUCCESS:
						getLogger()
								.info("The server has found an update and performed it successfully!");
						break;
					case UPDATE_AVAILABLE:
						getLogger()
								.info("There is an update available! You can download it by typing in /crates update");
						break;
					}
				} else {
					getLogger()
							.info("Update notifications are disabled! You can enable then by setting 'look-for-updates' in the config to 'true'");
				}

			}
		});
	}

	@EventHandler
	public void onCraft(PrepareItemCraftEvent e) {
		if (e.getRecipe().getResult().equals(crate)) {
			if (!e.getView().getPlayer().hasPermission("crate.craft")) {
				e.getInventory().setItem(0, new ItemStack(Material.AIR));
			}
		}
		if (e.getRecipe().getResult().equals(endercrate)) {
			if (!e.getView().getPlayer().hasPermission("endercrate.craft")) {
				e.getInventory().setItem(0, new ItemStack(Material.AIR));
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onCrateClick(final PlayerInteractEvent e) {
		if (e.getAction() != Action.RIGHT_CLICK_BLOCK || e.isCancelled())
			return;
		
		Block block = e.getClickedBlock();
		
		if (!isCrate(block) && !isEnderCrate(block))
			return;
			
		Player player = e.getPlayer();
		
		boolean b = false;
		if (isCrate(block)) {
			openCrates.put(player.getUniqueId(), block.getLocation());
			getCrate(block.getLocation()).open(player);
			b = true;
		} else if (isEnderCrate(block)) {
			getEnderCrate(player).open(player);
			b = true;
		}
		if (b) {
			e.setCancelled(true);
			e.setUseItemInHand(Result.DENY);
			e.setUseInteractedBlock(Result.DENY);
		}
	}

	@EventHandler
	public void onCrateDestory(final BlockBreakEvent e) {
		if(e.isCancelled()) {
			return;
		}
		
		Block block = e.getBlock();
		Location loc = block.getLocation();
		
		if (isCrate(block)) {
			e.setCancelled(true);
			Crate crate = getCrate(loc);
			
			block.setType(Material.AIR);
			loc.getWorld().dropItem(loc, this.crate);
			
			for (ItemStack is : crate.getInv().getContents()) {
				if (is == null) {
					continue;
				}
				loc.getWorld().dropItem(loc, is);
			}
			
			CrateFiles.removeCrate(crate);
		} else if (isEnderCrate(block)) {
			e.setCancelled(true);
			
			block.setType(Material.AIR);
			loc.getWorld().dropItem(loc, endercrate);
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onCratePlace(final BlockPlaceEvent e) {
		if (e.isCancelled() || !e.canBuild()) {
			return;
		}
		
		Location loc = e.getBlockPlaced().getLocation();
		ItemStack i = e.getItemInHand();
		
		try {
			if (i.getType() == crate.getType() &&
					i.getItemMeta().getDisplayName().equals(getConfig().getString("crate.display-name"))) {
				placeCrate(loc);
			} else if (i.getType() == endercrate.getType() &&
					i.getItemMeta().getDisplayName().equals(getConfig().getString("endercrate.display-name"))) {
				placeEnderCrate(loc);
			}
		} catch (Exception ex) {
			// No item meta
		}
	}

	@EventHandler
	public void onCrateClose(InventoryCloseEvent e) {
		UUID uuid = e.getPlayer().getUniqueId();
		
		if (e.getInventory().getTitle().equals(getConfig().getString("crate.display-name"))) {
			Crate crate = getCrate(openCrates.get(uuid));
			openCrates.remove(uuid);
			crate.setInv(e.getInventory());
			
			CrateFiles.saveCrate(crate);
		} else if (e.getInventory().getTitle().equals(getConfig().getString("endercrate.display-name"))) {
			EnderCrate crate = getEnderCrate((Player) e.getPlayer());
			crate.setInv(e.getInventory());
			
			CrateFiles.saveEnderCrate(crate);
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (command.getName().equals("crate")) {
			if (sender instanceof Player) {
				Player p = (Player) sender;
				if (args.length == 0) {
					FancyMessage msg = getPrefix().then("Here is your Crate!")
							.color(ChatColor.GREEN);
					msg.send(p);
					p.getInventory().addItem(crate);
				} else if (args.length == 1) {
					if (!p.hasPermission("crate.command.other")) {
						FancyMessage msg = getPrefix().then(
								"You don't have permission to give " + args[0]
										+ " a crate!").color(ChatColor.RED);
						msg.send(p);
						return true;
					}
					@SuppressWarnings("deprecation")
					Player p2 = Bukkit.getPlayer(args[0]);
					if (p2 == null) {
						FancyMessage msg = getPrefix().then(
								"That player is not online!").color(
								ChatColor.RED);
						msg.send(p);
						return true;
					}
					FancyMessage msg = getPrefix().then("Here is your Crate!")
							.color(ChatColor.GREEN);
					msg.send(p);
					msg = getPrefix().then(
							"You gave " + p2.getName() + " a crate").color(
							ChatColor.GREEN);
					msg.send(p);
					p2.getInventory().addItem(crate);
				} else {
					FancyMessage msg = getPrefix().then("Wrong usage! ")
							.color(ChatColor.RED).then("/crate [player]")
							.color(ChatColor.YELLOW);
					msg.send(p);
				}
			} else {
				sender.sendMessage(ChatColor.RED
						+ "The consol can not perform this command!");
			}
			return true;
		} else if (command.getName().equals("endercrate")) {
			if (sender instanceof Player) {
				Player p = (Player) sender;
				if (args.length == 0) {
					FancyMessage msg = getPrefix().then(
							"Here is your EnderCrate!").color(ChatColor.GREEN);
					msg.send(p);
					p.getInventory().addItem(endercrate);
				} else if (args.length == 1) {
					if (!p.hasPermission("endercrate.command.other")) {
						FancyMessage msg = getPrefix().then(
								"You don't have permission to give " + args[0]
										+ " a endercrate!")
								.color(ChatColor.RED);
						msg.send(p);
						return true;
					}
					@SuppressWarnings("deprecation")
					Player p2 = Bukkit.getPlayer(args[0]);
					if (p2 == null) {
						FancyMessage msg = getPrefix().then(
								"That player is not online!").color(
								ChatColor.RED);
						msg.send(p);
						return true;
					}
					FancyMessage msg = getPrefix().then(
							"Here is your EnderCrate!").color(ChatColor.GREEN);
					msg.send(p);
					msg = getPrefix().then(
							"You gave " + p2.getName() + " a endercrate")
							.color(ChatColor.GREEN);
					msg.send(p);
					p2.getInventory().addItem(endercrate);
				} else {
					FancyMessage msg = getPrefix().then("Wrong usage! ")
							.color(ChatColor.RED).then("/endercrate [player]")
							.color(ChatColor.YELLOW);
					msg.send(p);
				}
			} else {
				sender.sendMessage(ChatColor.RED
						+ "The consol can not perform this command!");
			}
			return true;
		} else if (command.getName().equals("openendercrate")) {
			if (sender instanceof Player) {
				Player p = (Player) sender;
				if (args.length == 0) {
					getEnderCrate(p).open(p);
				} else if (args.length == 1) {
					if (!p.hasPermission("openendercrate.command.other")) {
						FancyMessage msg = getPrefix().then(
								"You don't have permission to open " + args[0]
										+ "'s endercrate!")
								.color(ChatColor.RED);
						msg.send(p);
						return true;
					}
					@SuppressWarnings("deprecation")
					Player p2 = Bukkit.getPlayer(args[0]);
					if (p2 == null) {
						FancyMessage msg = getPrefix().then(
								"That player is not online!").color(
								ChatColor.RED);
						msg.send(p);
						return true;
					}
					getEnderCrate(p2).open(p);
				} else {
					FancyMessage msg = getPrefix().then("Wrong usage! ")
							.color(ChatColor.RED)
							.then("/openendercrate [player]")
							.color(ChatColor.YELLOW);
					msg.send(p);
				}
			} else {
				sender.sendMessage(ChatColor.RED
						+ "The consol can not perform this command!");
			}
			return true;
		} else if (command.getName().equals("crates")) {
			if (args.length == 1) {
				if (args[0].equals("update")) {
					Updater updater = new Updater(this, 79422, this.getFile(),
							UpdateType.DEFAULT, false);
					switch (updater.getResult()) {
					case DISABLED:
						sender.sendMessage(consolPrefix
								+ "You have disabled the updating system, so that my plugin can not look for updates ;(");
						break;
					case FAIL_APIKEY:
						sender.sendMessage(consolPrefix
								+ "You have changed something so that my updater broke!");
						break;
					case FAIL_BADID:
						sender.sendMessage(consolPrefix
								+ "I have failed to provide a valid api key for the updater! Please report this error!");
						break;
					case FAIL_DBO:
						sender.sendMessage(consolPrefix
								+ "The updater could not contact dbo!");
						break;
					case FAIL_DOWNLOAD:
						sender.sendMessage(consolPrefix
								+ "The updater failed to download the update!");
						break;
					case FAIL_NOVERSION:
						sender.sendMessage(consolPrefix
								+ "I failed to name my files on dbo right! Please report this error!");
						break;
					case NO_UPDATE:
						sender.sendMessage(consolPrefix
								+ "The plugin is up-to-date!");
						break;
					case SUCCESS:
						sender.sendMessage(consolPrefix
								+ "The server has found an update and performed it successfully!");
						break;
					case UPDATE_AVAILABLE:
						sender.sendMessage(consolPrefix
								+ "There is an update available! You can download it by typing in /crates update");
						break;
					}
					return true;
				}
			}
			if (sender instanceof Player) {
				Player p = (Player) sender;
				FancyMessage msg = getPrefix().then("The Server is using ")
						.color(ChatColor.GRAY).then("MoreCrates ")
						.color(ChatColor.YELLOW).then(" version ")
						.color(ChatColor.GRAY)
						.then(getDescription().getVersion())
						.color(ChatColor.YELLOW).then(" by ")
						.color(ChatColor.GRAY).then("MiniDigger!")
						.color(ChatColor.YELLOW);
				msg.send(p);
			} else {
				sender.sendMessage(consolPrefix
						+ "This Serve ris using MoreCrate version "
						+ getDescription().getVersion() + " by MiniDigger");
			}
		}
		return false;
	}

	private void placeCrate(Location loc) {
		loc.getBlock().setTypeIdAndData(33, (byte) 6, false);
	}

	
	private void placeEnderCrate(Location loc) {
		loc.getBlock().setTypeIdAndData(29, (byte) 6, false);
	}

	
	private boolean isCrate(Block block) {
		return block.getTypeId() == 33 && block.getData() == 6;
	}

	
	private boolean isEnderCrate(Block block) {
		return block.getTypeId() == 29 && block.getData() == 6;
	}

	/**
	 * @see Crates#getCrate(Location)
	 */
	private EnderCrate getEnderCrate(Player p) {
		EnderCrate crate = CrateFiles.loadEnderCrate(p.getUniqueId());
		
		if (crate == null) {
			crate = new EnderCrate(p.getUniqueId());
			CrateFiles.saveEnderCrate(crate);
		}

		return crate;
	}

	
	/**
	 * Get crate on specified location. If crate file is not exists, it will be created.
	 * @param loc Location.
	 * @return Crate.
	 */
	private Crate getCrate(Location loc) {
		Crate crate = CrateFiles.loadCrate(loc);
		
		if (crate == null) {
			crate = new Crate(loc);
			CrateFiles.saveCrate(crate);
		}

		return crate;
	}

	public static Crates getInstance() {
		return instance;
	}
}
