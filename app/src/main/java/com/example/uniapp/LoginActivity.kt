package com.example.uniapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

/*
    This application is making use of Ion library from Koushikdutta
    to retrive json data from Google news api
    This application is making use of Picasson library
    to pull images of articles from Google news api urls
 */

class LoginActivity : AppCompatActivity() {

    private var mAuth = FirebaseAuth.getInstance()
    private var currentUser = mAuth.currentUser
    private var emailEditText: EditText? = null
    private var passwordEditText: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        emailEditText = findViewById<EditText>(R.id.email_edit_text)
        passwordEditText = findViewById<EditText>(R.id.password_edit_text)

    }

    fun lunchMain() {
        try {
            val newIntent = Intent(this, MainActivity::class.java)
            startActivity(newIntent)
        } catch (e: Exception) {
            Log.i("AAAAA", "actvity2")
        }
    }



    fun register(view: View) {
        displayMessage(view, "attempt1")
        if (mAuth.currentUser != null) {
            displayMessage(view, "already loged in")
        } else {
            displayMessage(view, "attempt")
            if (emailEditText != null && passwordEditText != null) {
                mAuth.createUserWithEmailAndPassword(
                    emailEditText!!.text.toString(),
                    passwordEditText!!.text.toString()
                ).addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        closeKeyBoard(this)
                        displayMessage(view, " Success r")

                        val database = Firebase.database
                        val usersRef = database.getReference("users")
                        val newUser = hashMapOf(
                            "email" to emailEditText!!.text.toString(),
                            "country" to "United Kingdom"
                        )
                        usersRef.child(emailEditText!!.text.toString().replace(".", ",")).setValue(newUser)

                        lunchMain()
                    } else {
                        closeKeyBoard(this)
                        displayMessage(view, "Fail r")
                    }
                }
            }
        }
    }

    fun starndardLogin(view: View) {
        mAuth.signInWithEmailAndPassword("test9@aa.aa",
            "12341234").addOnCompleteListener(this) {task ->
            if (task.isSuccessful) {
                closeKeyBoard(this)
                displayMessage(view, "Success l")
                lunchMain()
            } else {
                closeKeyBoard(this)
                displayMessage(view, "Fail l")

            }
        }
    }
    fun login(view: View) {
        mAuth.signInWithEmailAndPassword(emailEditText!!.text.toString(),
            passwordEditText!!.text.toString()).addOnCompleteListener(this) {task ->
            if (task.isSuccessful) {
                closeKeyBoard(this)
                displayMessage(view, "Success l")
                lunchMain()
            } else {
                closeKeyBoard(this)
                displayMessage(view, "Fail l")

            }
        }
    }

    fun logout(view: View) {
        mAuth.signOut()
    }


    private fun displayMessage(view: View, s: String) {
        val sb = Snackbar.make(view, s, Snackbar.LENGTH_SHORT)
        sb.show()

    }

    fun closeKeyBoard(activity: Activity) {
        val inputMethodManager =
            activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(activity.currentFocus?.windowToken, 0)
    }
}