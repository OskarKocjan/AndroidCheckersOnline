package com.example.androidcheckersonline

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*


class LeaderboardActivity : AppCompatActivity() {

    private lateinit var listViewLeaderboard: ListView

    private lateinit var database: FirebaseDatabase
    private lateinit var playerListRef: DatabaseReference

    private lateinit var playerList: MutableList<Pair<String, Int>>
    private lateinit var playerName: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)
        playerName = intent.getStringExtra("playerName")!!
        listViewLeaderboard = findViewById(R.id.listViewPlayers)

        database = FirebaseDatabase.getInstance()
        playerListRef = database.getReference("players")

        playerList = mutableListOf()

        playerListRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                playerList.clear()
                for (tempPlayer in snapshot.children){
                    playerList.add(tempPlayer.key!! to tempPlayer.getValue(PlayerData::class.java)!!.rank)
                }
                playerList.sortByDescending { it.second }

                val adapter = PlayersAdapter(applicationContext, R.layout.leaderboard_element, playerList)
                listViewLeaderboard.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, "Connection Error", Toast.LENGTH_LONG).show()
            }
        })
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                val intent = Intent()
                intent.putExtra("playerName", playerName)
                setResult(RESULT_OK, intent)
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}