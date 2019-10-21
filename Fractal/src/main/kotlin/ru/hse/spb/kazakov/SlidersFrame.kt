package ru.hse.spb.kazakov

import java.util.*
import javax.swing.*
import javax.swing.event.ChangeListener


class SlidersFrame : JFrame("Settings") {
    val iterationSlider = JSlider(1, 100)
    val thresholdSlider = JSlider(1, 100)

    init {
        iterationSlider.paintLabels = true;
        val iterationLabels = Hashtable<Int, JLabel>()
        iterationLabels[1] = JLabel("1")
        iterationLabels[100] = JLabel("100")
        iterationSlider.labelTable = iterationLabels

        thresholdSlider.paintLabels = true;
        val thresholdLabels = Hashtable<Int, JLabel>()
        thresholdLabels[1] = JLabel("0.1")
        thresholdLabels[100] = JLabel("10")
        thresholdSlider.labelTable = thresholdLabels

        val thresholdSliderLabel = JLabel("Threshold:", JLabel.CENTER)
        thresholdSliderLabel.alignmentX = JLabel.CENTER_ALIGNMENT
        val iterationSliderLabel = JLabel("Iterations number:", JLabel.CENTER)
        iterationSliderLabel.alignmentX = JLabel.CENTER_ALIGNMENT

        val panel = JPanel()
        panel.border = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        val boxLayout = BoxLayout(panel, BoxLayout.Y_AXIS)
        panel.layout = boxLayout
        panel.add(iterationSliderLabel)
        panel.add(iterationSlider)
        panel.add(thresholdSliderLabel)
        panel.add(thresholdSlider)

        add(panel)
        setSize(300, 150)
    }

    fun setListener(listener: ChangeListener) {
        iterationSlider.addChangeListener(listener)
        thresholdSlider.addChangeListener(listener)
    }
}
