package com.github.argon.sos.mod.sdk.util;

import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;
import org.jetbrains.annotations.Nullable;

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

    /**
     * Writes a given text into the clipboard
     */
    public static void write(@Nullable String string) {
        if (string == null) {
            return;
        }

        java.awt.datatransfer.Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection stringSelection = new StringSelection(string);

        clipboard.setContents(stringSelection, stringSelection);
    }

    /**
     * Reads everything from the clipboard
     *
     * @return empty in case of an error
     */
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
