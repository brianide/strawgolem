package nivoridocs.strawgolem.entity.capability.lifespan;

import nivoridocs.strawgolem.StrawgolemConfig;

public class Lifespan implements ILifespan {
	private int tickLeft;
	
	public Lifespan() {
		this.tickLeft = StrawgolemConfig.getLifespan();
	}

	@Override
	public void update() {
		if (tickLeft > 0)
			tickLeft --;
	}

	@Override
	public boolean isOver() {
		return tickLeft == 0;
	}
	
	@Override
	public int get() {
		return tickLeft;
	}
	
	@Override
	public void set(int tickLeft) {
		this.tickLeft = tickLeft;
	}

}
