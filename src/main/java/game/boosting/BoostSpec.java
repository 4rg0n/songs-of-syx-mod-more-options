package game.boosting;

public final class BoostSpec {

	public final Booster booster;
	public final Boostable boostable;
	public final CharSequence tName;
//	public final boolean isMul;
	
	BoostSpec(Booster source, Boostable target, CharSequence append) {
		this.booster = source;
		this.boostable = target;
		CharSequence tName = target.name;
		if (append != null)
			tName = tName + " (" + append + ")";
		this.tName = tName;
//		this.isMul = source.isMul;
	}
	
	public final double get(BOOSTABLE_O t) {
		return booster.get(boostable, t);
	}
	
	public final double inc(BOOSTABLE_O t) {
		return booster.get(boostable, t)-(booster.isMul ? 1 : 0);
	}
	
	public boolean isPositive(double input) {
		return (booster.isMul && booster.getValue(input) >= 1) || booster.getValue(input) > 0;
	}	

	public boolean isSameAs(BoostSpec other) {
		if (booster.isMul == booster.isMul && boostable == other.boostable) {
			for (BTYPE<?> t : BOOSTING.TYPES().ALL())
				if (booster.has(t.cl) != other.booster.has(t.cl))
					return false;
			return true;
		}
		return false;
	}
	
	public String identifier() {
		return boostable.key + booster.isMul;
	}

	
	
}
