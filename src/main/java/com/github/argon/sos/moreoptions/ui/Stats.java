package com.github.argon.sos.moreoptions.ui;

import com.github.argon.sos.moreoptions.game.ui.ColorBox;
import init.sprite.SPRITES;
import init.sprite.UI.UI;
import snake2d.CoreStats;
import snake2d.SPRITE_RENDERER;
import snake2d.util.color.COLOR;
import snake2d.util.sprite.SPRITE;
import util.gui.misc.GText;
import util.info.GFORMAT;


public class Stats extends ColorBox {
    private final GText text = new GText(UI.FONT().S, 16);

    public Stats() {
        super(150, 46, COLOR.WHITE20);
    }

    @Override
    public void render(SPRITE_RENDERER r, float ds) {
        super.render(r, ds);

        text.clear();
        GFORMAT.i(text, (int) CoreStats.updatePercentage.ave);
        render(r, SPRITES.icons().s.clock, 0);

        text.clear();
        GFORMAT.i(text, (int) CoreStats.renderPercentage.ave);
        render(r, SPRITES.icons().s.clock, 1);

        text.clear();
        GFORMAT.i(text, (int) CoreStats.FPS.ave);
        render(r, SPRITES.icons().s.camera, 2);

        text.clear();
        GFORMAT.i(text, (int) CoreStats.usedHeap.ave);
        render(r, SPRITES.icons().s.money, 3);
    }

    private void render(SPRITE_RENDERER r, SPRITE s, int i) {
        int y1 = body().y1()+ 5 + 18*(i /2);
        int x1 = body().x1()+ 5 + 80*(i%2);

        s.render(r, x1, y1);
        text.render(r, x1 + 18, y1);
    }
}
