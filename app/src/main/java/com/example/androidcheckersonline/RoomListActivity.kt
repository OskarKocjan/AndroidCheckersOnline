package com.example.androidcheckersonline

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.google.firebase.database.*

class RoomListActivity : AppCompatActivity() {

    private lateinit var listViewRooms: ListView
    private lateinit var buttonCreateRoom: Button

    private lateinit var database: FirebaseDatabase
    private lateinit var roomRef: DatabaseReference
    private lateinit var roomsRef: DatabaseReference

    private lateinit var playerName: String
    private lateinit var roomName: String
    private lateinit var roomsList: MutableList<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_list)
        listViewRooms = findViewById(R.id.listViewRooms)
        buttonCreateRoom = findViewById(R.id.buttonCreateRoom)

        playerName = intent.getStringExtra("playerName")!!
        roomName = playerName

        database = FirebaseDatabase.getInstance()

        roomsRef = database.getReference("rooms")

        buttonCreateRoom.setOnClickListener{
            buttonCreateRoom.text = "CREATING ROOM"
            buttonCreateRoom.isEnabled = false
            roomRef = database.getReference("rooms/$roomName/host")
            addRoomEventListener()
        }

        listViewRooms.setOnItemClickListener{ adapterView, view, position, id ->
            roomName = roomsList[position]
            roomRef = database.getReference("rooms/$roomName/guest")
            addRoomEventListener()
        }

        roomsRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                roomsList = mutableListOf()
                val rooms = snapshot.children
                for(tempRoom in rooms){
                    roomsList.add(tempRoom.key.toString())

                    val adapter = ArrayAdapter<String>(applicationContext, android.R.layout.simple_list_item_1, roomsList)
                    listViewRooms.adapter = adapter
                }
            }

            override fun onCancelled(error: DatabaseError) {}

        })
    }

    private fun addRoomEventListener() {
        roomRef.setValue(playerName)
        roomRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                buttonCreateRoom.text = "CREATE ROOM"
                buttonCreateRoom.isEnabled = true
                goToNextActivity()
            }

            override fun onCancelled(error: DatabaseError) {
                buttonCreateRoom.text = "CREATE ROOM"
                buttonCreateRoom.isEnabled = true
                Toast.makeText(applicationContext, "Error creating room", Toast.LENGTH_LONG).show()
            }

        })
    }

    private fun goToNextActivity(){
        TODO()
        val intent = Intent(this, MenuActivity::class.java).apply{}
        intent.putExtra("roomName", roomName)
        startActivity(intent)
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