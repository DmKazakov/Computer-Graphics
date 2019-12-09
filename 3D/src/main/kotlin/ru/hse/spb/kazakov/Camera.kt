package ru.hse.spb.kazakov

import com.jogamp.opengl.GL2
import java.awt.event.MouseEvent
import java.awt.event.MouseWheelEvent
import java.awt.event.MouseWheelListener
import javax.swing.event.MouseInputAdapter

private const val ZOOM_SPEED = 0.03
private const val ROTATION_FACT = 0.5

class Camera : MouseInputAdapter(), MouseWheelListener {
    private var zoomFactor = 5.0
    private var isPressed = false
    private var pressPoint = Pair(0, 0)
    private var currentRotation = Pair(0, 0)
    private var accumulatedRotation = Pair(0, 0)
    private var x = 3.0
    private var y = 3.0
    private var z = 3.0

    fun apply(gl: GL2) {
        gl.glTranslated(0.0, -.5, -zoomFactor)
        gl.glRotated((accumulatedRotation.first + currentRotation.first) * ROTATION_FACT, 0.0, 1.0, 0.0)
        gl.glRotated(-(accumulatedRotation.second + currentRotation.second) * ROTATION_FACT, 1.0, 0.0, 0.0)
    }

    private fun dotProduct(matrix: DoubleArray, vector: DoubleArray): List<Double> {
        val rows = matrix.size / vector.size
        val result = mutableListOf<Double>()

        for (row in 0 until rows) {
            var product = 0.0
            for (col in 0..vector.lastIndex) {
                product += vector[col] * matrix[row * vector.size + col]
            }
            result.add(product)
        }

        return result
    }

    override fun mouseWheelMoved(event: MouseWheelEvent) {
        zoomFactor += zoomFactor * event.wheelRotation * ZOOM_SPEED
        x += x * event.wheelRotation * ZOOM_SPEED
        y += y * event.wheelRotation * ZOOM_SPEED
        z += z * event.wheelRotation * ZOOM_SPEED

    }

    override fun mouseDragged(event: MouseEvent) {
        if (!isPressed) {
            isPressed = true
            pressPoint = event.toPoint()
        } else {
            currentRotation = event.toPoint() - pressPoint
        }
    }

    override fun mouseMoved(e: MouseEvent) {
        isPressed = false
        accumulatedRotation += currentRotation
        currentRotation = Pair(0, 0)
    }

    private fun MouseEvent.toPoint(): Pair<Int, Int> = Pair(x, y)

    private operator fun Pair<Int, Int>.minus(pair: Pair<Int, Int>) = Pair(first - pair.first, second - pair.second)

    private operator fun Pair<Int, Int>.plus(pair: Pair<Int, Int>) = Pair(first + pair.first, second + pair.second)
}


