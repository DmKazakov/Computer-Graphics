package ru.hse.spb.kazakov

import com.jogamp.opengl.GL.*
import com.jogamp.opengl.GL2
import com.jogamp.opengl.GL2GL3.GL_TEXTURE_1D
import java.awt.image.DataBufferByte
import java.io.File
import java.nio.ByteBuffer
import javax.imageio.ImageIO
import java.nio.IntBuffer


class Shader(private val gl: GL2, textureResource: String, shaderResource: String) {
    private var programId: Int

    init {
        setUpTexture(textureResource)
        programId = setUpShader(shaderResource)
    }

    private fun setUpShader(shaderResource: String): Int {
        val shaderId = gl.glCreateShader(GL2.GL_FRAGMENT_SHADER)

        val shader = this::class.java.classLoader.getResource(shaderResource).readText()
        gl.glShaderSource(shaderId, 1, arrayOf(shader), null)
        gl.glCompileShader(shaderId)

        val intBuffer = IntBuffer.allocate(1)
        gl.glGetShaderiv(shaderId, GL2.GL_COMPILE_STATUS, intBuffer)
        if (intBuffer.get(0) != 1) {
            gl.glGetShaderiv(shaderId, GL2.GL_INFO_LOG_LENGTH, intBuffer)
            val size = intBuffer.get(0)
            if (size > 0) {
                val byteBuffer = ByteBuffer.allocate(size)
                gl.glGetShaderInfoLog(shaderId, size, intBuffer, byteBuffer)
                println(String(byteBuffer.array()))
            }
            throw Exception("Error compiling shader!")
        }

        val programId = gl.glCreateProgram()
        gl.glAttachShader(programId, shaderId)
        gl.glLinkProgram(programId)
        gl.glUseProgram(programId)

        return programId
    }

    private fun setUpTexture(textureResource: String) {
        gl.glBindTexture(GL_TEXTURE_1D, 1)
        gl.glTexParameteri(GL_TEXTURE_1D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
        gl.glTexParameteri(GL_TEXTURE_1D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
        gl.glTexParameteri(GL_TEXTURE_1D, GL_TEXTURE_WRAP_S, GL_REPEAT)

        val texture = this::class.java.classLoader.getResourceAsStream(textureResource)
        val image = ImageIO.read(texture)
        val data = image.data.dataBuffer as DataBufferByte
        gl.glTexImage1D(GL_TEXTURE_1D, 0, GL_RGBA, 256, 0, GL_BGR, GL_UNSIGNED_BYTE, ByteBuffer.wrap(data.data))
        gl.glEnable(GL_TEXTURE_1D)
    }

    fun setValue(variable: String, value: Float) {
        val location = gl.glGetUniformLocation(programId, variable)
        gl.glUniform1f(location, value)
    }

    fun setValue(variable: String, value: Int) {
        val location = gl.glGetUniformLocation(programId, variable)
        gl.glUniform1i(location, value)
    }

    fun setValue(variable: String, value1: Float, value2: Float) {
        val location = gl.glGetUniformLocation(programId, variable)
        gl.glUniform2f(location, value1, value2)
    }
}
