package com.example.registerpage2

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

// 채팅방 관련 액티비티
// 판매자에게 메시지를 보내고 Firestore에 메시지 정보 저장
// 자신이 보낸 메시지가 표시됨
class ChatActivity : AppCompatActivity() {
    private lateinit var currentUser: String
    private lateinit var receiverEmail: String

    private lateinit var registration: ListenerRegistration
    private lateinit var adapter: ChatAdapter

    private val chatItemList = arrayListOf<ChatItem>()

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatting_room)

        // 이전 액티비티에서 전달 받은 Item 저장
        val item = intent?.getSerializableExtra("item") as? Item

        // 현재 로그인한 유저, 메시지 받는 사람의 이메일, 판매글 제목 저장
        currentUser = Firebase.auth.currentUser?.email.toString()
        if (item != null) {
            receiverEmail = item.seller
        }

        // 리사이클러 뷰 설정
        val rvList = findViewById<RecyclerView>(R.id.rv_list)
        rvList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        adapter = ChatAdapter(currentUser, chatItemList)
        rvList.adapter = adapter

        // 채팅창이 비어 있으면 전송 버튼 비활성화
        val etChatting = findViewById<EditText>(R.id.et_chatting)
        etChatting.addTextChangedListener { text ->
            val btnSend = findViewById<Button>(R.id.btn_send)
            btnSend.isEnabled = text.toString() != ""
        }

        // 전송 버튼 처리
        val btnSend = findViewById<Button>(R.id.btn_send)
        btnSend.setOnClickListener {
            // 입력 데이터
            val data = hashMapOf(
                "sender" to currentUser,
                "receiver" to receiverEmail,
                "contents" to etChatting.text.toString(),
                "time" to com.google.firebase.Timestamp.now(),
            )
            // Firestore에 기록
            db.collection("Chat").add(data)
                .addOnSuccessListener {
                    etChatting.text.clear()
                    Log.w("ChatActivity", "Document added: $it")
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "전송 실패", Toast.LENGTH_SHORT).show()
                    Log.w("ChatActivity", "Error occurs: $e")
                }
        }

        chatItemList.add(ChatItem("", "", "$receiverEmail 님께 메시지를 보내보세요:)", ""))
        val enterTime = Date(System.currentTimeMillis())

        registration = db.collection("Chat")
            .orderBy("time", Query.Direction.DESCENDING)
            .limit(1)
            .addSnapshotListener { snapshots, e ->
                // 오류 발생 시
                if (e != null) {
                    Log.w("ChatActivity", "Listen failed: $e")
                    return@addSnapshotListener
                }

                // 원하지 않는 문서 무시
                if (snapshots!!.metadata.isFromCache) return@addSnapshotListener

                // 문서 수신
                for (doc in snapshots.documentChanges) {
                    val timestamp = doc.document["time"] as com.google.firebase.Timestamp

                    if (doc.type == DocumentChange.Type.ADDED && timestamp.toDate() > enterTime) {
                        val sender = doc.document["sender"].toString()
                        val receiver = doc.document["receiver"].toString()
                        val contents = doc.document["contents"].toString()

                        val sf = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.KOREA)
                        sf.timeZone = TimeZone.getTimeZone("Asia/Seoul")
                        val time = sf.format(timestamp.toDate())

                        val chatItem = ChatItem(sender, receiver, contents, time)
                        chatItemList.add(chatItem)
                    }
                    adapter.notifyDataSetChanged()
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        registration.remove()
    }
}