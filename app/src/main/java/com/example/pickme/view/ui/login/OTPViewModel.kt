import android.app.Activity
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit

class OTPViewModel : ViewModel() {
    private lateinit var storedVerificationId: String
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    var otp = mutableStateOf("")
    var otpError = mutableStateOf("")
    fun authenticate(phoneNumber: String, activity: Activity, onVerified: (Boolean) -> Unit) {
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                signInWithPhoneAuthCredential(credential, onVerified)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                storedVerificationId = verificationId
                resendToken = token
            }
        }

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber, // Phone number to verify
            60, // Timeout duration
            TimeUnit.SECONDS, // Unit of timeout
            activity, // Activity (for callback binding)
            callbacks
        ) // OnVerificationStateChangedCallbacks
    }

    fun verifyOTP(onVerified: (Boolean) -> Unit) {
    val credential = PhoneAuthProvider.getCredential(storedVerificationId, otp.value)
    signInWithPhoneAuthCredential(credential) { result ->
        if (result) {
            onVerified(true)
        } else {
            otpError.value = "Incorrect OTP. Please try again."
        }
    }
}

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                onResult(true)
            } catch (e: Exception) {
                // Handle error
                when (e) {
                    is FirebaseAuthInvalidCredentialsException -> {
                        onResult(false)
                    }

                    is FirebaseAuthInvalidUserException -> {
                        // The corresponding user record does not exist.
                    }

                    is FirebaseAuthUserCollisionException -> {
                        // There already exists an account with the email address asserted by the credential.
                    }

                    is FirebaseAuthRecentLoginRequiredException -> {
                        // The user's last sign-in time does not meet the security threshold. Use reauthenticateWithCredential to resolve.
                    }

                    else -> {
                        // Some other error occurred.
                    }
                }
            }
        }
    }

    fun resendOTP(phoneNumber: String, activity: Activity) {
    val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        // ... existing callbacks ...
        override fun onVerificationCompleted(p0: PhoneAuthCredential) {
            TODO("Not yet implemented")
        }

        override fun onVerificationFailed(p0: FirebaseException) {
            TODO("Not yet implemented")
        }
    }

    PhoneAuthProvider.getInstance().verifyPhoneNumber(
        phoneNumber, // Phone number to verify
        60, // Timeout duration
        TimeUnit.SECONDS, // Unit of timeout
        activity, // Activity (for callback binding)
        callbacks, // OnVerificationStateChangedCallbacks
        resendToken // Resend token from previous attempt
    )
}
}