package nya.kitsunyan.foxydroid.graphics

import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.graphics.Rect
import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.DrawableCompat

open class DrawableWrapper(val drawable: Drawable): Drawable() {
  init {
    drawable.callback = object: Callback {
      override fun invalidateDrawable(who: Drawable) {
        callback?.invalidateDrawable(who)
      }

      override fun scheduleDrawable(who: Drawable, what: Runnable, `when`: Long) {
        callback?.scheduleDrawable(who, what, `when`)
      }

      override fun unscheduleDrawable(who: Drawable, what: Runnable) {
        callback?.unscheduleDrawable(who, what)
      }
    }
  }

  override fun onBoundsChange(bounds: Rect) {
    drawable.bounds = bounds
  }

  override fun getIntrinsicWidth(): Int = drawable.intrinsicWidth
  override fun getIntrinsicHeight(): Int = drawable.intrinsicHeight
  override fun getMinimumWidth(): Int = drawable.minimumWidth
  override fun getMinimumHeight(): Int = drawable.minimumHeight

  override fun draw(canvas: Canvas) {
    drawable.draw(canvas)
  }

  override fun getAlpha(): Int {
    return DrawableCompat.getAlpha(drawable)
  }

  override fun setAlpha(alpha: Int) {
    drawable.alpha = alpha
  }

  override fun getColorFilter(): ColorFilter? {
    return DrawableCompat.getColorFilter(drawable)
  }

  override fun setColorFilter(colorFilter: ColorFilter?) {
    drawable.colorFilter = colorFilter
  }

  override fun setTint(tintColor: Int) {
    DrawableCompat.setTint(drawable, tintColor)
  }

  override fun setTintList(tint: ColorStateList?) {
    DrawableCompat.setTintList(drawable, tint)
  }

  override fun setTintMode(tintMode: PorterDuff.Mode?) {
    DrawableCompat.setTintMode(drawable, tintMode ?: PorterDuff.Mode.SRC_IN)
  }

  override fun setHotspot(x: Float, y: Float) {
    DrawableCompat.setHotspot(drawable, x, y)
  }

  override fun setHotspotBounds(left: Int, top: Int, right: Int, bottom: Int) {
    DrawableCompat.setHotspotBounds(drawable, left, top, right, bottom)
  }

  override fun getOpacity(): Int = drawable.opacity
}
