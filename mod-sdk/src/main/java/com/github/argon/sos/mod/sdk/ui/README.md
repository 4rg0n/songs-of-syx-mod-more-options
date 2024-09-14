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
        notificator.notify(input.text());
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
            new Window<NotificationDialog>("Notify", new NotificationDialog(
                ModSdkModule.notificator()
            )).show());
        
        // inject the button into the game top navigation panels
        ModSdkModule.gameApis().ui().injectIntoUITopPanels(notifyButton);
    }
}
```



