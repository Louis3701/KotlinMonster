package org.example.monde

import org.example.EspeceMonstre
import org.example.IndividuMonstre
import kotlin.random.Random

/**
 * Représente une zone du monde (route, caverne, mer, etc.).
 * Les zones forment une chaîne de routes : chaque zone peut avoir une zone suivante
 * et une zone précédente, permettant ainsi de se déplacer d'une zone à l'autre.
 */
class Zone(
    var id: Int,
    var nom: String,
    var expZone: Int,
    var especesMonstres: MutableList<EspeceMonstre> = mutableListOf(),
    var zoneSuivante: Zone? = null,
    var zonePrecedente: Zone? = null
) {

    /**
     * Génère un individu monstre sauvage appartenant à l'une des espèces de la zone,
     * choisie aléatoirement. Son expérience initiale est égale à l'expérience de la zone
     * à +/- 20% près (aléatoire).
     *
     * @return Le monstre généré.
     */
    fun genereMonstre(): IndividuMonstre {
        // Choix aléatoire d'une espèce parmi celles disponibles dans la zone
        val especeChoisie = especesMonstres.random()

        // Génère un facteur aléatoire entre 0.8 et 1.2 (soit +/- 20% autour de l'exp de la zone)
        val facteur = 0.8 + Random.nextDouble() * 0.4
        val expGeneree = expZone * facteur

        // Création et retour du nouvel individu monstre
        return IndividuMonstre(
            id = (1..100000).random(),
            nom = especeChoisie.nom,
            expInit = expGeneree,
            espece = especeChoisie
        )
    }
}