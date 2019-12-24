package ru.hse.spb.kazakov.particle

import com.jogamp.opengl.GL3
import glm_.size
import jglm.Mat4
import jglm.Vec3
import ru.hse.spb.kazakov.utils.*

private const val MAX_PARTICLES_NUMBER = 1000

class ParticleSystem {
    private lateinit var particlesProgram: Program
    private lateinit var billboardProgram: Program
    private val randomTexture = RandomTexture(1000)
    private val particleTexture = Texture("redlight.jpg", GL3.GL_TEXTURE_2D)
    private val tfObjects = IntArray(2)
    private val particlesBuffer = IntArray(2)
    private var currentVB = 0
    private var currentTFB = 1
    private var time = 0
    private var isFirstTime = true

    fun init(gl: GL3) {
        val launcher = Particle()
        setUpBuffers(gl, launcher)
        setUpParticlesProgram(gl)
        randomTexture.init(gl)
        randomTexture.bind(gl, RANDOM_TEXTURE_UNIT)
        setUpBillboardProgram(gl)
        particleTexture.load(gl)
    }

    private fun setUpBuffers(gl: GL3, launcher: Particle) {
        val particles = launcher.toFloatBuffer(MAX_PARTICLES_NUMBER)
        gl.glGenTransformFeedbacks(2, tfObjects, 0)
        gl.glGenBuffers(2, particlesBuffer, 0)
        for (i in 0..1) {
            gl.glBindTransformFeedback(GL3.GL_TRANSFORM_FEEDBACK, tfObjects[i])
            gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, particlesBuffer[i])
            gl.glBufferData(GL3.GL_ARRAY_BUFFER, particles.size.toLong(), particles, GL3.GL_DYNAMIC_DRAW)
            gl.glBindBufferBase(GL3.GL_TRANSFORM_FEEDBACK_BUFFER, 0, particlesBuffer[i])
        }
    }

    private fun setUpParticlesProgram(gl: GL3) {
        particlesProgram = Program(gl)
        particlesProgram.addShader("particles_vertex.glsl", GL3.GL_VERTEX_SHADER, gl)
        particlesProgram.addShader("particles_geom.glsl", GL3.GL_GEOMETRY_SHADER, gl)
        particlesProgram.transformFeedback(gl, arrayOf("newType", "newPosition", "newVelocity", "newAge"), GL3.GL_INTERLEAVED_ATTRIBS)
        particlesProgram.createProgram(gl)

        particlesProgram.enable(gl)
        particlesProgram.setValue("launcherLifetime", 100f, gl)
        particlesProgram.setValue("shellLifetime", 3000f, gl)
        particlesProgram.setValue("secondaryShellLifetime", 2500f, gl)
        particlesProgram.setValue("randomTexture", RANDOM_TEXTURE_UNIT_IND, gl)
    }

    private fun setUpBillboardProgram(gl: GL3) {
        billboardProgram = Program(gl)
        billboardProgram.addShader("billboard_fragment.glsl", GL3.GL_FRAGMENT_SHADER, gl)
        billboardProgram.addShader("billboard_vertex.glsl", GL3.GL_VERTEX_SHADER, gl)
        billboardProgram.addShader("billboard_geom.glsl", GL3.GL_GEOMETRY_SHADER, gl)
        billboardProgram.createProgram(gl)

        billboardProgram.enable(gl)
        billboardProgram.setValue("colorMap", COLOR_TEXTURE_UNIT_IND, gl)
        billboardProgram.setValue("billboardSize", 0.01f, gl)
    }

    fun render(gl: GL3, deltaTime: Int, vp: Mat4, cameraPos: Vec3) {
        time += deltaTime
        updateParticles(deltaTime, gl)
        renderParticles(vp, cameraPos, gl)
        currentVB = currentTFB
        currentTFB = (currentTFB + 1) and 1
    }

    private fun updateParticles(deltaTime: Int, gl: GL3) {
        particlesProgram.enable(gl)
        particlesProgram.setValue("time", time.toFloat(), gl)
        particlesProgram.setValue("deltaTimeMillis", deltaTime.toFloat(), gl)

        randomTexture.bind(gl, RANDOM_TEXTURE_UNIT)
        gl.glEnable(GL3.GL_RASTERIZER_DISCARD)

        gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, particlesBuffer[currentVB])
        gl.glBindTransformFeedback(GL3.GL_TRANSFORM_FEEDBACK, tfObjects[currentTFB])

        gl.glEnableVertexAttribArray(0)
        gl.glEnableVertexAttribArray(1)
        gl.glEnableVertexAttribArray(2)
        gl.glEnableVertexAttribArray(3)

        gl.glVertexAttribPointer(0, 1, GL3.GL_FLOAT, false, PARTICLE_SIZE, 0)
        gl.glVertexAttribPointer(1, 3, GL3.GL_FLOAT, false, PARTICLE_SIZE, 4)
        gl.glVertexAttribPointer(2, 3, GL3.GL_FLOAT, false, PARTICLE_SIZE, 16)
        gl.glVertexAttribPointer(3, 1, GL3.GL_FLOAT, false, PARTICLE_SIZE, 28)
        gl.glBeginTransformFeedback(GL3.GL_POINTS)

        if (isFirstTime) {
            gl.glDrawArrays(GL3.GL_POINTS, 0, 1)
            isFirstTime = false
        } else {
            gl.glDrawTransformFeedback(GL3.GL_POINTS, tfObjects[currentVB])
        }

        gl.glEndTransformFeedback()
        gl.glDisableVertexAttribArray(0)
        gl.glDisableVertexAttribArray(1)
        gl.glDisableVertexAttribArray(2)
        gl.glDisableVertexAttribArray(3)
    }

    private fun renderParticles(matrix: Mat4, cameraPos: Vec3, gl: GL3) {
        billboardProgram.enable(gl)
        billboardProgram.setValue("cameraPos", cameraPos.x, cameraPos.y, cameraPos.z, gl)
        billboardProgram.setValue("matrix", matrix.toFloatArray(), gl, false)
        particleTexture.bind(gl, COLOR_TEXTURE_UNIT)

        gl.glDisable(GL3.GL_RASTERIZER_DISCARD)
        gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, particlesBuffer[currentTFB])
        gl.glEnableVertexAttribArray(0);
        gl.glVertexAttribPointer(0, 3, GL3.GL_FLOAT, false, PARTICLE_SIZE, 4)
        gl.glDrawTransformFeedback(GL3.GL_POINTS, tfObjects[currentTFB])
        gl.glDisableVertexAttribArray(0)
    }
}
