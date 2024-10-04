package com.github.argon.sos.mod.sdk.log.writer;

import com.github.argon.sos.mod.sdk.phase.Phases;
import com.github.argon.sos.mod.sdk.util.CharUtil;
import com.github.argon.sos.mod.sdk.util.StringUtil;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.file.Path;

import static com.github.argon.sos.mod.sdk.util.StringUtil.stringifyValues;

/**
 * Can be used to write log entries into a file.
 * It will permanently hold an open file stream for writing.
 */
public class FileLogWriter extends AbstractLogWriter implements Phases {

    @Nullable
    private BufferedWriter bufferedWriter;

    private final Path logFile;

    public FileLogWriter(
        String prefix,
        String messageFormat,
        Path logFile
    ) {
        super(prefix, messageFormat);

        this.logFile = logFile;
    }

    /**
     * Opens a {@link BufferedWriter} stream to the log file.
     *
     * @return whether the stream was successfully opened
     */
    public boolean open() {
        File file = logFile.toFile();
        try {
            file.createNewFile();
            FileWriter fileWriter = new FileWriter(file, true);
            bufferedWriter = new BufferedWriter(fileWriter);
        } catch (IOException e) {
            problemLogging("NONE", new Object[]{}, e);
            return false;
        }

        return true;
    }

    /**
     * @return whether the writer has a file open to log into
     */
    public boolean isOpen() {
        return bufferedWriter != null;
    }

    @Override
    public void error(String name, String msgPrefix, String formatMsg, Object[] args) {
        if (bufferedWriter == null) return;

        try {
            String logMessage = String.format(messageFormat,
                prefix,
                timestamp(),
                name,
                msgPrefix,
                String.format(formatMsg, stringifyValues(args)));

            bufferedWriter.write(logMessage + CharUtil.LF);
            bufferedWriter.flush();
        } catch (Exception e) {
            problemLogging(formatMsg, args, e);
        }
    }

    @Override
    public void log(String name, String msgPrefix, String formatMsg, Object[] args) {
        if (bufferedWriter == null) return;

        try {
            String logMessage = String.format(messageFormat,
                prefix,
                timestamp(),
                name,
                msgPrefix,
                String.format(formatMsg, stringifyValues(args)));

            bufferedWriter.write(logMessage + CharUtil.LF);
            bufferedWriter.flush();
        } catch (Exception e) {
            problemLogging(formatMsg, args, e);
        }
    }

    @Override
    public void exception(Throwable exception) {
        if (bufferedWriter == null) return;

        String exceptionString = StringUtil.stringify(exception);
        try {
            bufferedWriter.write(exceptionString + CharUtil.LF);
            bufferedWriter.flush();
        } catch (IOException e) {
            problemLogging("NONE", new Object[]{}, e);
        }
    }

    @Override
    public void onCrash(Throwable e) {
        close();
    }

    /**
     * Close the open log file and release it from the {@link BufferedWriter} stream.
     */
    public void close() {
        if (bufferedWriter == null) return;

        try {
            bufferedWriter.close();
            bufferedWriter = null;
        } catch (IOException ex) {
            problemLogging("NONE", new Object[]{}, ex);
        }
    }
}
