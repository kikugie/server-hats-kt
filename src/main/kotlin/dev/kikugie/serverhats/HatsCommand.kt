package dev.kikugie.serverhats

import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import net.minecraft.commands.arguments.ResourceOrTagKeyArgument
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.silkmc.silk.commands.command
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.javaType

private val allowed get() = HatsMod.config.allowedItems

internal val command = command("hats") {
    requiresPermissionLevel(2)
    literal("list") {
        runs {
            val items = allowed
                .asSequence()
                .sorted()
                .joinToString { "§b$it§r" }
            source.sendSuccess({ Component.literal("Allowed items: [$items]") }, false)
        }
    }
    literal("add") {
        argument("predicate", ResourceOrTagKeyArgument.resourceOrTagKey(Registries.ITEM)) { pred ->
            runs {
                val key = pred(this).stringify()
                if (key in allowed) source.sendFailure(Component.literal("Predicate $key is already defined"))
                else {
                    allowed.add(key)
                    HatsMod.update()
                }
            }
        }
    }
    literal("remove") {
        argument("predicate", ResourceOrTagKeyArgument.resourceOrTagKey(Registries.ITEM)) { pred ->
            suggestList { allowed }
            runs {
                val key = pred(this).stringify()
                if (key !in allowed) throw SimpleCommandExceptionType(Component.literal("Predicate $key is not defined")).create()
                else {
                    allowed.remove(key)
                    HatsMod.update()
                }
            }
        }
    }
    literal("config") {
        val properties = HatsConfig::class.declaredMemberProperties.filter { it.returnType.javaType == Boolean::class.java }
        for (prop in properties) {
            @Suppress("UNCHECKED_CAST")
            prop as KMutableProperty1<HatsConfig, Boolean>
            literal(prop.name) {
                runs {
                    source.sendSuccess({ Component.literal("Config value §b${prop.name}§r is §6${prop.get(HatsMod.config)}§r") }, false)
                }
                argument<Boolean>("state") { arg ->
                    runs {
                        val new = arg(this)
                        prop.set(HatsMod.config, new)
                        source.sendSuccess({ Component.literal("Config value §b${prop.name}§r is set to: §6$new§r") }, false)
                    }
                }
            }
        }
    }
    literal("reload") {
        runs {
            source.sendSuccess({ Component.literal("Reloading configs") }, false)
            HatsMod.load()
        }
    }
}

private fun ResourceOrTagKeyArgument.Result<Item>.stringify(): String {
    fun ResourceLocation.withoutMinecraft() = if (namespace == "minecraft") path else "$namespace:$path"
    val either = unwrap()
    var str = ""
    either.ifLeft {
        str = it.location().withoutMinecraft()
    }
    either.ifRight {
        str = "#${it.location().withoutMinecraft()}"
    }
    return str
}
