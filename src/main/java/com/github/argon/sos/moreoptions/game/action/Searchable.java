package com.github.argon.sos.moreoptions.game.action;

import com.github.argon.sos.moreoptions.game.ui.Table;

/**
 * For searching through elements like {@link Table}
 *
 * @param <Term> type of the thing used for searching
 * @param <Result> type of the search result
 */
public interface Searchable<Term, Result> {
    Result search(Term term);
}
