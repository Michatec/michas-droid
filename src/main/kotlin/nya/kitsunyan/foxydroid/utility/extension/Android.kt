@file:Suppress("PackageDirectoryMismatch")
package nya.kitsunyan.foxydroid.utility.extension.android

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
  get() = if (Android.sdk(28)) longVersionCode else @Suppress("DEPRECATION") versionCode.toLong()

val PackageInfo.singleSignature: Signature?
  get() {
    return if (Android.sdk(28)) {
      val signingInfo = signingInfo
      if (signingInfo?.hasMultipleSigners() == false) signingInfo.apkContentsSigners
        ?.let { if (it.size == 1) it[0] else null } else null
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
      get() = if (sdk(23)) android.app.PendingIntent.FLAG_IMMUTABLE else 0

  }

  object PackageManager {
    val signaturesFlag: Int
      get() = (if (sdk(28)) android.content.pm.PackageManager.GET_SIGNING_CERTIFICATES else 0)
  }

  object ServiceInfo {
    val FOREGROUND_SERVICE_TYPE_DATA_SYNC: Int
      get() = if (sdk(29)) android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC else 0
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
