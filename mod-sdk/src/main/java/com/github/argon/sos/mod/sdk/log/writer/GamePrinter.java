package com.github.argon.sos.mod.sdk.log.writer;

import snake2d.Printer;

import static com.github.argon.sos.mod.sdk.util.StringUtil.stringifyValues;

public class GamePrinter extends AbstractLogWriter {

    public GamePrinter(String prefix, String messageFormat, String name) {
        super(prefix, messageFormat, name);
    }

    @Override
    public void error(String msgPrefix, String formatMsg, Object[] args) {
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

    @Override
    public void log(String msgPrefix, String formatMsg, Object[] args) {
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
}
