package com.example.eventdicoding.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.eventdicoding.R
import com.example.eventdicoding.databinding.ItemEventBinding
import com.example.eventdicoding.data.remote.model.Event
import com.example.eventdicoding.util.EventUtil

class EventAdapter(
    private val navController: NavController
) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    private var events: List<Event> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding)
    }

    override fun getItemCount(): Int = events.size

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(events[position], navController)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateEvents(newEvents: List<Event>) {
        events = newEvents
        notifyDataSetChanged()
    }

    class EventViewHolder(
        private val binding: ItemEventBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(event: Event, navController: NavController) {
            Glide.with(binding.root.context)
                .load(event.imageLogo)
                .centerCrop()
                .into(binding.eventImage)

            binding.eventTitle.text = event.name

            val formattedTime = "${event.beginTime} - ${event.endTime}"
            binding.tvEventTime.text = formattedTime

            binding.tvEventCategory.text = event.category

            binding.root.setOnClickListener {
                EventUtil.eventId = event.id
                navController.navigate(R.id.action_fragmentHome_to_detail_activity)
            }
        }
    }
}
