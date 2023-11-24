package com.example.registerpage2

// 리사이클러 뷰 아이템 데이터 클래스
// 발신자, 수신자, 메시지 내용, 보낸 시각
data class ChatItem(val sender: String, val receiver: String, val contents: String, val time: String)

