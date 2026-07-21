package com.github.argon.sos.mod.sdk.game.action;

/**
 * For ui elements which handle their own vertical mouse wheel scrolling.
 * Lets an outer scrollable container (e.g. a scrollable table) yield the mouse
 * wheel to this element while it is directly hovered and has scrollable content
 * of its own, instead of the outer container consuming the wheel first.
 */
public interface WheelScrollable {
    /**
     * Whether this element currently has scrollable overflow and wants to
     * consume the mouse wheel instead of an outer scroll container.
     *
     * @return whether wheel scroll priority is wanted right now
     */
    boolean wantsWheelScroll();
}
