package com.github.argon.sos.moreoptions.ui;

import com.github.argon.sos.mod.sdk.game.ui.ColorBox;
import com.github.argon.sos.mod.sdk.i18n.I18nTranslator;
import com.github.argon.sos.moreoptions.ModModule;
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

    private static final I18nTranslator i18n = ModModule.i18n().get(StatsUi.class);

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
        b.textLL(i18n.t("StatsUi.fps.name"));
        b.tab(6);
        b.add(GFORMAT.i(b.text(), (int) CoreStats.FPS.ave).selectify());
        b.NL(4);
        b.NL(4);

        b.add(SPRITES.icons().s.clock);
        b.textLL(i18n.t("StatsUi.time.update.name"));
        b.tab(6);
        b.add(GFORMAT.percBig(b.text(), CoreStats.updatePercentage.ave / 100).clickify());
        b.NL(4);

        b.add(SPRITES.icons().s.clock);
        b.textLL(i18n.t("StatsUi.time.render.name"));
        b.tab(6);
        b.add(GFORMAT.percBig(b.text(), CoreStats.renderPercentage.ave / 100).clickify());
        b.NL(4);

        b.add(UI.icons().s.clock);
        b.textLL(i18n.t("StatsUi.time.total.name"));
        b.tab(6);
        b.add(GFORMAT.percBig(b.text(), CoreStats.totalPercentage.ave / 100).clickify());
        b.NL(4);

        b.add(UI.icons().s.clock);
        b.textLL(i18n.t("StatsUi.updates.small.name"));
        b.tab(6);
        b.add(GFORMAT.f(b.text(), CoreStats.smallUpdates.ave, 9).clickify());
        b.NL(4);

        b.add(UI.icons().s.clock);
        b.textLL(i18n.t("StatsUi.updates.dropped.name"));
        b.tab(6);
        b.add(GFORMAT.f(b.text(), CoreStats.droppedTicks.ave, 9).clickify());
        b.NL(4);
        b.NL(4);

        b.add(UI.icons().s.money);
        b.textLL(i18n.t("StatsUi.heap.name"));
        b.tab(6);
        b.add(GFORMAT.i(b.text(), (int) CoreStats.heap.ave).lablify());
        b.NL(4);

        b.add(UI.icons().s.money);
        b.textLL(i18n.t("StatsUi.heap.used.name"));
        b.tab(6);
        b.add(GFORMAT.i(b.text(), (int) CoreStats.usedHeap.ave).lablify());
        b.NL(4);

        b.add(UI.icons().s.money);
        b.textLL(i18n.t("StatsUi.heap.growth.name"));
        b.tab(6);
        b.add(GFORMAT.i(b.text(), (int) CoreStats.heapGrowth.ave).lablify());
        b.NL(4);

        b.add(UI.icons().s.money);
        b.textLL(i18n.t("StatsUi.swap.name"));
        b.tab(6);
        b.add(GFORMAT.percBig(b.text(), CoreStats.swapPercentage.ave / 100).lablify());
        b.NL(4);
        b.NL(4);

        b.add(UI.icons().s.cog);
        b.textLL(i18n.t("StatsUi.core.total.name"));
        b.tab(6);
        b.add(GFORMAT.percBig(b.text(), CoreStats.coreTotal.ave / 100).lablifySub());
        b.NL(4);

        b.add(UI.icons().s.cog);
        b.textLL(i18n.t("StatsUi.core.poll.name"));
        b.tab(6);
        b.add(GFORMAT.percBig(b.text(), CoreStats.corePoll.ave / 100).lablifySub());
        b.NL(4);

        b.add(UI.icons().s.cog);
        b.textLL(i18n.t("StatsUi.core.flush.name"));
        b.tab(6);
        b.add(GFORMAT.percBig(b.text(), CoreStats.coreFlush.ave / 100).lablifySub());
        b.NL(4);

        b.add(UI.icons().s.cog);
        b.textLL(i18n.t("StatsUi.core.sound.name"));
        b.tab(6);
        b.add(GFORMAT.percBig(b.text(), CoreStats.coreSound.ave / 100).lablifySub());
        b.NL(4);

        b.add(UI.icons().s.cog);
        b.textLL(i18n.t("StatsUi.core.sleep.name"));
        b.tab(6);
        b.add(GFORMAT.percBig(b.text(), CoreStats.coreSleep.ave / 100).lablifySub());
        b.NL(4);
        b.NL(4);

        b.add(UI.icons().s.star);
        b.textLL(i18n.t("StatsUi.sprites.name"));
        b.tab(6);
        b.add(GFORMAT.i(b.text(), CORE.renderer().getSpritesSprocessed()).warnify());
        b.NL(4);

        b.add(UI.icons().s.star);
        b.textLL(i18n.t("StatsUi.shadows.name"));
        b.tab(6);
        b.add(GFORMAT.i(b.text(), CORE.renderer().getShadowsRendered()).warnify());
        b.NL(4);

        b.add(UI.icons().s.star);
        b.textLL(i18n.t("StatsUi.lights.name"));
        b.tab(6);
        b.add(GFORMAT.i(b.text(), CORE.renderer().getLightsProcessed()).warnify());
        b.NL(4);

        b.add(UI.icons().s.star);
        b.textLL(i18n.t("StatsUi.particles.name"));
        b.tab(6);
        b.add(GFORMAT.i(b.text(), CORE.renderer().getParticlesProcessed()).warnify());
        b.NL(4);

        super.hoverInfoGet(text);
    }
}
