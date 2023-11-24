package com.example.registerpage2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// 채팅 기능 관련 어댑터
class ChatAdapter(val currentUser: String, val chatItemList: ArrayList<ChatItem>): RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    // 자신이 보낸 메시지와 채팅방 입장 메시지 구분
    override fun getItemViewType(position: Int): Int {
        return if (currentUser == chatItemList[position].sender) {
            1
        } else {
            2
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layout = if (viewType == 1) R.layout.chat_message else R.layout.enter_message
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return chatItemList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.message?.text = chatItemList[position].contents
        holder.date?.text = chatItemList[position].time
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val message: TextView? = itemView.findViewById(R.id.txt_message)
        val date: TextView? = itemView.findViewById(R.id.txt_date)
    }
}

