package com.example.eventdicoding.adapters

import android.annotation.SuppressLint
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
        val binding = ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding, navController)
    }

    override fun getItemCount(): Int = events.size

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(events[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateEvents(newEvents: List<Event>) {
        events = newEvents
        notifyDataSetChanged()
    }

    class EventViewHolder(
        private val binding: ListItemBinding,
        private val navController: NavController
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(event: Event) {
            with(binding) {
                eventTitle.text = event.name
                eventTime.text = convertDate(itemView.context, event.beginTime, event.endTime)
                eventCategory.text = event.category

                loadEventImage(event.imageLogo)

                eventCard.setOnClickListener {
                    EventUtil.eventId = event.id
                    navigateToDetailActivity()
                }
            }
        }

        private fun loadEventImage(imageUrl: String?) {
            Glide.with(binding.root.context)
                .load(imageUrl ?: R.drawable.image_placeholder)
                .centerCrop()
                .into(binding.eventImage)
        }

        private fun navigateToDetailActivity() {
            navController.navigate(R.id.action_fragmentUpcoming_to_detail_activity)
        }
    }
}
