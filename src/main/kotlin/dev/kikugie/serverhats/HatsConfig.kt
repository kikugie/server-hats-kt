package dev.kikugie.serverhats

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import net.fabricmc.loader.api.FabricLoader
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import kotlin.io.path.inputStream
import kotlin.io.path.outputStream

val CONFIG_PATH = FabricLoader.getInstance().configDir.resolve("serverhats.json")

@Serializable
class HatsConfig {
    var shiftClickEquipping = false
    var dispenserEquipping = true
    var mobsCanEquipHats = false
    var allowAllItems = false
    var invertAllowList = false
    val allowedItems: MutableSet<String> = mutableSetOf(
        "#banners",
        "#beds",
        "feather",
        "end_rod",
        "lightning_rod",
        "spyglass",
        "cod",
        "lead",
        "bone",
        "conduit",

        "amethyst_cluster",
        "large_amethyst_bud",
        "medium_amethyst_bud",
        "small_amethyst_bud",

        "acacia_fence_gate",
        "birch_fence_gate",
        "dark_oak_fence_gate",
        "jungle_fence_gate",
        "oak_fence_gate",
        "spruce_fence_gate",
        "crimson_fence_gate",
        "warped_fence_gate",

        "azalea",
        "flowering_azalea",
        "scaffolding",
        "big_dripleaf",
        "slime_block",
        "honey_block",
        "composter",

        "glass",
        "white_stained_glass",
        "orange_stained_glass",
        "magenta_stained_glass",
        "light_blue_stained_glass",
        "yellow_stained_glass",
        "lime_stained_glass",
        "pink_stained_glass",
        "gray_stained_glass",
        "light_gray_stained_glass",
        "cyan_stained_glass",
        "purple_stained_glass",
        "blue_stained_glass",
        "brown_stained_glass",
        "green_stained_glass",
        "red_stained_glass",
        "black_stained_glass",
        "tinted_glass",
    )

    fun save() = HatsConfigLoader.save(CONFIG_PATH, this)
}


@OptIn(ExperimentalSerializationApi::class)
object HatsConfigLoader {
    val json = Json {
        isLenient = true
        ignoreUnknownKeys = true
        prettyPrint = true
        encodeDefaults = true
    }

    inline fun <reified T> load(file: Path, default: () -> T): T {
        if (file.exists()) try {
            return json.decodeFromStream(file.inputStream())
        } catch (e: Exception) {
            HatsMod.LOGGER.error("Failed to read config $file", e)
        }
        return default().also { save(file, it) }
    }

    inline fun <reified T> save(file: Path, instance: T) = try {
        file.parent.createDirectories()
        json.encodeToStream(instance, file.outputStream())
    } catch (e: Exception) {
        HatsMod.LOGGER.error("Failed to save config $file", e)
    }
}