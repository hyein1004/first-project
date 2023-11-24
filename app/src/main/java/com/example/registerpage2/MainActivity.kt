package com.example.registerpage2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity



class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.button_login).setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }

        // 로그아웃 버튼 클릭 시 LoginActivity로 이동
        //findViewById<Button>(R.id.button_logout).setOnClickListener {
          //  startActivity(Intent(this, SignInActivity::class.java))
        //}
        // 회원가입 버튼 클릭 시 RegisterActivity로 이동
        findViewById<Button>(R.id.button_register).setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

//        fun doLogin(userEmail: String, password: String) {
//            Firebase.auth.signInWithEmailAndPassword(userEmail, password)
//                .addOnCompleteListener(this) { // it: Task<AuthResult!>
//                    if (it.isSuccessful) {
//                        startActivity(
//                            Intent(this, ProductListActivity::class.java))
//                        finish()
//                    } else {
//                        Log.w("LoginActivity", "signInWithEmail", it.exception)
//                        Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
//                    }
//                }
//        }
//
//        val signin = findViewById<Button>(R.id.signin)
//        signin.setOnClickListener {
//            val userEmail = findViewById<EditText>(R.id.email).text.toString()
//            val password = findViewById<EditText>(R.id.password).text.toString()
//            doLogin(userEmail, password)
//        }

    }
}