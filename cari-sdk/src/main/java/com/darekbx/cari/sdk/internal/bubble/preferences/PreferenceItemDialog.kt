package com.darekbx.cari.sdk.internal.bubble.preferences

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import com.darekbx.cari.R
import com.darekbx.cari.sdk.internal.bubble.preferences.model.PreferenceItem
import kotlinx.android.synthetic.main.dialog_preference_item.*

class PreferenceItemDialog(context: Context, val preferenceItem: PreferenceItem) : Dialog(context) {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_preference_item)

        preferences_item_key.setText(preferenceItem.key)
        preferences_item_value.setText("${preferenceItem.value}")
    }
}