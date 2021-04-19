package com.example.androidcheckersonline

data class PlayerData(var password: String, var rank: Int = 800){

    constructor(): this(""){}
}