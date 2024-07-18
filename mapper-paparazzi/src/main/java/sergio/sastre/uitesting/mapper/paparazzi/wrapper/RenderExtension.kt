package sergio.sastre.uitesting.mapper.paparazzi.wrapper

import android.view.View

interface RenderExtension {
    /**
     * Allows this extension to modify the view hierarchy represented by [contentView].
     *
     * Returns the root view of the modified hierarchy.
     */
    fun renderView(contentView: View): View
}

/**
 * This is used via reflection to support accessibility
 */
class AccessibilityRenderExtension : RenderExtension {
    override fun renderView(contentView: View): View {
        // use reflection to avoid direct dependency on Paparazzi
        val parameterType: Class<*> = View::class.java
        val clazz = Class.forName("app.cash.paparazzi.accessibility.AccessibilityRenderExtension")
        val method = clazz.getDeclaredMethod("renderView", parameterType)
        val instance = clazz.getDeclaredConstructor().newInstance()

        return method.invoke(instance, contentView) as View
    }
}
