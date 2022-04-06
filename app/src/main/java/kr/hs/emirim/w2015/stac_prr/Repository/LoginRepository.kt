package kr.hs.emirim.w2015.stac_prr.Repository

import android.app.Activity
import android.util.Log
import com.google.firebase.auth.FirebaseAuth

object LoginRepository {
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    suspend fun signUp(context: Activity){
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