package sergio.sastre.uitesting.utils.utils

import android.view.View.MeasureSpec
import android.view.ViewGroup
import android.util.TypedValue
import android.view.View
import android.widget.ListView

/**
 * A collection of static utilities for measuring and pre-drawing a view, usually a pre-requirement
 * for taking a Screenshot.
 *
 *
 * This will mostly be used something like this: `
 * ViewHelpers.setupView(view)
 * .setExactHeightPx(1000)
 * .setExactWidthPx(100)
 * .layout();
` *
 */
class MeasureViewHelpers private constructor(private val view: View) {
    private var widthMeasureSpec: Int
    private var heightMeasureSpec: Int
    private var guessListViewHeight = false

    init {
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
    }

    /**
     * Measure and layout the view after all the configuration is done.
     */
    fun layout() {
        if (!guessListViewHeight) {
            layoutInternal()
        } else {
            layoutWithHeightDetection()
        }
        dispatchOnGlobalLayout(view)
        dispatchPreDraw(view)
    }

    private fun layoutInternal() {
        do {
            view.measure(widthMeasureSpec, heightMeasureSpec)
            layoutView()
        } while (view.isLayoutRequested)
    }

    private fun layoutWithHeightDetection() {
        val view = view as ListView
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(HEIGHT_LIMIT, MeasureSpec.EXACTLY)
        layoutInternal()
        check(view.count == view.childCount) { "the ListView is too big to be auto measured" }
        var bottom = 0
        if (view.count > 0) {
            bottom = view.getChildAt(view.count - 1).bottom
        }
        if (bottom == 0) {
            bottom = 1
        }
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(bottom, MeasureSpec.EXACTLY)
        layoutInternal()
    }

    /** Configure the height in pixel  */
    fun setExactHeightPx(px: Int): MeasureViewHelpers {
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(px, MeasureSpec.EXACTLY)
        validateHeight()
        return this
    }

    fun guessListViewHeight(): MeasureViewHelpers {
        require(view is ListView) { "guessListViewHeight needs to be used with a ListView" }
        guessListViewHeight = true
        validateHeight()
        return this
    }

    private fun validateHeight() {
        check(!(guessListViewHeight && heightMeasureSpec != 0)) { "Can't call both setExactHeight && guessListViewHeight" }
    }

    /** Configure the width in pixels  */
    fun setExactWidthPx(px: Int): MeasureViewHelpers {
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(px, MeasureSpec.EXACTLY)
        return this
    }

    /** Configure the height in dip  */
    fun setExactWidthDp(dp: Int): MeasureViewHelpers {
        setExactWidthPx(dpToPx(dp))
        return this
    }

    /** Configure the width in dip  */
    fun setExactHeightDp(dp: Int): MeasureViewHelpers {
        setExactHeightPx(dpToPx(dp))
        return this
    }

    /** Configure the height in pixels  */
    fun setMaxHeightPx(px: Int): MeasureViewHelpers {
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(px, MeasureSpec.AT_MOST)
        return this
    }

    /** Configure the height in dip  */
    fun setMaxHeightDp(dp: Int): MeasureViewHelpers {
        setMaxHeightPx(dpToPx(dp))
        return this
    }

    /** Configure the with in pixels  */
    fun setMaxWidthPx(px: Int): MeasureViewHelpers {
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(px, MeasureSpec.AT_MOST)
        return this
    }

    /** Configure the width in dip  */
    fun setMaxWidthDp(dp: Int): MeasureViewHelpers {
        setMaxWidthPx(dpToPx(dp))
        return this
    }

    /**
     * Some views (e.g. SimpleVariableTextLayoutView) in FB4A rely on the predraw. Actually I don't
     * know why, ideally it shouldn't.
     *
     *
     * However if you find that text is not showing in your layout, try dispatching the pre draw
     * using this method. Note this method is only supported for views that are not attached to a
     * Window, and the behavior is slightly different than views attached to a window. (Views attached
     * to a window have a single ViewTreeObserver for all child views, whereas for unattached views,
     * each child has its own ViewTreeObserver.)
     */
    private fun dispatchPreDraw(view: View) {
        while (view.viewTreeObserver.dispatchOnPreDraw()) {
            // no-op
        }
        if (view is ViewGroup) {
            val vg = view
            for (i in 0 until vg.childCount) {
                dispatchPreDraw(vg.getChildAt(i))
            }
        }
    }

    private fun dispatchOnGlobalLayout(view: View) {
        if (view is ViewGroup) {
            val vg = view
            for (i in 0 until vg.childCount) {
                dispatchOnGlobalLayout(vg.getChildAt(i))
            }
        }
        view.viewTreeObserver.dispatchOnGlobalLayout()
    }

    private fun layoutView() {
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
    }

    private fun dpToPx(dp: Int): Int {
        val resources = view.context.resources
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            resources.displayMetrics
        ).toInt()
    }

    companion object {
        private const val HEIGHT_LIMIT = 100_000

        /** Start setup for a view, see class documentation for details.  */
        fun setupView(view: View): MeasureViewHelpers {
            return MeasureViewHelpers(view)
        }
    }
}