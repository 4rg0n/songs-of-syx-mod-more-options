package com.github.argon.sos.moreoptions;

import com.github.argon.sos.moreoptions.game.api.GameUiApi;
import com.github.argon.sos.moreoptions.game.ui.Notification;
import com.github.argon.sos.moreoptions.init.Updateable;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import init.C;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;
import snake2d.util.color.COLOR;
import util.colors.GCOLOR;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

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
    private Instant lastShowedAt;

    @Setter
    @Accessors(fluent = true)
    private int showSeconds = 10;

    @Setter
    @Accessors(fluent = true)
    private int showDelaySeconds = 1;
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

        notification.onHide(closedNotification -> {
            queue.remove(closedNotification);
            current = null;
            lastShowedAt = null;
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
        lastShowedAt = Instant.now();
        gameUiApi.notification().show(notification, C.DIM().x2(), C.DIM().y2());
    }

    @Override
    public void update(float seconds) {
        // wait until showSeconds for next
        if (current != null && lastShowedAt != null) {
            Instant now = Instant.now();
            Instant showUntil = lastShowedAt.plusSeconds(showSeconds);

            if (now.isAfter(showUntil)) {
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
        if (lastShowedAt != null) {
            Instant now = Instant.now();
            Instant delayUntil = lastShowedAt.plusSeconds(showSeconds).plusSeconds(showDelaySeconds);

            if (now.isBefore(delayUntil)) {
                return;
            } else {
                lastShowedAt = null;
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
}
