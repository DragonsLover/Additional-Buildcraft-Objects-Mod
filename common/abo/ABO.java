/** 
 * Copyright (C) 2011-2013 Flow86
 * 
 * AdditionalBuildcraftObjects is open-source.
 *
 * It is distributed under the terms of my Open Source License. 
 * It grants rights to read, modify, compile or run the code. 
 * It does *NOT* grant the right to redistribute this software or its 
 * modifications in any form, binary or source, except if expressively
 * granted by the copyright holder.
 */

package abo;

import java.io.File;
import java.util.LinkedList;
import java.util.logging.Logger;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Property;
import abo.actions.ActionSwitchOnPipe;
import abo.actions.ActionToggleOffPipe;
import abo.actions.ActionToggleOnPipe;
import abo.items.ABOItem;
import abo.items.ItemGateSettingsDuplicator;
import abo.pipes.items.PipeItemsBounce;
import abo.pipes.items.PipeItemsCrossover;
import abo.pipes.items.PipeItemsExtraction;
import abo.pipes.items.PipeItemsInsertion;
import abo.pipes.items.PipeItemsRoundRobin;
import abo.pipes.items.PipeItemsStripes;
import abo.pipes.liquids.PipeLiquidsBalance;
import abo.pipes.liquids.PipeLiquidsDiamond;
import abo.pipes.liquids.PipeLiquidsGoldenIron;
import abo.pipes.liquids.PipeLiquidsValve;
import abo.pipes.power.PipePowerDiamond;
import abo.pipes.power.PipePowerIron;
import abo.pipes.power.PipePowerSwitch;
import abo.proxy.ABOProxy;
import abo.triggers.ABOTriggerProvider;
import abo.triggers.TriggerEngineSafe;
import buildcraft.BuildCraftCore;
import buildcraft.BuildCraftEnergy;
import buildcraft.BuildCraftTransport;
import buildcraft.api.core.IIconProvider;
import buildcraft.api.gates.ActionManager;
import buildcraft.api.gates.IAction;
import buildcraft.api.gates.ITrigger;
import buildcraft.core.utils.Localization;
import buildcraft.transport.BlockGenericPipe;
import buildcraft.transport.ItemPipe;
import buildcraft.transport.Pipe;
import buildcraft.transport.blueprints.BptItemPipeDiamond;
import buildcraft.transport.blueprints.BptItemPipeIron;
import buildcraft.transport.blueprints.BptItemPipeWooden;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

/**
 * @author Flow86
 * 
 */
@Mod(modid = "Additional-Buildcraft-Objects", name = "Additional-Buildcraft-Objects", version = "@ABO_VERSION@", dependencies = "required-after:BuildCraft|Transport;required-after:BuildCraft|Energy")
public class ABO {
	public static final String VERSION = "@ABO_VERSION@";

	public IIconProvider itemIconProvider = new ItemIconProvider();
	public IIconProvider pipeIconProvider = new PipeIconProvider();

	public static ABOConfiguration aboConfiguration;
	public static Logger aboLog = Logger.getLogger("Additional-Buildcraft-Objects");

	public static int itemGateSettingsDuplicatorID = 10100;
	public static Item itemGateSettingsDuplicator = null;

	public static int pipeLiquidsValveID = 10200;
	public static Item pipeLiquidsValve = null;

	public static int pipeLiquidsGoldenIronID = 10201;
	public static Item pipeLiquidsGoldenIron = null;

	public static int pipeLiquidsBalanceID = 10203;
	public static Item pipeLiquidsBalance = null;

	public static int pipeLiquidsDiamondID = 10204;
	public static Item pipeLiquidsDiamond = null;

	public static int pipeItemsRoundRobinID = 10300;
	public static Item pipeItemsRoundRobin = null;

	// public static int pipeItemsCompactorID = 10301;
	// public static Item pipeItemsCompactor = null;

	public static int pipeItemsInsertionID = 10302;
	public static Item pipeItemsInsertion = null;

	public static int pipeItemsExtractionID = 10303;
	public static Item pipeItemsExtraction = null;

	public static int pipeItemsBounceID = 10304;
	public static Item pipeItemsBounce = null;

	public static int pipeItemsCrossoverID = 10305;
	public static Item pipeItemsCrossover = null;

	public static int pipeItemsStripesID = 4071;
	public static Item pipeItemsStripes = null;

	public static int pipePowerSwitchID = 10400;
	public static Item pipePowerSwitch = null;

	public static int pipePowerIronID = 10401;
	public static Item pipePowerIron = null;

	public static int pipePowerDiamondID = 10402;
	public static Item pipePowerDiamond = null;

	public static int triggerEngineSafeID = 128;
	public static ITrigger triggerEngineSafe = null;

	public static int actionSwitchOnPipeID = 128;
	public static IAction actionSwitchOnPipe = null;

	public static int actionToggleOnPipeID = 129;
	public static IAction actionToggleOnPipe = null;

	public static int actionToggleOffPipeID = 130;
	public static IAction actionToggleOffPipe = null;

	@Instance("Additional-Buildcraft-Objects")
	public static ABO instance;

	@PreInit
	public void preInitialize(FMLPreInitializationEvent evt) {

		aboLog.setParent(FMLLog.getLogger());
		aboLog.info("Starting Additional-Buildcraft-Objects #@BUILD_NUMBER@ " + VERSION
				+ " (Built for Minecraft @MINECRAFT_VERSION@ with Buildcraft @BUILDCRAFT_VERSION@ and Forge @FORGE_VERSION@");
		aboLog.info("Copyright (c) Flow86, 2011-2013");

		aboConfiguration = new ABOConfiguration(new File(evt.getModConfigurationDirectory(), "abo/main.conf"));
		try {
			aboConfiguration.load();

			itemGateSettingsDuplicator = createItem(itemGateSettingsDuplicatorID, ItemGateSettingsDuplicator.class, "Gate Settings Duplicator",
					BuildCraftCore.wrenchItem, BuildCraftTransport.pipeGateAutarchic, null);

			pipeLiquidsValve = createPipe(pipeLiquidsValveID, PipeLiquidsValve.class, "Valve Pipe", 1, BuildCraftTransport.pipeLiquidsWood,
					BuildCraftTransport.pipeGateAutarchic, null);

			pipeLiquidsGoldenIron = createPipe(pipeLiquidsGoldenIronID, PipeLiquidsGoldenIron.class, "Golden Iron Waterproof Pipe", 1,
					BuildCraftTransport.pipeLiquidsGold, BuildCraftTransport.pipeLiquidsIron, null);

			pipeLiquidsBalance = createPipe(pipeLiquidsBalanceID, PipeLiquidsBalance.class, "Balancing Waterproof Pipe", 1,
					BuildCraftTransport.pipeLiquidsWood, new ItemStack(BuildCraftEnergy.engineBlock, 1, 0), BuildCraftTransport.pipeLiquidsWood);

			pipeLiquidsDiamond = createPipe(pipeLiquidsDiamondID, PipeLiquidsDiamond.class, "Diamond Waterproof Pipe", 1, BuildCraftTransport.pipeItemsDiamond,
					BuildCraftTransport.pipeWaterproof, null);

			pipeItemsRoundRobin = createPipe(pipeItemsRoundRobinID, PipeItemsRoundRobin.class, "RoundRobin Transport Pipe", 1,
					BuildCraftTransport.pipeItemsStone, Block.gravel, null);

			// pipeItemsCompactor = createPipe(pipeItemsCompactorID,
			// PipeItemsCompactor.class, "Compactor Pipe", 1,
			// BuildCraftTransport.pipeItemsStone,
			// Block.pistonBase, null);

			pipeItemsInsertion = createPipe(pipeItemsInsertionID, PipeItemsInsertion.class, "Insertion Pipe", 1, BuildCraftTransport.pipeItemsCobblestone,
					Item.redstone, null);

			pipeItemsExtraction = createPipe(pipeItemsExtractionID, PipeItemsExtraction.class, "Extraction Transport Pipe", 1,
					BuildCraftTransport.pipeItemsWood, Block.planks, null);

			pipeItemsBounce = createPipe(pipeItemsBounceID, PipeItemsBounce.class, "Bounce Transport Pipe", 1, BuildCraftTransport.pipeItemsStone,
					Block.cobblestone, null);

			pipeItemsCrossover = createPipe(pipeItemsCrossoverID, PipeItemsCrossover.class, "Crossover Transport Pipe", 1, BuildCraftTransport.pipeItemsStone,
					BuildCraftTransport.pipeItemsIron, null);

			pipeItemsStripes = createPipe(pipeItemsStripesID, PipeItemsStripes.class, "Stripes Transport Pipe", 8, new ItemStack(Item.dyePowder, 1, 0),
					Block.glass, new ItemStack(Item.dyePowder, 1, 11));

			pipePowerSwitch = createPipe(pipePowerSwitchID, PipePowerSwitch.class, "Power Switch Pipe", 1, BuildCraftTransport.pipePowerGold, Block.lever, null);

			pipePowerIron = createPipe(pipePowerIronID, PipePowerIron.class, "Iron Power Pipe", 1, Item.redstone, BuildCraftTransport.pipeItemsIron, null);

			pipePowerDiamond = createPipe(pipePowerDiamondID, PipePowerDiamond.class, "Diamond Power Pipe", 1, Item.redstone,
					BuildCraftTransport.pipeItemsDiamond, null);

			triggerEngineSafe = new TriggerEngineSafe(triggerEngineSafeID);
			actionSwitchOnPipe = new ActionSwitchOnPipe(actionSwitchOnPipeID);
			actionToggleOnPipe = new ActionToggleOnPipe(actionToggleOnPipeID);
			actionToggleOffPipe = new ActionToggleOffPipe(actionToggleOffPipeID);

			ActionManager.registerTriggerProvider(new ABOTriggerProvider());

			BuildCraftCore.itemBptProps[pipeItemsExtraction.itemID] = new BptItemPipeWooden();
			BuildCraftCore.itemBptProps[pipeLiquidsValve.itemID] = new BptItemPipeIron();
			BuildCraftCore.itemBptProps[pipeLiquidsGoldenIron.itemID] = new BptItemPipeIron();
			BuildCraftCore.itemBptProps[pipeLiquidsDiamond.itemID] = new BptItemPipeDiamond();
			BuildCraftCore.itemBptProps[pipePowerIron.itemID] = new BptItemPipeIron();
		} finally {
			aboConfiguration.save();
		}
	}

	@Init
	public void load(FMLInitializationEvent evt) {

		Localization.addLocalization("/lang/abo/", "en_US");

		// fix problem with autarchic gate initialization sequence
		ABORecipe recipe = new ABORecipe();

		recipe.itemID = pipeLiquidsValve.itemID;
		recipe.isShapeless = true;
		recipe.result = new ItemStack(pipeLiquidsValve, 1);
		recipe.input = new Object[] { BuildCraftTransport.pipeLiquidsWood, BuildCraftTransport.pipeGateAutarchic };

		aboRecipes.add(recipe);

		loadRecipes();
	}

	private static class ABORecipe {
		int itemID;
		boolean isPipe = false;
		boolean isShapeless = false;
		ItemStack result;
		Object[] input;
	}

	private static LinkedList<ABORecipe> aboRecipes = new LinkedList<ABORecipe>();

	private static Item createItem(int defaultID, Class<? extends ABOItem> clazz, String descr, Object ingredient1, Object ingredient2, Object ingredient3) {
		String name = Character.toLowerCase(clazz.getSimpleName().charAt(0)) + clazz.getSimpleName().substring(1);

		Property prop = aboConfiguration.getItem(name + ".id", defaultID);

		int id = prop.getInt(defaultID);

		Item item = null;
		try {
			item = clazz.getConstructor(int.class).newInstance(id);
		} catch (Throwable t) {
			t.printStackTrace();
		}

		if (item == null)
			return item;

		item.setUnlocalizedName(clazz.getSimpleName());
		LanguageRegistry.addName(item, descr);

		addReceipe(item, 1, ingredient1, ingredient2, ingredient3);

		return item;
	}

	private static void addReceipe(Item item, int count, Object ingredient1, Object ingredient2, Object ingredient3) {
		// Add appropriate recipe to temporary list
		ABORecipe recipe = new ABORecipe();

		recipe.isPipe = (item instanceof ItemPipe);
		recipe.itemID = item.itemID;

		if (ingredient1 != null && ingredient2 != null && ingredient3 != null) {
			recipe.result = new ItemStack(item, count);
			recipe.input = new Object[] { "ABC", Character.valueOf('A'), ingredient1, Character.valueOf('B'), ingredient2, Character.valueOf('C'), ingredient3 };

			aboRecipes.add(recipe);
		} else if (ingredient1 != null && ingredient2 != null) {
			recipe.isShapeless = true;
			recipe.result = new ItemStack(item, count);
			recipe.input = new Object[] { ingredient1, ingredient2 };

			aboRecipes.add(recipe);
		}

	}

	private static Item createPipe(int defaultID, Class<? extends Pipe> clazz, String descr, int count, Object ingredient1, Object ingredient2,
			Object ingredient3) {
		String name = Character.toLowerCase(clazz.getSimpleName().charAt(0)) + clazz.getSimpleName().substring(1);

		Property prop = aboConfiguration.getItem(name + ".id", defaultID);

		int id = prop.getInt(defaultID);
		ItemPipe pipe = BlockGenericPipe.registerPipe(id, clazz);
		pipe.setPipesIcons(instance.pipeIconProvider);
		pipe.setUnlocalizedName(clazz.getSimpleName());
		LanguageRegistry.addName(pipe, descr);

		addReceipe(pipe, count, ingredient1, ingredient2, ingredient3);

		return pipe;
	}

	public void loadRecipes() {
		// Add pipe recipes
		for (ABORecipe recipe : aboRecipes) {
			if (recipe.isShapeless) {
				GameRegistry.addShapelessRecipe(recipe.result, recipe.input);
			} else {
				GameRegistry.addRecipe(recipe.result, recipe.input);
			}

			if (recipe.isPipe)
				ABOProxy.proxy.registerPipe(recipe.itemID);
		}
	}
}