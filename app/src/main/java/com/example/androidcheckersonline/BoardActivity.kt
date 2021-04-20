package com.example.androidcheckersonline

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.uno.dbbc.checkers.Cell
import java.util.*

class BoardActivity : AppCompatActivity() {
    private lateinit var buttonsId: IntArray
    private lateinit var buttonBoard: Array<Array<Button?>>
    private lateinit var moves: ArrayList<Cell?>
    private lateinit var highlightedCells: ArrayList<Cell>
    private lateinit var player1: Player
    private lateinit var player2: Player
    private lateinit var currentPlayer: Player
    private var computerMode: Boolean = false
    private var computerTurn: Boolean = false
    private var srcCellFixed: Boolean = false
    private var cellBoard = Board()
    private var srcCell: Cell? = null
    private var dstCell: Cell? = null
    private lateinit var delayHandler: Handler


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board)

        cellBoard.initialBoardSetup()
        srcCell = null
        dstCell = null
        srcCellFixed = false
        delayHandler = Handler()
        highlightedCells = ArrayList()
        buttonsId = intArrayOf(R.id.button0, R.id.button2, R.id.button4, R.id.button6,
                R.id.button9, R.id.button11, R.id.button13, R.id.button15,
                R.id.button16, R.id.button18, R.id.button20, R.id.button22,
                R.id.button25, R.id.button27, R.id.button29, R.id.button31,
                R.id.button32, R.id.button34, R.id.button36, R.id.button38,
                R.id.button41, R.id.button43, R.id.button45, R.id.button47,
                R.id.button48, R.id.button50, R.id.button52, R.id.button54,
                R.id.button57, R.id.button59, R.id.button61, R.id.button63)
        println(buttonsId)

        buttonBoard = Array<Array<Button?>>(8) { arrayOfNulls(8) }
        println(buttonBoard[0][0])


        var index = 0
        for (i in 0..7) {
            for (j in 0..7) {
                if ((i + j) % 2 == 0) {
                    buttonBoard[i][j] = findViewById(buttonsId[index])
                    index++
                    buttonBoard[i][j]!!.tag = i * 10 + j
                    buttonBoard[i][j]!!.setBackgroundColor(Color.TRANSPARENT)
                    buttonBoard[i][j]!!.backgroundTintMode = PorterDuff.Mode.SRC_ATOP
                    buttonBoard[i][j]!!.setOnClickListener(listener)
                }
            }
        }





        updateBoard(buttonBoard, cellBoard)
        moves = ArrayList()

        chooseColorDialog()
        choosePlayerDialog()



    }


    fun choosePlayerDialog() {

        computerMode = false
        updateTurnTracker()

    }

    /*
     * Creates dialog menu to let player 1 pick their color
     */
    fun chooseColorDialog() {
        val choices = arrayOf<CharSequence>("Light", "Dark")

        player1 = Player(Piece.LIGHT)
        player2 = Player(Piece.DARK)
        currentPlayer = player2
        updateTurnTracker()

        }






    private val listener = View.OnClickListener { v ->
        val tag = v.tag as Int
        val xCord = tag / 10
        val yCord = tag % 10
        if (!computerTurn) {
            playerTurn(xCord, yCord)
        }
    }

    fun playerTurn(xCord: Int, yCord: Int) {

        // If both players have pieces, game IS RUNNING
        if (player1.hasMoves(cellBoard) && player1.hasMoves(cellBoard)) {

            // If piece exists AND color of piece matches players piece AND counter == 0, let the player take a turn
            if (cellBoard.getCell(xCord, yCord)!!.containsPiece() && cellBoard.getCell(xCord, yCord)!!.placedPiece?.color.equals(currentPlayer.color) && srcCell == null) {
                unHighlightPieces() // unhighlight other pieces if user clicks a source cell
                srcCell = cellBoard.getCell(xCord, yCord)!!
                moves = cellBoard.possibleMoves(srcCell)

                //If the user taps the cell with no moves then show the message stating that
                if (moves.isEmpty()) {
                    Toast.makeText(applicationContext, "No possible moves!", Toast.LENGTH_SHORT).show()
                    srcCell = null
                    updateTurnTracker()
                } else {
                    showPossibleMoves(moves)
                    srcCell = cellBoard.getCell(xCord, yCord)!!
                    updatePiecePressed(srcCell!!)
                }
            } else if (srcCell != null && srcCell == cellBoard.getCell(xCord, yCord) && !srcCellFixed) {
                srcCell = null
                updatePieces(xCord, yCord) // updates the graphical pieces
                updateTurnTracker()
            } else if (!cellBoard.getCell(xCord, yCord)!!.containsPiece() && moves.contains(cellBoard.getCell(xCord, yCord)) && srcCell != null) {
                dstCell = cellBoard.getCell(xCord, yCord)!!
                onSecondClick(srcCell!!, dstCell!!)
            }
        }

        // If player who is light runs out of pieces, they lose
        if (!player1.hasMoves(cellBoard) && player2.hasMoves(cellBoard) ||
                player1.hasMoves(cellBoard) && !player2.hasMoves(cellBoard)) {
            gameOverDialog()
        } else if (!player1.hasMoves(cellBoard) && !player2.hasMoves(cellBoard)) {
            Toast.makeText(applicationContext, "DRAW, NO WINNERS!", Toast.LENGTH_LONG).show()
        }
    }


    fun unHighlightPieces() {
        var highlightedCell: Cell
        while (!highlightedCells.isEmpty()) {
            highlightedCell = highlightedCells.removeAt(0)
            if (highlightedCell.piece!!.color == Piece.LIGHT) {
                if (highlightedCell.piece!!.isKing) {
                    buttonBoard[highlightedCell.x][highlightedCell.y]!!.setBackgroundResource(R.drawable.light_king_piece)
                } else {
                    buttonBoard[highlightedCell.x][highlightedCell.y]!!.setBackgroundResource(R.drawable.light_piece)
                }
            } else {
                if (highlightedCell.piece!!.isKing) {
                    buttonBoard[highlightedCell.x][highlightedCell.y]!!.setBackgroundResource(R.drawable.dark_king_piece)
                } else {
                    buttonBoard[highlightedCell.x][highlightedCell.y]!!.setBackgroundResource(R.drawable.dark_piece)
                }
            }
        }
    }


    fun updateTurnTracker() {
        if (currentPlayer != null) {
            // Get all the pieces of the current player that can move & highlight them

            // Get all the pieces of the current player that can move & highlight them
            val currentPlayerPieces = cellBoard.getPieces(currentPlayer.color!!)
            var moves: ArrayList<Cell?>
            if (currentPlayerPieces != null) {
                for (piece in currentPlayerPieces) {
                    moves = cellBoard.possibleMoves(piece)
                    if (!moves.isEmpty()) {
                        if (piece.color == Piece.DARK && piece.isKing) {
                            buttonBoard[piece.placedCell!!.x][piece.placedCell!!.y]!!.setBackgroundResource(R.drawable.dark_king_highlighted)
                        } else if (piece.color == Piece.DARK) {
                            buttonBoard[piece.placedCell!!.x][piece.placedCell!!.y]!!.setBackgroundResource(R.drawable.dark_piece_highlighted)
                        } else if (piece.color == Piece.LIGHT && piece.isKing) {
                            buttonBoard[piece.placedCell!!.x][piece.placedCell!!.y]!!.setBackgroundResource(R.drawable.light_king_highlighted)
                        } else if (piece.color == Piece.LIGHT) {
                            buttonBoard[piece.placedCell!!.x][piece.placedCell!!.y]!!.setBackgroundResource(R.drawable.light_piece_highlighted)
                        }
                        highlightedCells.add(piece.placedCell!!)
                    }
                }
            }
        }
    }


    fun showPossibleMoves(moves: ArrayList<Cell?>) {
        for (cell in moves) {
            buttonBoard[cell!!.x][cell.y]!!.setBackgroundResource(R.drawable.possible_moves_image) // color possible moves square
        }
    }


    fun updatePiecePressed(givenCell: Cell) {
        // If current player is light AND the piece selected is a light piece, player can ONLY move light pieces and can jump ONLY dark pieces
        if (currentPlayer.color.equals(Piece.LIGHT) && givenCell.piece!!.color == Piece.LIGHT) {

            // If light AND king
            if (givenCell.piece!!.isKing) {
                buttonBoard[givenCell.x][givenCell.y]!!.setBackgroundResource(R.drawable.light_king_piece_pressed)
            } else {
                buttonBoard[givenCell.x][givenCell.y]!!.setBackgroundResource(R.drawable.light_piece_pressed) // fill selected light piece as pressed piece image
            }
        }
        // If current player is dark AND the piece selected is a dark piece, player can ONLY move dark pieces and can jump ONLY light pieces
        if (currentPlayer.color.equals(Piece.DARK) && givenCell.piece!!.color == Piece.DARK) {

            // If dark AND king
            if (cellBoard.getCell(givenCell.x, givenCell.y)!!.placedPiece!!.isKing) {
                buttonBoard[givenCell.x][givenCell.y]!!.setBackgroundResource(R.drawable.dark_king_piece_pressed)
            } else {
                buttonBoard[givenCell.x][givenCell.y]!!.setBackgroundResource(R.drawable.dark_piece_pressed) // fill selected dark piece as pressed piece image
            }
        }
    }

    fun gameOverDialog() {
        updateTurnTracker()
        val winner: String
        winner = if (!player1.hasMoves(cellBoard)) {
            "Player 2"
        } else {
            "Player 1"
        }


    }


    fun updatePieces(xCord: Int, yCord: Int) {

        // For all of the possible moves colored in on the cellBoard, after a piece moves we want to remove them
        var possMoves: Cell
        for (i in moves.indices) {
            possMoves = moves[i]!!
            buttonBoard[possMoves.x][possMoves.y]!!.setBackgroundResource(R.drawable.blank_square) // color possible moves blank
        }

        // If the piece is light
        if (cellBoard.getCell(xCord, yCord)!!.placedPiece!!.color.equals(Piece.LIGHT) && cellBoard.getCell(xCord, yCord)!!.containsPiece()) {
            // If piece is light AND is king
            if (cellBoard.getCell(xCord, yCord)!!.placedPiece!!.isKing) {
                buttonBoard[xCord][yCord]!!.setBackgroundResource(R.drawable.light_king_piece)
            } else {
                buttonBoard[xCord][yCord]!!.setBackgroundResource(R.drawable.light_piece)
            }
        } else {
            // // If piece is dark AND is king
            if (cellBoard.getCell(xCord, yCord)!!.placedPiece!!.isKing) {
                buttonBoard[xCord][yCord]!!.setBackgroundResource(R.drawable.dark_king_piece)
            } else {
                buttonBoard[xCord][yCord]!!.setBackgroundResource(R.drawable.dark_piece)
            }
        }
    }

    /*
     * When a piece jumps an opponent piece, we want to remove the piece jumped and update new piece graphic at its destination
     * @param int xCordSrc - The x-coordinate of a piece that will jump opponent piece
     * @param int yCordSrc - The y-coordinate of a piece that will jump opponent piece
     * @param int xCordDst - The new x-coordinate of a piece after it jumped an opponent piece
     * @param int yCordDst - The new y-coordinate of a piece after it jumped an opponent piece
     * @param Cell pieceCaptured - The piece that was captured
     */
    fun updatePieces(changedCells: ArrayList<Cell?>) {

        // For all of the possible moves colored in on the cellBoard, after a piece jumps we want to remove them
        var possMoves: Cell
        for (i in moves.indices) {
            possMoves = moves[i]!!
            buttonBoard[possMoves.x][possMoves.y]!!.setBackgroundResource(R.drawable.blank_square) // color possible moves blank
        }
        for (cell in changedCells) {
            if (!cell!!.containsPiece()) {
                buttonBoard[cell!!.x][cell.y]!!.setBackgroundResource(R.drawable.blank_square)
            } else if (cell!!.piece!!.color == Piece.LIGHT) {
                if (cell.piece!!.isKing) {
                    buttonBoard[cell.x][cell.y]!!.setBackgroundResource(R.drawable.light_king_piece)
                } else {
                    buttonBoard[cell.x][cell.y]!!.setBackgroundResource(R.drawable.light_piece)
                }
            } else if (cell.piece!!.color == Piece.DARK) {
                if (cell.piece!!.isKing) {
                    buttonBoard[cell.x][cell.y]!!.setBackgroundResource(R.drawable.dark_king_piece)
                } else {
                    buttonBoard[cell.x][cell.y]!!.setBackgroundResource(R.drawable.dark_piece)
                }
            }
        }
    }


    fun onSecondClick(givenSrcCell: Cell, givenDstCell: Cell) {
        unHighlightPieces()
        val captureMove = cellBoard.isCaptureMove(givenSrcCell, givenDstCell)
        val changedCells = cellBoard.movePiece(givenSrcCell.coords, givenDstCell.coords)!! // moves piece, store captured piece into array list
        updatePieces(changedCells)
        if (captureMove) {
            moves = cellBoard.getCaptureMoves(givenDstCell) // stores the future capture moves of the cell

            // If the piece that captured opponents piece has no capture moves, end turn
            if (moves.isEmpty()) {
                srcCell = null
                dstCell = null
                srcCellFixed = false
                changeTurn()
            } else {
                srcCell = dstCell
                srcCellFixed = true
                updatePiecePressed(srcCell!!)
                showPossibleMoves(moves)

                //If current player is computer

            }
        } else {
            srcCell = null
            dstCell = null
            srcCellFixed = false
            changeTurn()
        }
    }

    fun changeTurn() {
        // If both players have moves, we can switch turns
        if (player1.hasMoves(cellBoard) && player2.hasMoves(cellBoard)) {
            if (currentPlayer.equals(player1)) {
                currentPlayer = player2
                updateTurnTracker()
            } else {
                currentPlayer = player1
                if (computerMode) {
                    computerTurn = false
                }
                updateTurnTracker()
            }
        } else {
            gameOverDialog()
        }
    }


    fun updateBoard(buttonIndexes: Array<Array<Button?>>, board: Board) {
        for (i in 0..7) {
            for (j in 0..7) {
                if ((i + j) % 2 == 0) {
                    if (!board.getCell(i, j)!!.containsPiece()) {
                        buttonIndexes[i][j]!!.setBackgroundResource(R.drawable.blank_square)
                    } else if (board.getCell(i, j)!!.placedPiece!!.color == Piece.LIGHT) {
                        //King light piece
                        if (board.getCell(i, j)!!.placedPiece!!.isKing) {
                            buttonIndexes[i][j]!!.setBackgroundResource(R.drawable.light_king_piece)
                        } else {
                            buttonIndexes[i][j]!!.setBackgroundResource(R.drawable.light_piece)
                        }
                    } else if (board.getCell(i, j)!!.placedPiece!!.color == Piece.DARK) {
                        // King dark piece
                        if (board.getCell(i, j)!!.placedPiece!!.isKing) {
                            buttonIndexes[i][j]!!.setBackgroundResource(R.drawable.dark_king_piece)
                        } else {
                            buttonIndexes[i][j]!!.setBackgroundResource(R.drawable.dark_piece)
                        }
                    }
                }
            }
        }
    }





}