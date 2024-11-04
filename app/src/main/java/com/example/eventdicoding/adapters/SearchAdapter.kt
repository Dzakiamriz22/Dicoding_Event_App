package com.example.eventdicoding.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.eventdicoding.data.remote.model.Event
import com.example.eventdicoding.databinding.ListItemBinding
import com.example.eventdicoding.ui.DetailActivity
import com.example.eventdicoding.util.DateTime.convertDate
import com.example.eventdicoding.util.EventUtil

class SearchAdapter(
    private var events: List<Event>
) : RecyclerView.Adapter<SearchAdapter.EventViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemBinding.inflate(inflater, parent, false)
        return EventViewHolder(binding)
    }

    override fun getItemCount(): Int = events.size

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(events[position])
    }

    fun updateEvents(newEvents: List<Event>) {
        events = newEvents
        notifyDataSetChanged()
    }

    class EventViewHolder(
        private val binding: ListItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(event: Event) {
            loadEventImage(event.imageLogo)
            binding.eventTitle.text = event.name
            binding.eventTime.text = convertDate(itemView.context, event.beginTime, event.endTime)
            binding.eventCategory.text = event.category

            binding.eventCard.setOnClickListener {
                EventUtil.eventId = event.id
                navigateToDetailActivity(itemView.context)
            }
        }

        private fun loadEventImage(imageUrl: String?) {
            Glide.with(binding.root.context)
                .load(imageUrl)
                .centerCrop()
                .into(binding.eventImage)
        }

        private fun navigateToDetailActivity(context: Context) {
            val intent = Intent(context, DetailActivity::class.java)
            context.startActivity(intent)
        }
    }
}
