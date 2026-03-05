package nya.kitsunyan.foxydroid.database

import android.database.Cursor
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import nya.kitsunyan.foxydroid.entity.ProductItem

class CursorOwner: Fragment(), LoaderManager.LoaderCallbacks<Cursor> {
  sealed class Request {
    internal abstract val id: Int

    data class ProductsAvailable(val searchQuery: String, val section: ProductItem.Section,
      val order: ProductItem.Order): Request() {
      override val id: Int
        get() = 1
    }

    data class ProductsInstalled(val searchQuery: String, val section: ProductItem.Section,
      val order: ProductItem.Order): Request() {
      override val id: Int
        get() = 2
    }

    data class ProductsUpdates(val searchQuery: String, val section: ProductItem.Section,
      val order: ProductItem.Order): Request() {
      override val id: Int
        get() = 3
    }

    object Repositories: Request() {
      override val id: Int
        get() = 4
    }
  }

  interface Callback {
    fun onCursorData(request: Request, cursor: Cursor?)
  }

  data class ActiveRequest(val request: Request, val callback: Callback?, val cursor: Cursor?)

  class CursorViewModel : ViewModel() {
    internal val activeRequests = mutableMapOf<Int, ActiveRequest>()

    override fun onCleared() {
      activeRequests.values.forEach { it.cursor?.close() }
      activeRequests.clear()
    }
  }

  private val viewModel by lazy { ViewModelProvider(this)[CursorViewModel::class.java] }
  private val activeRequests: MutableMap<Int, ActiveRequest>
    get() = viewModel.activeRequests

  fun attach(callback: Callback, request: Request) {
    val oldActiveRequest = activeRequests[request.id]
    if (oldActiveRequest?.callback != null &&
      oldActiveRequest.callback != callback && oldActiveRequest.cursor != null) {
      oldActiveRequest.callback.onCursorData(oldActiveRequest.request, null)
    }
    val cursor = if (oldActiveRequest?.request == request && oldActiveRequest.cursor != null) {
      callback.onCursorData(request, oldActiveRequest.cursor)
      oldActiveRequest.cursor
    } else {
      null
    }
    activeRequests[request.id] = ActiveRequest(request, callback, cursor)
    if (cursor == null) {
      LoaderManager.getInstance(this).restartLoader(request.id, null, this)
    }
  }

  fun detach(callback: Callback) {
    for (id in activeRequests.keys) {
      val activeRequest = activeRequests[id]!!
      if (activeRequest.callback == callback) {
        activeRequests[id] = activeRequest.copy(callback = null)
      }
    }
  }

  override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
    val request = activeRequests[id]!!.request
    return QueryLoader(requireContext()) {
      when (request) {
        is Request.ProductsAvailable -> Database.ProductAdapter
          .query(
              installed = false,
              updates = false,
              searchQuery = request.searchQuery,
              section = request.section,
              order = request.order,
              signal = it
          )
        is Request.ProductsInstalled -> Database.ProductAdapter
          .query(
              installed = true,
              updates = false,
              searchQuery = request.searchQuery,
              section = request.section,
              order = request.order,
              signal = it
          )
        is Request.ProductsUpdates -> Database.ProductAdapter
          .query(
              installed = true,
              updates = true,
              searchQuery = request.searchQuery,
              section = request.section,
              order = request.order,
              signal = it
          )
        is Request.Repositories -> Database.RepositoryAdapter.query(it)
      }
    }
  }

  override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
    val activeRequest = activeRequests[loader.id]
    if (activeRequest != null) {
      activeRequests[loader.id] = activeRequest.copy(cursor = data)
      activeRequest.callback?.onCursorData(activeRequest.request, data)
    }
  }

  override fun onLoaderReset(loader: Loader<Cursor>) = onLoadFinished(loader, null)
}
