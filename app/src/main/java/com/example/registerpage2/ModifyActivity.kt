package com.example.registerpage2

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.*
import androidx.core.widget.addTextChangedListener
import com.google.firebase.firestore.FirebaseFirestore

class ModifyActivity : AppCompatActivity() {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val itemsCollectionRef = db.collection("products")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_modify)

        val modifybtn = findViewById<Button>(R.id.modify)

       val item = intent?.getSerializableExtra("item") as? Item

        val title = findViewById<TextView>(R.id.ProductTitle)
        val price = findViewById<EditText>(R.id.editPrice)
        val content = findViewById<TextView>(R.id.productContent)
        val StateRadioGroup = findViewById<RadioGroup>(R.id.StateRadioGroup)
        val pricewarning = findViewById<TextView>(R.id.pricewarning)
        var state: Boolean? = null
        var id: String? = null

        if(item != null){
            id = item.id
            title.text = item.title
            price.text = Editable.Factory.getInstance().newEditable(item.price.toString())
            content.text = item.content
            state = item.state

            if(state){
                val onSale = StateRadioGroup.findViewById<RadioButton>(R.id.onSale)
                onSale.isChecked = true
            }
            else{
                val soldOut = StateRadioGroup.findViewById<RadioButton>(R.id.SoldOut)
                soldOut.isChecked = true
            }

        }


        price.addTextChangedListener {
            pricewarning.text = ""
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

        modifybtn.setOnClickListener {
            val priceText = price.text.toString()

            val price = priceText.toIntOrNull()
            if (price == null) {
                pricewarning.setTextColor(Color.RED)
                pricewarning.text = "가격을 입력해주세요."
            }

            if (price != null) {
                val itemMap = hashMapOf(
                    "price" to price.toInt(),
                    "state" to state
                )

                // itemsCollectionRef.document(id).set(itemMap)
                if (id != null) {
                    itemsCollectionRef.document(id).update(itemMap as Map<String, Any>)
                        .addOnSuccessListener { documentReference ->
                            // 데이터가 성공적으로 추가된 경우 실행할 코드
                            Log.d("ModifyActivity", "Document ID: $id")
                            val intent = Intent(this, ProductListActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        .addOnFailureListener { e ->
                            // 데이터 추가 실패한 경우 실행할 코드
                            // 실패 이유에 대한 처리를 추가할 수 있습니다
                        }
                }
            }
        }
    }
}