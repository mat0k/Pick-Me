import android.app.Activity
import android.widget.Toast
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit

class OTPViewModel : ViewModel() {
    private lateinit var storedVerificationId: String
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    var isLoading = mutableStateOf(false)
    var otp = mutableStateOf("")
    var otpError = mutableStateOf("")
    var isResendEnabled = mutableStateOf(false)
    fun authenticate(phoneNumber: String, activity: Activity, onVerified: (Boolean) -> Unit) {
        isLoading.value = true
        isResendEnabled.value = false
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                signInWithPhoneAuthCredential(credential, onVerified)
                isLoading.value = false
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                otpError.value = "Invalid phone number. Please enter a valid phone number."
                isLoading.value = false
                Toast.makeText(activity, "Invalid phone number. Please enter a valid phone number.", Toast.LENGTH_SHORT).show()
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                storedVerificationId = verificationId
                resendToken = token
                Toast.makeText(activity, "OTP sent successfully", Toast.LENGTH_SHORT).show()
                isLoading.value = false

                viewModelScope.launch {
                    delay(60 * 1000L)
                    isResendEnabled.value = true
                }
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
        isLoading.value = true
        val credential = PhoneAuthProvider.getCredential(storedVerificationId, otp.value)
        signInWithPhoneAuthCredential(credential) { result ->
            isLoading.value = false
            if (result) {
                onVerified(true)
            } else {
                otpError.value = "Incorrect OTP. Please try again."
            }
        }
    }

    private fun signInWithPhoneAuthCredential(
        credential: PhoneAuthCredential,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            try {
                auth.signInWithCredential(credential).await()
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