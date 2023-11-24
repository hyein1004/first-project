package com.example.registerpage2

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase



class ProductListActivity : AppCompatActivity() {
    private var adapter: MyAdapter? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    val db: FirebaseFirestore = Firebase.firestore
    private var snapshotListener: ListenerRegistration? = null
    val itemsCollectionRef = db.collection("products")
    private val recyclerViewItems by lazy {findViewById<RecyclerView>(R.id.recyclerView) }
    private val cb by lazy { findViewById<CheckBox>(R.id.check) }
    private var start: Int? = null
    private var end: Int? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_list)

        val registbtn2 = findViewById<ExtendedFloatingActionButton>(R.id.registbtn2)
        registbtn2.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        val chatlistbtn = findViewById<ExtendedFloatingActionButton>(R.id.chatlistbtn)
        chatlistbtn.setOnClickListener{
            val intent = Intent(this, ChatListActivity::class.java)
            startActivity(intent)
        }

        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener {
            modalWithRoundCorner()
        }
        cb.setOnCheckedChangeListener{ buttonView, isChecked ->
            fetchProductsFromFirestore()
        }

        recyclerViewItems.layoutManager = LinearLayoutManager(this)
        adapter = MyAdapter(this, emptyList())
        adapter?.setOnItemClickListener {
        }
        recyclerViewItems.adapter = adapter

        updateList()

        this.supportFragmentManager.setFragmentResultListener("MODAL", this) { requestKey, result ->
            if (requestKey == "MODAL") {
                start = result["start"] as Int
                end = result["end"] as Int
                button.text = "${start}원 ~ ${end}원"
                fetchProductsFromFirestore()
            }
        }
        this.supportFragmentManager.setFragmentResultListener("RESET", this) { requestKey, result ->
            if (requestKey == "RESET") {
                start = null
                end = null
                fetchProductsFromFirestore()
                button.text = "가격"
            }
        }
    }
    override fun onStart() {
        super.onStart()

        // snapshot listener for all items
        snapshotListener = itemsCollectionRef.addSnapshotListener { snapshot, error ->
            for (doc in snapshot!!.documentChanges) {
                fetchProductsFromFirestore()
            }
        }
        // sanpshot listener for single item
        /*
        itemsCollectionRef.document("1").addSnapshotListener { snapshot, error ->
            Log.d(TAG, "${snapshot?.id} ${snapshot?.data}")
        }*/
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_logout, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_logout -> {
                // Firebase에서 로그아웃 수행
                Firebase.auth.signOut()

                // Firebase에서 로그아웃된 경우 currentUser는 null이 됩니다.
                if (Firebase.auth.currentUser == null) {
                    // 로그아웃이 성공적으로 이루어졌음을 확인

                    // 여기에서 메인 엑티비티로 이동하는 코드를 추가
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish() // 현재 엑티비티 종료
                } else {
                    showToast("로그아웃 실패")
                }

                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun updateList() {
        itemsCollectionRef.get().addOnSuccessListener { // it: QuerySnapshot
            val items = mutableListOf<Item>()
            for (doc in it) {
                items.add(Item(doc)) // Item의 생성자가 doc를 받아 처리
            }
            adapter?.updateList(items)
        }
    }

    private fun modalWithRoundCorner() {
        val modal= ModalBottomSheet()
        var bundle = Bundle()

        modal.setStyle(DialogFragment.STYLE_NORMAL, R.style.RoundCornerBottomSheetDialogTheme)
        modal.show(supportFragmentManager, ModalBottomSheet.TAG)
    }

    override fun onDestroy() {
        super.onDestroy()
    }


    private fun fetchProductsFromFirestore() {
        // 체크박스 상태 확인
        val showSpecialProducts = cb.isChecked

        // 사용자가 입력한 최소 및 최대 가격
        val minPrice = start ?: 0.0
        val maxPrice = end ?: Double.MAX_VALUE

        // Firestore 쿼리 구성
        var query = db.collection("products")
            .whereGreaterThanOrEqualTo("price", minPrice)
            .whereLessThanOrEqualTo("price", maxPrice)

        query.get()
            .addOnSuccessListener { documents ->
                val items = mutableListOf<Item>()

                // state 필터링
                val stateFilteredItems = if (showSpecialProducts) {
                    documents.filter { doc -> doc.getBoolean("state") == true }
                } else {
                    documents.toList() // 필터링하지 않을 경우 모든 문서 반환
                }

                // 필터링된 문서로부터 Item 객체 생성
                for (document in stateFilteredItems) {
                    items.add(Item(document))
                }

                // Adapter 업데이트
                adapter?.updateList(items)
            }
    }
}