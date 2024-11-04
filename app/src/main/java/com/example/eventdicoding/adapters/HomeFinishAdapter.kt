package com.example.eventdicoding.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.eventdicoding.R
import com.example.eventdicoding.data.remote.model.Event
import com.example.eventdicoding.databinding.ItemHomeFinishBinding
import com.example.eventdicoding.util.EventUtil

class HomeFinishAdapter(
    private var events: List<Event>,
    private val navController: NavController
) : RecyclerView.Adapter<HomeFinishAdapter.EventViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ItemHomeFinishBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding)
    }

    override fun getItemCount(): Int = events.size

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(events[position], navController)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newEvents: List<Event>) {
        events = newEvents
        notifyDataSetChanged()
    }

    class EventViewHolder(
        private val binding: ItemHomeFinishBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(event: Event, navController: NavController) {
            loadImage(event.imageLogo)
            binding.eventTitle.text = event.name
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
                navController.navigate(R.id.action_fragmentHome_to_detail_activity)
            }
        }
    }
}
