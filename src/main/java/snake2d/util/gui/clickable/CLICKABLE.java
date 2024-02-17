package snake2d.util.gui.clickable;

import snake2d.MButt;
import snake2d.SPRITE_RENDERER;
import snake2d.SoundEffect;
import snake2d.util.datatypes.*;
import snake2d.util.gui.GUI_BOX;
import snake2d.util.gui.Hoverable.HOVERABLE;
import snake2d.util.gui.renderable.RENDEROBJ;
import snake2d.util.misc.ACTION;

public abstract interface CLICKABLE extends HOVERABLE {
	
	
	
	public CLICKABLE hoverSoundSet(SoundEffect sound);

	@Override
	public CLICKABLE hoverInfoSet(CharSequence s);

	public CLICKABLE clickSoundSet(SoundEffect sound);

	public CLICKABLE selectedSet(boolean yes);
	
	public CLICKABLE selectTmp();

	public CLICKABLE selectedToggle();

	@Override
	public CLICKABLE visableSet(boolean yes);

	public CLICKABLE clickActionSet(ACTION f);
	
	public boolean click();
	public boolean activeIs();
	public CLICKABLE activeSet(boolean activate);
	public boolean selectedIs();

	@Override
	public CLICKABLE hoverTitleSet(CharSequence s);
	
	public abstract class ClickableAbs implements CLICKABLE {
		public static boolean clickableHovered = false;
		
		private float repTimer = 0;
		private float clicks = 0;
		
		public static SoundEffect defaultHoverSound = null;
		public static SoundEffect defaultClickSound = null;

		public final Rec body = new Rec();

		protected boolean isHovered = false;
		private boolean isVisable = true;
		private boolean isSelected = false;
		private boolean isActive = true;
		private boolean wasHovered = false;
		private boolean tmpSelect = false;
		private boolean repetative;

		private SoundEffect hoverSound = defaultHoverSound;
		private SoundEffect clickSound = defaultClickSound;
		protected CharSequence hoverInfo = null;
		protected CharSequence hoverTitle = null;
		protected ACTION clickAction;

		
		
		protected ClickableAbs() {

		}
		
		protected ClickableAbs(int width, int height) {
			body.setWidth(width).setHeight(height);
		}

		@Override
		public boolean activeIs() {
			return isActive;
		}

		@Override
		public boolean hover(COORDINATE mCoo) {
			if (!isVisable)
				return false;


			if (mCoo.isWithinRec(body())) {
				isHovered = true;
				if (!isActive)
					return true;
				
				if (!wasHovered) {
					repTimer = 0;
					wasHovered = true;
					if (hoverSound != null)
						hoverSound.play(false);
				}

			} else {
				isHovered = false;
				wasHovered = false;
				repTimer = 0;
			}
			if (isRepetative() && isHovered && MButt.LEFT.isDown()) {

				for (int i = 0; i < (int) clicks; i++) {
					if (clickAction != null)
						clickAction.exe();
					clickA();
				}
				clicks -= (int)clicks;
				
//				if (repTimer > 80) {
//					click = 1;
//				}else if(repTimer > 60) {
//					click = (repTimer >>> 1);
//				}else if(repTimer > 40) {
//					click = (repTimer >>> 2);
//				}else if(repTimer > 20) {
//					click = (repTimer >>> 3);
//				}
//				
//				if (click) {
//					
//				}
				
			}
			if (isHovered)
				clickableHovered = true;
			
			return isHovered;
		}

		@Override
		public boolean hoveredIs() {
			return isHovered;
		}

		@Override
		public void hoverInfoGet(GUI_BOX text) {
			if (hoverInfo != null){
				text.text(hoverInfo);
			}
			if (hoverTitle != null)
				text.title(hoverTitle);
		}

		@Override
		public boolean click() {
			if (isVisable && isHovered && isActive) {
				clickA();
				if (clickSound != null)
					clickSound.play(false);
				if (clickAction != null)
					clickAction.exe();
				
				repTimer = 0;
				
				return true;
			}
			return false;
		}
		
		protected void clickA() {
			
		}

		@Override
		public boolean visableIs() {
			return isVisable;
		}

		@Override
		public RECTANGLEE body() {
			return body;
		}

		protected void renAction() {
			
		}
		
		@Override
		public final void render(SPRITE_RENDERER r, float ds) {
			renAction();
			if (isRepetative() && isHovered && MButt.LEFT.isDown()) {
				repTimer += (1+repTimer*2)*ds/15.0;
				if (repTimer > 10000)
					repTimer = 10000;
				clicks += repTimer;

			}else {
				repTimer = 0;
				clicks = 0;
			}
			
			if (isVisable)
				render(r, ds, isActive, isSelected | tmpSelect, isHovered);
			isHovered = false;
			tmpSelect = false;

		}

		protected abstract void render(SPRITE_RENDERER r, float ds,
				boolean isActive, boolean isSelected, boolean isHovered);


		@Override
		public CLICKABLE hoverSoundSet(SoundEffect sound) {
			this.hoverSound = sound;
			return this;

		}

		@Override
		public CLICKABLE hoverInfoSet(CharSequence s) {
			this.hoverInfo = s;
			return this;
		}

		@Override
		public CLICKABLE hoverTitleSet(CharSequence s) {
			this.hoverTitle = s;
			return this;
		}
		
		@Override
		public CLICKABLE clickSoundSet(SoundEffect sound) {
			this.clickSound = sound;
			return this;
		}

		@Override
		public CLICKABLE activeSet(boolean activate) {
			this.isActive = activate;
			return this;
		}

		@Override
		public CLICKABLE selectedSet(boolean yes) {
			this.isSelected = yes;
			return this;
		}
		
		@Override
		public CLICKABLE selectTmp() {
			tmpSelect = true;
			return this;
		}

		@Override
		public ClickableAbs selectedToggle() {
			isSelected ^= true;
			return this;
		}

		@Override
		public CLICKABLE visableSet(boolean yes) {
			this.isVisable = yes;
			return this;
		}

		@Override
		public boolean selectedIs() {
			return isSelected || tmpSelect;
		}

		@Override
		public CLICKABLE clickActionSet(ACTION f) {
			this.clickAction = f;
			return this;
		}
		
		public final boolean isRepetative() {
			return repetative;
		}
		public CLICKABLE repetativeSet(boolean repetative) {
			this.repetative = repetative;
			return this;
		}

	}
	
	

	
	public class ClickSwitch extends ClickWrap {
		
		private RENDEROBJ c;
		
		public ClickSwitch(RENDEROBJ c) {
			super(c);
			this.c = c;
		}
		
		public ClickSwitch(int w, int h) {
			super(w,h);
		}
		
		public void set(RENDEROBJ c) {
			this.c = c;
		}

		//MODDED: why the hell was it as protected?
		@Override
		public RENDEROBJ pget() {
			return c;
		}
		
		public RENDEROBJ current() {
			return c;
		}

	}
	
	public abstract class ClickWrap implements CLICKABLE {

		boolean dirty = false;
		private final Rec body = new Rec() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			@Override
			public Rec moveX1(double X1) {
				
				dirty = true;
				return super.moveX1(X1);
			};
			@Override
			public Rec moveY1(double X1) {
				
				dirty = true;
				return super.moveY1(X1);
			};
		};
		private DIR dd = DIR.NW;
		
		public ClickWrap(DIMENSION dim) {
			body.setDim(dim);
		}
		
		public ClickWrap(int width, int height) {
			body.setDim(width, height);
		}
		
		public ClickWrap(RENDEROBJ obj) {
			body.set(obj.body());
		}
		
		public ClickWrap setD(DIR dd) {
			this.dd = dd;
			return this;
		}
		
		private RENDEROBJ get() {
			RENDEROBJ o = pget();
			if (o == null)
				return null;
			if (dirty) {
				dirty = false;
				int dw = (body.width() - o.body().width())/2;
				int dh = (body.height() - o.body().height())/2;
				o.body().moveC(body.cX(), body.cY());
				o.body().incrX(dw*dd.x());
				o.body().incrY(dh*dd.y());
				this.body.unify(o.body());
				
			}
			return o;
		}
		protected abstract RENDEROBJ pget();
		
		private HOVERABLE hov() {
			if (get() != null && get() instanceof HOVERABLE)
				return (HOVERABLE) get();
			return null;
		}
		
		private CLICKABLE cli() {
			if (get() != null && get() instanceof CLICKABLE)
				return (CLICKABLE) get();
			return null;
		}
		
		@Override
		public boolean activeIs() {
			if (cli() == null)
				return false;
			return cli().activeIs();
		}

		@Override
		public boolean hover(COORDINATE mCoo) {
			dirty = true;
			if (hov() == null)
				return false;
			return hov().hover(mCoo);
		}

		@Override
		public boolean hoveredIs() {
			if (hov() == null)
				return false;
			return hov().hoveredIs();
		}

		@Override
		public void hoverInfoGet(GUI_BOX text) {
			if (hov() != null)
				hov().hoverInfoGet(text);
		}

		@Override
		public boolean click() {
			if (cli() == null)
				return false;
			return cli().click();
		}

		@Override
		public boolean visableIs() {
			if (get() == null)
				return false;
			return get().visableIs();
		}

		@Override
		public RECTANGLEE body() {
			return body;
		}
		
		@Override
		public final void render(SPRITE_RENDERER r, float ds) {
			dirty = true;
			if (get() != null) {
				get().render(r, ds);
			}
			renAction();
			dirty = true;
		}
		
		protected void renAction() {
			
		}

		@Override
		public CLICKABLE hoverSoundSet(SoundEffect sound) {
			if (cli() == null)
				return null;
			return cli().hoverSoundSet(sound);
		}

		@Override
		public CLICKABLE hoverInfoSet(CharSequence s) {
			if (hov() == null)
				return null;
			hov().hoverInfoSet(s);
			return cli();
		}
		
		@Override
		public CLICKABLE hoverTitleSet(CharSequence s) {
			if (hov() == null)
				return null;
			hov().hoverTitleSet(s);
			return cli();
		}

		@Override
		public CLICKABLE clickSoundSet(SoundEffect sound) {
			if (cli() == null)
				return null;
			return cli().clickSoundSet(sound);
		}

		@Override
		public CLICKABLE activeSet(boolean activate) {
			if (cli() == null)
				return null;
			return cli().activeSet(activate);
		}

		@Override
		public CLICKABLE selectedSet(boolean yes) {
			if (cli() == null)
				return null;
			return cli().selectedSet(yes);
		}
		
		@Override
		public CLICKABLE selectTmp() {
			if (cli() == null)
				return null;
			return cli().selectTmp();
		}

		@Override
		public CLICKABLE selectedToggle() {
			if (cli() == null)
				return null;
			return cli().selectedToggle();
		}

		@Override
		public CLICKABLE visableSet(boolean yes) {
			if (cli() == null)
				return null;
			return cli().visableSet(yes);
		}

		@Override
		public boolean selectedIs() {
			if (cli() == null)
				return false;
			return cli().selectedIs();
		}

		@Override
		public CLICKABLE clickActionSet(ACTION f) {
			if (cli() == null)
				return null;
			return cli().clickActionSet(f);
		}

	}
	
	public class Pair extends ClickableAbs {

		private final RENDEROBJ a;
		private final RENDEROBJ b;
		final int offax,offay,offbx,offby;
		private HOVERABLE h;
		
		public Pair(RENDEROBJ a, RENDEROBJ b, DIR align, int margin){
			int dy = (a.body().height()+b.body().height())/2 + margin;
			int dx = (a.body().width()+b.body().width())/2 + margin;
			int sx = a.body().cX();
			int sy = a.body().cY();
			b.body().moveC(sx+dx*align.x(), sy+dy*align.y());
			this.a = a;
			this.b = b;
			body.set(a);
			body.unify(b.body());
			offax = a.body().x1()-body.x1();
			offay = a.body().y1()-body.y1();
			offbx = b.body().x1()-body.x1();
			offby = b.body().y1()-body.y1();
		}
		
		public Pair(RENDEROBJ a, RENDEROBJ b){
			this.a = a;
			this.b = b;
			body.set(a);
			body.unify(b.body());
			offax = a.body().x1()-body.x1();
			offay = a.body().y1()-body.y1();
			offbx = b.body().x1()-body.x1();
			offby = b.body().y1()-body.y1();
		}

		@Override
		public boolean hover(COORDINATE mCoo) {
			if (!visableIs())
				return false;
			h = null;
			if (a instanceof HOVERABLE && ((HOVERABLE) a).hover(mCoo)) {
				h = (HOVERABLE) a;
				if (b instanceof HOVERABLE)
					((HOVERABLE) b).hover(mCoo);
			}else if (b instanceof HOVERABLE && ((HOVERABLE) b).hover(mCoo)) {
				h = (HOVERABLE) b;
				if (b instanceof HOVERABLE)
					((HOVERABLE) b).hover(mCoo);
			}
			isHovered = h != null || mCoo.isWithinRec(body);
			return isHovered;
		}
		
		@Override
		protected void render(SPRITE_RENDERER r, float ds, boolean isActive, boolean isSelected, boolean isHovered) {
			a.body().moveX1Y1(body);
			a.body().incrX(offax).incrY(offay);
			b.body().moveX1Y1(body);
			b.body().incrX(offbx).incrY(offby);
			a.render(r, ds);
			b.render(r, ds);
		}
		
		@Override
		public boolean click() {
			if (h != null && h instanceof CLICKABLE) {
				((CLICKABLE) h).click();
				return true;
			}
			return super.click();
		}
		
		@Override
		public void hoverInfoGet(GUI_BOX text) {
			if (h != null && h instanceof HOVERABLE)
				((HOVERABLE) h).hoverInfoGet(text);
			super.hoverInfoGet(text);
		}



	}
	

}
