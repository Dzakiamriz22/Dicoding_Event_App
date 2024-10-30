package com.example.eventdicoding.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.eventdicoding.R
import com.example.eventdicoding.data.local.entity.Event
import com.example.eventdicoding.databinding.ListItemBinding
import com.example.eventdicoding.util.DateTime.convertDate
import com.example.eventdicoding.util.EventUtil

class FavoriteAdapter(
    private var favoriteEvents: List<Event>,
    private val navController: NavController
) : RecyclerView.Adapter<FavoriteAdapter.EventViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding)
    }

    override fun getItemCount(): Int = favoriteEvents.size

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(favoriteEvents[position], navController)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newEvents: List<Event>) {
        favoriteEvents = newEvents
        notifyDataSetChanged()
    }

    class EventViewHolder(
        private val binding: ListItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(event: Event, navController: NavController) {
            loadImage(event.imageLogo)
            binding.eventTitle.text = event.name
            binding.eventTime.text = convertDate(itemView.context, event.beginTime, event.endTime)
            binding.eventCategory.text = event.category
            setClickListener(event.id, navController)
        }

        private fun loadImage(imageUrl: String) {
            Glide.with(binding.root.context)
                .load(imageUrl)
                .centerCrop()
                .into(binding.eventImage)
        }

        private fun setClickListener(eventId: Int, navController: NavController) {
            binding.eventCard.setOnClickListener {
                EventUtil.eventId = eventId
                navController.navigate(R.id.action_fragmentFavorite_to_detailActivity)
            }
        }
    }
}
