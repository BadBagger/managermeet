package com.smithware.managermeet

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import com.smithware.managermeet.data.ManagerMeetDatabase
import kotlinx.coroutines.runBlocking

class ManagerMeetSummaryProvider : ContentProvider() {
    override fun onCreate(): Boolean = true

    override fun query(uri: Uri, projection: Array<out String>?, selection: String?, selectionArgs: Array<out String>?, sortOrder: String?): Cursor? {
        if (uri.authority != AUTHORITY || uri.lastPathSegment != PATH_SUMMARY) return null
        val appContext = context?.applicationContext ?: return MatrixCursor(COLUMNS)
        val dao = ManagerMeetDatabase.get(appContext).dao()
        val summary = runBlocking {
            val active = dao.activeProjectCount()
            val total = dao.projectCount()
            val average = dao.averageProgress()?.toInt() ?: 0
            val names = dao.latestProjectNames()
            val status = if (active == 0) "No active manager plans" else "Manager plans ready"
            val alert = if (average < 50 && active > 0) "Average plan progress is $average%. Pick one next action." else null
            Summary(status, "$active active plans, $average% average progress", alert, "$active active|$total total|$average% progress", names.ifEmpty { listOf("No active plans") }.joinToString("|"))
        }
        return MatrixCursor(COLUMNS).apply {
            addRow(arrayOf(APP_ID, summary.status, summary.keyInfo, summary.alert.orEmpty(), summary.counts, summary.dueSoon, "just now", "ManagerMeet summary provider"))
        }
    }

    override fun getType(uri: Uri): String? = null
    override fun insert(uri: Uri, values: ContentValues?): Uri? = null
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0
    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int = 0

    private data class Summary(val status: String, val keyInfo: String, val alert: String?, val counts: String, val dueSoon: String)

    companion object {
        private const val AUTHORITY = "com.smithware.managermeet.summary"
        private const val PATH_SUMMARY = "summary"
        private const val APP_ID = "managermeet"
        private val COLUMNS = arrayOf("app_id", "status", "key_info", "alert", "counts", "due_soon", "last_updated", "source")
    }
}
