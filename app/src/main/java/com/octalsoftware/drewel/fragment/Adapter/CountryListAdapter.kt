package com.octalsoftware.drewel.fragment.Adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.octalsoftware.drewel.R
import com.octalsoftware.drewel.constant.Tags
import com.octalsoftware.drewel.interfaces.OnClickItemListener
import com.octalsoftware.drewel.model.CountryModel

/**
 * Created by heena on 23/8/17.
 */
class CountryListAdapter(internal var context: Context, internal var onClickItemListener: OnClickItemListener, var countryModelArray: List<CountryModel>?) : RecyclerView.Adapter<CountryListAdapter.ViewHolder>() {
    var countryModelArrayfilter: java.util.ArrayList<CountryModel>? = null

    init {
        if (this.countryModelArrayfilter == null)
            this.countryModelArrayfilter = java.util.ArrayList<CountryModel>()
        if (this.countryModelArray == null)
            this.countryModelArray = java.util.ArrayList<CountryModel>()
        countryModelArrayfilter!!.addAll(countryModelArray!!)
    }

    override fun getItemCount(): Int {
        return countryModelArrayfilter!!.size
    }

    fun getItem(position: Int): CountryModel {
        return countryModelArrayfilter!!.get(position)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        if (countryModelArrayfilter!![position].name.contains("\\r\\n"))
            holder?.txt_c_listvalue!!.text = countryModelArrayfilter!![position].name.replace("\\r\\n", "")
        else
            holder?.txt_c_listvalue!!.text = countryModelArrayfilter!![position].name
        holder.txt_c_listvalue!!.setOnClickListener(View.OnClickListener {
            if (onClickItemListener != null) {
                onClickItemListener.setOnItemClick(Tags.country, position)
            }
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {

        return ViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.country_item, parent, false))
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var txt_c_listvalue: TextView? = null

        init {
            txt_c_listvalue = view.findViewById(R.id.txt_c_listvalue)
        }
    }

    fun filter(string: String) {
        countryModelArrayfilter!!.clear()
        if (string.trim().length != 0) {
            for (cityItems in countryModelArray!!) {
                if (cityItems.name.toLowerCase().contains(string.trim().toLowerCase())) {
                    countryModelArrayfilter!!.add(cityItems)
                }
            }
        } else {
            countryModelArrayfilter!!.addAll(countryModelArray!!)
        }
        notifyDataSetChanged()
    }
}