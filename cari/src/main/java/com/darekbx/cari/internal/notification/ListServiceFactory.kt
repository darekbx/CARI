package com.darekbx.cari.internal.notification

import android.content.Context
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.darekbx.cari.R

class ListServiceFactory(val context: Context)  : RemoteViewsService.RemoteViewsFactory {

    override fun onCreate() {

    }

    override fun getLoadingView(): RemoteViews {
        return RemoteViews(context.packageName, R.layout.layout_notification_small)
    }

    override fun getItemId(position: Int): Long {
        return 1L
    }

    override fun onDataSetChanged() {

    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun getViewAt(position: Int): RemoteViews {
        return RemoteViews(context.packageName, R.layout.layout_row)
    }

    override fun getCount(): Int {
        return 3
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun onDestroy() {

    }

}