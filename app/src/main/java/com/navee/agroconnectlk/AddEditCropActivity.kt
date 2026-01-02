package com.navee.agroconnectlk

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AddEditCropActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private var cropId: String? = null

    private lateinit var etName: EditText
    private lateinit var etPrice: EditText
    private lateinit var etQty: EditText
    private lateinit var btnSave: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_crop)

        // ---------- UI ----------
        etName = findViewById(R.id.etCropName)
        etPrice = findViewById(R.id.etPrice)
        etQty = findViewById(R.id.etQty)
        btnSave = findViewById(R.id.btnSave)

        // ---------- GET DATA FROM INTENT (EDIT MODE) ----------
        cropId = intent.getStringExtra("id")

        if (cropId != null) {
            // Edit mode → fill data
            etName.setText(intent.getStringExtra("name"))
            etPrice.setText(intent.getStringExtra("price"))
            etQty.setText(intent.getStringExtra("qty"))
            btnSave.text = "Update Crop"
        }

        // ---------- SAVE / UPDATE ----------
        btnSave.setOnClickListener {

            val name = etName.text.toString().trim()
            val price = etPrice.text.toString().trim()
            val qty = etQty.text.toString().trim()

            if (name.isEmpty() || price.isEmpty() || qty.isEmpty()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val user = auth.currentUser
            if (user == null) {
                Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val userId = user.uid
            
            // Fetch farmer's full name and phone from 'users' collection
            db.collection("users").document(userId).get().addOnSuccessListener { userDoc ->
                if (userDoc.exists()) {
                    val farmerName = userDoc.getString("name") ?: "Agro Farmer"
                    val farmerPhone = userDoc.getString("phone") ?: ""

                    val product = Product(
                        id = cropId ?: "",
                        name = name,
                        price = price,
                        quantity = qty,
                        farmerId = userId,
                        farmerName = farmerName,
                        farmerPhone = farmerPhone
                    )

                    if (cropId == null) {
                        // -------- ADD NEW CROP --------
                        db.collection("crops")
                            .add(product)
                            .addOnSuccessListener { doc ->
                                db.collection("crops")
                                    .document(doc.id)
                                    .update("id", doc.id)

                                Toast.makeText(this, "Crop added", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Failed to add crop", Toast.LENGTH_SHORT).show()
                            }

                    } else {
                        // -------- UPDATE EXISTING CROP --------
                        db.collection("crops")
                            .document(cropId!!)
                            .set(product)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Crop updated", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Failed to update crop", Toast.LENGTH_SHORT).show()
                            }
                    }
                } else {
                    Toast.makeText(this, "User data not found in database", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Error fetching user info", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
