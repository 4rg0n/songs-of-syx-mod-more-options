package game.boosting;

import snake2d.util.sets.ArrayListGrower;

public abstract class BTYPE<T extends BOOSTABLE_O> {

	public final int bit;
	public final Class<? extends BOOSTABLE_O> cl;
	public final CharSequence name;
	BTYPE(ArrayListGrower<BTYPE<?>> all, Class<? extends BOOSTABLE_O> cl, CharSequence name) {
		this.cl = cl;
		int i = all.add(this);
		bit = 1 << i; 
		this.name = name;
	}
	
	public abstract double value(T t, Boostable b, BValue v);
	

}
