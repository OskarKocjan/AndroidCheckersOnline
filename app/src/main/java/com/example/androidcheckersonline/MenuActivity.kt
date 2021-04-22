package com.example.androidcheckersonline

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class MenuActivity : AppCompatActivity() {

    private lateinit var leaderboardButton: Button
    private lateinit var roomListButton: Button
    private lateinit var textViewPlayerName: TextView
    private lateinit var textViewPlayerRank: TextView

    private lateinit var playerRef: DatabaseReference

    private lateinit var playerData: PlayerData


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        leaderboardButton = findViewById(R.id.buttonLeaderboard)
        roomListButton = findViewById(R.id.buttonRoomList)
        textViewPlayerName = findViewById(R.id.textViewPlayerName)
        textViewPlayerRank = findViewById(R.id.textViewRank)

        val playerName = intent.getStringExtra("playerName")
        val database = FirebaseDatabase.getInstance()

        playerRef = database.getReference("players/$playerName")
        playerRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                playerData = snapshot.getValue(PlayerData::class.java)!!
                textViewPlayerRank.text = playerData!!.rank.toString()
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        textViewPlayerName.text = playerName


        leaderboardButton.setOnClickListener{
            val intent = Intent(this, LeaderboardActivity::class.java).apply{}
            intent.putExtra("playerName", playerName)
            startActivity(intent)
        }

        roomListButton.setOnClickListener{
            val intent = Intent(this, RoomListActivity::class.java).apply{}
            intent.putExtra("myRank", playerData.rank)
            intent.putExtra("playerName", playerName)
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == 2404) {
            if (data != null) {
                val playerName = data.getStringExtra("playerName")
            }
        }
    }

}