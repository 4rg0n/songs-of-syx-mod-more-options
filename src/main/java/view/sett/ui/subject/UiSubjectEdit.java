package view.sett.ui.subject;

import com.github.argon.sos.moreoptions.game.ui.*;
import com.github.argon.sos.moreoptions.game.util.UiUtil;
import com.github.argon.sos.moreoptions.util.Lists;
import game.boosting.BOOSTABLES;
import game.boosting.Boostable;
import init.sprite.UI.UI;
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

        List<Boostable> boosters = Lists.fromGameLIST(BOOSTABLES.BATTLE().all());
        boosters.add(BOOSTABLES.PHYSICS().STAMINA);
        addDownC(15, boosters(boosters));

        List<ColumnRow<Integer>> settingRows = new ArrayList<>();

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
        Lists.fromGameLIST(STATS.BATTLE().TRAINING_ALL).stream()
            .map(statTraining -> statTraining.stat)
            .filter(stat -> stat.info() != null && stat.indu() != null)
            .forEach(stat -> settingRows.add(slider(stat, Lists.of())));

        // NEEDS
        settingRows.add(header("Needs"));
        Lists.fromGameLIST(STATS.NEEDS().SNEEDS).stream()
            .filter(need -> need.stat().info() != null && need.stat().indu() != null)
            .forEach(need -> settingRows.add(slider(need)));

        // OTHER NEEDS
        Lists.fromGameLIST(STATS.NEEDS().OTHERS).stream()
            .filter(stat -> stat.info() != null && stat.indu() != null)
            .forEach(stat -> settingRows.add(slider(stat, Lists.of(
                UiUtil.toGuiSection(new NeedBox(humanoid, stat))
            ))));

        // EDUCATION
        Lists.fromGameLIST(STATS.EDUCATION().all()).stream()
            .filter(stat -> stat.info() != null && stat.indu() != null)
            .forEach(stat -> settingRows.add(slider(stat, Lists.of(
                UiUtil.toGuiSection(new StatBar(humanoid, stat))
            ))));

        Table<Integer> settingsTable = Table.<Integer>builder()
            .rows(settingRows)
            .displayHeight(600)
            .highlight(true)
            .evenOdd(true)
            .rowPadding(5)
            .scrollable(true)
            .displaySearch(true)
            .build();

        addDownC(10, settingsTable);
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

    private GuiSection boosters(List<Boostable> boostables) {
        GuiSection section = new GuiSection();
        List<List<Boostable>> boostableChunks = Lists.chunk(boostables, 8);

        for (List<Boostable> boostableChunk : boostableChunks) {
            if (boostableChunk.isEmpty()) {
                continue;
            }

            GuiSection row = new GuiSection();
            for (Boostable boostable : boostableChunk) {
                BoosterBox boosterBox = new BoosterBox(boostable, humanoid);
                row.addRight(2, boosterBox);
            }

            section.addDown(0, row);
        }

        return section;
    }

    private static <T> ColumnRow<T> header(String name) {
        GuiSection header = UiUtil.toGuiSection(new GHeader(name));
        header.pad(0, 20);

        return ColumnRow.<T>builder()
            .column(header)
            .isHeader(true)
            .build();
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

    private ColumnRow<Integer> slider(StatsNeeds.StatNeed need) {
        return slider(
            need.stat().info().name.toString(),
            need.stat().info().desc.toString(),
            need.stat().indu().min(humanoid.indu()),
            need.stat().indu().max(humanoid.indu()),
            () -> need.stat().indu().get(humanoid.indu()),
            value -> need.stat().indu().set(humanoid.indu(), value),
            Lists.of(
                UiUtil.toGuiSection(new NeedBox(humanoid, need)),
                UiUtil.toGuiSection(new StatBar(humanoid, need.stat()))
            )
        );
    }

    private ColumnRow<Integer> slider(STAT stat, List<GuiSection> extraColumns) {
        return slider(
            stat.info().name.toString(),
            stat.key() + ": " + stat.info().desc.toString(),
            stat.indu().min(humanoid.indu()),
            stat.indu().max(humanoid.indu()),
            () -> stat.indu().get(humanoid.indu()),
            value -> stat.indu().set(humanoid.indu(), value),
            extraColumns
        );
    }

    private ColumnRow<Integer> slider(INT_O.INT_OE<Induvidual> stat, List<GuiSection> extraColumns) {
        return slider(
            stat.info().name.toString(),
            stat.info().desc.toString(),
            stat.min(humanoid.indu()),
            stat.max(humanoid.indu()),
            () -> stat.get(humanoid.indu()),
            value -> stat.set(humanoid.indu(), value),
            extraColumns
        );
    }

    private ColumnRow<Integer> slider(
        String name,
        String description,
        int min,
        int max,
        Supplier<Integer> valueSupplier,
        Consumer<Integer> valueConsumer,
        List<GuiSection> extraColumns
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

        ColumnRow.ColumnRowBuilder<Integer> columnRowBuilder = ColumnRow.<Integer>builder()
            .column(Label.builder()
                .name(name)
                .font(UI.FONT().M)
                .description(description)
                .build())
            .column(slider)
            .margin(10)
            .searchTerm(name)
            .value(value)
            .valueSupplier(valueSupplier)
            .valueConsumer(valueConsumer);

        extraColumns.forEach(columnRowBuilder::column);

        return columnRowBuilder.build();
    }


}
