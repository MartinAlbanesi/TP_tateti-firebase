package ar.com.develup.tateti.actividades

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import ar.com.develup.tateti.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import kotlinx.android.synthetic.main.actividad_inicial.*
import kotlinx.android.synthetic.main.actividad_partidas.*

class ActividadInicial : AppCompatActivity() {

    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var auth: FirebaseAuth
    private lateinit var remoteConfig: FirebaseRemoteConfig

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actividad_inicial)

        firebaseAnalytics = Firebase.analytics
        auth = Firebase.auth
        remoteConfig = Firebase.remoteConfig

        // Asignar settings a Firebase Remote Config
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
            fetchTimeoutInSeconds = 10
        }
        remoteConfig.setConfigSettingsAsync(configSettings)

        // Listeners
        iniciarSesion.setOnClickListener { iniciarSesion() }
        registrate.setOnClickListener { registrate() }
        olvideMiContrasena.setOnClickListener { olvideMiContrasena() }

        if (usuarioEstaLogueado()) {
            // Si el usuario esta logueado, se redirige a la pantalla
            // de partidas
            verPartidas()
            finish()
        }

        actualizarRemoteConfig()
        configurarOlvideMiContrasena()
    }

    private fun usuarioEstaLogueado(): Boolean {
        // Validar que currentUser sea != null
        if(FirebaseAuth.getInstance().currentUser != null){
            return true
        }
        return false
    }

    private fun verPartidas() {
        val intent = Intent(this, ActividadPartidas::class.java)
        startActivity(intent)

    }

    private fun registrate() {
        val intent = Intent(this, ActividadRegistracion::class.java)
        startActivity(intent)
    }

    private fun actualizarRemoteConfig() {
        configurarDefaultsRemoteConfig()
        configurarOlvideMiContrasena()
    }

    private fun configurarDefaultsRemoteConfig() {
        // Configurar los valores por default para remote config,
        // ya sea por codigo o por XML
        Firebase.remoteConfig.setDefaultsAsync(R.xml.firebase_config_defaults)
    }

    private fun configurarOlvideMiContrasena() {
        // Obtener el valor de la configuracion para saber si mostrar
        // o no el boton de olvide mi contraseña
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener(this) {
                val botonOlvideHabilitado = Firebase.remoteConfig.getBoolean("mostrarOlvideMiContrasena")
                if (botonOlvideHabilitado) {
                    olvideMiContrasena.visibility = View.VISIBLE
                } else {
                    olvideMiContrasena.visibility = View.GONE
                }
            }
    }

    private fun olvideMiContrasena() {
        // Obtengo el mail
        val email = email.text.toString()

        // Si no completo el email, muestro mensaje de error
        if (email.isEmpty()) {
            Snackbar.make(rootView!!, "Completa el email", Snackbar.LENGTH_SHORT).show()
        } else {
            // Si completo el mail debo enviar un mail de reset
            // Para ello, utilizamos sendPasswordResetEmail con el email como parametro
            // Agregar el siguiente fragmento de codigo como CompleteListener, que notifica al usuario
            // el resultado de la operacion
            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
              .addOnCompleteListener { task ->
                  if (task.isSuccessful) {
                      Snackbar.make(rootView, "Email enviado", Snackbar.LENGTH_SHORT).show()
                  } else {
                      Snackbar.make(rootView, "Error " + task.exception, Snackbar.LENGTH_SHORT).show()
                  }
              }
        }
    }

    private fun iniciarSesion() {
        try {
            val email = email.text.toString()
            val password = password.text.toString()
            FirebaseAuth.getInstance()
                .signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(authenticationListener)
            if(email.isEmpty() && password.isEmpty()){
                throw RuntimeException("No se ingresó ningún valor")
            }
        }catch(e: RuntimeException){
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

        private val authenticationListener: OnCompleteListener<AuthResult?> = OnCompleteListener<AuthResult?> { task ->
            if (task.isSuccessful) {
                if (usuarioVerificoEmail()) {
                    verPartidas()
                } else {
                    desloguearse()
                    Snackbar.make(rootView!!, "Verifica tu email para continuar", Snackbar.LENGTH_SHORT).show()
                }
            } else {
                if (task.exception is FirebaseAuthInvalidUserException) {
                    Snackbar.make(rootView!!, "El usuario no existe", Snackbar.LENGTH_SHORT).show()
                } else if (task.exception is FirebaseAuthInvalidCredentialsException) {
                    Snackbar.make(rootView!!, "Credenciales inválidas", Snackbar.LENGTH_SHORT).show()
                }
            }
        }

    private fun usuarioVerificoEmail(): Boolean {
        // Preguntar al currentUser si verifico email
        if(FirebaseAuth.getInstance().currentUser?.isEmailVerified == true){
            return true
        }
        return false
    }

    private fun desloguearse() {
        // Hacer signOut de Firebase
        FirebaseAuth.getInstance().signOut()
    }
}