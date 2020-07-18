package com.darekbx.cari.sdk.internal.bubble

import android.app.Activity
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.ExpandableListView
import com.darekbx.cari.R
import com.darekbx.cari.sdk.internal.bubble.database.DatabaseParser
import com.darekbx.cari.sdk.internal.bubble.database.DatabasesAdapter
import com.darekbx.cari.sdk.internal.bubble.database.model.DatabaseItem
import com.darekbx.cari.sdk.internal.bubble.preferences.PreferenceItemDialog
import com.darekbx.cari.sdk.internal.bubble.preferences.PreferencesAdapter
import com.darekbx.cari.sdk.internal.bubble.preferences.PreferencesParser
import com.darekbx.cari.sdk.internal.bubble.preferences.model.PreferenceItem
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
            MODE_PREFERENCES -> loadPreferences()
            MODE_DATABASE -> loadDatabase()
        }
    }

    private fun loadDatabase() {
        object : AsyncTask<Void, Void, List<DatabaseItem>>() {

            override fun doInBackground(vararg params: Void?) =
                DatabaseParser(this@BubbleActivity).readDatabases()

            override fun onPostExecute(result: List<DatabaseItem>?) {
                val adapter = DatabasesAdapter(this@BubbleActivity).apply {
                    result?.let {
                        databases = result
                    }
                }

                preferences_container.visibility = View.GONE
                databases_container.visibility = View.VISIBLE
                databases_tree_hint.setText(R.string.databases_tree_title)
                databases_tree.setOnChildClickListener(object :
                    ExpandableListView.OnChildClickListener {
                    override fun onChildClick(
                        parent: ExpandableListView?,
                        v: View?,
                        groupPosition: Int,
                        childPosition: Int,
                        id: Long
                    ): Boolean {
                        val database = adapter.getGroup(groupPosition).database
                        val table = adapter.getChild(groupPosition, childPosition)
                        loadTable(database, table)
                        return false
                    }
                })

                databases_tree.setAdapter(adapter)
            }
        }.execute()
    }

    private fun loadTable(database: String, table: String) {

        object : AsyncTask<Void, Void, List<List<String>>>() {

            override fun doInBackground(vararg params: Void?) =
                DatabaseParser(this@BubbleActivity).loadTable(database, table)

            override fun onPostExecute(result: List<List<String>>?) {

                // TODO: Display dialog with table contents?

            }
        }.execute()
    }

    private fun loadPreferences() {
        Thread().run {
            val result = PreferencesParser(this@BubbleActivity).parse()

            preferences_tree?.post {
                databases_container.visibility = View.GONE
                preferences_container.visibility = View.VISIBLE
                preferences_tree_hint.setText(R.string.preferences_tree_title)

                result?.let {
                    preferences_tree.setAdapter(PreferencesAdapter(this@BubbleActivity).apply {
                        scopes = result
                        onItemEdit = { scope, preferenceItem -> editPreferenceItem(scope, preferenceItem) }
                    })
                }
            }
        }
    }

    private fun editPreferenceItem(scope: String, preferenceItem: PreferenceItem) {
        PreferenceItemDialog(this, scope, preferenceItem).apply {
            valueChanged = { refreshPreference(scope, preferenceItem) }
        }.show()
    }

    private fun refreshPreference(scope: String, preferenceItem: PreferenceItem) {

        // TODO: refresh changed value on the preference list, refresh single row

        val adapter = preferences_tree.expandableListAdapter as PreferencesAdapter
        adapter.notifyDataSetInvalidated()

    }
}