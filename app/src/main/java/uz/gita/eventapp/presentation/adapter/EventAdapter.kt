package uz.gita.eventapp.presentation.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.gita.eventapp.R
import uz.gita.eventapp.data.room.entity.EventData
import uz.gita.eventapp.data.utils.Constants
import uz.gita.eventapp.databinding.ItemEventBinding


class EventAdapter() :
    ListAdapter<EventData, EventAdapter.ViewHolder>(itemEventCallback) {

    private var switchChangedListener: ((EventData) -> Unit)? = null

    fun setSwitchChangedListener(block: (EventData) -> Unit) {
        switchChangedListener = block
    }

    inner class ViewHolder(private val binding: ItemEventBinding) :
        RecyclerView.ViewHolder(binding.root) {


        init {
            binding.internet.setOnClickListener {
                val data = getItem(absoluteAdapterPosition)
                switchChangedListener?.invoke(data.copy(state = 1 - data.state))
            }
        }

        fun onBind() {
            val data = getItem(absoluteAdapterPosition)
            binding.apply {
                text.text = data.name
                internet.isChecked = data.state == 1
                internetIcon.setImageResource(Constants.images[getItem(absoluteAdapterPosition).imageId - 1].image)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.onBind()
}

private val itemEventCallback = object : DiffUtil.ItemCallback<EventData>() {
    override fun areItemsTheSame(oldItem: EventData, newItem: EventData) = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: EventData, newItem: EventData) =
        oldItem.name == newItem.name && oldItem.state == newItem.state

}