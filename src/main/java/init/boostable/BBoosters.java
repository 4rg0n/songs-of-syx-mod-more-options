package init.boostable;

import game.GAME;
import init.boostable.BOOST_LOOKUP.BOOSTER_LOOKUP_IMP;
import init.race.Race;
import lombok.Getter;
import settlement.army.Div;
import settlement.entity.humanoid.HCLASS;
import settlement.stats.Induvidual;
import snake2d.util.misc.CLAMP;
import snake2d.util.sets.ArrayList;
import snake2d.util.sets.LIST;
import snake2d.util.sets.LinkedList;

import java.util.HashMap;

public final class BBoosters extends BOOSTER_LOOKUP_IMP{

	private Node[] all = new Node[BOOSTABLES.all().size()];

	@Getter
	private final LinkedList<BOOSTABLE> boosters = new LinkedList<>();
	
	public BBoosters(LIST<BBooster> all) {
		super("stats");

		HashMap<BOOSTABLE, LinkedList<BBooster>> map = new HashMap<>();
		
		for (BBooster b : all) {
			if (!map.containsKey(b.boost.boostable))
				map.put(b.boost.boostable, new LinkedList<>());
			map.get(b.boost.boostable).add(b);
			boosters.add(b.boost.boostable);
		}

		for (BOOSTABLE b : map.keySet()) {
			this.all[b.index] = new Node(map.get(b));
		}
		
		for (int i = 0; i < this.all.length; i++) {
			if (this.all[i] == null)
				this.all[i] = new Node(new LinkedList<>());
		}
		for (Node n : this.all) {
			for (BBooster b :n.all) {
				init(b.boost, 1);
			}
		}
	}
	
	private static class Node {
		
		private int cacheI = -60;
		private double cacheMul = 1;
		private double cacheAdd = 0;
		private final LIST<BBooster> muls;
		private final LIST<BBooster> adds;
		private final LIST<BBooster> all;
		
		Node(LIST<BBooster> all){
			LinkedList<BBooster> muls = new LinkedList<>();
			LinkedList<BBooster> adds = new LinkedList<>();
			LinkedList<BBooster> a = new LinkedList<>();
			
			for (BBooster b : all) {
				if (b.boost.isMul()) {
					a.add(b);
				}
				if (!b.canCache) {
					if (b.boost.isMul()) {
						muls.add(b);
					}else
						adds.add(b);
				}
			}
			
			for (BBooster b : all) {
				if (!b.boost.isMul()) {
					a.add(b);
				}
				if (b.canCache) {
					if (b.boost.isMul()) {
						muls.add(b);
					}else
						adds.add(b);
				}
			}
			
			this.muls = new ArrayList<BBooster>(muls);
			this.adds = new ArrayList<BBooster>(adds);
			this.all = new ArrayList<BBooster>(a);
		}
		
	}
	
	public double max(BOOSTABLE b) {
		return maxMul(b)*(b.defAdd+maxAdd(b));
	}
	
	public double min(BOOSTABLE b) {
		return Math.min((b.defAdd+minAdd(b)), minMul(b));
	}

	public LIST<BBooster> all(BOOSTABLE b) {
		return all[b.index].all;
	}
	
	public LIST<BBooster> muls(BOOSTABLE b) {
		return all[b.index].muls;
	}
	
	public LIST<BBooster> adders(BOOSTABLE b) {
		return all[b.index].adds;
	}

	private void cache(BOOSTABLE b) {
		
		
		Node n = all[b.index];
		if (GAME.updateI() - n.cacheI >= 60) {
			double mul = 1;
			double add = 0;
			for (BBooster bb : n.muls) {
				if (bb.canCache) {
					mul *= bb.value(null, null);
				}
			}
			for (BBooster bb : n.adds) {
				if (bb.canCache) {
					add += bb.value(null, null);
				}
			}
			n.cacheI = GAME.updateI();
			n.cacheMul = mul;
			n.cacheAdd = add;
		}
	}
	
	public double get(BOOSTABLE b, Induvidual v) {
		cache(b);
		Node n = all[b.index];
		double mul = n.cacheMul;
		double add = b.defAdd + n.cacheAdd;
		
		
		for (int bi = 0; bi < n.muls.size(); bi++) {
			BBooster bb = n.muls.get(bi);
			if (bb.canCache)
				break;
			mul *= bb.value(v);
		}
		for (BBooster bb : n.adds) {
			if (bb.canCache)
				break;
			add += bb.value(v);
		}
		double d = (add)*mul;
		return CLAMP.d(d, 0, 100000);
	}
	
	public double get(BOOSTABLE b, HCLASS c, Race r) {
		cache(b);
		Node n = all[b.index];
		double mul = n.cacheMul;
		double add = b.defAdd + n.cacheAdd;
		
		for (int bi = 0; bi < n.muls.size(); bi++) {
			BBooster bb = n.muls.get(bi);
			if (bb.canCache)
				break;
			mul *= bb.value(c, r);
		}
		for (int bi = 0; bi < n.adds.size(); bi++) {
			BBooster bb = n.adds.get(bi);
			if (bb.canCache)
				break;
			add += bb.value(c, r);
		}
		double d = (add)*mul;
		return CLAMP.d(d, 0, 100000);
	}

	public double get(BOOSTABLE b, Div v) {
		cache(b);
		Node n = all[b.index];
		double mul = n.cacheMul;
		double add = b.defAdd + n.cacheAdd;
		
		for (int bi = 0; bi < n.muls.size(); bi++) {
			BBooster bb = n.muls.get(bi);
			if (bb.canCache)
				break;
			mul *= bb.value(v);
		}
		for (int bi = 0; bi < n.adds.size(); bi++) {
			BBooster bb = n.adds.get(bi);
			if (bb.canCache)
				break;
			add += bb.value(v);
		}
		double d = (add)*mul;
		return CLAMP.d(d, 0, 100000);
	}
	
	public double mul(BOOSTABLE b, Induvidual v) {
		if (b == null)
			return 1;
		double mul = 1;
		for (BBooster bb : all[b.index].muls) {
			mul *= bb.value(v);
		}
		return mul;
	}
	
	public double mul(BOOSTABLE b, HCLASS c, Race r) {
		if (b == null)
			return 0;
		double mul = 1;
		for (BBooster bb : all[b.index].muls) {
			mul *= bb.value(c, r);
		}
		return mul;
	}

	public double mul(BOOSTABLE b, Div v) {
		if (b == null)
			return 0;
		double mul = 1;
		for (BBooster bb : all[b.index].muls) {
			mul *= bb.value(v);
		}
		return mul;
	}

	
	public double add(BOOSTABLE b, Induvidual v) {
		if (b == null)
			return 0;
		double add = 0;
		for (BBooster bb : all[b.index].adds) {
			add += bb.value(v);
		}
		return add;
	}
	
	public double add(BOOSTABLE b, HCLASS c, Race r) {
		if (b == null)
			return 0;
		double add = 0;
		for (BBooster bb : all[b.index].adds) {
			add += bb.value(c, r);
		}
		return add;
	}

	public double add(BOOSTABLE b, Div v) {
		if (b == null)
			return 0;
		double add = 0;
		for (BBooster bb : all[b.index].adds) {
			add += bb.value(v);
		}
		return add;
	}

}
