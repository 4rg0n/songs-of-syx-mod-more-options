package com.github.argon.sos.moreoptions.ui;

import com.github.argon.sos.moreoptions.config.ConfigStore;
import com.github.argon.sos.moreoptions.game.ui.Button;
import com.github.argon.sos.moreoptions.game.ui.HorizontalLine;
import init.sprite.UI.UI;
import lombok.Getter;
import snake2d.util.color.COLOR;
import snake2d.util.gui.GuiSection;
import util.gui.misc.GText;

public class BackupModal extends GuiSection {
    @Getter
    private Button applyButton;
    @Getter
    private Button editButton;
    @Getter
    private Button discardButton;

    public BackupModal() {
        GText fileText = new GText(UI.FONT().M, "Backup file found at: " + ConfigStore.backupConfigPath());
        GText text1 = new GText(UI.FONT().M, "A backup file of your More Options config was loaded. ");
        GText text2 = new GText(UI.FONT().M,"Probably because the game just crashed. What do you want to do with it?");

        addDownC(0, fileText);
        addDownC(20, text1);
        addDownC(5, text2);
        GuiSection buttons = buttons();

        addDownC(10, new HorizontalLine(text2.width(), 14, 1));
        addDownC(10, buttons);
    }
    private GuiSection buttons() {
        GuiSection section = new GuiSection();

        this.applyButton = new Button("Apply", COLOR.WHITE15);
        applyButton.hoverInfoSet("Apply the loaded backup. This could crash the game again.");
        section.addRight(0, applyButton);

        this.editButton = new Button("Edit", COLOR.WHITE15);
        editButton.hoverInfoSet("Edit the loaded backup before applying it.");
        section.addRight(20, editButton);

        this.discardButton = new Button("Discard", COLOR.WHITE15);
        discardButton.hoverInfoSet("Delete the the loaded backup and use default configs.");
        section.addRight(20, discardButton);

        return section;
    }
}
