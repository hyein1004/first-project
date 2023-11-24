package com.example.registerpage2

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.registerpage2.R
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import java.io.Serializable

data class Item(val id: String, val content: String, val seller: String, val price: Int, val state: Boolean = true, val title: String) : Serializable {
    constructor(doc: QueryDocumentSnapshot) :
            this(doc.id, doc["content"].toString(),doc["seller"].toString(),doc["price"].toString().toIntOrNull() ?: 0,
                doc["state"] as Boolean, doc["title"].toString())
    constructor(key: String, map: Map<*, *>) :
            this(key, map["content"].toString(),map["seller"].toString(),map["price"].toString().toIntOrNull() ?: 0,
                map["state"] as Boolean, map["title"].toString())
}

class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view)

class MyAdapter(private val context: Context, private var items: List<Item>): RecyclerView.Adapter<MyViewHolder>() {
    private val auth = FirebaseAuth.getInstance()

    fun interface OnItemClickListener {
        fun onItemClick(student_id: String)
    }

    private var itemClickListener: OnItemClickListener? = null

        fun setOnItemClickListener(listener: OnItemClickListener) { //아이템 클릭시
        itemClickListener = listener
        }

        fun updateList(newList: List<Item>) {
        items = newList
        notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.sale_item, parent, false)
        return MyViewHolder(view)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val item = items[position]
            holder.view.findViewById<TextView>(R.id.tv_title).text = item.title
            holder.view.findViewById<TextView>(R.id.tv_information).text = item.seller
            holder.view.findViewById<TextView>(R.id.tv_price).text = item.price.toString() +"원"
            if(item.state) {
                holder.view.findViewById<TextView>(R.id.tv_state).visibility = View.GONE
            }
            else{
                holder.view.findViewById<TextView>(R.id.tv_state).visibility = View.VISIBLE
            }


            holder.view.setOnClickListener{
                itemClickListener?.onItemClick(item.id)
                val useremail = auth.currentUser?.email
                if(item.seller == useremail) {
                    val intent = Intent(context, ModifyActivity::class.java)
                    intent.putExtra("item", item)
                    context.startActivity(intent)

                }
                else{
                    val intent = Intent(context, PostDetailActivity::class.java)
                    intent.putExtra("item", item)
                    context.startActivity(intent)
                }
            }
        }

        override fun getItemCount() = items.size
        }