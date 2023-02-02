package com.example.uniapp

class MyModel {
    var modelName: String? = null
    private var modelImage: Int = 0

    /*
     * Return the team name
     */
    fun getNames(): String {
        return modelName.toString()
    }

    /*
     * Set a team name
     */
    fun setNames(name: String) {
        this.modelName = name
    }

    /* Return a team logo
     */
    fun getImages(): Int {
        return modelImage
    }

    /* Set a team logo
     */
    fun setImages(image_drawable: Int) {
        this.modelImage = image_drawable
    }
}
