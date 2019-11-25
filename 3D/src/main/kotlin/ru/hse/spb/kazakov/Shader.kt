package ru.hse.spb.kazakov

import com.jogamp.opengl.GL2
import java.nio.ByteBuffer
import java.nio.IntBuffer


class Shader(private val gl: GL2, fragmentShader: String, vertexShader: String) {
    private val programId = gl.glCreateProgram()
    private val fragmentShaderId: Int
    private val vertexShaderId: Int

    init {
        val (fragmentShaderId, vertexShaderId) = setUpShader(fragmentShader, vertexShader)
        this.fragmentShaderId = fragmentShaderId
        this.vertexShaderId = vertexShaderId
    }

    private fun setUpShader(fragmentShader: String, vertexShader: String): Pair<Int, Int> {
        val fragmentShaderCode = this::class.java.classLoader.getResource(fragmentShader).readText()
        val vertexShaderCode = this::class.java.classLoader.getResource(vertexShader).readText()
        val fragmentShaderId = createShaderProgram(fragmentShaderCode, GL2.GL_FRAGMENT_SHADER)
        val vertexShaderId = createShaderProgram(vertexShaderCode, GL2.GL_VERTEX_SHADER)

        gl.glLinkProgram(programId)
        checkProgramErrors(GL2.GL_LINK_STATUS)
        gl.glValidateProgram(programId)
        checkProgramErrors(GL2.GL_VALIDATE_STATUS)
        gl.glUseProgram(programId)

        return Pair(fragmentShaderId, vertexShaderId)
    }

    private fun createShaderProgram(shaderCode: String, shaderType: Int): Int {
        val shaderId = gl.glCreateShader(shaderType)
        if (shaderId == 0) {
            throw Exception("Unable to create shader");
        }

        gl.glShaderSource(shaderId, 1, arrayOf(shaderCode), null)
        gl.glCompileShader(shaderId)
        checkShaderErrors(shaderId, GL2.GL_COMPILE_STATUS)
        gl.glAttachShader(programId, shaderId)

        return shaderId
    }

    private fun checkShaderErrors(shaderId: Int, statusToCheck: Int) {
        val intBuffer = IntBuffer.allocate(1)
        gl.glGetShaderiv(shaderId, statusToCheck, intBuffer)

        if (intBuffer.get(0) != 1) {
            gl.glGetShaderiv(shaderId, GL2.GL_INFO_LOG_LENGTH, intBuffer)
            val size = intBuffer.get(0)
            val byteBuffer = ByteBuffer.allocate(size)
            gl.glGetShaderInfoLog(shaderId, size, intBuffer, byteBuffer)
            throw Exception("Shader error: ${String(byteBuffer.array())}")
        }
    }

    private fun checkProgramErrors(statusToCheck: Int) {
        val intBuffer = IntBuffer.allocate(1)
        gl.glGetProgramiv(programId, statusToCheck, intBuffer)

        if (intBuffer.get(0) != 1) {
            gl.glGetProgramiv(programId, GL2.GL_INFO_LOG_LENGTH, intBuffer)
            val size = intBuffer.get(0)
            val byteBuffer = ByteBuffer.allocate(size)
            gl.glGetProgramInfoLog(programId, size, intBuffer, byteBuffer)
            throw Exception("Program error: ${String(byteBuffer.array())}")
        }
    }

    fun delete() {
        gl.glDetachShader(programId, vertexShaderId)
        gl.glDetachShader(programId, fragmentShaderId)
        gl.glDeleteProgram(programId)
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
