package menu.json;

import com.github.argon.sos.moreoptions.json.element.JsonLong;
import com.github.argon.sos.moreoptions.json.element.JsonObject;

public class JsonUiMapper {
    public static Integer[] colors(JsonObject json) {
        Integer[] colors = new Integer[3];

        colors[0] = json.getAs("R", JsonLong.class)
            .map(JsonLong::getValue)
            .map(Long::intValue)
            .orElse(null);
        colors[1] = json.getAs("G", JsonLong.class)
            .map(JsonLong::getValue)
            .map(Long::intValue)
            .orElse(null);
        colors[2] = json.getAs("B", JsonLong.class)
            .map(JsonLong::getValue)
            .map(Long::intValue)
            .orElse(null);

        return colors;
    }

    public static JsonObject colors(Integer red, Integer green, Integer blue) {
        JsonObject json = new JsonObject();
        json.put("R", JsonLong.of(red));
        json.put("G", JsonLong.of(green));
        json.put("B", JsonLong.of(blue));
        return json;
    }
}
