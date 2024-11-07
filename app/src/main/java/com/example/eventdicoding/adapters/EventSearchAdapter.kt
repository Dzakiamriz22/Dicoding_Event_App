package com.example.eventdicoding.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.eventdicoding.data.remote.model.Event
import com.example.eventdicoding.databinding.ItemEventBinding
import com.example.eventdicoding.ui.DetailActivity
import com.example.eventdicoding.util.DateTime.convertDate
import com.example.eventdicoding.util.EventUtil

class EventSearchAdapter(
    private var eventList: List<Event>
) : RecyclerView.Adapter<EventSearchAdapter.EventViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemEventBinding.inflate(inflater, parent, false)
        return EventViewHolder(binding)
    }

    override fun getItemCount(): Int = eventList.size

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(eventList[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateEventList(newEventList: List<Event>) {
        eventList = newEventList
        notifyDataSetChanged()
    }

    class EventViewHolder(
        private val binding: ItemEventBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(event: Event) {
            loadEventImage(event.imageLogo)
            binding.eventTitle.text = event.name
            binding.tvEventTime.text = convertDate(itemView.context, event.beginTime, event.endTime)
            binding.tvEventCategory.text = event.category

            binding.card.setOnClickListener {
                EventUtil.eventId = event.id
                navigateToEventDetail(itemView.context)
            }
        }

        private fun loadEventImage(imageUrl: String?) {
            Glide.with(binding.root.context)
                .load(imageUrl)
                .centerCrop()
                .into(binding.eventImage)
        }

        private fun navigateToEventDetail(context: Context) {
            val intent = Intent(context, DetailActivity::class.java)
            context.startActivity(intent)
        }
    }
}
