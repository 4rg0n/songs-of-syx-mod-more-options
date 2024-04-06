package com.github.argon.sos.moreoptions.game.ui;

import menu.Ui;
import org.jetbrains.annotations.Nullable;
import snake2d.SPRITE_RENDERER;
import snake2d.util.color.COLOR;
import snake2d.util.color.ColorImp;
import snake2d.util.gui.GuiSection;
import util.data.INT;

public class ColorPicker extends GuiSection {
    private final INT.INTE red;
    private final INT.INTE green;
    private final INT.INTE blue;
    private final ColorBox colorBox;

    public final static COLOR DUMMY_COLOR = new ColorImp(255, 0, 255);

    public ColorPicker(Integer[] colors) {
        this(colors[0], colors[1], colors[2]);
    }

    public ColorPicker(Integer red, Integer green, Integer blue) {
        this(red, green, blue, false);
    }

    public ColorPicker(Integer red, Integer green, Integer blue, boolean horizontal) {
        this.red = new INT.INTE.IntImp(0, 255);
        this.green = new INT.INTE.IntImp(0, 255);
        this.blue = new INT.INTE.IntImp(0, 255);

        setRed(red);
        setGreen(green);
        setBlue(blue);

        GInputInt redInput = new GInputInt(this.red, true, true, Ui.MOUSE_COO_SUPPLIER, 40);
        GInputInt greenInput = new GInputInt(this.green, true, true, Ui.MOUSE_COO_SUPPLIER, 40);
        GInputInt blueInput = new GInputInt(this.blue, true, true, Ui.MOUSE_COO_SUPPLIER, 40);

        GuiSection colorInputs = new GuiSection();

        if (horizontal) {
            colorInputs.addRightC(0, redInput);
            colorInputs.addRightC(5, greenInput);
            colorInputs.addRightC(5, blueInput);
            this.colorBox = new ColorBox(blueInput.body().height(), blueInput.body().height(), color());
        } else {
            colorInputs.addDownC(0, redInput);
            colorInputs.addDownC(5, greenInput);
            colorInputs.addDownC(5, blueInput);
            this.colorBox = new ColorBox(colorInputs.body().height(), colorInputs.body().height(), color());
        }

        addRightC(0, colorBox);
        addRightC(10, colorInputs);
    }

    @Override
    public void render(SPRITE_RENDERER r, float ds) {
        colorBox.setColor(color());
        super.render(r, ds);
    }

    public void setRed(int value) {
        red.set(value);
    }

    public void setGreen(int value) {
        green.set(value);
    }

    public void setBlue(int value) {
        blue.set(value);
    }

    public COLOR color() {
        try {
            int red = getRed();
            int green = getGreen();
            int blue = getBlue();

            return new ColorImp(red, green, blue);
        } catch (Exception e) {
            return DUMMY_COLOR;
        }
    }

    public int getRed() {
        return this.red.get();
    }

    public int getGreen() {
        return this.green.get();
    }

    public int getBlue() {
        return this.blue.get();
    }

    public String to(String delimiter) {
        return getRed() + delimiter + getGreen() + delimiter + getBlue();
    }

    public void set(String colorString, String delimiter) {
        Integer[] colors = ofParts(colorString, delimiter);
        set(colors);
    }

    public void set(Integer[] colors) {
        if (colors.length < 3) {
            return;
        }

        set(colors[0], colors[1], colors[2]);
    }

    public void set(@Nullable Integer red, @Nullable Integer green, @Nullable Integer blue) {
        if (red == null) red = 255;
        if (green == null) green = 0;
        if (blue == null) blue = 255;

        setRed(red);
        setGreen(green);
        setBlue(blue);
    }

    public static ColorPicker ofString(String colorString, String delimiter) {
        Integer[] ints = ofParts(colorString, delimiter);
        return new ColorPicker(ints[0], ints[1], ints[2]);
    }

    private static Integer[] ofParts(String colorString, String delimiter) {
        String[] parts = colorString.split(delimiter);

        Integer[] ints = new Integer[3];
        if (parts.length != 3) {
            return ints;
        }

        try {
            ints[0] = Integer.parseInt(parts[0]);
            ints[1] = Integer.parseInt(parts[1]);
            ints[2]= Integer.parseInt(parts[2]);

            return ints;
        } catch (Exception e) {
            return new Integer[3];
        }
    }
}
