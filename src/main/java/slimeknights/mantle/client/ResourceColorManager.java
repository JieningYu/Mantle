package slimeknights.mantle.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.data.ISafeManagerReloadListener;
import slimeknights.mantle.util.JsonHelper;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Class allowing the resource pack to set colors for various things. Safe to call in serverside code, but will have no effect
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Log4j2
public class ResourceColorManager implements ISafeManagerReloadListener, IdentifiableResourceReloadListener {
  /** Modifier file to load, has merging behavior but forge prevents multiple mods from loading the same file */
  private static final String COLORS_PATH = "mantle/colors.json";
  /** Modifier file to load, has merging behavior but forge prevents multiple mods from loading the same file */
  private static final String FALLBACK_PATH = "tinkering/colors.json";
  /** Default color so the getter can be nonnull */
  public static final TextColor WHITE = TextColor.fromRgb(-1);
  /** Instance of this manager */
  public static final ResourceColorManager INSTANCE = new ResourceColorManager();

  /** Model overrides, if not in this map the default is used */
  private static Map<String,TextColor> COLORS = Collections.emptyMap();

  /**
   * Initializes this manager, registering it with the resource manager
   */
  public static void init() {
    ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(INSTANCE);
  }

  /** Recursively parses the given objects */
  private static void parseRecursive(String prefix, JsonObject json, Map<String,TextColor> colors) {
    // right now just do simply key value pairs
    for (Entry<String,JsonElement> entry : json.entrySet()) {
      String key = entry.getKey();
      JsonElement element = entry.getValue();
      // json means we combine the keys
      if (element.isJsonObject()) {
        parseRecursive(prefix + key + ".", element.getAsJsonObject(), colors);
      } else if (element.isJsonPrimitive()) {
        String fullPath = prefix + key;
        if (!colors.containsKey(fullPath)) {
          String text = element.getAsString();
          TextColor color = TextColor.parseColor(text);
          if (color == null) {
            log.error("Color at key '{}' could not be parsed, got '{}'", fullPath, text);
          } else {
            colors.put(fullPath, color);
          }
        }
        // treat nulls as comments
      } else if (!element.isJsonNull()) {
        log.error("Skipping color key '{}' as the value is not a string", key);
      }
    }
  }

  @Override
  public void onReloadSafe(ResourceManager manager) {
    // start building the model map
    Map<String,TextColor> colors = new HashMap<>();

    // get a list of files from all namespaces
    List<JsonObject> jsonFiles = JsonHelper.getFileInAllDomainsAndPacks(manager, COLORS_PATH, null);
    // first object is bottom most pack, so upper resource packs will replace it
    for (int i = jsonFiles.size() - 1; i >= 0; i--) {
      parseRecursive("", jsonFiles.get(i), colors);
    }
    // load in fallback files second, so mantle files take precedence
    jsonFiles = JsonHelper.getFileInAllDomainsAndPacks(manager, FALLBACK_PATH, COLORS_PATH);
    // first object is bottom most pack, so upper resource packs will replace it
    for (int i = jsonFiles.size() - 1; i >= 0; i--) {
      parseRecursive("", jsonFiles.get(i), colors);
    }
    // replace the map
    COLORS = colors;
  }

  /** Gets the text color at the given path, or null if undefined */
  @Nullable
  public static TextColor getOrNull(String path) {
    return COLORS.get(path);
  }

  /** Gets the text color at the given path */
  public static TextColor getTextColor(String path) {
    return COLORS.getOrDefault(path, WHITE);
  }

  /** Gets an integer color for the given path */
  public static int getColor(String path) {
    return getTextColor(path).getValue();
  }

  @Override
  public ResourceLocation getFabricId() {
    return Mantle.getResource("resource_color_manager");
  }
}
