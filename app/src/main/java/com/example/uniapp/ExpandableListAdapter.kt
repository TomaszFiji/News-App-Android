package com.example.uniapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import com.google.android.material.textview.MaterialTextView

class ExpandableListAdapter(
    context: Context,
    private val groupData: List<String>,
    private val childData: Map<String, List<String>>,
    private val emailLabel: MaterialTextView
) : BaseExpandableListAdapter() {

    private val context = context
    var selectedCountry: String? = null

    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getGroupCount(): Int {
        return groupData.size
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return childData[groupData[groupPosition]]?.size ?: 0
    }

    override fun getGroup(groupPosition: Int): Any {
        return groupData[groupPosition]
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return childData[groupData[groupPosition]]!![childPosition]
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getGroupView(
        groupPosition: Int, isExpanded: Boolean,
        convertView: View?, parent: ViewGroup
    ): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(
            R.layout.list_group, parent, false
        )
        val lblListHeader = view.findViewById<TextView>(R.id.lblListHeader)
        lblListHeader.text = getGroup(groupPosition) as String
        return view
    }

    override fun getChildView(
        groupPosition: Int, childPosition: Int, isLastChild: Boolean,
        convertView: View?, parent: ViewGroup
    ): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(
            R.layout.list_item, parent, false
        )
        val countryNameView = view.findViewById<TextView>(R.id.lblListItem)
        val tickSymbolView = view.findViewById<TextView>(R.id.tick_symbol)
        val country = getChild(groupPosition, childPosition) as String
        countryNameView.text = country

        if (country == selectedCountry) {
            tickSymbolView.visibility = View.VISIBLE
        } else {
            tickSymbolView.visibility = View.GONE
        }

        return view
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }
}
