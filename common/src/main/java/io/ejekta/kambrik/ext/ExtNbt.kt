package io.ejekta.kambrik.ext

import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.StringNbtReader
import net.minecraft.nbt.NbtElement
import net.minecraft.network.PacketByteBuf

operator fun NbtCompound.iterator(): Iterator<Pair<String, NbtElement>> {
    return keys.map { it to get(it)!! }.iterator()
}

fun NbtCompound.toMap(): Map<String, NbtElement> {
    return keys.associateWith { get(it)!! }
}

fun Map<String, NbtElement>.toNbtCompound(): NbtCompound {
    return NbtCompound().apply {
        this@toNbtCompound.forEach { (key, tag) ->
            put(key, tag)
        }
    }
}

fun StringNbtReader.parseTag(nbt: String): NbtElement {
    return StringNbtReader.parse("{content:$nbt}")
}

fun String.toTag(): NbtElement {
    return StringNbtReader.parse("{content:$this}").get("content")!!
}

fun PacketByteBuf.unwrapToTag(): NbtElement {
    return readNbt()!!.get("content")!!
}


