package com.github.argon.sos.moreoptions.game.ui;

import init.sprite.UI.UI;
import settlement.entity.humanoid.Humanoid;
import settlement.room.service.module.RoomService;
import settlement.stats.colls.StatsNeeds;
import settlement.stats.stat.STAT;
import snake2d.SPRITE_RENDERER;
import snake2d.util.gui.GUI_BOX;
import snake2d.util.gui.clickable.CLICKABLE;
import util.colors.GCOLOR;
import util.gui.misc.GBox;
import util.gui.misc.GButt;
import util.gui.misc.GMeter;
import util.gui.misc.GText;
import util.info.GFORMAT;

public class NeedBox extends CLICKABLE.ClickableAbs {

    private final Humanoid humanoid;
    private final StatsNeeds.StatNeed need;
    private final STAT stat;
    private final GText work = new GText(UI.FONT().S, 32);

    public NeedBox(Humanoid humanoid, StatsNeeds.StatNeed need) {
        super(500, 32);

        this.humanoid = humanoid;
        this.need = need;
        this.stat = need.stat();
    }

    public NeedBox(Humanoid humanoid, STAT stat) {
        super(500, 32);

        this.humanoid = humanoid;
        this.need = null;
        this.stat = stat;
    }

    @Override
    protected void render(SPRITE_RENDERER r, float ds, boolean isActive, boolean isSelected,
                          boolean isHovered) {

        GButt.ButtPanel.renderBG(r, isActive, isSelected, isHovered, body);
        GButt.ButtPanel.renderFrame(r, body);

        int x1 = body().x1()+8;

        if (need != null) {
            if (!humanoid.race().service().services(humanoid.indu(), need.need).isEmpty()) {
                humanoid.race().service().services(humanoid.indu(), need.need).get(0).room().icon.small.renderCY(r, x1, body().cY());
            }else
                need.need.rate.icon.renderCY(r, x1, body().cY());
        }
        x1+= 20;

        work.setFont(UI.FONT().S);
        work.clear();
        work.add(stat.info().name);
        work.lablify();
        work.renderCY(r, x1, body().cY());
        x1+= 280;

        int i = stat.indu().get(humanoid.indu());
        double m = stat.indu().max(humanoid.indu());
        double d = i/m;

        GMeter.GMeterCol c = GMeter.C_GREEN;
        if (need == null || i > need.breakpoint()) {
            c = GMeter.C_RED;
        }
        GMeter.render(r, c,
            d,
            x1, x1+75, body().y1()+8, body().y2()-8);



        if (need != null) {
            int x = (int) (x1 + 75*(need.breakpoint()/m));
            GCOLOR.UI().border().render(r, x, x+1, body().y1()+8, body().y2()-8);
        }

        x1 += 90;

        work.clear();

        if (need != null){
            double def = need.need.rate.baseValue;
            double rate = humanoid.race().boosts.value(need.need.rate);
            GFORMAT.percInc(work, rate);
            if (rate > def)
                work.color(GCOLOR.T().IBAD);
            else if (rate < def)
                work.color(GCOLOR.T().IGREAT);
            else
                work.color(GCOLOR.T().INORMAL);
            work.renderCY(r, x1, body().cY());
        }

    }

    @Override
    public void hoverInfoGet(GUI_BOX text) {
        text.title(stat.info().name);
        text.text(stat.info().desc);
        text.NL(4);
        GBox b = (GBox) text;
        b.textLL("Current Need");
        b.tab(6);
        double d = stat.indu().getD(humanoid.indu());
        b.add(GFORMAT.perc(b.text(), d));

        if (need != null) {
            b.NL();
            b.textLL("Related Services");

            for (RoomService s : humanoid.race().service().services(humanoid.indu(), need.need)) {
                b.add(s.room().icon);
            }
            b.NL();
            b.sep();
            GText t = b.text();
            t.add("Increase per day: {0}% / day").insert(0, (int)(100*need.need.rate.get(humanoid.indu())));
            need.need.rate.hover(b, humanoid.indu(), t, true);
        }
    }
}
