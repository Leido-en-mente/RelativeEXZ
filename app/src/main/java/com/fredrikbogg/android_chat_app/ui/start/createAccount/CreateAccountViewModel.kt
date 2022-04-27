package com.fredrikbogg.android_chat_app.ui.start.createAccount

import android.content.ContentValues.TAG
import android.util.Log
import android.widget.EditText
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fredrikbogg.android_chat_app.R
import com.fredrikbogg.android_chat_app.data.Event
import com.fredrikbogg.android_chat_app.data.Result
import com.fredrikbogg.android_chat_app.data.db.entity.User
import com.fredrikbogg.android_chat_app.data.db.repository.AuthRepository
import com.fredrikbogg.android_chat_app.data.db.repository.DatabaseRepository
import com.fredrikbogg.android_chat_app.data.model.CreateUser
import com.fredrikbogg.android_chat_app.ui.DefaultViewModel
import com.fredrikbogg.android_chat_app.util.isEmailValid
import com.fredrikbogg.android_chat_app.util.isTextValid
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.ktx.auth


class CreateAccountViewModel : DefaultViewModel() {

    private lateinit var auth: FirebaseAuth
    private var firebaseAuth: FirebaseAuth? = null

    private val dbRepository = DatabaseRepository()
    private val authRepository = AuthRepository()
    private val mIsCreatedEvent = MutableLiveData<Event<FirebaseUser>>()

    val isCreatedEvent: LiveData<Event<FirebaseUser>> = mIsCreatedEvent
    val displayNameText = MutableLiveData<String>() // Two way
    val emailText = MutableLiveData<String>() // Two way
    val passwordText = MutableLiveData<String>() // Two way
    val isCreatingAccount = MutableLiveData<Boolean>()

    private fun createAccount() {
        isCreatingAccount.value = true
        val createUser =
            CreateUser(displayNameText.value!!, emailText.value!!, passwordText.value!!)

        authRepository.createUser(createUser) { result: Result<FirebaseUser> ->
            onResult(null, result)
            if (result is Result.Success) {
                mIsCreatedEvent.value = Event(result.data!!)
                dbRepository.updateNewUser(User().apply {
                    info.id = result.data.uid
                    info.displayName = createUser.displayName
                })
            }
            if (result is Result.Success || result is Result.Error) isCreatingAccount.value = false
        }
    }

    private fun caida() {

        auth = Firebase.auth
        firebaseAuth = FirebaseAuth.getInstance()

        val user = Firebase.auth.currentUser

        auth.createUserWithEmailAndPassword(
                    emailText.value.toString(),
            passwordText.value.toString()
        ).addOnCompleteListener {

            if (it.isSuccessful) {
                Log.d(TAG, "createUserWithEmail:success")
                if (user != null) {
                    user.sendEmailVerification()
                }}}

    }

    fun createAccountPressed() {



        if (!isTextValid(2, displayNameText.value)) {
            mSnackBarText.value = Event("Nombre demasiado corto")
            return
        }

        if (!isEmailValid(emailText.value.toString())) {
            mSnackBarText.value = Event("Correo Electrónico Inválido")
            return
        }
        if (!isTextValid(6, passwordText.value)) {
            mSnackBarText.value = Event("Contraseña muy corta")
            return
        }
        caida()
        createAccount()
    }
}