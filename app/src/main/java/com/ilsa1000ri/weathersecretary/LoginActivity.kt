package com.ilsa1000ri.weathersecretary

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kakao.sdk.auth.AuthCodeHandlerActivity

class LoginActivity : AppCompatActivity() {
    //구글 로그인 부분
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)
        // Initialize Firebase Auth
        auth = Firebase.auth

        // Google 로그인 버튼을 찾습니다.
        val googleSignInButton = findViewById<SignInButton>(R.id.googleSignInButton)
        val kakaoSignInButton = findViewById<ImageButton>(R.id.kakaoSignInButton)

        // Google 로그인 버튼 클릭 이벤트 핸들러를 설정합니다.
        googleSignInButton.setOnClickListener {
                // Configure Google Sign In
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build()

                googleSignInClient = GoogleSignIn.getClient(this, gso)

                // signIn() 함수 호출
                signIn()
        }

        kakaoSignInButton.setOnClickListener {
            val intent = Intent(this, AuthCodeHandlerActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

        override fun onStart() {
            super.onStart()
            // 작업을 초기화할 때 사용자가 현재 로그인되어 있는지 확인
            val currentUser = auth.currentUser
            updateUI(currentUser)
        }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)

            // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
            if (requestCode == RC_SIGN_IN) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    val account = task.getResult(ApiException::class.java)!!
                    Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    // Google Sign In failed, update UI appropriately
                    Log.w(TAG, "Google sign in failed", e)
                }
            }
        }

        private fun firebaseAuthWithGoogle(idToken: String) {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            auth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success")
                        val user = auth.currentUser
                        updateUI(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.exception)
                        updateUI(null)
                    }
                }
        }
    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            // 사용자가 로그인되어 있는 경우 MainActivity로 이동합니다.
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // 로그인 액티비티를 종료합니다.
        } else {
            // 사용자가 로그아웃한 경우에 대한 처리를 여기에 추가할 수 있습니다.
        }
    }
    companion object {
        private const val TAG = "Login"
        private const val RC_SIGN_IN = 9001
    }
}