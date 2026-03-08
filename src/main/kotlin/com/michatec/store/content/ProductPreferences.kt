package com.michatec.store.content

import android.content.Context
import android.content.SharedPreferences
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import com.michatec.store.database.Database
import com.michatec.store.entity.ProductPreference
import com.michatec.store.utility.extension.json.*
import java.io.ByteArrayOutputStream
import androidx.core.content.edit

object ProductPreferences {
  private val defaultProductPreference = ProductPreference(false, 0L)
  private lateinit var preferences: SharedPreferences
  private val subject = PublishSubject.create<Pair<String, Long?>>()
  private var disposable: Disposable? = null

  fun init(context: Context) {
    preferences = context.getSharedPreferences("product_preferences", Context.MODE_PRIVATE)
    Database.LockAdapter.putAll(preferences.all.keys
      .mapNotNull { packageName -> this[packageName].databaseVersionCode?.let { Pair(packageName, it) } })
    disposable?.dispose()
    disposable = subject
      .observeOn(Schedulers.io())
      .subscribe { (packageName, versionCode) ->
        if (versionCode != null) {
          Database.LockAdapter.put(Pair(packageName, versionCode))
        } else {
          Database.LockAdapter.delete(packageName)
        }
      }
  }

  private val ProductPreference.databaseVersionCode: Long?
    get() = when {
      ignoreUpdates -> 0L
      ignoreVersionCode > 0L -> ignoreVersionCode
      else -> null
    }

  operator fun get(packageName: String): ProductPreference {
    return if (preferences.contains(packageName)) {
      try {
        Json.factory.createParser(preferences.getString(packageName, "{}"))
          .use { it.parseDictionary(ProductPreference.Companion::deserialize) }
      } catch (e: Exception) {
        e.printStackTrace()
        defaultProductPreference
      }
    } else {
      defaultProductPreference
    }
  }

  operator fun set(packageName: String, productPreference: ProductPreference) {
    val oldProductPreference = this[packageName]
    val json = ByteArrayOutputStream().apply {
        Json.factory.createGenerator(this).use { it.writeDictionary(productPreference::serialize) }
    }.toByteArray().toString(Charsets.UTF_8)
    
    preferences.edit {
        putString(packageName, json)
    }

    if (oldProductPreference.ignoreUpdates != productPreference.ignoreUpdates ||
      oldProductPreference.ignoreVersionCode != productPreference.ignoreVersionCode) {
      subject.onNext(Pair(packageName, productPreference.databaseVersionCode))
    }
  }
}
