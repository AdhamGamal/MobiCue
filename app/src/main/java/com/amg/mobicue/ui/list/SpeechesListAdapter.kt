package com.amg.mobicue.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.amg.mobicue.databinding.SpeechesListItemBinding
import com.amg.mobicue.getColorResource
import com.amg.mobicue.room.SpeechItem

class SpeechesListAdapter:
    ListAdapter<SpeechItem, SpeechesListAdapter.TransViewHolder>(SpeechDiffCallback()) {

    class TransViewHolder(private var binding: SpeechesListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(speech: SpeechItem) {
            //update item UI, set loan by loan
            binding.speech = speech

            val context = binding.root.context
            binding.containerLayout.setBackgroundColor(getColorResource(context ,speech.sBackground))
            binding.speechName.setTextColor(getColorResource(context ,speech.tColor))
            binding.speechContent.setTextColor(getColorResource(context ,speech.tColor))

            binding.root.setOnClickListener { view ->
                view.findNavController().navigate(SpeechesListFragmentDirections.toDisplayFragment(speech.id))
            }
            //set data to view update UI
            binding.executePendingBindings()
        }
    }

    //create each view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransViewHolder {
        return TransViewHolder(
            SpeechesListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    //set viewholder
    override fun onBindViewHolder(holder: TransViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
