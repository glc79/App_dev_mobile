package com.example.exo4

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ZoneDeTirActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zone_de_tir)

        // Récupérer le nombre de tours restants
        val remainingLaps = intent.getIntExtra("REMAINING_LAPS", 1)

        // Initialisation des vues
        val chronometerText: TextView = findViewById(R.id.text_chronometer)
        val noTargetText: TextView = findViewById(R.id.text_no_target)
        val buttonNext: Button = findViewById(R.id.button_next)

        // Afficher le chronomètre (exemple fictif)
        chronometerText.text = "Chronomètre : 0:14:39"

        // Logique pour le bouton "Suivant"
        buttonNext.setOnClickListener {
            if (remainingLaps > 1) {
                // Retour à CourirActivity si des tours restent
                val intent = Intent(this, CourirActivity::class.java)
                intent.putExtra("REMAINING_LAPS", remainingLaps - 1) // Réduire le nombre de tours
                startActivity(intent)
            } else {
                // Terminer l'activité si tous les tours sont terminés
                val intent = Intent(this, ResultActivity::class.java) // Exemple : aller à une activité de résultats
                startActivity(intent)
            }
            finish()
        }
    }
}
