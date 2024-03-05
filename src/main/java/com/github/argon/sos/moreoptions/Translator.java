package com.github.argon.sos.moreoptions;

import com.github.argon.sos.moreoptions.i18n.Dictionary;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Translator {

    @Getter(lazy = true)
    private final static Translator instance = new Translator(
        Dictionary.getInstance()
    );

    private final Dictionary dictionary;

    // todo implement i18n
    //      * see UiInfo class and usage of Translatable interface
    //      * look for places missing translation and implement Translator
    //      * refactor current translations with Translator
}
