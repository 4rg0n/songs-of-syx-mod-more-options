package game.boosting;

import game.boosting.BValue.BValueSimple;
import game.faction.Faction;

public abstract class BoosterImp extends Booster implements BValueSimple{

	private final double from;
	private final double to;

	public BoosterImp(BSourceInfo info, double from, double to, boolean isMul) {
		super(info, isMul);
		this.from = from;
		this.to = to;
	}
	
	public BoosterImp(BSourceInfo info, double value, boolean isMul) {
		super(info, isMul);
		to = value;
		
		if (isMul) {
			from = 1;
		}else {
			from = 0;
		}
	}
	
	@Override
	public double from() {
		return from;
	}
	
	@Override
	public double to() {
		return to;
	}
	


	public static class DUMMY extends BoosterImp {

		public DUMMY(BSourceInfo info, double value, boolean isMul) {
			super(info, value, isMul);
		}

		@Override
		public double vGet(Faction f) {
			return 1.0;
		}
		
	}


}