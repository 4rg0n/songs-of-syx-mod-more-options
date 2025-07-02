package game.event.engine;

import com.github.argon.sos.mod.sdk.data.TreeNode;
import com.github.argon.sos.mod.sdk.game.api.GameEventsApi;
import game.faction.FACTIONS;
import init.race.RACES;
import init.race.Race;
import init.type.TERRAIN;
import init.type.TERRAINS;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import snake2d.util.gui.GUI_BOX;
import util.dic.Dic;
import util.gui.misc.GBox;
import util.info.GFORMAT;

@Data
@Builder
@EqualsAndHashCode
public class EventTree {
    private String key;
    private TreeNode<GameEventsApi.EventContainer> tree;
    private boolean enabled;

    // FIXME move this out of here + refactor this
    public void hover(GUI_BOX box) {
        GBox b = (GBox) box;
        Event event = this.tree.get().getEvent();

        b.title(event.info.name);
        if (event.info.icon != null)
            b.add(event.info.icon);
        b.NL();

        for (CharSequence message : event.info.messages) {
            String[] messageParts = message.toString().split("\n");

            for (String messagePart : messageParts) {
                b.text(messagePart.trim());
                b.NL();
            }
        }

        b.sep();
        b.NL();
        b.textLL(Dic.¤¤Occurrence);
        b.NL();
        int tt = 0;

        for (TERRAIN t : TERRAINS.ALL()) {
            if (tt > 6) {
                tt = 0;
                b.NL();
            }
            b.add(t.icon());
            b.add(GFORMAT.f0(b.text(), event.occurence.toccurence[t.index()]));
        }
        b.NL();

        for (Race rr : RACES.all()) {
            if (tt > 6) {
                tt = 0;
                b.NL();
            }
            b.add(rr.appearance().icon);
            b.add(GFORMAT.f0(b.text(), event.occurence.roccurence[rr.index()]));
        }

        b.sep();
        b.NL();
        event.occurence.plockable.hover(b, FACTIONS.player());
    }
}
