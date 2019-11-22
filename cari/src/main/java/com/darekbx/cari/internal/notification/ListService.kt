package com.darekbx.cari.internal.notification

import android.content.Intent
import android.widget.RemoteViewsService

class ListService  : RemoteViewsService() {

    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        return ListServiceFactory(applicationContext)
    }
}