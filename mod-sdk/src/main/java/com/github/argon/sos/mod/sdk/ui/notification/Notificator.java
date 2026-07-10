package com.github.argon.sos.mod.sdk.ui.notification;

import com.github.argon.sos.mod.sdk.game.action.Hideable;
import com.github.argon.sos.mod.sdk.game.api.GameUiApi;
import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;
import com.github.argon.sos.mod.sdk.phase.Phases;
import com.github.argon.sos.mod.sdk.game.action.Updateable;
import init.constant.C;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;
import snake2d.SPRITE_RENDERER;
import snake2d.util.color.COLOR;
import util.colors.GCOLOR;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * For displaying notification messages in a little gui box.
 * Notifications will be queued and shown in order.
 */
@RequiredArgsConstructor
public class Notificator implements Updateable, Hideable, Phases {
    private final static Logger log = Loggers.getLogger(Notificator.class);

    private final GameUiApi gameUiApi;

    private final List<Notification> queue = new ArrayList<>();

    private final COLOR colorError =  GCOLOR.UI().badFlash();
    private final COLOR colorSuccess = GCOLOR.UI().goodFlash();

    @Nullable
    private Instant showUntil;

    /**
     * How long the notification shall be visible.
     * Seconds per one hundred (100) characters.
     */
    @Setter
    @Accessors(fluent = true)
    private int showSecondsPer100Chars = 25;

    /**
     * No matter how many chars, show at minimum.
     */
    @Setter
    @Accessors(fluent = true)
    private int showSecondsMin = 5;

    /**
     * Delay until the next notification is displayed.
     */
    @Setter
    @Accessors(fluent = true)
    private int showNextDelaySeconds = 1;

    /**
     * The currently displayed notification.
     */
    @Nullable
    private Notification current;

    /**
     * Displays a notification text without title.
     *
     * @param text to display
     */
    public void notify(String text) {
        log.info("[NOTIFY] " + text);
        notify(text, null, null);
    }

    /**
     * Displays a notification text with given title and background color.
     *
     * @param text to display
     * @param title for the notification
     * @param titleBackgroundColor to use for the notification title
     */
    public void notify(String text, @Nullable String title, @Nullable COLOR titleBackgroundColor) {
        Notification notification = Notification.builder()
            .titleBackground(titleBackgroundColor)
            .title(title)
            .text(text)
            .width(200)
            .height(150)
            .build();

        notification.hideAction(closedNotification -> {
            queue.remove(closedNotification);
            current = null;
            showUntil = null;
        });

        queue(notification);
    }

    /**
     * Displays a notification text as an error.
     * Will log exception as an error.
     *
     * @param text to display
     * @param exception to log
     */
    public void notifyError(String text, Throwable exception) {
        log.error("[NOTIFY] " + text, exception);
        notify(text, "Error", colorError);
    }

    /**
     * Displays a notification text as an error.
     *
     * @param text to display
     */
    public void notifyError(String text) {
        log.warn("[NOTIFY] " + text);
        notify(text, "Error", colorError);
    }

    /**
     * Displays a notification text as a success message.
     *
     * @param text to display
     */
    public void notifySuccess(String text) {
        notify(text, "Success", colorSuccess);
    }

    private void queue(Notification notification) {
        queue.add(notification);
        log.debug("Queued new notification. In queue: %s", queue.size());
        log.trace("Notification: %s", notification);
    }

    private void show(Notification notification) {
        log.debug("Show notification");
        current = notification;
        showUntil = Instant.now().plusSeconds(calculateShowSeconds(notification.getText()));
        gameUiApi.notification().show(notification, C.DIM().x2(), C.DIM().y2());
    }

    /**
     * Will remove the currently shown notification.
     * Will not prevent the next notification in the queue from popping up.
     */
    @Override
    public void hide() {
        gameUiApi.notification().closeSilent();
        queue.remove(current);
        current = null;
    }

    /**
     * Has to be called from the outside by a looping game process like
     * {@link snake2d.util.gui.renderable.RENDEROBJ#render(SPRITE_RENDERER, float)}
     *
     * Controls the showing and hiding of {@link Notification}s.
     *
     * @param deltaSeconds since last update loop
     */
    @Override
    public void update(double deltaSeconds) {
        // wait until showSeconds for next
        if (current != null) {
            Instant now = Instant.now();
            if (showUntil != null && now.isAfter(showUntil)) {
                // time elapsed, hide notifications!
                hide();
            }
        }

        // anything new to show?
        if (queue.isEmpty()) {
            return;
        }

        // wait until showDelaySeconds passed for next
        if (showUntil != null) {
            Instant now = Instant.now();
            Instant delayUntil = showUntil.plusSeconds(showNextDelaySeconds);

            if (now.isBefore(delayUntil)) {
                return;
            } else {
                showUntil = null;
            }
        }

        // show next from queue
        Notification nextNotification = queue.getLast();
        show(nextNotification);
    }

    public void close() {
        log.debug("Close notifications");
        gameUiApi.notification().close();
    }

    private int calculateShowSeconds(String text) {
        int showSeconds = (int) (((double) text.length() / 100) * showSecondsPer100Chars);

        if (showSeconds < showSecondsMin) {
            return showSecondsMin;
        }

        return showSeconds;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onGameUpdate(double seconds) {
        update(seconds);
    }
}
