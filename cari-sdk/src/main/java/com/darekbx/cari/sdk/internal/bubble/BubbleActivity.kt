package com.darekbx.cari.sdk.internal.bubble

import android.app.Activity
import android.os.Bundle
import android.view.View
import com.darekbx.cari.R
import com.darekbx.cari.sdk.internal.bubble.preferences.PreferencesAdapter
import com.darekbx.cari.sdk.internal.bubble.preferences.PreferencesParser
import kotlinx.android.synthetic.main.activity_bubble.*

class BubbleActivity : Activity() {

    companion object {
        private val MODE_PREFERENCES = 1
        private val MODE_DATABASE = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bubble)

        button_database.setOnClickListener { switchView(MODE_DATABASE) }
        button_preferences.setOnClickListener { switchView(MODE_PREFERENCES) }
    }

    private fun switchView(mode: Int) {
        welcome_message.visibility = View.GONE
        when (mode) {
            MODE_PREFERENCES -> {
                val scopesList = PreferencesParser(this).parse()

                preferences_container.visibility = View.VISIBLE
                preferences_tree_hint.setText(R.string.preferences_tree_title)

                preferences_tree.setAdapter(PreferencesAdapter(this).apply {
                  scopes = scopesList
                })

            }
            MODE_DATABASE -> {}
        }
    }
}