package com.example.attendeaze

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class UserInfoActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etLocation: EditText
    private lateinit var btnSave: Button
    private val sharedPreferencesName = "UserDetails"
    private val userNameKey = "user_name"
    private val userLocationKey = "user_location"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info)

        etName = findViewById(R.id.et_name)
        etLocation = findViewById(R.id.et_location)
        btnSave = findViewById(R.id.btn_save)

        btnSave.setOnClickListener {
            val name = etName.text.toString().trim()
            val location = etLocation.text.toString().trim()

            if (name.isEmpty() || location.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
            } else {
                saveUserDetails(name, location)

                // Redirect to MainActivity after saving
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish() // Close UserInfoActivity
            }
        }
    }

    private fun saveUserDetails(name: String, location: String) {
        val sharedPreferences = getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(userNameKey, name)
        editor.putString(userLocationKey, location)
        editor.apply()

        Toast.makeText(this, "Details saved successfully!", Toast.LENGTH_SHORT).show()
    }
}
