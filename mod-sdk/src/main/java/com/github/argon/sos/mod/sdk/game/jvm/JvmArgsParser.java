package com.github.argon.sos.mod.sdk.game.jvm;

import lombok.Getter;
import lombok.Setter;


public class JvmArgsParser {
    @Getter
    private final String rawContent;

    /**
     * For parsing the jvm args with {@link JvmArgsParser}.
     * Points to the current character in {@link JvmArgsParser#rawContent} to parse.
     */
    @Setter
    @Getter
    private int index;
    private final int length;

    /**
     * Creates a new {@link JvmArgsParser} instance with given raw json parsed as content.
     * Will use {@code JsonWriters.gameJsonUnquotedPretty()} as writer.
     *
     * @param rawContent to use as content
     */
    public JvmArgsParser(String rawContent) {
        this.rawContent = rawContent;
        this.length = rawContent.length();
    }

    /**
     * For parsing the {@link JvmArgsParser#rawContent}
     *
     * @return the current character the index is pointing at
     */
    public char currentChar(){
        return rawContent.charAt(index);
    }

    /**
     * For parsing the {@link JvmArgsParser#rawContent}
     * Moves the index to the next character.
     */
    public void indexMove(){
        this.index++;
    }


    /**
     * Returns the string until a stop character is reached.
     * See {@link JvmArgsParser#isEndOfValue(char)}
     *
     * @return the next value
     */
    public String getNextValue() {
        StringBuilder sb = new StringBuilder();
        while (!atEnd()) {
            char c = currentChar();
            if (isEndOfValue(c)) {
                break;
            }
            sb.append(c);
            indexMove();
        }
        return sb.toString();
    }

    /**
     * Checks whether end of content is reached.
     *
     * @return whether the end of the arguments string is reached
     */
    public boolean atEnd(){
        return getIndex() >= length;
    }

    private boolean isEndOfValue(char c) {
        return isWhiteSpace(c);
    }

    private boolean isWhiteSpace(char c) {
        return (c == ' '
            || c=='\n'
            || c=='\t'
            || c=='\f'
            || c=='\r'
        );
    }
}
