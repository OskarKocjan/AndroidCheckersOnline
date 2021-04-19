package com.example.androidcheckersonline

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import java.util.*

class PlayersAdapter(val cntxt: Context, val layoutId: Int, val playerList: MutableList<Pair<String,Int>>):
    ArrayAdapter<Pair<String,Int>>(cntxt, layoutId, playerList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater = LayoutInflater.from(cntxt)
        val view = layoutInflater.inflate(layoutId, null)

        val textViewLeaderboardPlayerName = view.findViewById<TextView>(R.id.textViewLeaderboardPlayerName)
        val textViewLeaderboardRank = view.findViewById<TextView>(R.id.textViewLeaderboardRank)

        val player = playerList[position]

        textViewLeaderboardPlayerName.text = player.first
        textViewLeaderboardRank.text = player.second.toString()

        return view
    }
}