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
            new Window<NotificationDialog>("Notify", new NotificationDialog(
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

