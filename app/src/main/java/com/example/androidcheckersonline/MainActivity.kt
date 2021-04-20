package com.example.androidcheckersonline

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

    private lateinit var loginButton: Button
    private lateinit var loginText: EditText
    private lateinit var passwordText: EditText

    private lateinit var database: FirebaseDatabase
    private lateinit var playerRef: DatabaseReference

    private lateinit var playerName: String
    private lateinit var playerPassword: String
    private var playerData: PlayerData? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loginButton = findViewById(R.id.buttonLogin)
        loginText = findViewById(R.id.editTextLogin)
        passwordText = findViewById(R.id.editTextPassword)
        playerName = ""
        playerPassword = ""

        database = FirebaseDatabase.getInstance()

        loginButton.setOnClickListener {
            playerName = loginText.text.toString()
            playerPassword = passwordText.text.toString()

            if(!(playerName == "" || playerPassword == "")){
                playerExistence()
            } else {
                Toast.makeText(applicationContext, "Please insert login and password", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun goToNextActivity(){
        val intent = Intent(this, MenuActivity::class.java).apply{}
        intent.putExtra("playerName", playerName)
        startActivity(intent)
    }

    private fun playerExistence(){
        playerRef = database.getReference("players/$playerName")
        playerRef.addValueEventListener(object: ValueEventListener{

            @SuppressLint("SetTextI18n")
            override fun onDataChange(snapshot: DataSnapshot) {
                loginButton.isEnabled = false
                loginButton.text = "LOGGING IN"

                if(snapshot.exists()){
                    playerData = snapshot.getValue(PlayerData::class.java)

                    if(playerPassword == playerData!!.password){
                        goToNextActivity()
                    } else {
                        loginButton.text = getString(R.string.loginBtnText)
                        loginButton.isEnabled = true
                        Toast.makeText(applicationContext, "Bad password or login", Toast.LENGTH_LONG).show()
                    }

                } else {
                    playerRef.setValue(PlayerData(playerPassword))
                    goToNextActivity()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, "Connection Error", Toast.LENGTH_LONG).show()
            }

        })
    }
}