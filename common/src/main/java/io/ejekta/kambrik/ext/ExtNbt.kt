package io.ejekta.kambrik.ext

import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.nbt.TagParser
import net.minecraft.network.FriendlyByteBuf

operator fun CompoundTag.iterator(): Iterator<Pair<String, Tag>> {
    return keySet().map { it to get(it)!! }.iterator()
}

fun CompoundTag.toMap(): Map<String, Tag> {
    return keySet().associateWith { get(it)!! }
}

fun Map<String, Tag>.toCompoundTag(): CompoundTag {
    return CompoundTag().apply {
        this@toCompoundTag.forEach { (key, tag) ->
            put(key, tag)
        }
    }
}

fun parseTagNonCompound(nbt: String): Tag {
    return TagParser.parseCompoundFully("{content:$nbt}").get("content")!!
}

fun String.toTagNonCompound(): Tag {
    return TagParser.parseCompoundFully("{content:$this}").get("content")!!
}

fun FriendlyByteBuf.unwrapToTag(): Tag {
    return readNbt()!!.get("content")!!
}


