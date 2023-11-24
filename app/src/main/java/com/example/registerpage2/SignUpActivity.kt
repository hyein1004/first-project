package com.example.registerpage2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.firestore.FirebaseFirestore

class SignUpActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Firebase 인증 및 Firestore 초기화
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // 등록 버튼에 대한 OnClickListener 설정
        findViewById<Button>(R.id.btn_register)?.setOnClickListener {
            // 사용자 입력을 가져오기
            val userEmail = findViewById<EditText>(R.id.register_email)?.text.toString()
            val password = findViewById<EditText>(R.id.register_pass)?.text.toString()
            val name = findViewById<EditText>(R.id.register_name)?.text.toString()
            val birthdate = findViewById<EditText>(R.id.register_birthdate)?.text.toString()

            // 필드 중 하나라도 비어 있으면 안내 메시지 표시
            if (userEmail.isEmpty() || password.isEmpty() || name.isEmpty() || birthdate.isEmpty()) {
                showToast("모든 필드를 입력해주세요.")
            } else if (birthdate.length != 6) {
                showToast("생년월일을 6자리로 입력해주세요.")
            } else {
                // 모든 필드가 유효하면 사용자 등록 진행
                registerUser(userEmail, password, name, birthdate)
            }
        }
    }

    // Toast 메시지 표시
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    // Firebase 인증을 사용하여 사용자 등록
    private fun registerUser(userEmail: String, password: String, name: String, birthdate: String) {
        auth.createUserWithEmailAndPassword(userEmail, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // 등록이 성공하면 사용자 데이터를 Firestore에 저장
                    val uid = auth.currentUser?.uid
                    saveUserDataToFirestore(uid, name, birthdate, userEmail)

                    showToast("회원가입 및 로그인이 성공했습니다.")
                    startActivity(Intent(this, ProductListActivity::class.java))
                    finish()
                } else {
                    // 등록이 실패하면 실패 처리
                    handleRegistrationFailure(task.exception)
                }
            }
    }

    // 다양한 유형의 등록 실패 처리
    private fun handleRegistrationFailure(exception: Exception?) {
        when (exception) {
            is FirebaseAuthInvalidCredentialsException -> {
                showToast("이메일 주소 형식이 잘못되었습니다.")
                Log.e("RegisterActivity", "이메일 주소 형식이 잘못되었습니다.", exception)
            }
            else -> {
                showToast("회원가입 또는 로그인에 실패했습니다.")
                Log.e("RegisterActivity", "사용자 등록에 실패했습니다.", exception)
            }
        }
    }

    // 사용자 데이터를 Firestore에 저장
    private fun saveUserDataToFirestore(uid: String?, name: String, birthdate: String, email: String) {
        if (uid != null) {
            val user = hashMapOf(
                "name" to name,
                "birthdate" to birthdate,
                "email" to email
            )

            firestore.collection("users").document(email)
                .set(user)
                .addOnSuccessListener {
                    Log.d("RegisterActivity", "사용자 데이터가 Firestore에 성공적으로 저장되었습니다.")
                }
                .addOnFailureListener { e ->
                    Log.e("RegisterActivity", "Firestore에 사용자 데이터를 저장하는 도중 오류가 발생했습니다.", e)
                }
        }
    }
}
