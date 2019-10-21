package ru.hse.spb.kazakov

import java.awt.event.MouseEvent
import java.awt.event.MouseWheelEvent
import java.awt.event.MouseWheelListener

import javax.swing.event.MouseInputAdapter

private const val ZOOM_SPEED = 0.1f
private const val DRAG_SPEED = 0.5f

class Camera : MouseInputAdapter(), MouseWheelListener {
    var shiftX = 0.0f
    var shiftY = 0.0f
    var zoom = 2.0f
    private var lastDrag: Pair<Float, Float>? = null

    override fun mouseWheelMoved(event: MouseWheelEvent) {
        val point = event.toPoint()
        val sign = if (event.wheelRotation < 0) 1 else -1
        shiftX -= sign * point.first * zoom * ZOOM_SPEED * 0.5f
        shiftY += sign * point.second * zoom * ZOOM_SPEED * 0.5f
        zoom *= (1 - sign * ZOOM_SPEED)
    }

    override fun mouseDragged(event: MouseEvent) {
        val point = event.toPoint()
        val lastDrag = lastDrag

        if (lastDrag != null) {
            shiftX += (point.first - lastDrag.first) * zoom * DRAG_SPEED
            shiftY -= (point.second - lastDrag.second) * zoom * DRAG_SPEED
        }

        this.lastDrag = point
    }

    override fun mouseMoved(e: MouseEvent) {
        lastDrag = null
    }

    private fun MouseEvent.toPoint(): Pair<Float, Float> {
        val x = 2.0f * (x / SCREEN_WIDTH.toFloat() - 0.5f)
        val y = 2.0f * (y / SCREEN_HEIGHT.toFloat() - 0.5f)
        return Pair(x, y)
    }
}
