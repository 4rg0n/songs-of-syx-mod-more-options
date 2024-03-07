package com.github.argon.sos.moreoptions;

import com.github.argon.sos.moreoptions.game.api.GameUiApi;
import com.github.argon.sos.moreoptions.game.ui.Notification;
import com.github.argon.sos.moreoptions.phase.Updateable;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import init.C;
import lombok.AccessLevel;
import lombok.Getter;
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
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Notificator implements Updateable {
    private final static Logger log = Loggers.getLogger(Notificator.class);

    @Getter(lazy = true)
    private final static Notificator instance = new Notificator(GameUiApi.getInstance());

    private final GameUiApi gameUiApi;

    private final List<Notification> queue = new ArrayList<>();

    private final COLOR colorError =  GCOLOR.UI().badFlash();
    private final COLOR colorSuccess = GCOLOR.UI().goodFlash();

    @Nullable
    private Instant showUntil;

    /**
     * Seconds per one hundred (100) characters
     */
    @Setter
    @Accessors(fluent = true)
    private int showSecondsPer100Chars = 25;

    /**
     * No matter how many chars, show at minimum
     */
    @Setter
    @Accessors(fluent = true)
    private int showSecondsMin = 5;

    /**
     * Delay until next notification is displayed
     */
    @Setter
    @Accessors(fluent = true)
    private int showNextDelaySeconds = 1;
    @Nullable
    private Notification current;

    public void notify(String text) {
        log.info("[NOTIFY] " + text);
        notify(text, null, null);
    }

    public void notify(String text, @Nullable String title, @Nullable COLOR color) {
        Notification notification = Notification.builder()
            .titleBackground(color)
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


    public void notifyError(String text, Exception e) {
        log.error("[NOTIFY] " + text, e);
        notifyError(text);
    }

    public void notifyError(String text) {
        log.warn("[NOTIFY] " + text);
        notify(text, "Error", colorError);
    }

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
     * Has to be called from the outside by a looping game process like
     * {@link snake2d.util.gui.renderable.RENDEROBJ#render(SPRITE_RENDERER, float)}
     *
     * Controls the showing and hiding of {@link Notification}s.
     */
    @Override
    public void update(float seconds) {
        // wait until showSeconds for next
        if (current != null) {
            Instant now = Instant.now();
            if (showUntil != null && now.isAfter(showUntil)) {
                // time elapsed, hide notifications!
                GameUiApi.getInstance().notification().closeSilent();
                queue.remove(current);
                current = null;
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
        Notification nextNotification = queue.get(queue.size() - 1);
        show(nextNotification);
    }

    public void close() {
        log.debug("Close notifications");
        GameUiApi.getInstance().notification().close();
    }

    private int calculateShowSeconds(String text) {
        int showSeconds = (int) (((double) text.length() / 100) * showSecondsPer100Chars);

        if (showSeconds < showSecondsMin) {
            return showSecondsMin;
        }

        return showSeconds;
    }
}
