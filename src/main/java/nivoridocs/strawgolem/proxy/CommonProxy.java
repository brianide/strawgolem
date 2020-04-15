package nivoridocs.strawgolem.proxy;

import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockPumpkin;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.init.Blocks;
import net.minecraft.init.Bootstrap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import nivoridocs.strawgolem.Strawgolem;
import nivoridocs.strawgolem.entity.EntityStrawGolem;
import nivoridocs.strawgolem.entity.capability.lifespan.ILifespan;
import nivoridocs.strawgolem.entity.capability.lifespan.Lifespan;
import nivoridocs.strawgolem.entity.capability.lifespan.LifespanStorage;

@Mod.EventBusSubscriber
public class CommonProxy {

	public void preInit(FMLPreInitializationEvent event) {
		ResourceLocation registryName = new ResourceLocation(Strawgolem.MODID, "strawgolem");
		EntityRegistry.registerModEntity(registryName, EntityStrawGolem.class,
				"strawgolem", 1, Strawgolem.instance, 64, 3, false, 0xccb211, 0xa05a0b);
		
		CapabilityManager.INSTANCE.register(ILifespan.class, new LifespanStorage(), Lifespan::new);
		
		LootTableList.register(EntityStrawGolem.LOOT);

		// Wrap vanilla Pumpkin-dispensing behavior
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(Item.getItemFromBlock(Blocks.PUMPKIN), new BehaviorDefaultDispenseItem() {
			IBehaviorDispenseItem baseBehavior = BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.getObject(Item.getItemFromBlock(Blocks.PUMPKIN));
			boolean forwarded = false;

			@Override
			protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
				World world = source.getWorld();
				BlockPos blockpos = source.getBlockPos().offset((EnumFacing)source.getBlockState().getValue(BlockDispenser.FACING));
				BlockPumpkin blockpumpkin = (BlockPumpkin)Blocks.PUMPKIN;

				// If there's a hay block underneath the target location, allow the placement
				if (world.isAirBlock(blockpos) && world.getBlockState(blockpos.down()).getBlock() == Blocks.HAY_BLOCK) {
					if (!world.isRemote)
						world.setBlockState(blockpos, blockpumpkin.getDefaultState(), 3);

					stack.shrink(1);
					return stack;
				} else {
					// Fall back to vanilla handling
					forwarded = true;
					return baseBehavior.dispense(source, stack);
				}
			}

			@Override
			protected void playDispenseSound(IBlockSource source) {
				if(!forwarded)
					super.playDispenseSound(source);
			}

			@Override
			protected void spawnDispenseParticles(IBlockSource source, EnumFacing facingIn) {
				if(!forwarded)
					super.spawnDispenseParticles(source, facingIn);
			}
		});
	}

	public void init(FMLInitializationEvent event) {
		//
	}

	public void postInit(FMLPostInitializationEvent event) {
		//	
	}
	
}
