package com.example.androidcheckersonline

import com.uno.dbbc.checkers.Cell
import java.io.Serializable


class Piece(var color: String, var isKing: Boolean = false, var placedCell: Cell? = null) : Serializable {





    fun makeKing() {
        isKing = true
    }





    /**
     * Checks if the given Object is equal to this Piece.
     * @param obj Object to be compared
     * @return Returns true if the given object is equal to this Piece, false otherwise.
     * The given object is equal to this Piece if the given object is an instance of Piece, has the same color as this Piece and is located in the same Cell location as this Piece.
     */
    override fun equals(obj: Any?): Boolean {
        if (obj !is Piece) {
            return false
        }
        val givenPiece = obj
        return (givenPiece.color == color && givenPiece.isKing == isKing && givenPiece.placedCell?.x === placedCell?.x && givenPiece.placedCell?.y === placedCell?.y)
    }

    companion object {
        const val DARK = "Dark"
        const val LIGHT = "Light"


        fun getOpponentColor(givenColor: String): String? {
            return when (givenColor) {
                DARK -> {
                    LIGHT
                }
                LIGHT -> {
                    DARK
                }
                else -> {
                    println("Given color is not valid. Given color: $givenColor")
                    null
                }
            }
        }

    }

        /**
         * Returns the color of the opponent player i.e. returns the color opposite of this Piece
         * @param: Color of the player
         * @return: opponent's color
         */




} //End of class
