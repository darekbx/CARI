package com.darekbx.cari.sdk.internal.bubble.preferences

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import com.darekbx.cari.R
import com.darekbx.cari.sdk.internal.bubble.preferences.model.PreferenceScope

class PreferencesAdapter(val context: Context) : BaseExpandableListAdapter() {

    var scopes: List<PreferenceScope> = emptyList()

    override fun getGroupCount() = scopes.size

    override fun getChildrenCount(groupPosition: Int) = getGroup(groupPosition).items.size

    override fun getGroup(groupPosition: Int) = scopes[groupPosition]

    override fun getChild(groupPosition: Int, childPosition: Int) = getGroup(groupPosition).items[childPosition]

    override fun getGroupId(groupPosition: Int) = -1L

    override fun getChildId(groupPosition: Int, childPosition: Int) = -1L

    override fun hasStableIds() = false

    override fun isEmpty() = scopes.isEmpty()

    override fun isChildSelectable(groupPosition: Int, childPosition: Int) = false

    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val view = convertView ?: inflater.inflate(R.layout.adapter_preferences_scope, null)
        val scope = getGroup(groupPosition)
        view.findViewById<TextView>(R.id.scope_items_count).setText("(${scope.items.size})")
        view.findViewById<TextView>(R.id.scope_name).setText(scope.scopeName)
        return view
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val view = convertView ?: inflater.inflate(R.layout.adapter_preferences_item, null)
        val item = getChild(groupPosition, childPosition)
        view.findViewById<TextView>(R.id.item_key).setText(item.key)
        view.findViewById<TextView>(R.id.item_value).setText("${item.value}")
        return view
    }

    private val inflater by lazy { LayoutInflater.from(context) }
}