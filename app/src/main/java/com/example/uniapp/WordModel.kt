package com.example.uniapp

class WordModel {

    var modelName: String? = null

    fun getName(): String {
        return modelName.toString()
    }

    fun setName(name: String) {
        this.modelName = name
    }
}