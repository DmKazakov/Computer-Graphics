package ru.hse.spb.kazakov.utils

import com.jogamp.opengl.GL3
import com.jogamp.opengl.util.texture.TextureIO
import java.nio.FloatBuffer
import kotlin.random.Random

const val RANDOM_TEXTURE_UNIT = GL3.GL_TEXTURE3
const val RANDOM_TEXTURE_UNIT_IND = 3
const val COLOR_TEXTURE_UNIT = GL3.GL_TEXTURE0
const val COLOR_TEXTURE_UNIT_IND = 0

class Texture(private val textureFile: String, private val textureTarget: Int) {
    private val textureObject = intArrayOf(0)

    fun load(gl: GL3) {
        val texture = this::class.java.classLoader.getResourceAsStream(textureFile)
        var extension = ""
        val i = textureFile.lastIndexOf('.')
        if (i > 0) {
            extension = textureFile.substring(i + 1)
        }
        val textureData = TextureIO.newTextureData(gl.glProfile, texture, false, extension)
        val buffer = textureData.buffer
        buffer.rewind()
        gl.glBindTexture(textureTarget, textureObject[0])
        gl.glTexImage2D(
            textureTarget, 0, GL3.GL_RGB, textureData.width, textureData.height,
            0, GL3.GL_RGB, GL3.GL_UNSIGNED_BYTE, buffer
        )
        gl.glTexParameteri(textureTarget, GL3.GL_TEXTURE_MIN_FILTER, GL3.GL_LINEAR)
        gl.glTexParameteri(textureTarget, GL3.GL_TEXTURE_MAG_FILTER, GL3.GL_LINEAR)
        gl.glBindTexture(textureTarget, 0)
    }

    fun bind(gl: GL3, textureUnit: Int) {
        gl.glActiveTexture(textureUnit)
        gl.glBindTexture(textureTarget, textureObject[0])
    }

    fun unbind(gl: GL3, textureUnit: Int) {
        gl.glActiveTexture(textureUnit)
        gl.glBindTexture(textureTarget, 0)
    }
}

class RandomTexture(private val size: Int) {
    private val textureObject = intArrayOf(0)

    fun init(gl: GL3) {
        val random = FloatBuffer.wrap(FloatArray(size * 3) { Random.nextFloat() })
        gl.glGenTextures(1, textureObject, 0)
        gl.glBindTexture(GL3.GL_TEXTURE_1D, textureObject[0])
        gl.glTexImage1D(GL3.GL_TEXTURE_1D, 0, GL3.GL_RGB, size, 0, GL3.GL_RGB, GL3.GL_FLOAT, random)
        gl.glTexParameteri(GL3.GL_TEXTURE_1D, GL3.GL_TEXTURE_MIN_FILTER, GL3.GL_LINEAR)
        gl.glTexParameteri(GL3.GL_TEXTURE_1D, GL3.GL_TEXTURE_MAG_FILTER, GL3.GL_LINEAR)
        gl.glTexParameteri(GL3.GL_TEXTURE_1D, GL3.GL_TEXTURE_WRAP_S, GL3.GL_REPEAT)
    }

    fun bind(gl: GL3, textureUnit: Int) {
        gl.glActiveTexture(textureUnit)
        gl.glBindTexture(GL3.GL_TEXTURE_1D, textureObject[0])
    }
}
