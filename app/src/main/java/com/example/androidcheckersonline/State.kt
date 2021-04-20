package com.example.androidcheckersonline

import com.uno.dbbc.checkers.Cell

class State(var givenBoard: Board, var player1: Player, var player2: Player, var currenPlayer: Player, var singlePlayerMode: Boolean, var srcCell: Cell, var dstCell: Cell, var srcCellFixed: Boolean)