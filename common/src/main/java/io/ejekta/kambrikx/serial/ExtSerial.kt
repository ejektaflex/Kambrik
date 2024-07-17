package io.ejekta.kambrikx.serial

import com.mojang.serialization.Codec
import com.mojang.serialization.JsonOps
import com.mojang.serialization.Lifecycle
import io.ejekta.kambrik.message.INetworkLink
import io.ejekta.kambrik.message.KambrikMsg
import io.ejekta.percale.*
import io.ejekta.percale.encoder.PassEncoder
import io.ejekta.percale.reverse.*
import kotlinx.serialization.Contextual
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.IntArraySerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtInt
import net.minecraft.nbt.NbtList
import net.minecraft.nbt.NbtOps
import net.minecraft.nbt.NbtString
import net.minecraft.network.RegistryByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos

fun <M : KambrikMsg> KSerializer<M>.toSimplePacketCodec(): PacketCodec<RegistryByteBuf, M> {
    val json = INetworkLink.defaultJson
    return PacketCodec.of(
        { value, buf -> buf.writeString(json.encodeToString(this, value)) },
        { json.decodeFromString(this, it.readString()) }
    )
}

@OptIn(ExperimentalSerializationApi::class)
fun main() {

    @Serializable
    data class Hobby(val name: String)

    @Serializable
    data class Doot(val amount: Int, val where: @Contextual Identifier)

//    val serMod = SerializersModule {
//        contextualCodec(Identifier.CODEC)
//        polymorphic(NbtElement::class) {
//            subclass(NbtString::class, NbtStringSerializer)
//            subclass(NbtInt::class, NbtIntSerializer)
//            subclass(NbtCompound::class, NbtCompoundSerializer)
//            subclass(NbtList::class, NbtListSerializer)
//        }
//    }

    println(Identifier.CODEC.encodeStart(NbtOps.INSTANCE, Identifier.of("blah")))


    //val serModTwo = PassEncoder.pickEncoder(Doot.serializer().descriptor, NbtOps.INSTANCE, serMod).serializersModule

//    println(serMod)
//    println(serMod.getContextual(Identifier::class))
//    println(serModTwo)
//    println(serModTwo.getContextual(Identifier::class))

    // !!!!!!
//    val idCodec = Identifier.CODEC
//    val asNbt = idCodec.parse(NbtOps.INSTANCE, NbtString.of("minecraft:here"))
//    println(asNbt)
//
//    println("###")
//
//    val asNbtSer = idCodec.toKotlinNbtSerializer()
//    val asNbtTwo = NbtOps.INSTANCE.deserialize(NbtString.of("bo:here"), asNbtSer)
//    println(asNbtTwo)

//    val dootCodec = Doot.serializer().toCodec(serMod)
//    val dootObj = NbtCompound().apply {
//        put("amount", NbtInt.of(42))
//        put("where", NbtString.of("bo:here"))
//    }
//
//    val result = dootCodec.parse(NbtOps.INSTANCE, dootObj).result().get()
//
//    println(result)
//
//    val resultTwo = dootCodec.encodeStart(NbtOps.INSTANCE, result)
//
//    println(resultTwo)

    val agnosticSerializer = BlockPos.CODEC.toSerializer()

    val derp = Identifier.CODEC

    val item = BlockPos(33, 32, 31)

    val doot = NbtOps.INSTANCE.serialize(
        item, agnosticSerializer
    )

    println(doot)

    println(Json.encodeToString(IntArraySerializer(), intArrayOf(33, 32, 31)))

//    val result = NbtOps.INSTANCE.serialize(
//        Identifier.of("who", "what"),
//        agnosticSerializer
//    )
//
//    println(result)

//    val dootCodec = Doot.serializer().toCodec(serMod)
//    val asNbt = dootCodec.encodeStart(NbtOps.INSTANCE, Doot(42, Identifier.of("doot")))
//
//    println("NBT Result:")
//    println(asNbt)
//
//    val asThingAgain = dootCodec.parse(NbtOps.INSTANCE, asNbt.orThrow)
//
//    println("Reverse Result:")
//    println(asThingAgain)

}