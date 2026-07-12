package com.github.argon.sos.mod.sdk.log.writer;

import com.github.argon.sos.mod.sdk.util.StringUtil;

import static com.github.argon.sos.mod.sdk.util.StringUtil.stringifyValues;

/**
 * Writes to {@link System#out} or {@link System#err}
 */
public class StdOutWriter extends AbstractLogWriter {

    /**
     * Creates a new {@link StdOutWriter} with given prefix and message format.
     *
     * @param prefix to use for each message
     * @param messageFormat to print the message
     */
    public StdOutWriter(String prefix, String messageFormat) {
        super(prefix, messageFormat);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void error(String name, String msgPrefix, String formatMsg, Object[] args) {
        try {
            System.err.printf((messageFormat) + "%n",
                prefix,
                timestamp(),
                name,
                msgPrefix,
                String.format(formatMsg, stringifyValues(args)));
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
            System.out.printf((messageFormat) + "%n",
                prefix,
                timestamp(),
                name,
                msgPrefix,
                String.format(formatMsg, StringUtil.stringifyValues(args)));
        } catch (Exception e) {
            problemLogging(formatMsg, args, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void exception(Throwable ex) {
        System.err.println("\n" + ex.getMessage());
        ex.printStackTrace(System.err);
        System.err.println();
    }
}
