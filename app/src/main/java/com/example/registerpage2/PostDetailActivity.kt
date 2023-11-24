package com.example.registerpage2

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

// 판매글 상세 페이지 관련
class PostDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_detail)

        // 이전 액티비티에서 전달 받은 Item 저장
        val item = intent?.getSerializableExtra("item") as? Item

        val title = findViewById<TextView>(R.id.ProductTitle)
        val seller = findViewById<TextView>(R.id.seller)
        val price = findViewById<TextView>(R.id.showPrice)
        val content = findViewById<TextView>(R.id.productContent)
        val StateRadioGroup = findViewById<RadioGroup>(R.id.StateRadioGroup)

        var state: Boolean? = null
        var id: String? = null

        if (item != null) {
            id = item.id
            title.text = item.title
            seller.text = item.seller
            price.text = Editable.Factory.getInstance().newEditable("₩ ${item.price}")
            content.text = item.content
            state = item.state

            if (state != null) {
                if (state) {
                    val onSale = StateRadioGroup.findViewById<RadioButton>(R.id.onSale)
                    onSale.isChecked = true
                } else {
                    val soldOut = StateRadioGroup.findViewById<RadioButton>(R.id.SoldOut)
                    soldOut.isChecked = true
                }
            }
        }

        StateRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.onSale -> {
                    // onSale 버튼이 클릭되었을 때
                    state = true
                }
                R.id.SoldOut -> {
                    // SoldOut 버튼이 클릭되었을 때
                    state = false
                }
            }
        }

        // 라디오 버튼 수정 비활성화
        for (i in 0 until StateRadioGroup.childCount) {
            val button = StateRadioGroup.getChildAt(i) as RadioButton
            button.isEnabled = false
        }

        // 메시지 보내기 버튼 클릭 시 ChatActivity로 이동
        val messageButton = findViewById<Button>(R.id.message_button)
        messageButton.setOnClickListener {
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("item", item)
            startActivity(intent)
        }
    }
}
