package com.navee.agroconnectlk

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val etName = findViewById<EditText>(R.id.etName)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etPhone = findViewById<EditText>(R.id.etPhone)
        val rgRole = findViewById<RadioGroup>(R.id.rgRole)
        val btnRegister = findViewById<Button>(R.id.btnRegister)

        btnRegister.setOnClickListener {

            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val phone = etPhone.text.toString().trim()

            val role = when (rgRole.checkedRadioButtonId) {
                R.id.rbFarmer -> "Farmer"
                R.id.rbBuyer -> "Buyer"
                else -> ""
            }

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || phone.isEmpty() || role.isEmpty()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {

                    val userId = auth.currentUser!!.uid

                    val userMap = hashMapOf(
                        "name" to name,
                        "email" to email,
                        "phone" to phone,
                        "role" to role
                    )

                    db.collection("users")
                        .document(userId)
                        .set(userMap)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Account created", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, LoginActivity::class.java))
                            finish()
                        }
                }
                .addOnFailureListener {
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }
        }
    }
}
