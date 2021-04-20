package com.example.androidcheckersonline

import java.io.Serializable


class Player(var color: String? = null) : Serializable {

    fun hasMoves(board: Board): Boolean {
        val pieces = board.getPieces(color!!)
        if (pieces.size > 0) {
            for (piece in pieces) {
                if (board.possibleMoves(piece)!!.size > 0) {
                    return true
                }
            }
        }
        return false
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Player) {
            return false
        }
        return other.color == color

    }
}