package game.boosting;

import game.boosting.BValue.BValueSimple;

public abstract class BoosterSimple extends Booster implements BValueSimple{
	
	public BoosterSimple(BSourceInfo info, boolean isMul){
		super(info, isMul);
	}
	
	
}