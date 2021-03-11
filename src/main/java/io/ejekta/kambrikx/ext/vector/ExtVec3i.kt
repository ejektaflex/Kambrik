package io.ejekta.kambrikx.ext.vector

import net.minecraft.util.math.Vec3i

operator fun Vec3i.times(other: Vec3i): Vec3i {
    return Vec3i(x * other.x, y * other.y, z * other.z)
}

operator fun Vec3i.times(num: Int): Vec3i {
    return Vec3i(x * num, y * num, z * num)
}

