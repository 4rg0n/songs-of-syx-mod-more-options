package view.sett.ui.subject;

import com.github.argon.sos.moreoptions.game.ui.*;
import com.github.argon.sos.moreoptions.util.Lists;
import settlement.entity.humanoid.Humanoid;
import settlement.stats.Induvidual;
import settlement.stats.STATS;
import settlement.stats.colls.StatsNeeds;
import settlement.stats.stat.STAT;
import snake2d.util.gui.GuiSection;
import util.data.INT_O;
import util.gui.misc.GHeader;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class UiSubjectEdit extends GuiSection {

    private final Humanoid humanoid;

    public UiSubjectEdit(Humanoid humanoid) {
        this.humanoid = humanoid;

        String name = STATS.APPEARANCE().name(humanoid.indu()).toString();
        GHeader header = new GHeader("Edit: " + name);
        addDownC(0, header);

        ColumnRow<Object> nameEdit = ColumnRow.builder()
            .column(Label.builder()
                .name("Name")
                .build())
            .column(InputStr.builder()
                .placeHolder(name)
                .valueConsumer(value -> STATS.APPEARANCE().customName(humanoid.indu()).clear().add(value))
                .valueSupplier(() -> STATS.APPEARANCE().name(humanoid.indu()).toString())
                .build())
            .margin(10)
            .build();
        addDownC(10, nameEdit);

        List<ColumnRow<Integer>> rows = new ArrayList<>();

        // BATTLE
        rows.add(header("Battle"));
        rows.add(slider(STATS.BATTLE().basicTraining));
        Lists.fromGameLIST(STATS.BATTLE().TRAINING_ALL).stream()
            .map(statTraining -> statTraining.stat)
            .filter(stat -> stat.info() != null && stat.indu() != null)
            .forEach(stat -> rows.add(slider(stat)));

        // NEEDS
        rows.add(header("Needs"));
        Lists.fromGameLIST(STATS.NEEDS().SNEEDS).stream()
            .map(StatsNeeds.StatNeed::stat)
            .filter(stat -> stat.info() != null && stat.indu() != null)
            .forEach(stat -> rows.add(slider(stat)));

        // OTHER NEEDS
        rows.add(header("Other Needs"));
        Lists.fromGameLIST(STATS.NEEDS().OTHERS).stream()
            .filter(stat -> stat.info() != null && stat.indu() != null)
            .forEach(stat -> rows.add(slider(stat)));

        // EDUCATION
        rows.add(header("Education"));
        Lists.fromGameLIST(STATS.EDUCATION().all()).stream()
            .filter(stat -> stat.info() != null && stat.indu() != null)
            .forEach(stat -> rows.add(slider(stat)));

        // WORK
        rows.add(header("Work"));
        Lists.fromGameLIST(STATS.WORK().all()).stream()
            .filter(stat -> stat.info() != null && stat.indu() != null)
            .forEach(stat -> rows.add(slider(stat)));

        // NOBLE
//        rows.add(header("Noble Personality"));
//        Lists.fromGameLIST(BOOSTABLES.NOBLE().all()).stream()
//            .map(booster -> booster)

        Table<Integer> table = Table.<Integer>builder()
            .rows(rows)
            .displayHeight(500)
            .highlight(true)
            .evenOdd(true)
            .rowPadding(5)
            .scrollable(true)
            .displaySearch(true)
            .build();


        addDownC(10, table);
    }

    private static <T> ColumnRow<T> header(String name) {
        ColumnRow<T> row = ColumnRow.<T>builder()
            .column(new GHeader(name))
            .isHeader(true)
            .build();
        row.pad(5);
        return row;
    }

//    private ColumnRow<Integer> slider(Boostable boostable) {
//
//        MathUtil.fromPercentage(boostable.get(humanoid.indu()));
//
//        double boosterValue = boostable.get(humanoid.indu());
//
//        boostable.all().forEach(booster -> booster.getValue(boosterValue));
//
//        return slider(
//            boostable.name.toString(),
//            boostable.desc.toString(),
//            MathUtil.fromPercentage(boostable.min(Induvidual.class)),
//            MathUtil.fromPercentage(boostable.max(Induvidual.class)),
//            () -> MathUtil.fromPercentage(boostable.baseValue),
//            value ->
//        );
//    }

    private ColumnRow<Integer> slider(STAT stat) {
        return slider(
            stat.info().name.toString(),
            stat.info().desc.toString(),
            stat.indu().min(humanoid.indu()),
            stat.indu().max(humanoid.indu()),
            () -> stat.indu().get(humanoid.indu()),
            // fixme setting the stat, will not necessary influence any outcome calculations
            value -> stat.indu().set(humanoid.indu(), value)
        );
    }

    private ColumnRow<Integer> slider(INT_O.INT_OE<Induvidual> stat) {
        return slider(
            stat.info().name.toString(),
            stat.info().desc.toString(),
            stat.min(humanoid.indu()),
            stat.max(humanoid.indu()),
            () -> stat.get(humanoid.indu()),
            // fixme setting the stat, will not necessary influence any outcome calculations
            value -> stat.set(humanoid.indu(), value)
        );
    }

    private ColumnRow<Integer> slider(
        String name,
        String description,
        int min,
        int max,
        Supplier<Integer> valueSupplier,
        Consumer<Integer> valueConsumer
    ) {
        int value = valueSupplier.get();

        Slider slider = Slider.builder()
            .width(200)
            .max(max)
            .min(min)
            .value(value)
            .valueDisplay(Slider.ValueDisplay.ABSOLUTE)
            .valueSupplier(valueSupplier)
            .valueConsumer(valueConsumer)
            .build();

        return ColumnRow.<Integer>builder()
            .column(Label.builder()
                .name(name)
                .description(description)
                .build())
            .column(slider)
            .margin(10)
            .searchTerm(name)
            .value(value)
            .valueSupplier(valueSupplier)
            .valueConsumer(valueConsumer)
            .build();
    }


}
