package ar.com.develup.tateti.actividades

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import ar.com.develup.tateti.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.ktx.actionCodeSettings
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.actividad_registracion.*
import android.content.ContentValues.TAG

class ActividadRegistracion : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actividad_registracion)
        registrar.setOnClickListener { registrarse() }

    }

    fun registrarse() {
        val passwordIngresada = password.text.toString()
        val confirmarPasswordIngresada = confirmarPassword.text.toString()
        val email = email.text.toString()

        if (email.isEmpty()) {
            // Si no completo el email, muestro mensaje de error
            Snackbar.make(rootView, "Email requerido", Snackbar.LENGTH_SHORT).show()
        } else if (passwordIngresada == confirmarPasswordIngresada) {
            // Si completo el email y las contraseñas coinciden, registramos el usuario en Firebase
            registrarUsuarioEnFirebase(email, passwordIngresada)
        } else {
            // No coinciden las contraseñas, mostramos mensaje de error
            Snackbar.make(rootView, "Las contraseñas no coinciden", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun registrarUsuarioEnFirebase(email: String, passwordIngresada: String) {
        // Crear el usuario con el email y passwordIngresada
        // Ademas, registrar en CompleteListener el listener registracionCompletaListener definido mas abajo
        FirebaseAuth.getInstance()
            .createUserWithEmailAndPassword(email, passwordIngresada)
            .addOnCompleteListener(registracionCompletaListener)
    }

    private val registracionCompletaListener: OnCompleteListener<AuthResult?> = OnCompleteListener { task ->
        if (task.isSuccessful) {
            // Si se registro OK, muestro mensaje y envio mail de verificacion
            Snackbar.make(rootView, "Registro exitoso", Snackbar.LENGTH_SHORT).show()
            enviarEmailDeVerificacion()
        } else if (task.exception is FirebaseAuthUserCollisionException) {
            // Si el usuario ya existe, mostramos error
            Snackbar.make(rootView, "El usuario ya existe", Snackbar.LENGTH_SHORT).show()
        } else {
            // Por cualquier otro error, mostramos un mensaje de error
            Snackbar.make(rootView, "El registro fallo: " + task.exception, Snackbar.LENGTH_LONG).show()
        }
    }

    private fun enviarEmailDeVerificacion() {
        var auth = FirebaseAuth.getInstance()
        FirebaseAuth.getInstance().currentUser?.sendEmailVerification()
    }
}