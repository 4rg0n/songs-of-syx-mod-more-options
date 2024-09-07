package com.github.argon.sos.moreoptions.game.ui;

import com.github.argon.sos.mod.sdk.game.action.Action;
import com.github.argon.sos.mod.sdk.game.action.Resettable;
import com.github.argon.sos.mod.sdk.game.action.Valuable;
import com.github.argon.sos.mod.sdk.game.util.TextFormatUtil;
import com.github.argon.sos.mod.sdk.i18n.I18n;
import com.github.argon.sos.mod.sdk.util.Lists;
import com.github.argon.sos.mod.sdk.util.MathUtil;
import com.github.argon.sos.moreoptions.config.domain.Range;
import com.github.argon.sos.moreoptions.ui.UiMapper;
import init.sprite.SPRITES;
import init.sprite.UI.Icon;
import init.sprite.UI.UI;
import init.text.D;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;
import snake2d.MButt;
import snake2d.SPRITE_RENDERER;
import snake2d.util.color.COLOR;
import snake2d.util.datatypes.COORDINATE;
import snake2d.util.datatypes.Coo;
import snake2d.util.gui.GUI_BOX;
import snake2d.util.gui.GuiSection;
import snake2d.util.misc.CLAMP;
import snake2d.util.misc.STRING_RECIEVER;
import snake2d.util.sprite.text.Str;
import util.colors.GCOLOR;
import util.data.INT;
import util.gui.misc.GBox;
import util.gui.misc.GButt;
import util.gui.misc.GStat;
import util.gui.misc.GText;
import util.gui.slider.GSliderInt;
import util.info.GFORMAT;
import view.main.VIEW;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;


/**
 * Uses mostly code from {@link GSliderInt} and adds handling for negative values.
 */
public class Slider extends GuiSection implements Valuable<Integer>, Resettable {

    private static final I18n i18n = I18n.get(Slider.class);

    private static final int midWidth = 8;
    private final CharSequence setAmount;
    private final CharSequence setAmountD;

    private final INT.INTE in;

    private final double initialDValue;

    private final int initialValue;

    private final int step;

    @Getter
    private final ValueDisplay valueDisplay;
    private final int resolution;
    private final int resolutionMulti;

    /**
     * Makes slider unusable and greyed out
     */
    @Getter
    @Setter
    private boolean enabled = true;

    /**
     * Will lock the possibility to move the slider with the mouse wheel
     */
    @Setter
    private boolean lockScroll = false;

    private final TreeMap<Integer, COLOR> thresholds;

    private final List<Integer> allowedValues;

    static {
        D.ts(GSliderInt.class);
    }

    @Setter
    @Nullable
    @Accessors(fluent = true, chain = false)
    private Supplier<Integer> valueSupplier;

    @Setter
    @Accessors(fluent = true, chain = false)
    private Consumer<Integer> valueConsumer = o -> {};

    @Setter
    @Accessors(fluent = true, chain = false)
    private Action<Integer> valueChangeAction = o -> {};

    @Setter
    @Nullable
    @Accessors(fluent = true, chain = false)
    private Supplier<Coo> mouseCooSupplier = () -> new Coo(VIEW.mouse().x(), VIEW.mouse().y());

    @Builder
    public Slider(
        int min,
        int max,
        int value,
        int width,
        int height,
        int step,
        int resolution,
        boolean controls,
        boolean lockScroll,
        boolean input,
        ValueDisplay valueDisplay,
        Map<Integer, COLOR> thresholds,
        List<Integer> allowedValues
    ){
        this.allowedValues = (allowedValues != null) ? allowedValues : Lists.of();
        this.resolution = (resolution > 0) ? resolution : 1;
        this.resolutionMulti = MathUtil.precisionMulti(this.resolution);
        min = resolutionMulti * min;
        max = resolutionMulti * max;

        this.in = new INT.IntImp(min, max);
        this.in.set(value);

        this.initialValue = in.get();
        this.initialDValue = in.getD();
        this.valueDisplay = valueDisplay;

        this.setAmount = i18n.t("Slider.amount.set");
        this.setAmountD = i18n.t("Slider.amount.range.set");

        int sliderWidth = (Math.abs(min) + Math.abs(max)) * 2;
        int sliderHeight = 24;

        if (width > 0) {
            sliderWidth = width;
        }

        if (height > 0) {
            sliderHeight = height;
        }

        this.step = (step == 0) ? 1 : step;

        if (thresholds != null) {
            // sort by key
            this.thresholds = thresholds.entrySet()
                .stream().sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                    (oldValue, newValue) -> oldValue, TreeMap::new));
        } else {
            this.thresholds = new TreeMap<>();
        }

        setLockScroll(lockScroll);

        if (controls) {
            sliderWidth -= (Icon.S+2)*3;
        }

        sliderWidth -= 4;

        if (sliderWidth < 0)
            sliderWidth = 0;

        if (controls) {
            addRightC(0, new GButt.ButtPanel(SPRITES.icons().s.minifier) {
                private double clickSpeed;

                @Override
                protected void clickA() {
                    in.inc(-1 * step);
                }

                @Override
                protected void render(SPRITE_RENDERER r, float ds, boolean isActive, boolean isSelected,
                                      boolean isHovered) {
                    if (isHovered &&  MButt.LEFT.isDown()) {
                        clickSpeed += ds;

                        if (clickSpeed > 10)
                            clickSpeed = 10;
                        List<Integer> allowedValues = Slider.this.allowedValues;
                        if (allowedValues.isEmpty()) {
                            in.inc(-(int)(clickSpeed * step));
                        } else {
                            int i = allowedValues.indexOf(in.get());
                            int index;

                            if (i < 0) {
                                int currentValue = in.get();
                                int nearest = MathUtil.nearest(currentValue, allowedValues);
                                i = allowedValues.indexOf(nearest);
                            }

                            if (i >= 0) {
                                index = i - Math.max(1, (int) (clickSpeed / 2));
                                if (index < 0) {
                                    index = 0;
                                }

                                Integer value = allowedValues.get(index);
                                setValue(value);
                            }
                        }
                    }else {
                        clickSpeed = 0;
                    }
                    super.render(r, ds, isActive, isSelected, isHovered);
                }
            });

        }

        addRightC(4, new Mid(sliderWidth, sliderHeight));

        if (controls) {
            addRightC(4, new GButt.ButtPanel(SPRITES.icons().s.magnifier) {
                private double clickSpeed;

                @Override
                protected void clickA() {
                    in.inc(1 * step);
                }

                @Override
                protected void render(SPRITE_RENDERER r, float ds, boolean isActive, boolean isSelected,
                                      boolean isHovered) {
                    if (isHovered &&  MButt.LEFT.isDown()) {
                        clickSpeed += ds*2;
                        if (clickSpeed > 10)
                            clickSpeed = 10;
                        List<Integer> allowedValues = Slider.this.allowedValues;
                        if (allowedValues.isEmpty()) {
                            in.inc((int)clickSpeed * step);
                        } else {
                            int i = allowedValues.indexOf(in.get());
                            int index;

                            if (i < 0) {
                                int currentValue = in.get();
                                int nearest = MathUtil.nearest(currentValue, allowedValues);
                                i = allowedValues.indexOf(nearest);
                            }

                            if (i >= 0) {
                                index = i + Math.max(1, (int) (clickSpeed / 2));
                                if (index > allowedValues.size() - 1) {
                                    index = allowedValues.size() - 1;
                                }

                                Integer value = allowedValues.get(index);
                                setValue(value);
                            }
                        }

                    }else {
                        clickSpeed = 0;
                    }
                    super.render(r, ds, isActive, isSelected, isHovered);
                }
            });

            if (input) {
                addRightC(0, new GButt.ButtPanel(SPRITES.icons().s.pluses) {

                    @Override
                    protected void clickA() {
                        Str.TMP.clear().add(setAmountD).insert(0, in.min()).insert(1, in.max());
                        VIEW.inters().input.requestInput(rec, Str.TMP);
                    }
                }.hoverInfoSet(setAmount));
            }
        }

        if (valueDisplay != ValueDisplay.NONE) {
            GuiSection section = new GuiSection();
            int maxValue = Math.max(Math.abs(in.min()), Math.abs(in.max()));
            String maxString = Integer.toString(maxValue);
            if (in.min() < 0) {
                maxString = "-" + maxString;
            }

            GStat valueText;
            if (valueDisplay == ValueDisplay.PERCENTAGE) {
                maxString = maxString + "%";

                if (this.resolution > 1) {
                    valueText = new GStat() {
                        @Override
                        public void update(GText text) {
                            GFORMAT.percBig(text, (in.get() / 100d) / Slider.this.resolutionMulti);
                        }
                    };
                } else {
                    valueText = new GStat() {
                        @Override
                        public void update(GText text) {
                            TextFormatUtil.percentage(text, in.get() / 100d / Slider.this.resolutionMulti);
                        }
                    };
                }


            } else {
                valueText = new GStat() {
                    @Override
                    public void update(GText text) {
                        GFORMAT.iBig(text, in.get());
                    }
                };
            }

            int valueWidth = UI.FONT().S.width(maxString, 0, maxString.length() - 1, 1.4);

            section.body().setWidth(valueWidth);
            body().setWidth(valueWidth);
            section.body().setHeight(body().height());

            section.addCentredX(valueText, 0);
            addRight(5, section);
        }
    }

    @Override
    public Integer getValue() {
        if (valueSupplier != null) {
            return valueSupplier.get();
        }

        return in.get();
    }

    public Double getValueD() {
        return (double) in.get() / 100 / resolutionMulti;
    }

    @Override
    public void setValue(Integer value) {
        valueConsumer.accept(value);

        if (in.get() != value) {
            valueChangeAction.accept(value);
        }

        in.set(value);
    }

    public void setValueD(Double value) {
        setValue((int) (value * resolutionMulti * 100));
    }

    private final STRING_RECIEVER rec = new STRING_RECIEVER() {

        @Override
        public void acceptString(CharSequence string) {
            String s = ""+string;
            try {
                int value = Integer.parseInt(s);
                value = CLAMP.i(value, in.min(), in.max());
                int rest = value % step;

                if (rest != 0) {
                    value = value - rest;
                }
                List<Integer> allowedValues = Slider.this.allowedValues;
                if (!allowedValues.isEmpty()) {
                    value = MathUtil.nearest(value, allowedValues);
                }

                setValue(value);
            }catch(Exception e) {

            }

        }
    };

    public int getMax() {
        return in.max();
    }

    public int getMin() {
        return in.min();
    }

    @Override
    public void render(SPRITE_RENDERER r, float ds) {
        activeSet(enabled && in.max() > 0);
        super.render(r, ds);
    }


    @Override
    public boolean hover(COORDINATE mCoo) {
        return super.hover(mCoo);
    }

    @Override
    public void hoverInfoGet(GUI_BOX text) {
        GBox b = (GBox) text;
        b.add(GFORMAT.i(b.text(), in.get()));
    }

    protected void renderPositiveMidColor(SPRITE_RENDERER r, int x1, int width, int y1, int y2) {
        for (Map.Entry<Integer, COLOR> entry : thresholds.descendingMap().entrySet()) {
            Integer threshold = entry.getKey();
            COLOR color = entry.getValue();

            if (threshold < getValue()) {
                color.render(r, x1, x1+width, y1, y2);
                return;
            }
        }

        COLOR.WHITE50.render(r, x1, x1+width, y1, y2);
    }

    protected void renderNegativeMidColor(SPRITE_RENDERER r, int x1, int width, int y1, int y2) {
        for (Map.Entry<Integer, COLOR> entry : thresholds.entrySet()) {
            Integer threshold = entry.getKey();
            COLOR color = entry.getValue();

            if (threshold > getValue()) {
                color.shade(0.66d).render(r, x1, x1+width, y1, y2);
                return;
            }
        }

        COLOR.WHITE35.render(r, x1, x1+width, y1, y2);
    }

    public void reset() {
        in.setD(initialDValue);
        setValue(initialValue);
    }

    private class Mid extends ClickableAbs {

        private boolean clicked = false;

        Mid(int width, int height){
            super(width, height-4);
        }

        @Override
        protected void clickA() {
            if (!enabled) {
                return;
            }

            clicked = true;
            setFromClickPos();
        }

        private void setFromClickPos() {
            double clickPos = getClickPos();
            double value;

            if (in.min() < 0) {
                value = CLAMP.d(clickPos, -1, 1);
            } else {
                value = CLAMP.d(clickPos, 0, 1);
            }

            int intValue = (int) Math.ceil(value * getMax());
            int rest = intValue % step;

            if (rest != 0) {
                intValue = intValue - rest;
            }

            List<Integer> allowedValues = Slider.this.allowedValues;
            if (!allowedValues.isEmpty()) {
                intValue = MathUtil.nearest(intValue, allowedValues);
            }
            setValue(intValue);
        }

        private double getClickPos() {
            int barStartX = body().x1();
            int clickX = mouseCooSupplier.get().x();
            int clickPos = clickX - barStartX;

            if (in.min() < 0) {
                int barLength = body().width() / 2;
                int barCenterX = barStartX + barLength;
                int barRange = clickX - barCenterX;

                return barRange / (double) (barLength);
            } else {
                return clickPos / (double) body().width();
            }
        }

        @Override
        protected void render(SPRITE_RENDERER r, float ds, boolean isActive, boolean isSelected, boolean isHovered) {
            clicked &= MButt.LEFT.isDown();
            int barFullWidth = body().width();
            int barStartX = body().x1();
            int barCenterX = barStartX + barFullWidth / 2;

            if (clicked) {
                setFromClickPos();
            }

            GCOLOR.UI().border().render(r, body, 2);
            GCOLOR.UI().bg(isActive, isSelected, isHovered).render(r, body, 1);

            int slideCursorPos;

            if (in.min() >= 0) {
                slideCursorPos = (int) (body.x1()+ barFullWidth * in.getD());
                renderWithPositiveValues(r);
            } else {
                renderWithNegativeValues(r);
                int barWidth = body().width() / 2;
                int barColoredLength = (int) (in.getD() * barWidth);

                if (in.get() < 0) {
                    slideCursorPos = body().x1() + barWidth - barColoredLength;
                } else {
                    slideCursorPos = body().x1() + barWidth + barColoredLength;
                }

                // splitting bar in the middle
                COLOR.WHITE120.render(r, barCenterX-1, barCenterX+1, body().y1()-1, body().y2()+1);
            }

            // draggable cursor
            if (!isHovered) {
                COLOR.WHITE85.render(r, slideCursorPos-1, slideCursorPos+1, body().y1()+1, body().y2()-1);
            } else {
                GCOLOR.UI().border().render(r, slideCursorPos-midWidth/2, slideCursorPos+midWidth/2, body().y1(), body().y2());
                COLOR c = isHovered || clicked ? GCOLOR.T().H1 : GCOLOR.T().H2;
                c.render(r, slideCursorPos-midWidth/2+1, slideCursorPos+midWidth/2-1, body().y1()+1, body().y2()-1);
                COLOR.BLACK.render(r, slideCursorPos-1, slideCursorPos+2, body().y1()+2, body().y2()-2);
            }
        }

        private void renderWithPositiveValues(SPRITE_RENDERER r) {
            int barFullWidth = body().width();
            int barColoredLength = (int) (in.getD() * barFullWidth);
            int barColoredEndPos = body().x1() + barColoredLength;
            int barColoredStartPos = body().x1();
            int barColoredWidth = barColoredEndPos - barColoredStartPos;

            // renders the white bar
            renderPositiveMidColor(r, barColoredStartPos, barColoredWidth, body().y1(), body().y2());
        }

        private void renderWithNegativeValues(SPRITE_RENDERER r) {
            int barWidth = body().width() / 2;
            int barColoredLength = (int) (in.getD() * barWidth);

            if (in.get() < 0) {
                barColoredLength = - barColoredLength;
            }

            int barColoredEndPos = body().x1() + barWidth;
            int barColoredStartPos = body().x1() + barWidth + barColoredLength;
            int barColoredWidth = barColoredEndPos - barColoredStartPos;

            if (in.get() < 0) {
                // renders negative bar part
                renderNegativeMidColor(r, barColoredStartPos, barColoredWidth, body().y1(), body().y2());
            } else {
                // renders positive bar part
                renderPositiveMidColor(r, barColoredStartPos, barColoredWidth, body().y1(), body().y2());
            }
        }
        @Override
        public boolean hover(COORDINATE mCoo) {
            boolean hover = super.hover(mCoo);

            // move slider when mouse wheel scrolls
            // this can be a bad idea when the slider is in a scrollable area
            if (hover && !lockScroll) {
                double d = MButt.clearWheelSpin();
                if (d < 0)
                    in.inc(-1);
                else if (d > 0)
                    in.inc(1);
                return true;
            }

            return hover;
        }
    }

    public enum ValueDisplay {
        NONE,
        ABSOLUTE,
        PERCENTAGE
    }

    public static class SliderBuilder {

        private Map<Integer, COLOR> thresholds = new TreeMap<>();

        public SliderBuilder valueD(Double value, int precision) {
            int precisionMulti = MathUtil.precisionMulti(precision);

            value((int) (precisionMulti * value * 100));
            resolution(precision);

            return this;
        }

        public static SliderBuilder fromRange(Range range) {
            return Slider.builder()
                .min(range.getMin())
                .max(range.getMax())
                .value(range.getValue())
                .valueDisplay(UiMapper.toValueDisplay(range.getDisplayMode()));
        }

        public SliderBuilder thresholds(Map<Integer, COLOR> thresholds) {
            this.thresholds = thresholds;
            return this;
        }

        public SliderBuilder threshold(Integer percent, COLOR color) {
            thresholds.put(percent, color);
            return this;
        }
    }
}
