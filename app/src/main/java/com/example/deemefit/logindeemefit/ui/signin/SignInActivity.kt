package com.example.deemefit.logindeemefit.ui.signin

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.example.deemefit.R
import com.example.deemefit.databinding.ActivitySigninBinding
import com.example.deemefit.logindeemefit.core.dialog.DialogFragmentLauncher
import com.example.deemefit.logindeemefit.core.dialog.ErrorDialog
import com.example.deemefit.logindeemefit.core.ex.dismissKeyboard
import com.example.deemefit.logindeemefit.core.ex.loseFocusAfterAction
import com.example.deemefit.logindeemefit.core.ex.onTextChanged
import com.example.deemefit.logindeemefit.core.ex.show
import com.example.deemefit.logindeemefit.ui.signin.model.UserSignIn
import com.example.deemefit.logindeemefit.ui.verification.VerificationActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SignInActivity : AppCompatActivity() {


    companion object {
        fun create(context: Context): Intent =
            Intent(context, SignInActivity::class.java)
    }

    //    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivitySigninBinding
    private val signInViewModel: SignInViewModel by viewModels()

    @Inject
    lateinit var dialogLauncher: DialogFragmentLauncher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUi()
//        auth = Firebase.auth

//        binding.btnRegistro.setOnClickListener {
//            val emailUsuario = binding.etEmailR.text.toString()
//            val passwordUsuario = binding.etContraseniaR.text.toString()
//            val passwordUsuario2 = binding.etContraseniaR2.text.toString()
//
//            //Creamos esta variable para determinar los mínimos que deberá tener la contraseña, si quisieramos añadir algún condicionante más podríamos hacerlo desde aquí
//            val passwordRegex = Pattern.compile(
//                "^" +
//                        "(?=.*[0-9])" +         //Debe contener 1 número
//                        "(?=.*[a-z])" +        //Debe contener 1 letra minúscula
//                        "(?=.*[A-Z])" +        //Debe contener 1 letra mayúscula
//                        "(?=\\S+$)" +           //No puede contener espacios en blanco
//                        ".{4,}" +               //Debe contener mínimo 4 caracteres
//                        "$"
//            )
//
//            if (emailUsuario.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(emailUsuario).matches()) {
//                Toast.makeText(
//                    this, "Ingrese un email válido",
//                    Toast.LENGTH_SHORT
//                ).show()
//            } else if (passwordUsuario.isEmpty() || !passwordRegex.matcher(passwordUsuario)
//                    .matches()
//            ) {
//                Toast.makeText(
//                    this,
//                    "La contraseña debe contener mínimo 4 caracteres, incluyendo: Mínimo 1 letra mayúscula, 1 letra minúscula y 1 número",
//                    Toast.LENGTH_LONG
//                ).show()
//            } else if (passwordUsuario != passwordUsuario2) {
//                Toast.makeText(
//                    this, "Las contraseñas introducidas NO coinciden",
//                    Toast.LENGTH_SHORT
//                ).show()
//            } else {
//                crearCuenta(emailUsuario, passwordUsuario)
//            }
//        }
    }

    private fun initUi() {
        initListeners()
        initObservers()
    }

    private fun initListeners() {
        binding.etEmailR.loseFocusAfterAction(EditorInfo.IME_ACTION_NEXT)
        binding.etEmailR.setOnFocusChangeListener { _, hasFocus -> onFieldChanged(hasFocus) }
        binding.etEmailR.onTextChanged { onFieldChanged() }

        binding.etContraseniaR.loseFocusAfterAction(EditorInfo.IME_ACTION_NEXT)
        binding.etContraseniaR.setOnFocusChangeListener { _, hasFocus -> onFieldChanged(hasFocus) }
        binding.etContraseniaR.onTextChanged { onFieldChanged() }

        binding.etContraseniaR2.loseFocusAfterAction(EditorInfo.IME_ACTION_DONE)
        binding.etContraseniaR2.setOnFocusChangeListener { _, hasFocus -> onFieldChanged(hasFocus) }
        binding.etContraseniaR2.onTextChanged { onFieldChanged() }

        with(binding) {
            btnRegistro.setOnClickListener {
                it.dismissKeyboard()
                signInViewModel.onSignInSelected(
                    UserSignIn(
                        email = binding.etEmailR.text.toString(),
                        password = binding.etContraseniaR.text.toString(),
                        passwordConfirmation = binding.etContraseniaR2.text.toString()
                    )
                )
            }
        }
    }

    private fun initObservers() {
        signInViewModel.navigateToVerifyEmail.observe(this) {
            it.getContentIfNotHandled()?.let {
                goToVerifyEmail()
            }
        }

        lifecycleScope.launchWhenStarted {
            signInViewModel.viewState.collect { viewState ->
                updateUI(viewState)
            }
        }

        signInViewModel.showErrorDialog.observe(this) { showError ->
            if (showError) showErrorDialog()
        }
    }

    private fun showErrorDialog() {
        ErrorDialog.create(
            title = getString(R.string.signin_error_dialog_title),
            description = getString(R.string.signin_error_dialog_body),
            positiveAction = ErrorDialog.Action(getString(R.string.signin_error_dialog_positive_action)) {
                it.dismiss()
            }
        ).show(dialogLauncher, this)
    }

    private fun updateUI(viewState: SignInViewState) {
        with(binding) {
            pbLoading.isVisible = viewState.isLoading
            binding.tilEmail.error =
                if (viewState.isValidEmail) null else getString(R.string.login_error_mail)
            binding.tilPassword.error =
                if (viewState.isValidPassword) null else getString(R.string.login_error_password)
            binding.tilRepeatPassword.error =
                if (viewState.isPasswordsAreSame) null else getString(R.string.signin_error_password)
        }
    }

    private fun onFieldChanged(hasFocus: Boolean = false) {
        if (!hasFocus) {
            signInViewModel.onFieldsChanged(
                UserSignIn(
                    email = binding.etEmailR.text.toString(),
                    password = binding.etContraseniaR.text.toString(),
                    passwordConfirmation = binding.etContraseniaR2.text.toString()
                )
            )
        }
    }

    private fun goToVerifyEmail() {
        startActivity(VerificationActivity.create(this))
    }

//    /*Cuando el usuario quiera registrarse deberemos revisar si el usuario está verificado correctamente, esto lo haremos al iniciar la actividad
//    Se accedería a la condición del IF en el momento en que el usuario ya se ha registrado y vuelve acceder de nuevo a la pantalla de registro de usuario pero sin haber
//    verificado su correo electrónico*/
//    public override fun onStart() {
//        super.onStart()
//        val currentUser = auth.currentUser
//        if (currentUser != null) {
//            if (currentUser.isEmailVerified) {
//                reload()
//            } else {
//                val intent = Intent(this, VerificarEmailActivity::class.java)
//                startActivity(intent)
//            }
//        }
//    }
//
//    private fun crearCuenta(email: String, password: String) {
//        auth.createUserWithEmailAndPassword(email, password)
//            .addOnCompleteListener(this) { task ->
//                if (task.isSuccessful) {
//                    Toast.makeText(
//                        this, "¡Usuario creado correctamente!",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    val intent = Intent(this, VerificarEmailActivity::class.java)
//                    startActivity(intent)
//                } else {
//                    Log.w("TAG", "createUserWithEmail:failure", task.exception)
//                    Toast.makeText(
//                        this, "El correo electrónico introducido ya está en uso",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//    }
//
//    private fun reload() {
//        val intent = Intent(this, HomeActivity::class.java)
//        this.startActivity(intent)
//    }
}