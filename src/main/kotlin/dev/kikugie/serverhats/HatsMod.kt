package dev.kikugie.serverhats

import net.fabricmc.api.ModInitializer
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object HatsMod : ModInitializer {
    val LOGGER: Logger = LoggerFactory.getLogger(HatsMod::class.java)
    internal lateinit var config: HatsConfig
    private lateinit var checker: ItemPredicateChecker

    override fun onInitialize() {
        command
        load()
        update()
    }

    @JvmStatic
    fun isAllowed(stack: ItemStack) = checker.check(stack)

    internal fun load() {
        config = HatsConfigLoader.load(CONFIG_PATH, ::HatsConfig)
    }

    internal fun update() {
        config.save()
        val items = mutableSetOf<Item>()
        val tags = mutableSetOf<TagKey<Item>>()

        for (it in config.allowedItems) {
            if (it.startsWith('#')) {
                val id = ResourceLocation.tryParse(it.drop(1)) ?: continue
                tags += TagKey.create(Registries.ITEM, id)
            }
            else {
                val id = ResourceLocation.tryParse(it) ?: continue
                val item = BuiltInRegistries.ITEM[id].takeUnless { it == Items.AIR } ?: continue
                items += item
            }
        }
        checker = ItemPredicateChecker(items, tags)
    }
}