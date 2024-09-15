# Ui

More complex and simple ui elements. The [UiShowroom](UiShowroom.java) contains some examples on how to use them.
The base of all ui elements in the game is the `RENDEROBJ`. This is a game interface class, which describes the basic api of all rendered things.

```java
package snake2d.util.gui.renderable;

public interface RENDEROBJ extends BODY_HOLDERE {

    void render(SPRITE_RENDERER r, float ds);

    boolean visableIs();

    RENDEROBJ visableSet(boolean yes);
}
```

More simple ui elements use this as their base. More complex ones are grouped into a `GuiSection` or [Section](Section.java) class container though.
You can think of it as some kind of `<div>` container in HTML. It can hold and arrange multiple other ui elements. 
Let's build a simple dialog where you can enter a text and show a notification with it.

```java
public class NotificationDialog extends GuiSection {
    private final Notificator notificator;
    private final StringInputSprite input;

    public NotificationDialog(Notificator notificator) {
        this.notificator = notificator;
        
        // text next to the inout field
        Label label = Label.builder()
            .name("Enter a text")
            .build();
        
        // the actual text input
        this.input = new StringInputSprite(16, UI.FONT().M).placeHolder("Text");
        Input textInput = new Input(input);
        
        // the button to show the notification
        Button notifyButton = new Button("Notify");
        notifyButton.clickActionSet(this::clickNotify);

        // we want to arrange our elements next to each other
        // we will use an extra container for this
        GuiSection inputContainer = new GuiSection();
        
        // adding everything "right" next from each other into the container
        inputContainer.addRightC(0, label);
        inputContainer.addRightC(10, textInput);
        inputContainer.addRightC(10, notifyButton);
        
        // add the container to this GuiSection
        add(inputContainer);
    }

    private void clickNotify() {
        notificator.notify(input.text().toString());
    }
}
```

Now that we have our dialog, we need a way to display it. So we build a button and add it into the games top navigation.

```java
public class YourModScript extends AbstractModSdkScript {
    @Override
    public void initSettlementUiPresent() {
        super.initSettlementUiPresent();
        
        // create a button to show the dialog
        Button notifyButton = new Button("Notify");
        notifyButton.clickActionSet(() -> 
            // create a window with the dialog and show() it on button click
            // the window is bound to the game render loop and will display anything in it
            // a GuiSection or any other RENDEROBJ alone can not be displayed without the renderer
            new Window<NotificationDialog>("Notification", new NotificationDialog(
                ModSdkModule.notificator()
            )).show());
        
        // inject the button into the game top navigation panels
        ModSdkModule.gameApis().ui().injectIntoUITopPanels(notifyButton);
    }
}
```

## Externalize logic or not

In the `NotificationDialog` we added the logic of the button directly into the dialog class itself. 
This works well for self-contained ui components, but if you want to reuse this dialog and maybe do something else on a button click, you would have no option to do so.
A `Controller` can come in handy here. You can have different controllers for the same ui component. And you could also provide some commonly used components with an [AbstractController](controller/AbstractController.java).
This `Controller` thingy is a commonly used pattern for controlling UI elements. A more powerful model would be an [MVVM](https://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93viewmodel), this would require some more effort though ;P

```java
// modify the dialog and expose the stuff we need access to
public class NotificationDialog extends GuiSection {
    private final Notificator notificator;
    @Getter // add a getter to read the input
    private final StringInputSprite input;
    @Getter // add the button as member variable and a getter to read it
    private final Button notifyButton;
}

// the actual controller
public class NotificationController extends AbstractController<NotificationDialog> {
    public NotificationController(Window<NotificationDialog> notificationDialogWindow) {
        super(notificationDialogWindow.getSection());

        // register action when button is clicked
        getElement().getNotifyButton().clickActionSet(this::clickNotify);
    }
    
    // if you make this "public", you can access the functionality from other controllers too
    public void clickNotify() {
        // this was in the NotificationDialog class itself
        getNotificator().notify(getElement().getInput().text().toString());
    }
}
```

Now we have to modify the creation of the window a lil bit too.

```java
public class YourModScript extends AbstractModSdkScript {
    @Override
    public void initSettlementUiPresent() {
        super.initSettlementUiPresent();

        // create a button to show the dialog
        Button notifyButton = new Button("Notify");
        notifyButton.clickActionSet(() -> {
            Window<NotificationDialog> notificationDialogWindow = new Window<NotificationDialog>("Notify", new NotificationDialog());
            // add the wanted functionality to the dialog window via a controller
            new NotificationController(notificationDialogWindow, ModSdkModule.notificator());
            notificationDialogWindow.show();
        });

        // inject the button into the game top navigation panels
        ModSdkModule.gameApis().ui().injectIntoUITopPanels(notifyButton);
    }
}
```

All [AbstractController](controller/AbstractController.java)s will be added into `ModSdkModule.controllers()` and can be accessed through it.
You can also access any other controller from inside an `AbstractController` via `getControllers().get(YourOtherController.class);`

## Elements

### [Button](Button.java)

A `Button` is one of the simplest ui elements.

```java
public class YourUiElement extends GuiSection {
    public YourUiElement() {
        // with a name only
        addDownC(0, new Button("Name"));
        // with a name and a description tooltip when hovered
        addDownC(0, new Button("Name", "Description"));
        // with an icon as sprite
        addDownC(0, new Button(UI.icons().m.cancel));
        // with an icon as sprite and a description tooltip when hovered
        addDownC(0, new Button(UI.icons().m.cancel, "Description"));
        
        // add an action when the button is clicked
        Button button = new Button("Name");
        button.clickActionSet(() -> 
            System.out.println("CLICK!"));
    }
}
```

### [ButtonMenu](ButtonMenu.java)

A `ButtonMenu` is a collection of buttons in form of a menu. It can be displayed horizontally or vertically.

```java
public class YourUiElement extends GuiSection {
    public YourUiElement() {
        add(ButtonMenu.builder()
            .displaySearch(true) // displays a search bar, which will look into the searchTerm of each button
            .search(null) // you can also pass your own search bar here
            .horizontal(false) // vertical menu
            .sameWidth(true) // all buttons will get the width of the widest one
            .buttonColor(COLOR.BLUE50) // custom color for all buttons
            .notHoverable(false) // you can disable whether the buttons shall be hover-able and therefore clickable 
            // these are the actual menu entries as buttons
            .button("menu1", new Button("Menu #1").searchTerm("1"))
            .button("menu2", new Button("Menu #2").searchTerm("2"))
            .button("menu3", new Button("Menu #3").searchTerm("3"))
            .build());
    }
}
```

### [ColorBox](ColorBox.java)

The `ColorBox` is a simple box with a certain color as background.

```java
public class YourUiElement extends GuiSection {
    public YourUiElement() {
        // 100 x 100 green box
        addDownC(0, new ColorBox(100, COLOR.GREEN40));
        // 100 x 50 red box
        addDownC(0, new ColorBox(100, 50, COLOR.RED50));
    }
}
```

### [VerticalLine](VerticalLine.java) and [HorizontalLine](HorizontalLine.java)

The `VerticalLine` and `HorizontalLine` are simple thin lines used to separate ui elements from each other.

```java
public class YourUiElement extends GuiSection {
    public YourUiElement() {
        // 300 px wide line with a 5 px high space above it
        addDownC(0, new HorizontalLine(300, 5));
        
        // 300 px high line with a 5 px wide space next to it
        addRightC(0, new VerticalLine(5, 300));
    }
}
```

### [Checkbox](Checkbox.java)

A `Checkbox` is a toggleable box with an on and off state.

```java
public class YourUiElement extends GuiSection {
    public YourUiElement() {
        // checked
        addDownC(0, new Checkbox(true));
        // unchecked
        addDownC(0, new Checkbox(false));
        
        // add an action when the checkbox is toggled
        Checkbox checkbox = new Checkbox(true);
        checkbox.toggleAction(checked -> 
            System.out.println("Checked? " + checked));
    }
}
```

### [Label](Label.java)

A `Label` is a short text, which is commonly used to describe something. Like "Name:" in front of a text input.

```java
public class YourUiElement extends GuiSection {
    public YourUiElement() {
        Label errorLabel = Label.builder()
            .name("Error")
            .style(Label.Style.ERROR) // makes it reddish
            .font(UI.FONT().H1) // a big label =)
            .build();

        GText errorText = new GText(UI.FONT().M, "Ooooh noooo... it happened!");
        
        addRightC(0, errorLabel);
        addRightC(10, errorText);
    }
}
```

### [Spacer](Spacer.java)

The `Spacer` is an invisible box element you can give a certain dimension. 
This is used mainly for laying out your ui elements.

```java
public class YourUiElement extends GuiSection {
    public YourUiElement() {
        GText text1 = new GText(UI.FONT().H1, "TOP");
        GText text2 = new GText(UI.FONT().H1, "DOWN");

        // 0 px wide, but 100 px high
        Spacer spacer = new Spacer(0, 100);

        addDownC(0, text1);
        addDownC(0, spacer);
        addDownC(0, text2);
    }
}
```

### [Input](Input.java) and [InputInt](InputInt.java)

`Input` and `InputInt` are fields where the player can enter a text or a number into it.

```java
public class YourUiElement extends GuiSection {
    public YourUiElement() {
        // holds the text the user enters
        StringInputSprite stringInputSprite = new StringInputSprite(32, UI.FONT().S)
            .placeHolder("Input text here");
        // the actual text input field shown in the ui
        Input stringInput = new Input(stringInputSprite);

        // holds the number the user enters. In this case it can be from 0 to 255
        INT.INTE.IntImp integerInputValue = new INT.INTE.IntImp(0, 255);
        // the actual integer input field shown in the ui
        InputInt integerInput = new InputInt(integerInputValue);

        addDownC(0, stringInput);
        addDownC(10, integerInput);

        // this would give you the entered text or number; it is empty at this point though
        stringInputSprite.text();
        integerInputValue.get();
    }
}
```

### [Section](Section.java)

A `Section` is a somewhat advanced version of the games `GuiSection`. 
It implements some actions and their functionality such as:

* [Hidable](../game/action/Hideable.java)
* [Renderable](../game/action/Renderable.java) 
* [Showable](../game/action/Showable.java)

```java
public class YourUiElement extends Section {
    public YourUiElement() {
        hideAction(() -> System.out.println("Hiding"));
        renderAction(() -> System.out.println("Rendering"));
        show(() -> System.out.println("Showing"));
    }
}
```

### [Window](Window.java) and [FullWindow](FullWindow.java)

The `Window` and `FullWindow` classes are containers, which display any `GuiSection` in them when shown.

A `Window` is a popup in the game UI. Here an example:
```java
public class YourModScript extends AbstractModSdkScript {
    @Override
    public void initSettlementUiPresent() {
        super.initSettlementUiPresent();

        Button button = new Button("ColorBox");
        ColorBox colorBox = new ColorBox(100, COLOR.RED50);
        button.clickActionSet(() ->
            // inject anything which is an instance of GuiSection into the Window
            // the last "true" will tell the window to be a "Modal", which blacks out the background
            new Window<ColorBox>("Red50", colorBox, true)
                .show());

        ModSdkModule.gameApis().ui().injectIntoUITopPanels(button);
    }
}
```

The `FillWindow` takes up the whole screen and can have an optional [Switcher](Switcher.java) menu.
This menu can be used to switch between different views in the window. 
So it's possible to display a different ui on a button click in the open window.

```java
public class YourModScript extends AbstractModSdkScript {
    @Override
    public void initSettlementUiPresent() {
        super.initSettlementUiPresent();

        Button button = new Button("ColorBoxes");
        // a switcher menu containing the buttons; the key e.g. "RED50" must match the keys of tha tabs further down
        Switcher.SwitcherBuilder<String> menu = Switcher.builder()
            .menu(ButtonMenu.builder()
                .button("RED50", new Button("Red50"))
                .button("GREEN40", new Button("Green40"))
                .button("BLUE50", new Button("Blue50"))
                .build());

        // a tabulator is able to switch between views via the switcher menu
        Tabulator<Object, RENDEROBJ, Object> tabulator = Tabulator.builder()
            // the different views, in our case simple color boxes
            // the keys must match the button keys defined in the switcher menu
            .tabs(Maps.of(
                "RED50", new ColorBox(100, COLOR.RED50),
                "GREEN40", new ColorBox(100, COLOR.GREEN40),
                "BLUE50", new ColorBox(100, COLOR.BLUE50)
            ))
            // this would force the tabulator to show its own menu above it, but we want the menu to be in the FullWindow
            //.direction(DIR.S) 
            .tabMenu(menu)
            .build();

        button.clickActionSet(() ->
            // inject anything which is an instance of GuiSection into the FullWindow
            // the "menu" is optional; you can also only just display one single view
            new FullWindow<ColorBox>("Colors", tabulator, menu)
                .show());

        ModSdkModule.gameApis().ui().injectIntoUITopPanels(button);
    }
}
```







