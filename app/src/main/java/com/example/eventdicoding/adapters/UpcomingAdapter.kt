package com.example.eventdicoding.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.eventdicoding.R
import com.example.eventdicoding.data.remote.model.Event
import com.example.eventdicoding.databinding.ListItemBinding
import com.example.eventdicoding.util.DateTime.convertDate
import com.example.eventdicoding.util.EventUtil

class UpcomingAdapter(
    private var events: List<Event>,
    private val navController: NavController
) : RecyclerView.Adapter<UpcomingAdapter.EventViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemBinding.inflate(inflater, parent, false)
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
        private val binding: ListItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(event: Event, navController: NavController) {
            loadEventImage(event.imageLogo)
            binding.eventTitle.text = event.name
            binding.eventTime.text = convertDate(itemView.context, event.beginTime, event.endTime)
            binding.eventCategory.text = event.category

            binding.eventCard.setOnClickListener {
                EventUtil.eventId = event.id
                navigateToDetailActivity(navController)
            }
        }

        private fun loadEventImage(imageUrl: String?) {
            Glide.with(binding.root.context)
                .load(imageUrl)
                .centerCrop()
                .into(binding.eventImage)
        }

        private fun navigateToDetailActivity(navController: NavController) {
            navController.navigate(R.id.action_fragmentUpcoming_to_detail_activity)
        }
    }
}
