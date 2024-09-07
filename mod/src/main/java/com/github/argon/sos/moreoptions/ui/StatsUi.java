package com.github.argon.sos.moreoptions.ui;

import com.github.argon.sos.mod.sdk.game.ui.ColorBox;
import com.github.argon.sos.mod.sdk.i18n.I18n;
import init.sprite.SPRITES;
import init.sprite.UI.UI;
import lombok.Getter;
import lombok.Setter;
import snake2d.CORE;
import snake2d.CoreStats;
import snake2d.SPRITE_RENDERER;
import snake2d.util.color.COLOR;
import snake2d.util.gui.GUI_BOX;
import snake2d.util.sprite.SPRITE;
import util.gui.misc.GBox;
import util.gui.misc.GText;
import util.info.GFORMAT;



public class StatsUi extends ColorBox {
    @Getter(lazy = true)
    private final static StatsUi instance = new StatsUi();

    // TODO implement me
    private static final I18n i18n = I18n.get(StatsUi.class);

    @Setter
    private boolean visible = true;

    private final GText statsText = new GText(UI.FONT().S, 16);


    private StatsUi() {
        super(130, 46, COLOR.WHITE15);
    }

    @Override
    public void render(SPRITE_RENDERER r, float ds) {
        if (!visible) return;
        super.render(r, ds);

        statsText.clear();
        GFORMAT.i(statsText, (int) CoreStats.updatePercentage.ave);
        render(r, SPRITES.icons().s.clock, 0);

        statsText.clear();
        GFORMAT.i(statsText, (int) CoreStats.renderPercentage.ave);
        render(r, SPRITES.icons().s.clock, 1);

        statsText.clear();
        GFORMAT.i(statsText, (int) CoreStats.FPS.ave);
        render(r, SPRITES.icons().s.camera, 2);

        statsText.clear();
        GFORMAT.i(statsText, (int) CoreStats.usedHeap.ave);
        render(r, SPRITES.icons().s.money, 3);
    }

    private void render(SPRITE_RENDERER r, SPRITE s, int i) {
        int y1 = body().y1()+ 5 + 18*(i /2);
        int x1 = body().x1()+ 5 + 60*(i%2);

        s.render(r, x1, y1);
        statsText.render(r, x1 + 18, y1);
    }

    @Override
    public void hoverInfoGet(GUI_BOX text) {

        GBox b = (GBox) text;

        b.add(UI.icons().s.cameraBig);
        b.textLL("FPS");
        b.tab(6);
        b.add(GFORMAT.i(b.text(), (int) CoreStats.FPS.ave).selectify());
        b.NL(4);
        b.NL(4);

        b.add(SPRITES.icons().s.clock);
        b.textLL("Update Time");
        b.tab(6);
        b.add(GFORMAT.percBig(b.text(), CoreStats.updatePercentage.ave / 100).clickify());
        b.NL(4);

        b.add(SPRITES.icons().s.clock);
        b.textLL("Render Time");
        b.tab(6);
        b.add(GFORMAT.percBig(b.text(), CoreStats.renderPercentage.ave / 100).clickify());
        b.NL(4);

        b.add(UI.icons().s.clock);
        b.textLL("Total Time");
        b.tab(6);
        b.add(GFORMAT.percBig(b.text(), CoreStats.totalPercentage.ave / 100).clickify());
        b.NL(4);

        b.add(UI.icons().s.clock);
        b.textLL("Small Updates");
        b.tab(6);
        b.add(GFORMAT.f(b.text(), CoreStats.smallUpdates.ave, 9).clickify());
        b.NL(4);

        b.add(UI.icons().s.clock);
        b.textLL("Dropped Ticks");
        b.tab(6);
        b.add(GFORMAT.f(b.text(), CoreStats.droppedTicks.ave, 9).clickify());
        b.NL(4);
        b.NL(4);

        b.add(UI.icons().s.money);
        b.textLL("Heap");
        b.tab(6);
        b.add(GFORMAT.i(b.text(), (int) CoreStats.heap.ave).lablify());
        b.NL(4);

        b.add(UI.icons().s.money);
        b.textLL("Used Heap");
        b.tab(6);
        b.add(GFORMAT.i(b.text(), (int) CoreStats.usedHeap.ave).lablify());
        b.NL(4);

        b.add(UI.icons().s.money);
        b.textLL("Heap Growth");
        b.tab(6);
        b.add(GFORMAT.i(b.text(), (int) CoreStats.heapGrowth.ave).lablify());
        b.NL(4);

        b.add(UI.icons().s.money);
        b.textLL("Swap");
        b.tab(6);
        b.add(GFORMAT.percBig(b.text(), CoreStats.swapPercentage.ave / 100).lablify());
        b.NL(4);
        b.NL(4);

        b.add(UI.icons().s.cog);
        b.textLL("Core Total");
        b.tab(6);
        b.add(GFORMAT.percBig(b.text(), CoreStats.coreTotal.ave / 100).lablifySub());
        b.NL(4);

        b.add(UI.icons().s.cog);
        b.textLL("Core Poll");
        b.tab(6);
        b.add(GFORMAT.percBig(b.text(), CoreStats.corePoll.ave / 100).lablifySub());
        b.NL(4);

        b.add(UI.icons().s.cog);
        b.textLL("Core Flush");
        b.tab(6);
        b.add(GFORMAT.percBig(b.text(), CoreStats.coreFlush.ave / 100).lablifySub());
        b.NL(4);

        b.add(UI.icons().s.cog);
        b.textLL("Core Sound");
        b.tab(6);
        b.add(GFORMAT.percBig(b.text(), CoreStats.coreSound.ave / 100).lablifySub());
        b.NL(4);

        b.add(UI.icons().s.cog);
        b.textLL("Core Sleep");
        b.tab(6);
        b.add(GFORMAT.percBig(b.text(), CoreStats.coreSleep.ave / 100).lablifySub());
        b.NL(4);
        b.NL(4);

        b.add(UI.icons().s.star);
        b.textLL("Sprites");
        b.tab(6);
        b.add(GFORMAT.i(b.text(), CORE.renderer().getSpritesSprocessed()).warnify());
        b.NL(4);

        b.add(UI.icons().s.star);
        b.textLL("Shadows");
        b.tab(6);
        b.add(GFORMAT.i(b.text(), CORE.renderer().getShadowsRendered()).warnify());
        b.NL(4);

        b.add(UI.icons().s.star);
        b.textLL("Lights");
        b.tab(6);
        b.add(GFORMAT.i(b.text(), CORE.renderer().getLightsProcessed()).warnify());
        b.NL(4);

        b.add(UI.icons().s.star);
        b.textLL("Particles");
        b.tab(6);
        b.add(GFORMAT.i(b.text(), CORE.renderer().getParticlesProcessed()).warnify());
        b.NL(4);

        super.hoverInfoGet(text);
    }
}
