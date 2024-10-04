package com.github.argon.sos.mod.sdk.log.writer;

import com.github.argon.sos.mod.sdk.util.StringUtil;
import snake2d.LOG;

import static com.github.argon.sos.mod.sdk.util.StringUtil.stringifyValues;

/**
 * Writes to the game {@link LOG#ln(Object)} or {@link LOG#err(Object)}
 */
public class GameLOG extends AbstractLogWriter {

    public GameLOG(String prefix, String messageFormat) {
        super(prefix, messageFormat);
    }

    @Override
    public void error(String name, String msgPrefix, String formatMsg, Object[] args) {
        try {
            LOG.err(String.format(messageFormat,
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
    public void log(String name, String msgPrefix, String formatMsg, Object[] args) {
        try {
            LOG.ln(String.format(messageFormat,
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
    public void exception(Throwable ex) {
        String exceptionString = StringUtil.stringify(ex);
        LOG.err(exceptionString);
    }
}
