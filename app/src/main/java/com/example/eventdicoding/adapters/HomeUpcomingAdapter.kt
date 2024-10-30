package com.example.eventdicoding.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.eventdicoding.R
import com.example.eventdicoding.data.remote.model.Event
import com.example.eventdicoding.databinding.ItemHomeUpcomingBinding
import com.example.eventdicoding.util.EventUtil

class HomeUpcomingAdapter(
    private var events: List<Event>,
    private val navController: NavController
) : RecyclerView.Adapter<HomeUpcomingAdapter.EventViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ItemHomeUpcomingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding)
    }

    override fun getItemCount(): Int = events.size

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(events[position], navController)
    }

    fun updateEvents(newEvents: List<Event>) {
        events = newEvents
        notifyDataSetChanged()
    }

    class EventViewHolder(
        private val binding: ItemHomeUpcomingBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(event: Event, navController: NavController) {
            Glide.with(binding.root.context)
                .load(event.imageLogo)
                .centerCrop()
                .into(binding.ivEventImage)

            binding.tvEventTitle.text = event.name

            binding.eventCard.setOnClickListener {
                EventUtil.eventId = event.id
                navController.navigate(R.id.action_fragmentHome_to_detailActivity)
            }
        }
    }
}
