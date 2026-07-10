package com.github.argon.sos.mod.sdk.ui.slider;

import com.github.argon.sos.mod.sdk.ModSdkModule;
import com.github.argon.sos.mod.sdk.data.IntegerValue;
import com.github.argon.sos.mod.sdk.data.domain.Range;
import com.github.argon.sos.mod.sdk.game.action.Action;
import com.github.argon.sos.mod.sdk.game.action.Resettable;
import com.github.argon.sos.mod.sdk.game.action.Valuable;
import com.github.argon.sos.mod.sdk.game.action.VoidAction;
import com.github.argon.sos.mod.sdk.game.util.TextFormatUtil;
import com.github.argon.sos.mod.sdk.game.util.UiMapper;
import com.github.argon.sos.mod.sdk.i18n.I18nTranslator;
import com.github.argon.sos.mod.sdk.util.MathUtil;
import init.sprite.SPRITES;
import init.sprite.UI.Icon;
import init.sprite.UI.UI;
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
import util.gui.misc.GBox;
import util.gui.misc.GButt;
import util.gui.misc.GStat;
import util.gui.misc.GText;
import util.gui.slider.GSliderInt;
import util.info.GFORMAT;
import util.text.D;
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

    private static final I18nTranslator i18n = ModSdkModule.i18n().get(Slider.class);

    private static final int midWidth = 8;
    private final CharSequence setAmount;
    private final CharSequence setAmountD;

    @Getter
    private final IntegerValue inputValue;
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
    private Action<Slider> inputClickAction;

    @Setter
    @Accessors(fluent = true, chain = false)
    private VoidAction resetAction = () -> {};

    @Setter
    @Nullable
    @Accessors(fluent = true, chain = false)
    private Supplier<Coo> mouseCooSupplier = () -> new Coo(VIEW.mouse().x(), VIEW.mouse().y());

    /**
     * Creates a new {@link Slider}.
     *
     * @param min value. Can be negative.
     * @param max value
     * @param value current value
     * @param width of the slider
     * @param height of the slider
     * @param step in which increments the slider slides
     * @param resolution used for calculating double values. A resolution of 100 would result in 0.01 when the slider has a value of 1.
     * @param showControls whether to show + and - buttons
     * @param lockScroll this will prevent the slider from changing values if you scroll the mouse wheel while hovering it
     * @param showInput whether to show a button for inputting the value manually
     * @param valueDisplay how the value should be displayed next to the slider
     * @param thresholds an optional map of values and colors, which will color the slider bar if reached
     * @param allowedValues an optional list of allowed values to set
     */
    @Builder
    public Slider(
        int min,
        int max,
        int value,
        int width,
        int height,
        int step,
        int resolution,
        boolean showControls,
        boolean lockScroll,
        boolean showInput,
        ValueDisplay valueDisplay,
        Map<Integer, COLOR> thresholds,
        List<Integer> allowedValues
    ){
        this.allowedValues = (allowedValues != null) ? allowedValues : List.of();
        this.resolution = (resolution > 0) ? resolution : 1;
        this.resolutionMulti = MathUtil.precisionMulti(this.resolution);
        min = resolutionMulti * min;
        max = resolutionMulti * max;

        this.inputValue = new IntegerValue(min, max);
        this.inputValue.setValue(value);

        this.initialValue = inputValue.getValue();
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

        if (showControls) {
            sliderWidth -= (Icon.S+2)*3;
        }

        sliderWidth -= 4;

        if (sliderWidth < 0)
            sliderWidth = 0;

        if (showControls) {
            addRightC(0, new GButt.ButtPanel(SPRITES.icons().s.minifier) {
                private double clickSpeed;

                @Override
                protected void clickA() {
                    inputValue.inc(-1 * Slider.this.step);
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
                            inputValue.inc(-(int)(clickSpeed * Slider.this.step));
                        } else {
                            int i = allowedValues.indexOf(inputValue.getValue());
                            int index;

                            if (i < 0) {
                                int currentValue = inputValue.getValue();
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

        addRightC(4, new Bar(sliderWidth, sliderHeight));

        if (showControls) {
            addRightC(4, new GButt.ButtPanel(SPRITES.icons().s.magnifier) {
                private double clickSpeed;

                @Override
                protected void clickA() {
                    inputValue.inc(Slider.this.step);
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
                            inputValue.inc((int)clickSpeed * Slider.this.step);
                        } else {
                            int i = allowedValues.indexOf(inputValue.getValue());
                            int index;

                            if (i < 0) {
                                int currentValue = inputValue.getValue();
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

            if (showInput) {
                addRightC(0, new GButt.ButtPanel(SPRITES.icons().s.pluses) {

                    @Override
                    protected void clickA() {
                        if (inputClickAction != null) {
                            inputClickAction.accept(Slider.this);
                        } else {
                            Str.TMP.clear().add(setAmountD).insert(0, inputValue.getMin()).insert(1, inputValue.getMax());
                            VIEW.inters().input.requestInput(rec, Str.TMP);
                        }
                    }
                }.hoverInfoSet(setAmount));
            }
        }

        if (valueDisplay != ValueDisplay.NONE) {
            GuiSection section = new GuiSection();
            int maxValue = Math.max(Math.abs(inputValue.getMin()), Math.abs(inputValue.getMax()));
            String maxString = Integer.toString(maxValue);
            if (inputValue.getMin() < 0) {
                maxString = "-" + maxString;
            }

            GStat valueText;
            if (valueDisplay == ValueDisplay.PERCENTAGE) {
                maxString = maxString + "%";

                if (this.resolution > 1) {
                    valueText = new GStat() {
                        @Override
                        public void update(GText text) {
                            GFORMAT.percBig(text, (inputValue.getValue() / 100d) / Slider.this.resolutionMulti);
                        }
                    };
                } else {
                    valueText = new GStat() {
                        @Override
                        public void update(GText text) {
                            TextFormatUtil.percentage(text, inputValue.getValue() / 100d / Slider.this.resolutionMulti);
                        }
                    };
                }


            } else {
                valueText = new GStat() {
                    @Override
                    public void update(GText text) {
                        GFORMAT.iBig(text, inputValue.getValue());
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

    /**
     * Returns the current value of the slider.
     *
     * @return current value of the slider
     */
    @Override
    public Integer getValue() {
        if (valueSupplier != null) {
            return valueSupplier.get();
        }

        return inputValue.getValue();
    }

    /**
     * Returns the current value as a {@link Double}.
     * If the value is 1, the double value would be 0.01.
     * The returned value will also calculate the {@link Slider#resolutionMulti} into it.
     *
     * @return current value as double.
     */
    public Double getValueD() {
        return (double) inputValue.getValue() / 100 / resolutionMulti;
    }

    /**
     * Sets a value for the slider.
     * Will call the {@link Slider#valueConsumer}.
     * Will call the {@link Slider#valueChangeAction} if the value actually changed.
     *
     * @param value to set
     */
    @Override
    public void setValue(Integer value) {
        valueConsumer.accept(value);

        if (inputValue.getValue() != value) {
            valueChangeAction.accept(value);
        }

        inputValue.setValue(value);
    }

    /**
     * Will set the value from a {@link Double}.
     * If the value to be set is 0.01, it will be 1.
     *
     * @param value to set
     */
    public void setValueD(Double value) {
        setValue((int) (value * resolutionMulti * 100));
    }

    private final STRING_RECIEVER rec = new STRING_RECIEVER() {

        @Override
        public void acceptString(CharSequence string) {
            String s = ""+string;
            try {
                int value = Integer.parseInt(s);
                value = CLAMP.i(value, inputValue.getMin(), inputValue.getMax());
                int rest = value % step;

                if (rest != 0) {
                    value = value - rest;
                }
                List<Integer> allowedValues = Slider.this.allowedValues;
                if (!allowedValues.isEmpty()) {
                    value = MathUtil.nearest(value, allowedValues);
                }

                setValue(value);
            } catch(Exception e) {
                // ignored
            }

        }
    };

    /**
     * Returns the maximum allowed value for the slider.
     *
     * @return the maximum allowed value for the slider
     */
    public int getMax() {
        return inputValue.getMax();
    }

    /**
     * Returns the minimum allowed value for the slider.
     *
     * @return minimum allowed value for the slider
     */
    public int getMin() {
        return inputValue.getMin();
    }

    /**
     * Executed when the slider is rendered.
     *
     * @param renderer to use
     * @param deltaSeconds since last render loop
     */
    @Override
    public void render(SPRITE_RENDERER renderer, float deltaSeconds) {
        activeSet(enabled && inputValue.getMax() > 0);
        super.render(renderer, deltaSeconds);
    }

    /**
     * Executed when the slider is hovered.
     *
     * @param mouseCoordinates coordinates of the mouse pointer
     * @return whether hovering is allowed
     */
    @Override
    public boolean hover(COORDINATE mouseCoordinates) {
        return super.hover(mouseCoordinates);
    }

    /**
     * Will fill the given {@link GUI_BOX} with text when hovered.
     *
     * @param text to fill your text into
     */
    @Override
    public void hoverInfoGet(GUI_BOX text) {
        GBox b = (GBox) text;
        b.add(GFORMAT.i(b.text(), inputValue.getValue()));
    }

    /**
     * Will render the positive value part of the slider bar.
     *
     * @param renderer to use
     * @param x1 position of the bar
     * @param width of the positive bar
     * @param y1 position of the bar
     * @param y2 position of the bar
     */
    protected void renderPositiveMidColor(SPRITE_RENDERER renderer, int x1, int width, int y1, int y2) {
        for (Map.Entry<Integer, COLOR> entry : thresholds.descendingMap().entrySet()) {
            Integer threshold = entry.getKey();
            COLOR color = entry.getValue();

            if (threshold < getValue()) {
                color.render(renderer, x1, x1+width, y1, y2);
                return;
            }
        }

        COLOR.WHITE50.render(renderer, x1, x1+width, y1, y2);
    }

    /**
     * Will render the negative value part of the slider bar.
     *
     * @param renderer to use
     * @param x1 position of the bar
     * @param width of the negative bar
     * @param y1 position of the bar
     * @param y2 position of the bar
     */
    protected void renderNegativeMidColor(SPRITE_RENDERER renderer, int x1, int width, int y1, int y2) {
        for (Map.Entry<Integer, COLOR> entry : thresholds.entrySet()) {
            Integer threshold = entry.getKey();
            COLOR color = entry.getValue();

            if (threshold > getValue()) {
                color.shade(0.66d).render(renderer, x1, x1+width, y1, y2);
                return;
            }
        }

        COLOR.WHITE35.render(renderer, x1, x1+width, y1, y2);
    }

    /**
     * Will reset the slider to its initial value.
     * Executes the {@link Slider#resetAction}.
     */
    @Override
    public void reset() {
        resetAction.accept();
        setValue(initialValue);
    }

    /**
     * The bar used for the slider.
     */
    private class Bar extends ClickableAbs {

        private boolean clicked = false;

        Bar(int width, int height){
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

            if (inputValue.getMin() < 0) {
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

            if (inputValue.getMin() < 0) {
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

            if (inputValue.getMin() >= 0) {
                slideCursorPos = (int) (body.x1()+ barFullWidth * inputValue.getValueDistance());
                renderWithPositiveValues(r);
            } else {
                renderWithNegativeValues(r);
                int barWidth = body().width() / 2;
                int barColoredLength = (int) (inputValue.getValueDistance() * barWidth);

                if (inputValue.getValue() < 0) {
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
            int barColoredLength = (int) (inputValue.getValueDistance() * barFullWidth);
            int barColoredEndPos = body().x1() + barColoredLength;
            int barColoredStartPos = body().x1();
            int barColoredWidth = barColoredEndPos - barColoredStartPos;

            // renders the white bar
            renderPositiveMidColor(r, barColoredStartPos, barColoredWidth, body().y1(), body().y2());
        }

        private void renderWithNegativeValues(SPRITE_RENDERER r) {
            int barWidth = body().width() / 2;
            int barColoredLength = (int) (inputValue.getValueDistance() * barWidth);

            if (inputValue.getValue() < 0) {
                barColoredLength = - barColoredLength;
            }

            int barColoredEndPos = body().x1() + barWidth;
            int barColoredStartPos = body().x1() + barWidth + barColoredLength;
            int barColoredWidth = barColoredEndPos - barColoredStartPos;

            if (inputValue.getValue() < 0) {
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
                    inputValue.inc(-step);
                else if (d > 0)
                    inputValue.inc(step);
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
