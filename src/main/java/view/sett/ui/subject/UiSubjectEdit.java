package view.sett.ui.subject;

import com.github.argon.sos.moreoptions.game.ui.*;
import com.github.argon.sos.moreoptions.game.util.UiUtil;
import com.github.argon.sos.moreoptions.util.Lists;
import game.boosting.BOOSTABLES;
import game.boosting.Boostable;
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

        List<ColumnRow<Integer>> settingRows = new ArrayList<>();
        List<ColumnRow<Void>> propertyRows = new ArrayList<>();

        // NAME
        settingRows.add(ColumnRow.<Integer>builder()
            .column(Label.builder()
                .name("Name")
                .build())
            .column(InputStr.builder()
                .placeHolder(name)
                .valueConsumer(value -> STATS.APPEARANCE().customName(humanoid.indu()).clear().add(value))
                .valueSupplier(() -> STATS.APPEARANCE().name(humanoid.indu()).toString())
                .build())
            .margin(10)
            .build());

        // BATTLE
        settingRows.add(header("Battle"));
        settingRows.add(slider(STATS.BATTLE().basicTraining));
        Lists.fromGameLIST(STATS.BATTLE().TRAINING_ALL).stream()
            .map(statTraining -> statTraining.stat)
            .filter(stat -> stat.info() != null && stat.indu() != null)
            .forEach(stat -> settingRows.add(slider(stat)));

        propertyRows.add(header("Battle"));
        propertyRows.addAll(properties(Lists.fromGameLIST(BOOSTABLES.BATTLE().all())));

        // NEEDS
        settingRows.add(header("Needs"));
        Lists.fromGameLIST(STATS.NEEDS().SNEEDS).stream()
            .map(StatsNeeds.StatNeed::stat)
            .filter(stat -> stat.info() != null && stat.indu() != null)
            .forEach(stat -> settingRows.add(slider(stat)));

        propertyRows.add(header("Needs"));
        Lists.fromGameLIST(STATS.NEEDS().SNEEDS)
            .forEach(need -> propertyRows.add(need(need)));

        // OTHER NEEDS
        settingRows.add(header("Other Needs"));
        Lists.fromGameLIST(STATS.NEEDS().OTHERS).stream()
            .filter(stat -> stat.info() != null && stat.indu() != null)
            .forEach(stat -> settingRows.add(slider(stat)));

        propertyRows.add(header("Other Needs"));
        Lists.fromGameLIST(STATS.NEEDS().OTHERS)
            .forEach(need -> propertyRows.add(need(need)));

        // EDUCATION
        // FIXME EDUCATION wont be saved correctly
        settingRows.add(header("Education"));
        Lists.fromGameLIST(STATS.EDUCATION().all()).stream()
            .filter(stat -> stat.info() != null && stat.indu() != null)
            .forEach(stat -> settingRows.add(slider(stat)));

        Table<Integer> settingsTable = Table.<Integer>builder()
            .rows(settingRows)
            .displayHeight(500)
            .highlight(true)
            .evenOdd(true)
            .rowPadding(5)
            .scrollable(true)
            .displaySearch(true)
            .build();

        Table<Void> propertiesTable = Table.<Void>builder()
            .rows(propertyRows)
            .displayHeight(500)
            .highlight(true)
            .evenOdd(true)
            .scrollable(true)
            .displaySearch(true)
            .build();

        GuiSection section = new GuiSection();
        section.addRight(0, settingsTable);
        section.addRight(20, propertiesTable);

        addDownC(10, section);
    }

    private ColumnRow<Void> need(StatsNeeds.StatNeed need) {
        return ColumnRow.<Void>builder()
            .column(UiUtil.toGuiSection(new NeedBox(humanoid, need)))
            .build();
    }

    private ColumnRow<Void> need(STAT stat) {
        return ColumnRow.<Void>builder()
            .column(UiUtil.toGuiSection(new NeedBox(humanoid, stat)))
            .build();
    }

    private List<ColumnRow<Void>> properties(List<Boostable> boostables) {
        List<ColumnRow<Void>> rows = new ArrayList<>();
        List<List<Boostable>> boostableChunks = Lists.chunk(boostables, 5);

        for (List<Boostable> boostableChunk : boostableChunks) {
            if (boostableChunk.isEmpty()) {
                continue;
            }

            GuiSection row = new GuiSection();
            List<String> searchTerms = new ArrayList<>();

            for (Boostable boostable : boostableChunk) {
                BoosterBox boosterBox = new BoosterBox(boostable, humanoid);
                row.addRight(2, boosterBox);
                searchTerms.add(boostable.name.toString());
            }

            ColumnRow<Void> columnRow = ColumnRow.<Void>builder()
                .column(row)
                .searchTerm(String.join(":", searchTerms))
                .build();
            rows.add(columnRow);
        }

        return rows;
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
            .lockScroll(true)
            .controls(true)
            .input(true)
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
            .margin(20)
            .searchTerm(name)
            .value(value)
            .valueSupplier(valueSupplier)
            .valueConsumer(valueConsumer)
            .build();
    }


}
