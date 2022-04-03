package kr.hs.emirim.w2015.stac_prr.ViewModel

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class LoginViewModel : ViewModel(){
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun signUp(context:Activity){
        auth.signInAnonymously()
            .addOnCompleteListener(context) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "signInAnonymously:success - 사용자등록 완료")
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("TAG", "signInAnonymously:failure", task.exception)
                }
            }
    }
}