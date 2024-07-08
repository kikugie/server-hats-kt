package dev.kikugie.serverhats

import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack

data class ItemPredicateChecker(
    val items: Set<Item>,
    val tags: Iterable<TagKey<Item>>
) {
    fun check(stack: ItemStack): Boolean = when {
        HatsMod.config.allowAllItems -> true
        stack.item in items -> true
        else -> tags.any { stack.`is`(it) }
    }
}