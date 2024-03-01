package com.github.argon.sos.moreoptions.game.ui;

public interface Searchable<Term, Result> {
    Result search(Term term);
}
