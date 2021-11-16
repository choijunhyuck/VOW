package com.vowcompany.vow.feed

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vowcompany.vow.R
import com.vowcompany.vow.VowActivity
import java.util.*

//This adapter class connects data to each feed
class BriefFeedListAdapter(
    private val context: Context,
    private val arrayList: ArrayList<BriefFeedListItem>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    //Adapter keeps track of the viewholder's id
    init {
        //To prevent duplicate view problem
        setHasStableIds(true);
    }

    /* Override functions */

    //Set widgets
    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, i: Int) {

        //Assign values
        var feedID = arrayList[i].id
        var createdAt = arrayList[i].created_at

        //Initialize widget
        var feedImage = (viewHolder as Model).feedImage
        var feedDate = (viewHolder).feedDate

        //Set background text
        feedDate.text = createdAt

        //Set background image
        when (i) {
            0 -> {
                feedImage.setImageDrawable(context.getDrawable(R.drawable.feed_rounded_rectangl_vow))
            }
            1 -> {
                feedImage.setImageDrawable(context.getDrawable(R.drawable.feed_rounded_rectangl_black))
            }
            else -> {
                feedImage.setImageDrawable(context.getDrawable(R.drawable.feed_rounded_rectangl_red_light))
            }
        }

        //Set onclicks
        feedImage.setOnClickListener {
            var intent = Intent(context, VowActivity::class.java)
            intent.putExtra("id", feedID)
            context.startActivity(intent)
        }

    }

    //Return id
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    //Return count
    override fun getItemCount(): Int {
        return arrayList.size
    }

    //Control holders by type
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        //Return feed layout
        val view = LayoutInflater.from(context).inflate(R.layout.feed_item, parent, false)
        return Model(view)

    }

    /* ViewHolders */

    inner class Model(itemView: View) : RecyclerView.ViewHolder(itemView) {

        //Initialize feed widgets
        val feedImage: ImageView = itemView.findViewById(R.id.feed_image)!!
        val feedDate: TextView = itemView.findViewById(R.id.feed_date)!!

    }

}