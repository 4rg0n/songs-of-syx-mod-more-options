package menu;

public class UiFactory {
    public static FullWindow<MoreOptionsEditor> moreOptionsFullsWindow() {
        MoreOptionsEditor moreOptionsEditor = new MoreOptionsEditor();
        return new FullWindow<>("More Options", moreOptionsEditor, moreOptionsEditor.getTabulator().getMenu());
    }
}
