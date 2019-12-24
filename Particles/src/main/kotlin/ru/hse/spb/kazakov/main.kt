package ru.hse.spb.kazakov

import java.awt.Frame
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import kotlin.system.exitProcess

const val WINDOW_HEIGHT = 700
const val WINDOW_WIDTH = 700
const val FOV = 60f
const val Z_NEAR = 0.1f
const val Z_FAR = 1000f

fun main() {
    val glDrawer = GLDrawer()
    val frame = Frame("Particles")
    frame.add(glDrawer.newtCanvasAWT)
    frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT)
    frame.addWindowListener(object : WindowAdapter() {
        override fun windowClosing(windowEvent: WindowEvent) {
            glDrawer.animator.stop()
            glDrawer.newtCanvasAWT.destroy()
            frame.dispose()
            exitProcess(0)
        }
    })
    frame.isResizable = false
    frame.isVisible = true
}
