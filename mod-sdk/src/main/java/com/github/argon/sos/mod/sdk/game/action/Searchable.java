package com.github.argon.sos.mod.sdk.game.action;


/**
 * For searching through elements
 *
 * @param <Term> type of the thing used for searching
 * @param <Result> type of the search result
 */
public interface Searchable<Term, Result> {
    /**
     * Search a component for a given term
     *
     * @param term to search for
     * @return the search result
     */
    Result search(Term term);
}
