package com.uno.dbbc.checkers

import com.example.androidcheckersonline.Piece
import java.io.Serializable

class Cell(var x: Int, var y: Int, var placedPiece: Piece? = null) : Serializable {


    val piece: Piece?
        get() = placedPiece

    /**
     * @return Returns the coordinate of this cell as an integer array of length two,
     * in which the first element is the x-coordinate of the cell and the second value is the y-coordinate of the cell.
     */
    val coords: IntArray
        get() = intArrayOf(x, y)

    /**
     * @param givenPiece The piece to place in this cell. If the piece are to their opposite end then the piece is made King.
     */
    fun placePiece(givenPiece: Piece?) {
        placedPiece = givenPiece
        if (givenPiece != null) {
            givenPiece.placedCell = this
            if (x == 0 && givenPiece.color == Piece.DARK) {
                placedPiece?.makeKing()
            } else if (x == 7 && givenPiece.color == Piece.LIGHT) {
                placedPiece?.makeKing()
            }
        }
    }

    /**
     * @return Returns if the cell contains any piece i.e returns true if this cell contains piece and false if the placed piece of this cell is null.
     */
    fun containsPiece(): Boolean {
        return placedPiece != null
    }

    /**
     * @param anotherCell Cell where the piece in this cell is to be moved.
     * @throws IllegalArgumentException Throws IllegalArgumentException if the Cell provided is null.
     */
    @Throws(IllegalArgumentException::class)
    fun movePiece(anotherCell: Cell?) {
        requireNotNull(anotherCell) { "Provided cell is null. Cannot move to a null Cell." }
        anotherCell.placePiece(placedPiece)
        placedPiece?.placedCell = anotherCell
        placedPiece = null
    }

    /**
     * @return String representation of the Cell.
     */
    override fun toString(): String {
        var str = ""
        str += "Cell Loc: (" + x + ", " + y + ") \t Placed piece: "
        if (placedPiece == null) {
            str += "nothing\n"
        } else {
            str += placedPiece!!.color + "  isKing: " + placedPiece!!.isKing + "\n"
        }
        return str
    }




} // End of class
