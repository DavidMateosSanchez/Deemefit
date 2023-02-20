package com.example.deemefit.logindeemefit.ui.login

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.example.deemefit.R
import com.example.deemefit.databinding.ActivityLoginBinding
import com.example.deemefit.logindeemefit.core.dialog.DialogFragmentLauncher
import com.example.deemefit.logindeemefit.core.dialog.ErrorDialog
import com.example.deemefit.logindeemefit.core.ex.dismissKeyboard
import com.example.deemefit.logindeemefit.core.ex.loseFocusAfterAction
import com.example.deemefit.logindeemefit.core.ex.onTextChanged
import com.example.deemefit.logindeemefit.core.ex.show
import com.example.deemefit.logindeemefit.ui.login.model.UserLogin
import com.example.deemefit.view.HomeActivity
import com.example.deemefit.logindeemefit.ui.recoveraccount.RecoverAccountActivity
import com.example.deemefit.logindeemefit.ui.signin.SignInActivity
import com.example.deemefit.logindeemefit.ui.verification.VerificarEmailActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    // Creamos un companion object para gestionar el cambio de otra activity a esta
    companion object {
        fun create(context: Context): Intent =
            Intent(context, LoginActivity::class.java)
    }

    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModels()

    @Inject
    lateinit var dialogLauncher: DialogFragmentLauncher

    private var exit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()


        if (!checkForInternet(this)) {
            Toast.makeText(
                this,
                "Por favor, utiliza una conexión de red para poder acceder a la aplicación correctamente.",
                Toast.LENGTH_SHORT
            ).show()
        }

        if (exit) {
            onBackPressed()
        }
    }

    private fun initUI() {
        initListeners()
        initObservers()
    }

    private fun initListeners() {
        binding.etEmail.loseFocusAfterAction(EditorInfo.IME_ACTION_NEXT)
        binding.etEmail.onTextChanged { onFieldChanged() }

        binding.etContrasenia.loseFocusAfterAction(EditorInfo.IME_ACTION_DONE)
        binding.etContrasenia.setOnFocusChangeListener { _, hasFocus -> onFieldChanged(hasFocus) }
        binding.etContrasenia.onTextChanged { onFieldChanged() }

        binding.tvOlvidasteContrasenia.setOnClickListener { loginViewModel.onForgotPasswordSelected() }

        binding.btnIniciarSesion.setOnClickListener {
            it.dismissKeyboard()
            loginViewModel.onLoginSelected(
                binding.etEmail.text.toString(),
                binding.etContrasenia.text.toString()
            )
        }

        binding.viewBottom.tvRegistrate.setOnClickListener { loginViewModel.onSignInSelected() }
    }

    private fun initObservers() {
        loginViewModel.navigateToHome.observe(this) {
            it.getContentIfNotHandled()?.let {
                goToHome()
            }
        }

        loginViewModel.navigateToSignIn.observe(this) {
            it.getContentIfNotHandled()?.let {
                goToSignIn()
            }
        }

        loginViewModel.navigateToRecoverAccount.observe(this) {
            it.getContentIfNotHandled()?.let {
                gotToRecoverAccount()
            }
        }

        loginViewModel.navigateToVerifyAccount.observe(this) {
            it.getContentIfNotHandled()?.let {
                goToVerifyAccount()
            }
        }

        loginViewModel.showErrorDialog.observe(this) { userLogin ->
            if (userLogin.showErrorDialog) showErrorDialog(userLogin)
        }

        lifecycleScope.launchWhenStarted {
            loginViewModel.viewState.collect { viewState ->
                updateUI(viewState)
            }
        }
    }

    private fun updateUI(viewState: LoginViewState) {
        with(binding) {
            pbLoading.isVisible = viewState.isLoading
            tilEmail.error =
                if (viewState.isValidEmail) null else getString(R.string.login_error_mail)
            tilPassword.error =
                if (viewState.isValidPassword) null else getString(R.string.login_error_password)
        }
    }

    private fun onFieldChanged(hasFocus: Boolean = false) {
        if (!hasFocus) {
            loginViewModel.onFieldsChanged(
                email = binding.etEmail.text.toString(),
                password = binding.etContrasenia.text.toString()
            )
        }
    }

    private fun showErrorDialog(userLogin: UserLogin) {
        ErrorDialog.create(
            title = getString(R.string.login_error_dialog_title),
            description = getString(R.string.login_error_dialog_body),
            negativeAction = ErrorDialog.Action(getString(R.string.login_error_dialog_negative_action)) {
                it.dismiss()
            },
            positiveAction = ErrorDialog.Action(getString(R.string.login_error_dialog_positive_action)) {
                loginViewModel.onLoginSelected(
                    userLogin.email,
                    userLogin.password
                )
                it.dismiss()
            }
        ).show(dialogLauncher, this)
    }

    private fun gotToRecoverAccount() {
        startActivity(RecoverAccountActivity.create(this))
    }

    private fun goToSignIn() {
        startActivity(SignInActivity.create(this))
    }

    private fun goToHome() {
        startActivity(HomeActivity.create(this))
    }

    private fun goToVerifyAccount() {
        startActivity(VerificarEmailActivity.create(this))
    }

    private fun checkForInternet(context: Context): Boolean {

        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false

            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true

                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true

                else -> false
            }
        } else {
            @Suppress("DEPRECATION") val networkInfo =
                connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }

    override fun onBackPressed() {
        if (exit) {
            finishAffinity()
        } else {
            Toast.makeText(this, "Pulsa atrás de nuevo para salir", Toast.LENGTH_SHORT).show()
            exit = true
            Handler().postDelayed({ exit = false }, 3 * 1000)
        }
    }
}