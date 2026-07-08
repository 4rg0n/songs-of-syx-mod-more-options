package com.github.argon.sos.mod.sdk.log.writer;

import com.github.argon.sos.mod.sdk.util.StringUtil;
import snake2d.Printer;

import static com.github.argon.sos.mod.sdk.util.StringUtil.stringifyValues;

/**
 * Writes to the game {@link Printer#ln(Object)} or {@link Printer#err(Object)}
 */
public class GamePrinterWriter extends AbstractLogWriter {

    /**
     * Creates a new {@link GamePrinterWriter} with given prefix and message format.
     *
     * @param prefix to use for each message
     * @param messageFormat to print the message
     */
    public GamePrinterWriter(String prefix, String messageFormat) {
        super(prefix, messageFormat);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void error(String name, String msgPrefix, String formatMsg, Object[] args) {
        try {
            Printer.err(String.format(messageFormat,
                prefix,
                timestamp(),
                name,
                msgPrefix,
                String.format(formatMsg, stringifyValues(args))));
        } catch (Exception e) {
            problemLogging(formatMsg, args, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void log(String name, String msgPrefix, String formatMsg, Object[] args) {
        try {
            Printer.ln(String.format(messageFormat,
                prefix,
                timestamp(),
                name,
                msgPrefix,
                String.format(formatMsg, stringifyValues(args))));
        } catch (Exception e) {
            problemLogging(formatMsg, args, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void exception(Throwable ex) {
        String exceptionString = StringUtil.stringify(ex);
        Printer.err(exceptionString);
    }
}
