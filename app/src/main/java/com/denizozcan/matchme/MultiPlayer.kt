package com.denizozcan.matchme
import com.denizozcan.matchme.databinding.MultiplayerBinding
import com.google.firebase.firestore.FirebaseFirestore
import androidx.appcompat.app.AppCompatActivity
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AlertDialog
import android.view.animation.Animation
import android.graphics.BitmapFactory
import kotlin.collections.ArrayList
import android.os.CountDownTimer
import android.widget.ImageView
import android.content.Intent
import android.graphics.Color
import android.widget.Toast
import android.util.Base64
import android.os.Handler
import android.view.View
import android.os.Looper
import android.os.Bundle

@Suppress("UNUSED_PARAMETER")
class MultiPlayer : AppCompatActivity() {
    private lateinit var cDT : CountDownTimer
    private lateinit var binding: MultiplayerBinding
    private lateinit var db : FirebaseFirestore
    var handler = Handler(Looper.getMainLooper())
    var mapping = HashMap<ImageView, Card>()
    var allCards = ArrayList<ImageView>()
    var opened = ArrayList<CardOpened>()
    var hidden = ArrayList<ImageView>()
    var players = ArrayList<Boolean>()
    var scores = ArrayList<Int>()
    var runnable = Runnable {}
    var mp = SoundService()
    var activeness = false
    var continuality = false

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding = MultiplayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        binding.harryme.setOnClickListener{showCards(binding.harryme)}
        db = FirebaseFirestore.getInstance()
        setLevel()
        getCards()
    }

    override fun onStop() {
        super.onStop()
        cDT.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        cDT.cancel()
    }

    private fun startTimer(){
        cDT = object  : CountDownTimer(60500,1000){
            override fun onFinish() {
                if(!activeness){
                    applicationContext.stopService(Intent(applicationContext, SoundService::class.java))
                    binding.timeText.text = "Time: 0"
                    handler.removeCallbacks(runnable)
                    val alert = AlertDialog.Builder(this@MultiPlayer,R.style.AlertDialog)
                    mp.playWhenFinished(applicationContext)
                    alert.setTitle("Game Over")
                    alert.setMessage("Do you guys want to try again?")
                    alert.setPositiveButton("") {_, _ ->
                        startActivity(intent)
                        finish()
                        applicationContext.startService(Intent(applicationContext,SoundService::class.java))
                    }
                    alert.setPositiveButtonIcon(getDrawable(R.drawable.switch_thumb_true))
                    alert.setNegativeButtonIcon(getDrawable(R.drawable.switch_thumb_false))
                    alert.setNegativeButton("") {_, _ ->
                        val intentx = Intent(applicationContext, AuthPage::class.java)
                        if(intent.getStringExtra("Music") == "true"){ intentx.putExtra("Music","true") }else{ intentx.putExtra("Music","false") }
                        startActivity(intentx)
                        finish()
                    }
                    alert.show()
                    activeness = true
                }
            }
            override fun onTick(millisUntilFinished: Long) {
                if(millisUntilFinished/1000<=10){ binding.timeText.setTextColor(Color.parseColor("#F50057")) }
                if(!activeness){
                    binding.timeText.text = "Time: ${millisUntilFinished/1000}"
                    if(scores[0]>scores[1]){
                        binding.scoreText1.setTextColor(Color.parseColor("#1DE9B6"))
                        binding.scoreText2.setTextColor(Color.parseColor("#F50057"))
                    }
                    if(scores[0]<scores[1]){
                        binding.scoreText1.setTextColor(Color.parseColor("#F50057"))
                        binding.scoreText2.setTextColor(Color.parseColor("#1DE9B6"))
                    }
                }
            }
        }.start()
    }

    private fun getCards(){
        val cardList =  ArrayList<Card>()
        db.collection("cards").get().addOnSuccessListener { documents ->
            if (documents != null) {
                for (document in documents) {
                    val name = document.get("name") as String
                    val image = document.get("image") as String
                    val house = document.get("house") as String
                    val point = document.get("point") as String
                    cardList.add(Card(name,house,point.toInt(), BitmapFactory.decodeByteArray(Base64.decode(image, Base64.DEFAULT),0, Base64.decode(image, Base64.DEFAULT).size)))
                }
                val sortedList = cardList.sortedWith(compareBy { it.house })
                val rx = randomNumberGenerator()
                val ry = ArrayList<Int>()
                for (i in 0 until allCards.size){ if (rx.indexOf(i)<0){ ry.add(i) }}
                var gry = 0
                var huf = 0
                var sly = 0
                var rav = 0
                for (i in 0 until rx.size){
                    if(allCards.size==4){
                        var k = sortedList[(0 until 44).random()]
                        allCards[rx[i]].setOnClickListener{openingAnimation(allCards[rx[i]], k, rx[i])}
                        allCards[ry[i]].setOnClickListener{openingAnimation(allCards[ry[i]], k, ry[i])}
                        mapping.put(allCards[rx[i]],k)
                        mapping.put(allCards[ry[i]],k)
                    } else{
                        var v = allCards.size/8
                        if (allCards.size==36){ v = 5 }
                        if (gry<v) {
                            var r1 = sortedList[(0 until 11).random()]
                            allCards[rx[i]].setOnClickListener{openingAnimation(allCards[rx[i]], r1, rx[i])}
                            allCards[ry[i]].setOnClickListener{openingAnimation(allCards[ry[i]], r1, ry[i])}
                            mapping.put(allCards[rx[i]],r1)
                            mapping.put(allCards[ry[i]],r1)
                            gry+=1
                        } else if (sly<v) {
                            var r4 = sortedList[(33 until 44).random()]
                            allCards[rx[i]].setOnClickListener{openingAnimation(allCards[rx[i]], r4, rx[i])}
                            allCards[ry[i]].setOnClickListener{openingAnimation(allCards[ry[i]], r4, ry[i])}
                            mapping.put(allCards[rx[i]],r4)
                            mapping.put(allCards[ry[i]],r4)
                            sly+=1
                        } else if  (huf<allCards.size/8) {
                            var r2 = sortedList[(11 until 22).random()]
                            allCards[rx[i]].setOnClickListener{openingAnimation(allCards[rx[i]], r2, rx[i])}
                            allCards[ry[i]].setOnClickListener{openingAnimation(allCards[ry[i]], r2, ry[i])}
                            mapping.put(allCards[rx[i]],r2)
                            mapping.put(allCards[ry[i]],r2)
                            huf+=1
                        } else if  (rav<allCards.size/8) {
                            var r3 = sortedList[(22 until 33).random()]
                            allCards[rx[i]].setOnClickListener{openingAnimation(allCards[rx[i]], r3, rx[i])}
                            allCards[ry[i]].setOnClickListener{openingAnimation(allCards[ry[i]], r3, ry[i])}
                            mapping.put(allCards[rx[i]],r3)
                            mapping.put(allCards[ry[i]],r3)
                            rav+=1
                        }
                    }
                }
                startTimer()
            }else{
                Toast.makeText(applicationContext, "Database unreachable or empty.", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(applicationContext, it.localizedMessage, Toast.LENGTH_SHORT).show()
        }

    }

    private fun openingAnimation(imageView: ImageView, card: Card, i: Int){
        if (!activeness) {
            if (!continuality) {
                imageView.isClickable = false
                val animation = AnimationUtils.loadAnimation(this@MultiPlayer, R.anim.opencard)
                imageView.startAnimation(animation)
                animation.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation?) { continuality = true }
                    override fun onAnimationEnd(animation: Animation?) {
                        val animation2 = AnimationUtils.loadAnimation(this@MultiPlayer, R.anim.closecard)
                        imageView.startAnimation(animation2)
                        imageView.setImageBitmap(card.picture)
                        animation2.setAnimationListener(object : Animation.AnimationListener {
                            override fun onAnimationStart(animation: Animation?) {}
                            override fun onAnimationEnd(animation: Animation?) {
                                if (card.house=="gryffindor" || card.house=="slytherin"){ opened.add(CardOpened(card.name,2,card.point,i)) }
                                else{ opened.add(CardOpened(card.name,1,card.point,i))}
                                if (opened.size == 2) {
                                    if (opened[0].n == opened[1].n) {
                                        mp.playWhenMatched(applicationContext)
                                        allCards[opened[0].i].visibility = View.INVISIBLE
                                        allCards[opened[1].i].visibility = View.INVISIBLE
                                        if(players[0]){
                                            scores[0] += (2*opened[0].p*opened[0].h)
                                            binding.scoreText1.text = "🚩 Player 1: " + scores[0].toString()
                                            binding.scoreText2.text = "Player 2: " + scores[1].toString()
                                        } else{
                                            scores[1] += (2*opened[0].p*opened[0].h)
                                            binding.scoreText1.text = "Player 1: " + scores[0].toString()
                                            binding.scoreText2.text = "🚩 Player 2: " + scores[1].toString()
                                        }
                                        var counter = 0
                                        for(c in allCards){ if (c.visibility == View.INVISIBLE){ counter += 1 } }
                                        if(counter == allCards.size) {
                                            activeness = true
                                            val alert = AlertDialog.Builder(this@MultiPlayer,R.style.AlertDialog)
                                            mp.playWhenWin(applicationContext)
                                            if(scores[0]>scores[1]){
                                                alert.setTitle("Congratulations Player 1.")
                                            }else if(scores[0]==scores[1]){
                                                alert.setTitle("Congratulations.")
                                            } else{
                                                alert.setTitle("Congratulations Player 2.")
                                            }
                                            alert.setMessage("Do you guys want to try again?")
                                            alert.setPositiveButton("") {_, _ ->
                                                cDT.cancel()
                                                startActivity(intent)
                                                finish()
                                            }
                                            alert.setPositiveButtonIcon(getDrawable(R.drawable.switch_thumb_true))
                                            alert.setNegativeButtonIcon(getDrawable(R.drawable.switch_thumb_false))
                                            alert.setNegativeButton("") {_, _ ->
                                                cDT.cancel()
                                                val intentx = Intent(applicationContext, AuthPage::class.java)
                                                if(intent.getStringExtra("Music") == "true"){ intentx.putExtra("Music","true") }else{ intentx.putExtra("Music","false") }
                                                startActivity(intentx)
                                                finish()
                                            }
                                            alert.show()
                                        }
                                    }else if (opened[0].n != opened[1].n && opened[0].h == opened[1].h) {
                                        if(players[0]){
                                            scores[0] -= (opened[0].p+opened[1].p)/opened[0].h
                                            binding.scoreText1.text = "Player 1: " + scores[0].toString()
                                            binding.scoreText2.text = "🚩 Player 2: " + scores[1].toString()
                                            players[0]=false
                                            players[1]=true
                                        } else{
                                            scores[1] -= (opened[0].p+opened[1].p)/opened[0].h
                                            binding.scoreText1.text = "🚩 Player 1: " + scores[0].toString()
                                            binding.scoreText2.text = "Player 2: " + scores[1].toString()
                                            players[0]=true
                                            players[1]=false
                                        }
                                        closingAnimation(allCards[opened[0].i], opened[0].i)
                                        closingAnimation(allCards[opened[1].i], opened[1].i)
                                    }else {
                                        if(players[0]){
                                            scores[0] -= ((opened[0].p+opened[1].p)/2)*opened[0].h*opened[1].h
                                            binding.scoreText1.text = "Player 1: " + scores[0].toString()
                                            binding.scoreText2.text = "🚩 Player 2: " + scores[1].toString()
                                            players[0]=false
                                            players[1]=true
                                        } else{
                                            scores[1] -= ((opened[0].p+opened[1].p)/2)*opened[0].h*opened[1].h
                                            binding.scoreText1.text = "🚩 Player 1: " + scores[0].toString()
                                            binding.scoreText2.text = "Player 2: " + scores[1].toString()
                                            players[0]=true
                                            players[1]=false
                                        }
                                        closingAnimation(allCards[opened[0].i], opened[0].i)
                                        closingAnimation(allCards[opened[1].i], opened[1].i)
                                    }
                                    opened.clear()
                                }
                                continuality = false
                            }
                            override fun onAnimationRepeat(animation: Animation?) {}
                        })
                    }
                    override fun onAnimationRepeat(animation: Animation?) {}
                })
            }
        }

    }

    private fun closingAnimation(imageView: ImageView, i: Int){
        val animation = AnimationUtils.loadAnimation(this@MultiPlayer, R.anim.opencard)
        imageView.startAnimation(animation)
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) { continuality = true }
            override fun onAnimationEnd(animation: Animation?){
                val animation2 = AnimationUtils.loadAnimation(this@MultiPlayer, R.anim.closecard)
                imageView.startAnimation(animation2)
                imageView.setImageResource(R.drawable.backpic)
                animation2.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation?) {}
                    override fun onAnimationEnd(animation: Animation?) { continuality = false
                        imageView.isClickable = true
                    }
                    override fun onAnimationRepeat(animation: Animation?) {} })
            }
            override fun onAnimationRepeat(animation: Animation?) {}
        })
    }

    private fun randomNumberGenerator(): ArrayList<Int>{
        val arry = ArrayList<Int>()
        var go = true
        var counter = 0
        while (go){
            var rand = (0 until allCards.size).random()
            if (!arry.contains(rand)) {
                arry.add(rand)
                counter +=1
            }
            if (counter==allCards.size/2){ go = false }
        }
        return arry
    }

    private fun setLevel(){
        scores.add(0)
        scores.add(0)
        players.add(true)
        players.add(false)
        if (intent.getStringExtra("level")!!.toInt() == 4){
            for(i in 0 until 6){
                for(j in 0 until 6){
                    if(i==2 && j==2){ } else if(i==2 && j==3) { } else if(i==3 && j==2) { } else if(i==3 && j==3) { }
                    else{
                        var a:ImageView = binding.gridLayout.findViewWithTag("img$i$j")
                        hidden.add(a)
                        a.layoutParams.width = 0
                        a.layoutParams.height = 0
                        a.requestLayout()
                    }
                }
            }
            for(i in 2 until 4){
                for(j in 2 until 4) {
                    var a:ImageView = binding.gridLayout.findViewWithTag("img$i$j")
                    allCards.add(a)
                    a.layoutParams.width = 350
                    a.layoutParams.height = 350
                    a.requestLayout()
                }
            }
        } else if (intent.getStringExtra("level")!!.toInt() == 16){
            val array = arrayOf("00","01","02","03","04","05",10, 15, 20, 25, 30, 35, 40, 45, 50, 51, 52, 53, 54, 55)
            for (i in array){
                var a:ImageView = binding.gridLayout.findViewWithTag("img$i")
                hidden.add(a)
                a.layoutParams.width = 0
                a.layoutParams.height = 0
                a.requestLayout()
            }
            for(i in 1 until 5){
                for(j in 1 until 5){
                    var a:ImageView = binding.gridLayout.findViewWithTag("img$i$j")
                    allCards.add(a)
                    a.layoutParams.width = 250
                    a.layoutParams.height = 250
                    a.requestLayout()
                }
            }
        } else if (intent.getStringExtra("level")!!.toInt() == 36){
            for(i in 0 until 6){
                for(j in 0 until 6){
                    var a:ImageView = binding.gridLayout.findViewWithTag("img$i$j")
                    allCards.add(a)
                    a.layoutParams.width = 150
                    a.layoutParams.height = 150
                    a.requestLayout()
                }
            }
        }
    }

    private fun showCards(view: View){
        if(opened.size==0){
            object  : CountDownTimer(1000,1000){
                override fun onTick(millisUntilFinished: Long) {
                    for (card in mapping){
                        if(card.key.visibility==View.VISIBLE){
                            val animation = AnimationUtils.loadAnimation(this@MultiPlayer, R.anim.opencard)
                            card.key.startAnimation(animation)
                            animation.setAnimationListener(object : Animation.AnimationListener {
                                override fun onAnimationStart(animation: Animation?) { for(c in allCards){ c.isClickable = false }
                                    binding.harryme.isClickable = false }
                                override fun onAnimationEnd(animation: Animation?) {
                                    val animation2 = AnimationUtils.loadAnimation(this@MultiPlayer, R.anim.closecard)
                                    card.key.startAnimation(animation2)
                                    card.key.setImageBitmap(card.value.picture)
                                }
                                override fun onAnimationRepeat(animation: Animation?) {}
                            })
                        }
                    }
                }
                override fun onFinish() {
                    for (card in mapping) {
                        if(card.key.visibility==View.VISIBLE){
                            val animation3 = AnimationUtils.loadAnimation(this@MultiPlayer, R.anim.opencard)
                            card.key.startAnimation(animation3)
                            animation3.setAnimationListener(object : Animation.AnimationListener {
                                override fun onAnimationStart(animation: Animation?) {}
                                override fun onAnimationEnd(animation: Animation?){
                                    val animation4 = AnimationUtils.loadAnimation(this@MultiPlayer, R.anim.closecard)
                                    card.key.startAnimation(animation4)
                                    card.key.setImageResource(R.drawable.backpic)
                                    binding.harryme.isClickable = true
                                    for(c in allCards){ c.isClickable = true }
                                }
                                override fun onAnimationRepeat(animation: Animation?) {}
                            })
                        }
                    }
                }
            }.start()
        }
    }
}