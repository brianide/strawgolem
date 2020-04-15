package nivoridocs.strawgolem;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import nivoridocs.strawgolem.entity.EntityStrawGolem;

@EventBusSubscriber
public class StrawGolemCreationEventHandler {

	@SubscribeEvent
	public static void onWorldLoad(WorldEvent.Load event) {
		if(!event.getWorld().isRemote)
			event.getWorld().addEventListener(new WorldEventListenerAdapter() {
				@Override
				public void notifyBlockUpdate(World worldIn, BlockPos pos, IBlockState oldState, IBlockState newState, int flags) {
					Block block = newState.getBlock();
					BlockPos pumpkin, hay;

					if(block == Blocks.PUMPKIN) {
						pumpkin = pos;
						hay = pos.down();
					} else if (block == Blocks.HAY_BLOCK) {
						pumpkin = pos.up();
						hay = pos;
					} else return;

					if (checkStructure(worldIn, hay, pumpkin)) {
						pos = hay;
						worldIn.setBlockState(pumpkin, Blocks.AIR.getDefaultState());
						worldIn.setBlockState(hay, Blocks.AIR.getDefaultState());
						EntityStrawGolem strawGolem = new EntityStrawGolem(worldIn);
						strawGolem.setPosition(getCoord(pos.getX()), pos.getY(), getCoord(pos.getZ()));
						worldIn.spawnEntity(strawGolem);
					}
				}
			});
	}
	
	private static double getCoord(int c) {
		return c + Math.signum(c)*0.5D;
	}
	
	private static boolean checkStructure(World worldIn, BlockPos hay, BlockPos pumpkin) {
		return worldIn.getBlockState(hay).getBlock() == Blocks.HAY_BLOCK
				&& worldIn.getBlockState(pumpkin).getBlock() == Blocks.PUMPKIN;
	}
	
	private StrawGolemCreationEventHandler() {
		//
	}
	
}
