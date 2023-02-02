package com.example.uniapp

class MyModel {
    var modelName: String? = null
    private var modelImage: Int = 0

    fun getName(): String {
        return modelName.toString()
    }

    fun setName(name: String) {
        this.modelName = name
    }

    fun getImage(): Int {
        return modelImage
    }

    fun setImage(image_drawable: Int) {
        this.modelImage = image_drawable
    }
}
