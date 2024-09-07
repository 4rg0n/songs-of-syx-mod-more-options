package com.github.argon.sos.moreoptions.ui.json.factory;

import com.github.argon.sos.mod.sdk.game.ui.ColumnRow;
import com.github.argon.sos.mod.sdk.json.element.JsonElement;
import com.github.argon.sos.mod.sdk.json.element.JsonObject;
import com.github.argon.sos.mod.sdk.util.StringUtil;
import snake2d.util.gui.renderable.RENDEROBJ;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractJsonUiTemplate {
    protected final JsonUiElementFactory factory;
    protected final JsonUiElementStore store;

    public AbstractJsonUiTemplate(JsonUiElementFactory factory, JsonUiElementStore store) {
        this.factory = factory;
        this.store =  store;
    }

    public Set<JsonUiElementSingle<? extends JsonElement, ? extends RENDEROBJ>> getAll() {
        return store.getAll();
    }

    public List<ColumnRow<JsonUiElementSingle<? extends JsonElement, ? extends RENDEROBJ>>> toColumnRows() {
        return getAll().stream()
            .map(JsonUiElementSingle::toColumnRow)
            .collect(Collectors.toList());
    }

    public boolean isDirty() {
        return store.isDirty();
    }

    public JsonObject getConfig() {
        return store.getConfig();
    }

    public void reset() {
        store.reset();
    }

    public List<JsonUiElementSingle<? extends JsonElement, ? extends RENDEROBJ>> getOrphans() {
        return store.getOrphans();
    }

    public String getName() {
        return StringUtil.removeTrailing(getPath().getFileName().toString(), ".txt");
    }

    public String getFileName() {
        return getPath().getFileName().toString();
    }

    public Path getPath() {
        return factory.getPath();
    }
}
