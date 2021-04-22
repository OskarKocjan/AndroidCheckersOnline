package com.example.androidcheckersonline

import java.io.Serializable

class CellPieceToDataBase(var x: Int, var y: Int, var color: String = "none", var isKing: Boolean = false, var havePiece: Boolean = false) : Serializable{
    constructor(): this( 0, 0 )

    fun equals(cellTemp: CellPieceToDataBase): Boolean{
        return this.x == cellTemp.x && this.y == cellTemp.y && this.color == cellTemp.color && this.isKing == cellTemp.isKing && this.havePiece == cellTemp.havePiece
    }


}

























