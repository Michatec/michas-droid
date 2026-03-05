package nya.kitsunyan.foxydroid.database

import android.database.ContentObserver
import android.database.Cursor
import android.database.CursorWrapper
import java.util.concurrent.CopyOnWriteArraySet

class ObservableCursor(cursor: Cursor, private val observable: (register: Boolean,
  observer: () -> Unit) -> Unit): CursorWrapper(cursor) {
  private val observers = CopyOnWriteArraySet<ContentObserver>()

  private val onChange: () -> Unit = {
    for (observer in observers) {
      observer.dispatchChange(false, null)
    }
  }

  init {
    observable(true, onChange)
  }

  override fun registerContentObserver(observer: ContentObserver) {
    super.registerContentObserver(observer)
    observers.add(observer)
  }

  override fun unregisterContentObserver(observer: ContentObserver) {
    super.unregisterContentObserver(observer)
    observers.remove(observer)
  }

  override fun close() {
    super.close()
    observers.clear()
    observable(false, onChange)
  }
}
