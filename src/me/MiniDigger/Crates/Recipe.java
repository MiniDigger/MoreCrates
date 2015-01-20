package me.MiniDigger.Crates;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

public class Recipe {

	private String root;

	private String lineOne;
	private String lineTwo;
	private String lineThree;

	private char charOne;
	private char charTwo;
	private char charThree;
	private char charFour;
	private char charFive;
	private char charSix;
	private char charSeven;
	private char charEight;
	private char charNine;

	private Material materialOne;
	private Material materialTwo;
	private Material materialThree;
	private Material materialFour;
	private Material materialFive;
	private Material materialSix;
	private Material materialSeven;
	private Material materialEight;
	private Material materialNine;

	public void load() {
		lineOne = Crates.getInstance().getConfig().getString(root + ".lineOne");
		lineTwo = Crates.getInstance().getConfig().getString(root + ".lineTwo");
		lineThree = Crates.getInstance().getConfig()
				.getString(root + ".lineThree");

		charOne = lineOne.charAt(0);
		charTwo = lineOne.charAt(1);
		charThree = lineOne.charAt(2);

		charFour = lineTwo.charAt(0);
		charFive = lineTwo.charAt(1);
		charSix = lineTwo.charAt(2);

		charSeven = lineThree.charAt(0);
		charEight = lineThree.charAt(1);
		charNine = lineThree.charAt(2);

		materialOne = Material.valueOf(Crates.getInstance().getConfig()
				.getString(root + "." + charOne));
		materialTwo = Material.valueOf(Crates.getInstance().getConfig()
				.getString(root + "." + charTwo));
		materialThree = Material.valueOf(Crates.getInstance().getConfig()
				.getString(root + "." + charThree));
		materialFour = Material.valueOf(Crates.getInstance().getConfig()
				.getString(root + "." + charFour));
		materialFive = Material.valueOf(Crates.getInstance().getConfig()
				.getString(root + "." + charFive));
		materialSix = Material.valueOf(Crates.getInstance().getConfig()
				.getString(root + "." + charSix));
		materialSeven = Material.valueOf(Crates.getInstance().getConfig()
				.getString(root + "." + charSeven));
		materialEight = Material.valueOf(Crates.getInstance().getConfig()
				.getString(root + "." + charEight));
		materialNine = Material.valueOf(Crates.getInstance().getConfig()
				.getString(root + "." + charNine));
	}

	public void registerRecipe() {
		ItemStack is = Crates.getInstance().crate;
		if (root.contains("ender")) {
			is = Crates.getInstance().endercrate;
		}
		ShapedRecipe cR = new ShapedRecipe(is);
		cR.shape(lineOne, lineTwo, lineThree);
		cR.setIngredient(charOne, materialOne);
		cR.setIngredient(charTwo, materialTwo);
		cR.setIngredient(charThree, materialThree);
		cR.setIngredient(charFour, materialFour);
		cR.setIngredient(charFive, materialFive);
		cR.setIngredient(charSix, materialSix);
		cR.setIngredient(charSeven, materialSeven);
		cR.setIngredient(charEight, materialEight);
		cR.setIngredient(charNine, materialNine);
		Crates.getInstance().getServer().addRecipe(cR);
	}
}
