package com.denizozcan.matchme
import com.denizozcan.matchme.databinding.AuthpageBinding
import com.google.firebase.firestore.ktx.firestore
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import android.content.Intent
import android.widget.Toast
import android.view.View
import android.os.Bundle

@Suppress("UNUSED_PARAMETER")
class AuthPage : AppCompatActivity() {
    private lateinit var binding: AuthpageBinding
    private var auth = Firebase.auth
    private var db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding = AuthpageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
    }

    override fun onStart(){
        super.onStart()
        if (auth.currentUser != null) {
            startActivity(Intent(applicationContext, MainPage::class.java))
            finish()
        }
    }

    fun loginClicked(view: View){
        val email = binding.email.text.toString()
        val password = binding.password.text.toString()
        val username=binding.username.text.toString()
        if (email.isNotEmpty() && password.isNotEmpty() && username.isNotEmpty()) {
            db.collection("Users").get().addOnSuccessListener { documents ->
                if (documents != null) {
                    var que = 0
                    for (document in documents) {
                        if (document.get("Username") as String == username && document.get("E-Mail") as String == email){
                            que+=1
                        }
                    }
                    if(que>0){
                        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(applicationContext,"Welcome: $username",Toast.LENGTH_LONG).show()
                                val intent = Intent(applicationContext, MainPage::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }.addOnFailureListener { exception -> Toast.makeText(applicationContext,exception.localizedMessage,Toast.LENGTH_LONG).show() }
                    }else{
                        Toast.makeText(applicationContext,"There is no such user. Please control Email, Password and Username.",Toast.LENGTH_LONG).show()
                    }
                }
            }
        }else{
            Toast.makeText(applicationContext,"Provide username, e-mail and password.",Toast.LENGTH_LONG).show()
        }
    }

    fun signUpClicked(view: View){
        val username = binding.username.text.toString()
        val email = binding.email.text.toString()
        val password = binding.password.text.toString()
        if (username.isNotEmpty() && password.isNotEmpty() && email.isNotEmpty()) {
            auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener { task ->
                if (task.isSuccessful){
                    val infos = HashMap<String,Any>()
                    infos["Username"] = username
                    infos["E-Mail"] = email
                    db.collection("Users").add(infos).addOnCompleteListener { task2 ->
                        if (task2.isSuccessful) {
                            val intent = Intent(applicationContext, MainPage::class.java)
                            Toast.makeText(applicationContext,"You have successfully registered.",Toast.LENGTH_LONG).show()
                            intent.putExtra("name",username)
                            startActivity(intent)
                            finish()
                        }
                    }.addOnFailureListener { exception ->
                        Toast.makeText(applicationContext,exception.localizedMessage,Toast.LENGTH_LONG).show()
                    }
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(applicationContext,exception.localizedMessage,Toast.LENGTH_LONG).show()
            }
        } else{
            Toast.makeText(applicationContext,"Provide username, e-mail and password.",Toast.LENGTH_LONG).show()
        }
    }

    fun forgotClicked(view: View){
        val username = binding.username.text.toString()
        val email = binding.email.text.toString()
        if (email.isNotEmpty() && username.isNotEmpty()) {
            db.collection("Users").get().addOnSuccessListener { documents ->
                if (documents != null) {
                    var que = 0
                    for (document in documents) {
                        if (document.get("Username") as String == username && document.get("E-Mail") as String == email){
                            que+=1
                        }
                    }
                    if(que>0){
                        auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
                            if (task.isSuccessful){
                                Toast.makeText(applicationContext,"Email sent successfully for change the password",Toast.LENGTH_LONG).show()
                            }else{
                                Toast.makeText(applicationContext,"Email couldn't sent. Please try again.",Toast.LENGTH_LONG).show()
                            }
                        }
                    }else{
                        Toast.makeText(applicationContext,"There is no such user. Please control Email and Username.",Toast.LENGTH_LONG).show()
                    }
                }
            }
        }else{
            Toast.makeText(applicationContext,"Provide at least username and e-mail.",Toast.LENGTH_LONG).show()
        }
    }
}