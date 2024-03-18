package com.kakao.sdk.auth

import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthProvider
import com.google.firebase.auth.ktx.auth
import com.ilsa1000ri.weathersecretary.MainActivity
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.user.Constants.TAG
import com.kakao.sdk.user.UserApiClient
import com.google.firebase.ktx.Firebase
class AuthCodeHandlerActivity: AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            Log.e(TAG, "카카오계정으로 로그인 실패", error)
        } else if (token != null) {
            Log.i(TAG, "카카오계정으로 로그인 성공 ${token.accessToken}")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Firebase Auth 인스턴스 초기화
        auth = Firebase.auth
        KakaoSdk.init(this, "d305e81785d18b3f2ca0b8a1373a27fd")

        // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
            Log.e(TAG, "isKakaoTalkLoginAvailabale 실행 완료")
            UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->
                if (error != null) {
                    Log.e(TAG, "카카오톡으로 로그인 실패", error)

                    // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                    // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        return@loginWithKakaoTalk
                    }

                    // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                    UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
                }
                //로그인 성공
                else if (token != null) {
                    Log.i(TAG, "카카오톡으로 로그인 성공 ${token.accessToken}")
                    // 사용자 정보 요청 (기본)
                    UserApiClient.instance.me { user, error ->
                        if (error != null) {
                            Log.e(TAG, "사용자 정보 요청 실패", error)
                        }
                        else if (user != null) {
                            Log.i(TAG, "사용자 정보 요청 성공" +
                                    "\n회원번호: ${user.id}" +
                                    "\n이메일: ${user.kakaoAccount?.email}")
                        }
//                        val properties = mapOf("${CUSTOM_PROPERTY_KEY}" to "${CUSTOM_PROPERTY_VALUE}")
//
//                        UserApiClient.instance.updateProfile(properties) { error ->
//                            if (error != null) {
//                                Log.e(TAG, "사용자 정보 저장 실패", error)
//                            }
//                            else {
//                                Log.i(TAG, "사용자 정보 저장 성공")
//                            }
//                        }
                    }
//                    val intent = Intent(this, MainActivity::class.java)
//                    startActivity(intent)
//                    finish()
                }
            }
        } else {
            // 카카오계정으로 로그인
            UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
        }
    }
//선택항목, 필수항목 동의받는 코드 추가해야 함.

    fun oidc() {
        val providerBuilder = OAuthProvider.newBuilder("oidc.kakao")
//        getPendingAuthResult()
        signInWithProvider(providerBuilder)
    }

//    fun getPendingAuthResult() {
//        // [START auth_oidc_pending_result]
//        val pendingResultTask = auth.pendingAuthResult
//        if (pendingResultTask != null) {
//            // There's something already here! Finish the sign-in for your user.
//            pendingResultTask
//                .addOnSuccessListener {
//                    // User is signed in.
//                    // IdP data available in
//                    //                     authResult.getAdditionalUserInfo().getProfile().
//                    // The OAuth access token can also be retrieved:
//                    // ((OAuthCredential)authResult.getCredential()).getAccessToken().
//                    // The OAuth secret can be retrieved by calling:
//                    // ((OAuthCredential)authResult.getCredential()).getSecret().
//                }
//                .addOnFailureListener {
//                    // Handle failure.
//                }
//        } else {
//            // There's no pending result so you need to start the sign-in flow.
//            // See below.
//        }
//        // [END auth_oidc_pending_result]
//    }

    fun signInWithProvider(provider: OAuthProvider.Builder) {
        auth
            .startActivityForSignInWithProvider(this, provider.build())
            .addOnSuccessListener {
//                User is signed in .
//                IdP data available in
//                        authResult.getAdditionalUserInfo()
//                            .getProfile().The OAuth access token can also be retrieved :
//                ((OAuthCredential) authResult . getCredential ()).getAccessToken().The OAuth secret can be retrieved by calling :
//                ((OAuthCredential) authResult . getCredential ()).getSecret().
            }
            .addOnFailureListener {
//                Handle failure .
            }
    }
}
