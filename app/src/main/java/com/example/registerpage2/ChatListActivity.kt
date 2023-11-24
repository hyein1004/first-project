package com.example.registerpage2

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import org.checkerframework.checker.units.qual.min
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import kotlin.math.min

// 현재 로그인한 사용자의 채팅 리스트
// firestore에서 다른 사용자들에게 받은 구매문의 메시지들을 가져와서 목록으로 표시
class ChatListActivity : AppCompatActivity() {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatting_room_list)

        val chatListView = findViewById<ListView>(R.id.chat_list)
        val noChatMessageView = findViewById<TextView>(R.id.no_chat_message)

        val currentUser = Firebase.auth.currentUser?.email.toString()

        if (currentUser != null) {
            // 현재 사용자가 받은 채팅 메시지를 가져옴
            db.collection("Chat")
                .whereEqualTo("receiver", currentUser)
                .get()
                .addOnSuccessListener { documents ->
                    val chatList = ArrayList<ChatItem>()
                    for (document in documents) {
                        val sender = document.getString("sender")
                        val contents = document.getString("contents")
                        val timeStamp = document.getTimestamp("time")

                        if (sender != null && contents != null && timeStamp != null) {
                            val sf = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.KOREA)
                            sf.timeZone = TimeZone.getTimeZone("Asia/Seoul")
                            val time = sf.format(timeStamp.toDate())

                            val chat = ChatItem(sender, "", contents, time)
                            chatList.add(chat)
                        }
                    }

                    // 받은 채팅이 없을 때
                    if(chatList.isEmpty()){
                        noChatMessageView.visibility = View.VISIBLE
                    }
                    // ListView에 채팅 메시지 표시
                    else{
                        val adapter = ChatListAdapter(this, chatList)
                        chatListView.adapter = adapter

                        adapter.notifyDataSetChanged()
                        noChatMessageView.visibility = View.GONE
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w("ChatListActivity", "Error getting documents: ", exception)
                }
        }
    }

    class ChatListAdapter(context: Context, private val chatList: ArrayList<ChatItem>) : ArrayAdapter<ChatItem>(context, 0, chatList) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var itemView = convertView
            if (itemView == null) {
                itemView = LayoutInflater.from(context).inflate(R.layout.chatting_room_item, parent, false)
            }

            val chat = chatList[position]

            val senderView = itemView!!.findViewById<TextView>(R.id.sender)
            val timeView = itemView.findViewById<TextView>(R.id.time)
            val contentsView = itemView.findViewById<TextView>(R.id.contents)

            senderView.text = chat.sender
            timeView.text = chat.time

            // 메시지가 너무 길거나 줄바꿈 문자(엔터키)가 있으면 ...으로 생략하여 표시
            val newlineIndex = chat.contents.indexOf("\n")
            val shortContents = if (chat.contents.length > 48 || newlineIndex != -1) {
                "${chat.contents.substring(0, min(48, if (newlineIndex != -1) newlineIndex else chat.contents.length))}..."
            } else {
                chat.contents
            }
            contentsView.text = shortContents

            return itemView
        }
    }
}