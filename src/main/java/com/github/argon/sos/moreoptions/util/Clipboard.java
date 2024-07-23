package com.github.argon.sos.moreoptions.util;

import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.util.Optional;

/**
 * For reading and writing to the OS clipboard
 */
public class Clipboard {

    private final static Logger log = Loggers.getLogger(Clipboard.class);

    public static boolean write(String string) {
        try {
            java.awt.datatransfer.Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            StringSelection stringSelection = new StringSelection(string);

            clipboard.setContents(stringSelection, stringSelection);
        } catch (Exception e) {
            log.warn("Could not write to system clipboard: %s", e.getMessage());
            log.trace("", e);
            return false;
        }
        return true;
    }

    public static Optional<String> read() {
        java.awt.datatransfer.Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable transfer = clipboard.getContents( null );

        if (!transfer.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            return Optional.empty();
        }

        try {
            String text = (String) transfer.getTransferData(DataFlavor.stringFlavor);
            return Optional.of(text);
        } catch (Exception e) {
            log.info("Could not read from system clipboard: %s", e.getMessage());
            log.trace("", e);
            return Optional.empty();
        }
    }
}
