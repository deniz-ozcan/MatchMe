package com.denizozcan.matchme
import com.denizozcan.matchme.databinding.MainpageBinding
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import android.content.Intent
import android.os.Bundle
import android.view.View

@Suppress("UNUSED_PARAMETER")
class MainPage : AppCompatActivity() {
    private var auth = FirebaseAuth.getInstance()
    private lateinit var binding : MainpageBinding

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding = MainpageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        binding.musicsw.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {applicationContext.startService(Intent(applicationContext,SoundService::class.java))
            } else { applicationContext.stopService(Intent(applicationContext, SoundService::class.java)) }
        }
    }

    fun signOutApp(view: View){
        auth.signOut()
        val intent = Intent(applicationContext, AuthPage::class.java)
        applicationContext.stopService(Intent(applicationContext, SoundService::class.java))
        startActivity(intent)
        finish()
    }

    fun setLevel(view: View){
        when (view) {
            binding.begcheck -> {
                binding.begcheck.isChecked = true
                binding.hardcheck.isChecked = false
                binding.expcheck.isChecked = false
            }
            binding.hardcheck -> {
                binding.begcheck.isChecked = false
                binding.hardcheck.isChecked = true
                binding.expcheck.isChecked = false
            }
            else -> {
                binding.begcheck.isChecked = false
                binding.hardcheck.isChecked = false
                binding.expcheck.isChecked = true
            }
        }
    }

    fun startGame(view: View){
        val intent = if(view==binding.button1){
            Intent(applicationContext,SinglePlayer::class.java)
        }else{
            Intent(applicationContext,MultiPlayer::class.java)
        }
        if (binding.begcheck.isChecked){
            intent.putExtra("level","4")
        }else if (binding.hardcheck.isChecked){
            intent.putExtra("level","16")
        }else{
            intent.putExtra("level","36")
        }
        if (binding.musicsw.isChecked){
            intent.putExtra("Music","true")
        }else{
            intent.putExtra("Music","false")
        }
        startActivity(intent)
    }
}