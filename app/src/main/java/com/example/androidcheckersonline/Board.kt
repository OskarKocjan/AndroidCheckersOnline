package com.example.androidcheckersonline

import com.uno.dbbc.checkers.Cell
import java.io.Serializable
import java.util.*
import kotlin.math.abs

public class Board(val BOARD_SIZE: Int = 8, var changes: Int = 0):Serializable {

    var lightPieces: ArrayList<Piece> = ArrayList<Piece>()
        get() = field
        set(value) {field = value}

    var darkPieces: ArrayList<Piece> = ArrayList<Piece>()
        get() = field
        set(value) {field = value}

    var board: Array<Array<Cell>> = Array(BOARD_SIZE) { row -> Array(BOARD_SIZE) { col -> Cell(row, col) } }
        get() = field
        set(value) {field = value}




    // The configuration is as follows:
    //L -> Light Colored Piece  	D-> Dark Colored Piece  	 _-> a blank cell
    //	    0  1  2  3  4  5  6  7
    //	 0  L  _  L  _  L  _  L  _
    //	 1  _  L  _  L  _  L  _  L
    //	 2  L  _  L  _  L  _  L  _
    //	 3  _  _  _  _  _  _  _  _
    //	 4  _  _  _  _  _  _  _  _
    //	 5  _  D  _  D  _  D  _  D
    //	 6  D  _  D  _  D  _  D  _
    //	 7  _  D  _  D  _  D  _  D





    fun initialBoardSetup() {
        for (i in 0 until BOARD_SIZE) {
            for (j in 0 until BOARD_SIZE) {
                board[i][j] = Cell(i, j)

            }
        }
        run {
            var column = 0
            while (column < BOARD_SIZE) {
                this.board[0][column].placePiece(Piece(Piece.LIGHT))
                this.board[2][column].placePiece(Piece(Piece.LIGHT))
                this.board[6][column].placePiece(Piece(Piece.DARK))
                lightPieces.add(this.board[0][column].piece!!)
                lightPieces.add(this.board[2][column].piece!!)
                darkPieces.add(this.board[6][column].piece!!)
                column += 2
            }
        }
        var column = 1
        while (column < BOARD_SIZE) {
            board[1][column].placePiece(Piece(Piece.LIGHT))
            board[5][column].placePiece(Piece(Piece.DARK))
            board[7][column].placePiece(Piece(Piece.DARK))
            lightPieces.add(board[1][column].piece!!)
            darkPieces.add(board[5][column].piece!!)
            darkPieces.add(board[7][column].piece!!)
            column += 2
        }
    } // end of initialBoardSetup


    @Throws(IllegalArgumentException::class)
    fun getCell(x: Int, y: Int): Cell? {
        require(!(x < 0 || x > 7 || y < 0 || y > 7)) { "The coordinates provided are outside of the board" }
        return board[x][y]
    }


    @Throws(java.lang.IllegalArgumentException::class)
    fun getPieces(givenColor: String): ArrayList<Piece> {
        if (givenColor == Piece.LIGHT) {
            return lightPieces
        } else if (givenColor == Piece.DARK) {
            return darkPieces
        }
        throw java.lang.IllegalArgumentException("Given color is not the color of the pieces in board. Given color: $givenColor")
    }

    @Throws(NullPointerException::class, java.lang.IllegalArgumentException::class)
    fun movePiece(fromX: Int, fromY: Int, toX: Int, toY: Int): ArrayList<Cell?>? {
        val srcCell = getCell(fromX, fromY)
        val dstCell = getCell(toX, toY)
        val changedCells = ArrayList<Cell?>()
        if (srcCell!!.piece == null) {
            throw NullPointerException("The source cell does not contains piece to move.")
        }
        require(dstCell!!.piece == null) { "The destination cell already contains a piece. Cannot move to occupied cell." }
        if (isCaptureMove(srcCell, dstCell)) {
            val capturedCellX = (fromX + toX) / 2
            val capturedCellY = (fromY + toY) / 2
            val capturedPiece = board[capturedCellX][capturedCellY].piece
            removePiece(capturedPiece)
            if (capturedPiece != null) {
                changedCells.add(capturedPiece.placedCell)
            } // here capturedPiece might cause null pointer exception. Not sure yet.
        }
        srcCell.movePiece(dstCell)
        changedCells.add(srcCell)
        changedCells.add(dstCell)
        return changedCells
    } // End of move



    @Throws(java.lang.IllegalArgumentException::class)
    fun movePiece(src: IntArray, dst: IntArray): ArrayList<Cell?>? {
        require(!(src.size != 2 || dst.size != 2)) { "The given dimension of the points does not match." }
        return movePiece(src[0], src[1], dst[0], dst[1])
    }


    @Throws(java.lang.IllegalArgumentException::class)
    fun movePiece(move: IntArray): ArrayList<Cell?>? {
        require(move.size == 4) { "The given dimension of the points does not match." }
        return movePiece(move[0], move[1], move[2], move[3])
    }



    @Throws(IllegalStateException::class, java.lang.IllegalArgumentException::class)
    fun removePiece(capturedPiece: Piece?) {
        if (capturedPiece?.color.equals(Piece.LIGHT)) {
            check(lightPieces.remove(capturedPiece)) { "Error removing the piece" }
            capturedPiece?.placedCell?.placePiece(null)
        } else if (capturedPiece?.color.equals(Piece.DARK)) {
            check(darkPieces.remove(capturedPiece)) { "Error removing the piece" }
            capturedPiece?.placedCell?.placePiece(null)
        }
    }



    @Throws(java.lang.IllegalArgumentException::class)
    fun possibleMoves(x: Int, y: Int): ArrayList<Cell?> {
        require(!(x < 0 || x > 7 || y < 0 || y > 7)) { "Invalid value of x or y provided. (x, y) = ($x, )" }
        return possibleMoves(board[x][y])
    }



    @Throws(NullPointerException::class)
    fun possibleMoves(givenCell: Cell?): ArrayList<Cell?> {
        if (givenCell == null) {
            throw NullPointerException("Given Cell is null. Cannot find the possible moves of null Cell")
        }
        val nextMoves = ArrayList<Cell?>()
        val givenPiece = givenCell.piece ?: return nextMoves
        val playerColor: String = givenPiece.color
        val opponentColor: String = Piece.getOpponentColor(playerColor).toString()


        // if the piece is light-colored
        if (playerColor == Piece.LIGHT) {
            //the next move will be one row ahead i.e in row number X+1
            var nextX = givenCell.x + 1
            if (nextX < 8) {
                //next move = (currentRow +1, currentColumn +1)
                var nextY = givenCell.y + 1
                //if the cell is not out of bound further checking is required
                if (nextY < 8) {
                    //if the cell is empty then add the cell to next move
                    if (!board[nextX][nextY].containsPiece()) {
                        nextMoves.add(board[nextX][nextY])
                    } else if (board[nextX][nextY].piece!!.color == opponentColor) {
                        val xCoordAfterHoping = nextX + 1
                        val yCoordAfterHoping = nextY + 1
                        if (xCoordAfterHoping < 8 && yCoordAfterHoping < 8 && !board[xCoordAfterHoping][yCoordAfterHoping].containsPiece()) {
                            nextMoves.add(board[xCoordAfterHoping][yCoordAfterHoping])
                        }
                    }
                }


                //next move = (currentRow+1, currentColumn -1)
                nextY = givenCell.y - 1
                // if the cell is within bound and does not contains a piece then add it to nextMoves
                if (nextY >= 0) {
                    if (!board[nextX][nextY].containsPiece()) {
                        nextMoves.add(board[nextX][nextY])
                    } else if (board[nextX][nextY].piece!!.color == opponentColor) {
                        val xCoordAfterHoping = nextX + 1
                        val yCoordAfterHoping = nextY - 1
                        if (xCoordAfterHoping < 8 && yCoordAfterHoping >= 0 && !board[xCoordAfterHoping][yCoordAfterHoping].containsPiece()) {
                            nextMoves.add(board[xCoordAfterHoping][yCoordAfterHoping])
                        }
                    }
                }
            }

            //if the given piece is king then have to look to the row behind
            if (givenPiece.isKing) {
                nextX = givenCell.x - 1
                if (nextX >= 0) {
                    //nextMove = (currentRow -1, currentColumn+1)
                    //add this cell if it is within bound and doesnot contain piece
                    var nextY = givenCell.y + 1
                    if (nextY < 8 && !board[nextX][nextY].containsPiece()) {
                        nextMoves.add(board[nextX][nextY])
                    } else if (nextY < 8 && board[nextX][nextY].piece!!.color == opponentColor) {
                        val xCoordAfterHoping = nextX - 1
                        val yCoordAfterHoping = nextY + 1
                        if (xCoordAfterHoping >= 0 && yCoordAfterHoping < 8 && !board[xCoordAfterHoping][yCoordAfterHoping].containsPiece()) {
                            nextMoves.add(board[xCoordAfterHoping][yCoordAfterHoping])
                        }
                    }
                    //nextMove = (currentRow-1, currentColumn-1)
                    //add this cell if it is within bound and does not contains piece
                    nextY = givenCell.y - 1
                    if (nextY >= 0 && !board[nextX][nextY].containsPiece()) {
                        nextMoves.add(board[nextX][nextY])
                    } else if (nextY >= 0 && board[nextX][nextY].piece!!.color == opponentColor) {
                        val xCoordAfterHoping = nextX - 1
                        val yCoordAfterHoping = nextY - 1
                        if (xCoordAfterHoping >= 0 && yCoordAfterHoping >= 0 && !board[xCoordAfterHoping][yCoordAfterHoping].containsPiece()) {
                            nextMoves.add(board[xCoordAfterHoping][yCoordAfterHoping])
                        }
                    }
                }
            }
        } else if (givenPiece.color == Piece.DARK) {
            //dark pieces are on the higher rows and to move it forward we have to move them to rows with lower row number.
            //So by assigning currentRow = currentRow -1, we are actually advancing the pieces

            //next move will be on the next row of current row. Rember that currentRow -= 1 will advance the row for darker pieces
            var nextX = givenCell.x - 1
            if (nextX >= 0) {
                //next move = (currentRow -1, currentColumn +1) which is a row ahead and a column to right
                var nextY = givenCell.y + 1
                if (nextY < 8 && !board[nextX][nextY].containsPiece()) {
                    nextMoves.add(board[nextX][nextY])
                } else if (nextY < 8 && board[nextX][nextY].piece!!.color == opponentColor) {
                    val xCoordAfterHoping = nextX - 1
                    val yCoordAfterHoping = nextY + 1
                    if (xCoordAfterHoping >= 0 && yCoordAfterHoping < 8 && !board[xCoordAfterHoping][yCoordAfterHoping].containsPiece()) {
                        nextMoves.add(board[xCoordAfterHoping][yCoordAfterHoping])
                    }
                }
                //next move = (currentRow -1, currentColumn+1) which is a row ahead and a column to left
                nextY = givenCell.y - 1
                if (nextY >= 0 && !board[nextX][nextY].containsPiece()) {
                    nextMoves.add(board[nextX][nextY])
                } else if (nextY >= 0 && board[nextX][nextY].piece!!.color == opponentColor) {
                    val xCoordAfterHoping = nextX - 1
                    val yCoordAfterHoping = nextY - 1
                    if (xCoordAfterHoping >= 0 && yCoordAfterHoping >= 0 && !board[xCoordAfterHoping][yCoordAfterHoping].containsPiece()) {
                        nextMoves.add(board[xCoordAfterHoping][yCoordAfterHoping])
                    }
                }
            }

            //if the piece is king we have to look back; Remember in Dark pieces back row = currentRow +1
            if (givenPiece.isKing) {
                //getting to row behind current row
                nextX = givenCell.x + 1
                if (nextX < 8) {
                    //next move = (currentRow +1, currentColumn+1) which is a row behind and a column right
                    var nextY = givenCell.y + 1
                    if (nextY < 8 && !board[nextX][nextY].containsPiece()) {
                        nextMoves.add(board[nextX][nextY])
                    } else if (nextY < 8 && board[nextX][nextY].piece!!.color == opponentColor) {
                        val xCoordAfterHoping = nextX + 1
                        val yCoordAfterHoping = nextY + 1
                        if (xCoordAfterHoping < 8 && yCoordAfterHoping < 8 && !board[xCoordAfterHoping][yCoordAfterHoping].containsPiece()) {
                            nextMoves.add(board[xCoordAfterHoping][yCoordAfterHoping])
                        }
                    }

                    //next move = (currentRow +1, currentColumn-1) which is a row behind and a column left
                    nextY = givenCell.y - 1
                    if (nextY >= 0 && !board[nextX][nextY].containsPiece()) {
                        nextMoves.add(board[nextX][nextY])
                    } else if (nextY >= 0 && board[nextX][nextY].piece!!.color == opponentColor) {
                        val xCoordAfterHoping = nextX + 1
                        val yCoordAfterHoping = nextY - 1
                        if (xCoordAfterHoping < 8 && yCoordAfterHoping >= 0 && !board[xCoordAfterHoping][yCoordAfterHoping].containsPiece()) {
                            nextMoves.add(board[xCoordAfterHoping][yCoordAfterHoping])
                        }
                    }
                }
            }
        } // end of else if dark piece
        return nextMoves
    } // end of possibleMoves method


    @Throws(NullPointerException::class)
    fun possibleMoves(givenPiece: Piece?): ArrayList<Cell?> {
        if (givenPiece == null) {
            throw NullPointerException("The Piece provided is null. Cannot find possible moves of a null Piece")
        }
        return possibleMoves(givenPiece.placedCell)
    }



    @Throws(NullPointerException::class)
    fun getCaptureMoves(givenCell: Cell?): ArrayList<Cell?> {
        if (givenCell == null) {
            throw NullPointerException("The Cell provided is null.")
        }
        val possibleMovesOfCell = possibleMoves(givenCell)
        val capturingMoves = ArrayList<Cell?>()
        for (dstCell in possibleMovesOfCell) {
            if (isCaptureMove(givenCell, dstCell)) {
                capturingMoves.add(dstCell)
            }
        }
        return capturingMoves
    }



    @Throws(java.lang.IllegalArgumentException::class)
    fun getCaptureMoves(x: Int, y: Int): ArrayList<Cell?> {
        require(!(x < 0 || x > 7 || y < 0 || y > 7)) { "Invalid value of x or y provided. (x, y) = ($x, )" }
        return getCaptureMoves(board[x][y])
    }




    @Throws(NullPointerException::class, java.lang.IllegalArgumentException::class)
    fun isCaptureMove(srcCell: Cell?, dstCell: Cell?): Boolean {
        if (srcCell == null) {
            throw NullPointerException("The source cell is null. Cannot tell if the move is capture move or not if source cell is null.")
        }
        if (dstCell == null) {
            throw NullPointerException("The destination cell is null. Cannot tell if the move is capture move or not if destination cell is null.")
        }
        requireNotNull(srcCell.piece) { "The source cell does not contain a piece. Cannot be capture move if source cell does not have a piece. SrcCell: (" + srcCell.x + ", " + srcCell.y + ")" }
        return (abs(srcCell.x - dstCell.x) == 2 && abs(srcCell.y - dstCell.y) == 2)

    }



    @Throws(java.lang.IllegalArgumentException::class)
    fun isCaptureMove(givenMove: IntArray): Boolean {
        require(givenMove.size == 4) { "The dimension of the array that represents the move does not matches" }
        return isCaptureMove(board[givenMove[0]][givenMove[1]], board[givenMove[2]][givenMove[3]])
    }


}
