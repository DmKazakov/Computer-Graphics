package ru.hse.spb.kazakov.particle

import glm_.BYTES
import jglm.Vec3
import java.nio.FloatBuffer

val PARTICLE_SIZE = 8 * Float.BYTES

data class Particle(
    val position: Vec3 = Vec3(0f, 0f, 0f),
    val velocity: Vec3 = Vec3(0f, 0.001f, 0f),
    val type: ParticleType = ParticleType.LAUNCHER,
    val lifeTime: Float = 0f
) {
    fun toFloatBuffer(particlesNumber: Int): FloatBuffer {
        val array = FloatArray(particlesNumber * 8)
        array[0] = type.value
        array[1] = position.x
        array[2] = position.y
        array[3] = position.z
        array[4] = velocity.x
        array[5] = velocity.y
        array[6] = velocity.z
        array[7] = lifeTime
        return FloatBuffer.wrap(array)
    }
}

enum class ParticleType(val value: Float) {
    LAUNCHER(0.0f), SHELL(1.0f), SECONDARY_SHELL(2.0f)
}
