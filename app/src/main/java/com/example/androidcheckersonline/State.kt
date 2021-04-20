package com.example.androidcheckersonline

import com.uno.dbbc.checkers.Cell

class State(var givenBoard: Board, var currentPlayerName: String){
    constructor(): this(Board(), "" )
}