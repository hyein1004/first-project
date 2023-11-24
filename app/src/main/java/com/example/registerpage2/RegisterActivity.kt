package com.example.registerpage2

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val itemsCollectionRef = db.collection("products")
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_register)

        val useremail = auth.currentUser?.email

        val title = findViewById<EditText>(R.id.editTitle)
        val price = findViewById<EditText>(R.id.editPrice)
        val content = findViewById<EditText>(R.id.editContent)
        val submitbtn = findViewById<Button>(R.id.modify)

        val titlewarning = findViewById<TextView>(R.id.ProductTitle)
        val pricewarning = findViewById<TextView>(R.id.pricewarning)
        val contentwarning = findViewById<TextView>(R.id.contentwarning)

        title.addTextChangedListener {
            titlewarning.text = ""
        }

        price.addTextChangedListener {
            pricewarning.text = ""
        }

        content.addTextChangedListener {
            contentwarning.text = ""
        }

        submitbtn.setOnClickListener {
            val titleText = title.text.toString()
            val priceText = price.text.toString()
            val contentText = content.text.toString()

            if (titleText.isEmpty()) {
                title.requestFocus()
                titlewarning.setTextColor(Color.RED)
                titlewarning.text = "제목을 입력해주세요."
            }

            val price = priceText.toIntOrNull()
            if (price == null) {
                pricewarning.setTextColor(Color.RED)
                pricewarning.text = "가격을 입력해주세요."
            }

            if (contentText.isEmpty()) {
                contentwarning.setTextColor(Color.RED)
                contentwarning.text = "내용을 입력해주세요."
            }

            if (titleText.isNotEmpty() && price != null && contentText.isNotEmpty()) {
                val state = true


                val itemMap = hashMapOf(
                    "seller" to useremail,
                    "title" to titleText,
                    "price" to price,
                    "content" to contentText,
                    "state" to state
                )

               // itemsCollectionRef.document(id).set(itemMap)
                itemsCollectionRef.add(itemMap)
                    .addOnSuccessListener { documentReference ->
                        // 데이터가 성공적으로 추가된 경우 실행할 코드

                        val intent = Intent(this, ProductListActivity::class.java)
                        startActivity(intent)
                    }
                    .addOnFailureListener { e ->
                        // 데이터 추가 실패한 경우 실행할 코드
                        // 실패 이유에 대한 처리를 추가할 수 있습니다
                    }
            }
        }
    }
}
