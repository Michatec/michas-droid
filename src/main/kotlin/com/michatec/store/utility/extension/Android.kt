@file:Suppress("PackageDirectoryMismatch")
package com.michatec.store.utility.extension.android

import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.Signature
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Build

fun Cursor.asSequence(): Sequence<Cursor> {
  return generateSequence { if (moveToNext()) this else null }
}

fun Cursor.firstOrNull(): Cursor? {
  return if (moveToFirst()) this else null
}

fun SQLiteDatabase.execWithResult(sql: String) {
  rawQuery(sql, null).use { it.count }
}

val Context.notificationManager: NotificationManager
  get() = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

val PackageInfo.versionCodeCompat: Long
  get() = longVersionCode

val PackageInfo.singleSignature: Signature?
  get() {
    val signingInfo = signingInfo
    return if (signingInfo?.hasMultipleSigners() == false) {
      signingInfo.apkContentsSigners?.let { if (it.size == 1) it[0] else null }
    } else {
      null
    }
  }

object Android {
  val sdk: Int
    get() = Build.VERSION.SDK_INT

  val name: String
    get() = "Android ${Build.VERSION.RELEASE}"

  val platforms = Build.SUPPORTED_ABIS.toSet()

  val primaryPlatform: String?
    get() = Build.SUPPORTED_64_BIT_ABIS?.firstOrNull() ?: Build.SUPPORTED_32_BIT_ABIS?.firstOrNull()

  fun sdk(sdk: Int): Boolean {
    return Build.VERSION.SDK_INT >= sdk
  }

  object PendingIntent {
    val FLAG_IMMUTABLE: Int
      get() = android.app.PendingIntent.FLAG_IMMUTABLE
  }

  object PackageManager {
    val signaturesFlag: Int
      get() = android.content.pm.PackageManager.GET_SIGNING_CERTIFICATES
  }

  object ServiceInfo {
    val FOREGROUND_SERVICE_TYPE_DATA_SYNC: Int
      get() = android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
  }

  object Device {
    val isHuaweiEmui: Boolean
      get() {
        return try {
          Class.forName("com.huawei.android.os.BuildEx")
          true
        } catch (_: Exception) {
          false
        }
      }
  }
}
