package com.amg.mobicue.ui.list

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil
import com.amg.mobicue.room.SpeechItem

class SpeechDiffCallback : DiffUtil.ItemCallback<SpeechItem>() {

    override fun areItemsTheSame(oldItem: SpeechItem, newItem: SpeechItem): Boolean {
        return oldItem.id == newItem.id
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: SpeechItem, newItem: SpeechItem): Boolean {
        return oldItem == newItem
    }
}
