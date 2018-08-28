package nivoridocs.strawgolem.entity;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import nivoridocs.strawgolem.StrawgolemConfig;

public class EntityAIFleeRain extends EntityAIBase {

	private final EntityStrawGolem golem;
	private double shelterX;
	private double shelterY;
	private double shelterZ;
	private final double movementSpeed;
	private final World world;

	public EntityAIFleeRain(EntityStrawGolem golem, double movementSpeedIn) {
		this.golem = golem;
		this.movementSpeed = movementSpeedIn;
		this.world = golem.getEntityWorld();
		this.setMutexBits(1);
	}

	@Override
	public boolean shouldExecute() {
		if (StrawgolemConfig.isEscapeRainEnbled()) {
			BlockPos golemPos = new BlockPos(golem.posX, golem.getEntityBoundingBox().minY, golem.posZ);
			if (world.isRainingAt(golemPos) && world.canSeeSky(golemPos)) {
				Vec3d vec3d = findPossibleShelter();
				if (vec3d != null) {
					shelterX = vec3d.x;
					shelterY = vec3d.y;
					shelterZ = vec3d.z;
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean shouldContinueExecuting() {
		return !golem.getNavigator().noPath();
	}

	@Override
	public void startExecuting() {
		golem.getNavigator().tryMoveToXYZ(this.shelterX, this.shelterY, this.shelterZ, this.movementSpeed);
	}

	@Nullable
	private Vec3d findPossibleShelter() {
		Random random = golem.getRNG();
		BlockPos golemPos = new BlockPos(golem.posX, golem.getEntityBoundingBox().minY, golem.posZ);
		for (int i = 0; i < 10; ++i) {
			BlockPos auxPos = golemPos.add(random.nextInt(20) - 10, random.nextInt(6) - 3, random.nextInt(20) - 10);
			if (!world.canSeeSky(auxPos) && golem.getBlockPathWeight(auxPos) < 0.0F)
				return new Vec3d((double) auxPos.getX(), (double) auxPos.getY(), (double) auxPos.getZ());
		}
		return null;
	}

}
