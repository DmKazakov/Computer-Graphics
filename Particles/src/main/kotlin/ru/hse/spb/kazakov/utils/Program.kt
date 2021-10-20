package ru.hse.spb.kazakov.utils

import com.jogamp.opengl.GL3
import java.nio.ByteBuffer
import java.nio.IntBuffer


class Program(gl: GL3) {
    private val programId = gl.glCreateProgram()
    private val shaders = mutableListOf<Int>()

    fun createProgram(gl: GL3) {
        gl.glLinkProgram(programId)
        checkProgramErrors(GL3.GL_LINK_STATUS, gl)
        gl.glValidateProgram(programId)
        checkProgramErrors(GL3.GL_VALIDATE_STATUS, gl)
        shaders.forEach { gl.glDetachShader(programId, it) }
        shaders.forEach(gl::glDeleteShader)
    }

    fun addShader(shaderFile: String, shaderType: Int, gl: GL3) {
        val shaderCode = this::class.java.classLoader.getResource(shaderFile).readText()
        val shaderId = gl.glCreateShader(shaderType)
        if (shaderId == 0) {
            throw Exception("Unable to create shader");
        }

        gl.glShaderSource(shaderId, 1, arrayOf(shaderCode), null)
        gl.glCompileShader(shaderId)
        checkShaderErrors(shaderId, GL3.GL_COMPILE_STATUS, gl)
        gl.glAttachShader(programId, shaderId)

        shaders.add(shaderId)
    }

    fun enable(gl: GL3) {
        gl.glUseProgram(programId)
    }

    fun disable(gl: GL3) {
        gl.glUseProgram(0)
    }

    fun delete(gl: GL3) {
        gl.glDeleteProgram(programId)
    }

    private fun checkShaderErrors(shaderId: Int, statusToCheck: Int, gl: GL3) {
        val intBuffer = IntBuffer.allocate(1)
        gl.glGetShaderiv(shaderId, statusToCheck, intBuffer)

        if (intBuffer.get(0) != 1) {
            gl.glGetShaderiv(shaderId, GL3.GL_INFO_LOG_LENGTH, intBuffer)
            val size = intBuffer.get(0)
            val byteBuffer = ByteBuffer.allocate(size)
            gl.glGetShaderInfoLog(shaderId, size, intBuffer, byteBuffer)
            throw Exception("Shader error: ${String(byteBuffer.array())}")
        }
    }

    private fun checkProgramErrors(statusToCheck: Int, gl: GL3) {
        val intBuffer = IntBuffer.allocate(1)
        gl.glGetProgramiv(programId, statusToCheck, intBuffer)

        if (intBuffer.get(0) != 1) {
            gl.glGetProgramiv(programId, GL3.GL_INFO_LOG_LENGTH, intBuffer)
            val size = intBuffer.get(0)
            val byteBuffer = ByteBuffer.allocate(size)
            gl.glGetProgramInfoLog(programId, size, intBuffer, byteBuffer)
            throw Exception("Program error: ${String(byteBuffer.array())}")
        }
    }

    private fun getLocation(name: String, gl: GL3): Int {
        val location = gl.glGetUniformLocation(programId, name)
        if (location == -1) {
            throw java.lang.Exception("No variable $name")
        }
        return location
    }

    fun setValue(variable: String, value: Float, gl: GL3) {
        val location = getLocation(variable, gl)
        gl.glUniform1f(location, value)
    }

    fun setValue(variable: String, value: Int, gl: GL3) {
        val location = getLocation(variable, gl)
        gl.glUniform1i(location, value)
    }

    fun setValue(variable: String, value1: Float, value2: Float, gl: GL3) {
        val location = getLocation(variable, gl)
        gl.glUniform2f(location, value1, value2)
    }

    fun setValue(variable: String, value1: Float, value2: Float, value3: Float, gl: GL3) {
        val location = getLocation(variable, gl)
        gl.glUniform3f(location, value1, value2, value3)
    }

    fun setValue(variable: String, value: FloatArray, gl: GL3, transpose: Boolean = false) {
        val location = getLocation(variable, gl)
        gl.glUniformMatrix4fv(location, 1, transpose, value, 0)
    }

    fun transformFeedback(gl: GL3, varyings: Array<String>, mode: Int) {
        gl.glTransformFeedbackVaryings(programId, varyings.size, varyings, mode)
    }
}
