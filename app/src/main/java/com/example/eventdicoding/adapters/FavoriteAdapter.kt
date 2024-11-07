package com.example.eventdicoding.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.eventdicoding.R
import com.example.eventdicoding.data.local.entity.Event
import com.example.eventdicoding.databinding.ListItemBinding
import com.example.eventdicoding.util.DateTime.convertDate
import com.example.eventdicoding.util.EventUtil

class FavoriteAdapter(
    private val navController: NavController
) : ListAdapter<Event, FavoriteAdapter.EventViewHolder>(EventDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(getItem(position), navController)
    }

    class EventViewHolder(
        private val binding: ListItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(event: Event, navController: NavController) {
            binding.apply {
                loadImage(event.imageLogo)
                eventTitle.text = event.name
                eventTime.text = convertDate(itemView.context, event.beginTime, event.endTime)
                eventCategory.text = event.category
                eventCard.setOnClickListener {
                    EventUtil.eventId = event.id
                    navController.navigate(R.id.action_fragmentFavorite_to_detail_activity)
                }
            }
        }

        private fun loadImage(imageUrl: String) {
            Glide.with(binding.root.context)
                .load(imageUrl)
                .centerCrop()
                .into(binding.eventImage)
        }
    }

    class EventDiffCallback : DiffUtil.ItemCallback<Event>() {
        override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem == newItem
        }
    }
}
