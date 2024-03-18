package com.github.argon.sos.moreoptions.ui;

import com.github.argon.sos.moreoptions.game.ui.Button;
import com.github.argon.sos.moreoptions.game.ui.HorizontalLine;
import com.github.argon.sos.moreoptions.i18n.I18n;
import init.sprite.UI.UI;
import lombok.Getter;
import snake2d.util.color.COLOR;
import snake2d.util.gui.GuiSection;
import util.gui.misc.GText;

/**
 * Used when the player decides to edit the loaded backup config
 */
public class BackupDialog extends GuiSection {

    private final static I18n i18n = I18n.get(BackupDialog.class);

    @Getter
    private Button applyButton;
    @Getter
    private Button editButton;
    @Getter
    private Button discardButton;

    public BackupDialog() {
        GText fileText = new GText(UI.FONT().M, i18n.t("BackupDialog.text.backup.found"));
        GText text1 = new GText(UI.FONT().M, i18n.t("BackupDialog.text.line1"));
        GText text2 = new GText(UI.FONT().M,i18n.t("BackupDialog.text.line2"));

        addDownC(0, fileText);
        addDownC(20, text1);
        addDownC(5, text2);
        GuiSection buttons = buttons();

        addDownC(10, new HorizontalLine(text2.width(), 14, 1));
        addDownC(10, buttons);
    }
    private GuiSection buttons() {
        GuiSection section = new GuiSection();

        this.applyButton = new Button("Apply", COLOR.WHITE15,
            "Apply the loaded backup. This could crash the game again.");
        section.addRight(0, applyButton);

        this.editButton = new Button("Edit", COLOR.WHITE15,
            "Edit the loaded backup before applying it.");
        section.addRight(20, editButton);

        this.discardButton = new Button("Discard", COLOR.WHITE15,
            "Delete the the loaded backup and use default configs.");
        section.addRight(20, discardButton);

        return section;
    }
}
