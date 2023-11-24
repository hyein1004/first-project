package com.example.registerpage2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        findViewById<Button>(R.id.login)?.setOnClickListener {
            val userEmail = findViewById<EditText>(R.id.username)?.text.toString()
            val password = findViewById<EditText>(R.id.password)?.text.toString()

            if (validateInputs(userEmail, password)) {
                doLogin(userEmail, password)
            }
        }
    }

    private fun validateInputs(userEmail: String, password: String): Boolean {
        if (userEmail.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "이메일과 비밀번호를 모두 입력해주세요.", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun doLogin(userEmail: String, password: String) {
        auth.signInWithEmailAndPassword(userEmail, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // 로그인 성공 시 메시지 표시
                    Toast.makeText(this, "로그인이 성공했습니다.", Toast.LENGTH_SHORT).show()

                    // ProductListActivity로 바로 이동
                    startActivity(Intent(this, ProductListActivity::class.java))
                    finish()
                } else {
                    Log.w("SignInActivity", "signInWithEmailAndPassword", task.exception)
                    Toast.makeText(this, "아이디 혹은 비밀번호가 잘못됐습니다.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
