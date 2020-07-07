package com.darekbx.cari.sdk.internal.bubble.database

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import com.darekbx.cari.R
import com.darekbx.cari.sdk.internal.bubble.database.model.DatabaseItem

class DatabasesAdapter(val context: Context) : BaseExpandableListAdapter() {

    var databases: List<DatabaseItem> = emptyList()

    override fun getGroupCount() = databases.size

    override fun getChildrenCount(groupPosition: Int) = getGroup(groupPosition).tables.size

    override fun getGroup(groupPosition: Int) = databases[groupPosition]

    override fun getChild(groupPosition: Int, childPosition: Int) = getGroup(groupPosition).tables[childPosition]

    override fun getGroupId(groupPosition: Int) = -1L

    override fun getChildId(groupPosition: Int, childPosition: Int) = -1L

    override fun hasStableIds() = false

    override fun isEmpty() = databases.isEmpty()

    override fun isChildSelectable(groupPosition: Int, childPosition: Int) = true

    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val view = convertView ?: inflater.inflate(R.layout.adapter_database_name, null)
        val databaseItem = getGroup(groupPosition)
        view.findViewById<TextView>(R.id.database_name).setText(databaseItem.database)
        return view
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val view = convertView ?: inflater.inflate(R.layout.adapter_table_name, null)
        val item = getChild(groupPosition, childPosition)
        view.findViewById<TextView>(R.id.table_name).setText(item)
        return view
    }

    private val inflater by lazy { LayoutInflater.from(context) }
}