package io.ejekta.kambrik.serial.serializers

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.DoubleArraySerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.TagParser
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3

object SimpleNbtSerializer : KSerializer<CompoundTag> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("kambrik.CompoundTag", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: CompoundTag) {
        encoder.encodeString(value.toString())
    }
    override fun deserialize(decoder: Decoder): CompoundTag {
        return TagParser.parseTag(decoder.decodeString())
    }
}

object IdentitySer : KSerializer<ResourceLocation> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("kambrik.ResourceLocation", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: ResourceLocation) {
        encoder.encodeString(value.toString())
    }
    override fun deserialize(decoder: Decoder): ResourceLocation {
        return ResourceLocation.parse(decoder.decodeString())
    }
}

object Vec3DSer : KSerializer<Vec3> {
    private val delegateSerializer = DoubleArraySerializer()
    @OptIn(ExperimentalSerializationApi::class)
    override val descriptor: SerialDescriptor = SerialDescriptor("kambrik.Vec3d", delegateSerializer.descriptor)
    override fun serialize(encoder: Encoder, value: Vec3) {
        encoder.apply {
            value.run {
                encoder.encodeSerializableValue(delegateSerializer, doubleArrayOf(x, y, z))
            }
        }
    }

    override fun deserialize(decoder: Decoder): Vec3 {
        val arr = decoder.decodeSerializableValue(delegateSerializer)
        return arr.run {
            Vec3(arr[0], arr[1], arr[2])
        }
    }
}

object BoxSerializer : KSerializer<AABB> {
    private val delegateSerializer = DoubleArraySerializer()
    @OptIn(ExperimentalSerializationApi::class)
    override val descriptor: SerialDescriptor = SerialDescriptor("kambrik.Box", delegateSerializer.descriptor)

    override fun serialize(encoder: Encoder, value: AABB) {
        encoder.apply {
            value.run {
                encoder.encodeSerializableValue(delegateSerializer, doubleArrayOf(minX, minY, minZ, maxX, maxY, maxZ))
            }
        }
    }

    override fun deserialize(decoder: Decoder): AABB {
        val arr = decoder.decodeSerializableValue(delegateSerializer)
        return arr.run {
            AABB(arr[0], arr[1], arr[2], arr[3], arr[4], arr[5])
        }
    }
}

