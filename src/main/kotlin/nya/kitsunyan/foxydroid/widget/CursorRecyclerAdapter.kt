package nya.kitsunyan.foxydroid.widget

import android.annotation.SuppressLint
import android.database.Cursor
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

abstract class CursorRecyclerAdapter<VT: Enum<VT>, VH: RecyclerView.ViewHolder>: EnumRecyclerAdapter<VT, VH>() {
  init {
    super.setHasStableIds(true)
  }

  private var rowIdIndex = 0

  var cursor: Cursor? = null
    set(value) {
      if (field != value) {
        val oldCursor = field
        field = value
        rowIdIndex = value?.getColumnIndexOrThrow("_id") ?: 0
        onCursorChanged(oldCursor, value)
      }
    }

  @SuppressLint("NotifyDataSetChanged")
  protected open fun onCursorChanged(oldCursor: Cursor?, newCursor: Cursor?) {
    val oldSize = oldCursor?.count ?: 0
    val newSize = newCursor?.count ?: 0

    if (oldCursor == null || newCursor == null) {
      if (oldSize > 0) notifyItemRangeRemoved(0, oldSize)
      if (newSize > 0) notifyItemRangeInserted(0, newSize)
      return
    }

    // Further reduced threshold to 100 for DiffUtil to avoid any noticeable frame drops on the main thread.
    // JSON parsing and DB access during diffing are slow.
    if (oldSize > 100 || newSize > 100) {
        notifyDataSetChanged()
        return
    }

    val oldIdIndex = oldCursor.getColumnIndexOrThrow("_id")
    val newIdIndex = newCursor.getColumnIndexOrThrow("_id")

    try {
        val diffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
          override fun getOldListSize(): Int = oldSize
          override fun getNewListSize(): Int = newSize

          override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            if (!oldCursor.moveToPosition(oldItemPosition) || !newCursor.moveToPosition(newItemPosition)) return false
            return oldCursor.getLong(oldIdIndex) == newCursor.getLong(newIdIndex)
          }

          override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            if (!oldCursor.moveToPosition(oldItemPosition) || !newCursor.moveToPosition(newItemPosition)) return false
            return areContentsTheSame(oldCursor, newCursor)
          }
        })
        diffResult.dispatchUpdatesTo(this)
    } catch (_: Exception) {
        // Fallback in case of cursor issues during diffing
        notifyDataSetChanged()
    }
  }

  protected open fun areContentsTheSame(oldCursor: Cursor, newCursor: Cursor): Boolean = false

  final override fun setHasStableIds(hasStableIds: Boolean) {
    throw UnsupportedOperationException()
  }

  override fun getItemCount(): Int = cursor?.count ?: 0
  override fun getItemId(position: Int): Long = moveTo(position).getLong(rowIdIndex)

  fun moveTo(position: Int): Cursor {
    val cursor = cursor!!
    cursor.moveToPosition(position)
    return cursor
  }
}
