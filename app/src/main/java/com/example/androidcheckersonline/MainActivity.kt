package com.example.androidcheckersonline

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {
    internal lateinit var loginBtn: Button
    internal lateinit var loginText: EditText
    internal lateinit var passwordText: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loginBtn = findViewById(R.id.loginBtn)
        loginText = findViewById(R.id.editTextLogin)
        passwordText = findViewById(R.id.editTextPassword)

        //hindus tested
//        val ref = FirebaseDatabase.getInstance().getReference("heros")
//
//        val heroId = ref.push().key !!
//
//        val hero = "Elooooo"
//
//        ref.child(heroId).setValue(hero).addOnCompleteListener {
//            Toast.makeText(applicationContext, "Hero saved successfully", Toast.LENGTH_LONG).show()
//        }








        loginBtn.setOnClickListener { changeToMenu() }
    }

    private fun changeToMenu() {
        val intent: Intent
        intent = Intent(this, ManuActivity::class.java).apply{}
        startActivity(intent)
    }
}