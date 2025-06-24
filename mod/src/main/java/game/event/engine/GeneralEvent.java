package game.event.engine;

import game.GAME;
import game.event.actions.EventAction;
import game.events.EVENTS;
import game.faction.FACTIONS;
import init.race.RACES;
import init.race.Race;
import init.sprite.UI.UI;
import init.type.CLIMATE;
import init.type.CLIMATES;
import init.type.TERRAIN;
import init.type.TERRAINS;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import settlement.main.SETT;
import snake2d.util.gui.GUI_BOX;
import util.dic.Dic;
import util.gui.misc.GBox;
import util.info.GFORMAT;

@Data
@Builder
@EqualsAndHashCode
public class GeneralEvent {
    private String key;
    private Event event;
    private EVENTS.EventResource eventResource;
    private boolean enabled;

    // FIXME temporarily fix; move this into view
    public void hover(GUI_BOX b) {
        GBox gBox = (GBox) b;

        gBox.title(event.info.name);
        if (event.info.icon != null)
            gBox.add(event.info.icon);
        gBox.NL();
        for (CharSequence s : event.info.messages) {
            gBox.text(s);
            gBox.NL();
        }
        gBox.add(gBox.text().warnify().add(event.info.desc));
        gBox.NL();
        gBox.add(gBox.text().errorify().add(event.info.subject));
        gBox.NL();
        {
            gBox.NL();
            gBox.textLL(Dic.¤¤Occurrence);
            gBox.NL();
            gBox.add(gBox.text().add(GAME.EVENT().can(event)));
            gBox.NL();
            int tt = 0;

            for (TERRAIN t : TERRAINS.ALL()) {
                if (tt > 6) {
                    tt = 0;
                    gBox.NL();
                }
                gBox.add(t.icon());
                gBox.add(GFORMAT.f0(gBox.text(), event.occurence.toccurence[t.index()]));
            }
            gBox.NL();
            for (Race rr : RACES.all()) {
                if (tt > 6) {
                    tt = 0;
                    gBox.NL();
                }
                gBox.add(rr.appearance().icon);
                gBox.add(GFORMAT.f0(gBox.text(), event.occurence.roccurence[rr.index()]));
            }
            gBox.NL();
            CLIMATE climate = SETT.ENV().climate();
            gBox.textLL(CLIMATES.INFO().name);
            gBox.tab(6);
            gBox.add(GFORMAT.f0(gBox.text(), event.occurence.coccurence[climate.index()]));
            gBox.NL();
            gBox.textSLL(Dic.¤¤Total);
            gBox.tab(6);
            gBox.add(GFORMAT.f0(gBox.text(), event.occurence.occurence()));
            gBox.NL();
            gBox.text(Dic.¤¤Current);
            gBox.add(GFORMAT.iofk(gBox.text(), GAME.EVENT().occ(event), event.occurence.maxSpawns));
            gBox.NL();


            event.occurence.plockable.hover(b, FACTIONS.player());
            gBox.NL();
            gBox.add(gBox.text().add(event.occurence.plockable.passes(FACTIONS.player())));

            gBox.sep();

            gBox.text("tags");
            gBox.add(gBox.text().add(event.tags.can(GAME.EVENT().tags)));

            gBox.add(UI.icons().s.plus);
            for (String k : event.tags.adds) {
                gBox.text(k);
            }
            gBox.NL();
            gBox.add(UI.icons().s.minus);
            for (String k : event.tags.removes) {
                gBox.text(k);
            }
            gBox.NL();

            for (String k : event.tags.allows) {
                if (!GAME.EVENT().tags.containsKey(k) || GAME.EVENT().tags.get(k) == Boolean.FALSE)
                    gBox.add(gBox.text().errorify().add(k));
                else
                    gBox.add(gBox.text().normalify2().add(k));
            }
            gBox.NL();
            for (String k : event.tags.allows_not) {
                if (GAME.EVENT().tags.containsKey(k) && GAME.EVENT().tags.get(k) == Boolean.TRUE)
                    gBox.add(gBox.text().errorify().add(k));
                else
                    gBox.add(gBox.text().normalify2().add(k));
            }
            gBox.NL();
            gBox.sep();
        }

        {
            gBox.add(UI.icons().s.cancel);
            for (EventAction c : event.duration.on_expire)
                gBox.text(c.key);
            gBox.NL();


            for (EChoice ch : event.choices) {
                gBox.add(UI.icons().s.question);
                for (EventAction c : ch.actions)
                    gBox.text(c.key);
                gBox.NL();
            }


        }

        for (EventAction c : event.actions()) {
            gBox.text(c.key);
            gBox.NL();
        }

    }
}
