package sanskrit.ui

import java.awt.Container
import java.awt.Dimension
import java.awt.Toolkit
import java.awt.Window
import java.awt.event.ActionEvent
import java.awt.event.KeyEvent
import java.awt.event.WindowEvent
import java.net.URL
import javax.swing.AbstractAction
import javax.swing.Action
import javax.swing.ImageIcon
import javax.swing.JComponent
import javax.swing.JDialog
import javax.swing.JFrame
import javax.swing.KeyStroke

object GuiUtils {
    private val ESCAPE_KEYSTROKE = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0)
    val CLOSE_DIALOG_ACTION_MAP_KEY = "galvin.swing.dispatch:WINDOW_CLOSING"

    val PADDING = 5

    val quitKeyStroke: KeyStroke
        get() = if (SystemUtils.IS_MAC) {
            KeyStroke.getKeyStroke(KeyEvent.VK_Q, SystemUtils.PREFERED_MODIFIER_KEY)
        } else {
            KeyStroke.getKeyStroke(KeyEvent.VK_F4, SystemUtils.SECONDARY_MODIFIER_KEY)
        }

    fun center(window: Window) {
        val screenSize = Toolkit.getDefaultToolkit().screenSize
        val frameSize = window.size
        val x = (screenSize.width - frameSize.width) / 2
        val y = (screenSize.height - frameSize.height) / 2
        window.setLocation(x, y)
    }

    fun createImageIcon(path: String): ImageIcon {
        var url: URL? = ClassLoader.getSystemResource(path)
        if (url == null) {
            url = ClassLoader.getSystemResource(path)
        }
        return ImageIcon(url!!)
    }

    fun getMaxSize(components: Array<JComponent>): Dimension {
        var width = 0
        var height = 0

        for (i in components.indices) {
            val size = components[i].preferredSize
            width = Math.max(width, size.width)
            height = Math.max(height, size.height)
        }

        return Dimension(width, height)
    }

    fun setSize(size: Dimension, components: Array<JComponent>) {
        for (component in components) {
            component.size = size
        }
    }

    fun setSize(size: Dimension, components: List<JComponent>) {
        for (component in components) {
            component.size = size
        }
    }

    fun forceRepaint(component: JComponent) {
        component.invalidate()
        component.repaint()
        component.validate()
    }

    fun forceRepaint(component: Container) {
        component.invalidate()
        component.repaint()
        component.validate()
    }

    fun preferredSize(component: JComponent): Dimension {
        val size = component.preferredSize
        component.size = size
        return size
    }

    fun preferredSize(component: JComponent, overrideWidth: Int, overrideHeight: Int): Dimension {
        val size = component.preferredSize

        if (overrideWidth != -1) {
            size.width = overrideWidth
        }

        if (overrideHeight != -1) {
            size.height = overrideHeight
        }

        component.size = size
        return size
    }

    fun preferredSize(components: List<JComponent>) {
        for (component in components) {
            val size = component.preferredSize
            component.size = size
        }
    }

    fun sameSize(components: List<JComponent>): Dimension {
        val array = mutableListOf<JComponent>()
        for( component in components) array.add(component)
        return sameSize(array)
    }

    fun sameSize(components: Array<JComponent>): Dimension {
        var width = 0
        var height = 0

        for (i in components.indices) {
            val size = components[i].preferredSize
            width = Math.max(width, size.width)
            height = Math.max(height, size.height)
        }

        val result = Dimension(width, height)

        for (i in components.indices) {
            components[i].size = result
        }

        return result
    }

    fun sameWidth(components: Array<JComponent>, width: Int) {
        for (i in components.indices) {
            val size = components[i].size
            size.width = width
            components[i].size = size
        }
    }

    fun sameHeight(components: Array<JComponent>, height: Int) {
        for (i in components.indices) {
            val size = components[i].size
            size.height = height
            components[i].size = size
        }
    }

    fun setWidthPercentageOfScreen(window: Window, widthPercentage: Int) {
        val screenSize = Toolkit.getDefaultToolkit().screenSize
        val width = screenSize.width * widthPercentage / 100
        val height = window.height
        window.setSize(width, height)
        center(window)
    }

    fun setHeightPercentageOfScreen(window: Window, heightPercentage: Int) {
        val screenSize = Toolkit.getDefaultToolkit().screenSize
        val width = window.width
        val height = screenSize.height * heightPercentage / 100
        window.setSize(width, height)
        center(window)
    }

    fun setPercentageOfScreen(window: Window, widthPercentage: Int, heightPercentage: Int) {
        val screenSize = Toolkit.getDefaultToolkit().screenSize
        val width = screenSize.width * widthPercentage / 100
        val height = screenSize.height * heightPercentage / 100
        window.setSize(width, height)
        center(window)
    }

    fun setPercentageOfScreen(window: Window, widthPercentage: Double, heightPercentage: Double) {
        val screenSize = Toolkit.getDefaultToolkit().screenSize
        val width = (screenSize.width * widthPercentage).toInt()
        val height = (screenSize.height * heightPercentage).toInt()
        window.setSize(width, height)
        center(window)
    }

    fun setWidthPercentageOfScreen(window: Window, widthPercentage: Double) {
        val screenSize = Toolkit.getDefaultToolkit().screenSize
        val width = (screenSize.width * widthPercentage).toInt()
        val height = window.height
        window.setSize(width, height)
        center(window)
    }

    fun setHeightPercentageOfScreen(window: Window, heightPercentage: Double) {
        val screenSize = Toolkit.getDefaultToolkit().screenSize
        val width = window.width
        val height = (screenSize.height * heightPercentage).toInt()
        window.setSize(width, height)
        center(window)
    }

    fun closeOnEscape(dialog: JDialog): Action {
        return CloseDialogAction(dialog)
    }

    fun closeOnEscape(frame: JFrame): Action {
        return CloseFrameAction(frame)
    }

    private class CloseDialogAction(private val dialog: JDialog) : AbstractAction() {

        init {
            dialog.rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ESCAPE_KEYSTROKE, CLOSE_DIALOG_ACTION_MAP_KEY)
            dialog.rootPane.actionMap.put(CLOSE_DIALOG_ACTION_MAP_KEY, this)
        }

        override fun actionPerformed(event: ActionEvent) {
            dialog.dispatchEvent(WindowEvent(dialog, WindowEvent.WINDOW_CLOSING))
        }
    }

    private class CloseFrameAction(private val frame: JFrame) : AbstractAction() {

        init {
            frame.rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ESCAPE_KEYSTROKE, CLOSE_DIALOG_ACTION_MAP_KEY)
            frame.rootPane.actionMap.put(CLOSE_DIALOG_ACTION_MAP_KEY, this)
        }

        override fun actionPerformed(event: ActionEvent) {
            frame.dispatchEvent(WindowEvent(frame, WindowEvent.WINDOW_CLOSING))
        }
    }
}
