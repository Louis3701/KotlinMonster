package org.example.jeu

import org.example.IndividuMonstre
import org.example.dresseur.Entraineur
import org.example.especeAquamy
import org.example.especeFlamkip
import org.example.especeSpringleaf
import org.example.monde.Zone

/**
 * Représente une partie du jeu en cours.
 * Plusieurs objets Partie peuvent exister simultanément dans le même jeu,
 * par exemple pour gérer plusieurs sauvegardes ou plusieurs joueurs.
 */
class Partie(
    val id: Int,
    var joueur: Entraineur,
    var zone: Zone
) {
    /**
     * Crée 3 individus monstres (starters) et laisse le joueur en choisir un.
     * Le monstre choisi est renommé, ajouté à l'équipe du joueur,
     * et son entraîneur est défini comme étant le joueur.
     */
    fun choixStarter() {
        // Création des 3 starters possibles
        val monstre1 = IndividuMonstre(1, "Springleaf", 0.0, especeSpringleaf)
        val monstre2 = IndividuMonstre(2, "Flamkip", 0.0, especeFlamkip)
        val monstre3 = IndividuMonstre(3, "Aquamy", 0.0, especeAquamy)

        var starter: IndividuMonstre? = null

        // Boucle tant que le joueur n'a pas fait un choix valide (1, 2 ou 3)
        while (starter == null) {
            // Affichage des détails de chaque starter
            monstre1.afficheDetail()
            monstre2.afficheDetail()
            monstre3.afficheDetail()

            println("1. ${monstre1.nom}\n2. ${monstre2.nom}\n3. ${monstre3.nom}")
            val choixSelection = readlnOrNull()?.toIntOrNull()

            // Vérifie que le choix est bien compris entre 1 et 3
            if (choixSelection != null && choixSelection in 1..3) {
                starter = when (choixSelection) {
                    1 -> monstre1
                    2 -> monstre2
                    else -> monstre3
                }
            }
            // Sinon, on reboucle et on affiche les détails + le menu
        }

        // On demande au joueur de renommer son starter
        starter.renommer()

        // Ajout du starter à l'équipe du joueur, et on l'affecte comme son entraîneur
        joueur.equipeMonstre.add(starter)
        starter.entraineur = joueur
    }

    /**
     * Permet d'inverser la position de deux monstres dans l'équipe du joueur.
     * Demande la position du premier monstre, puis celle du second,
     * et échange leur place dans la liste.
     */
    fun modifierOrdreEquipe() {
        // On ne peut utiliser cette méthode que s'il y a au moins 2 monstres dans l'équipe
        if (joueur.equipeMonstre.size < 2) {
            println("Il faut au moins 2 monstres dans l'équipe pour changer leur ordre.")
            return
        }

        // Affiche l'équipe actuelle avec sa position
        joueur.equipeMonstre.forEachIndexed { index, monstre ->
            println("${index + 1}. ${monstre.nom}")
        }

        // Demande la première position
        println("Quelle est la position du monstre à déplacer ?")
        val position1 = readlnOrNull()?.toIntOrNull()

        // Demande la seconde position
        println("Quelle est la nouvelle position ?")
        val position2 = readlnOrNull()?.toIntOrNull()

        // Vérifie que les deux positions sont valides (correspondent à des emplacements occupés)
        if (position1 == null || position2 == null ||
            position1 !in 1..joueur.equipeMonstre.size ||
            position2 !in 1..joueur.equipeMonstre.size
        ) {
            println("Position invalide.")
            return
        }

        // Conversion des positions (1-indexées côté utilisateur) en index de liste (0-indexées)
        val index1 = position1 - 1
        val index2 = position2 - 1

        // Échange des deux monstres dans la liste
        val temp = joueur.equipeMonstre[index1]
        joueur.equipeMonstre[index1] = joueur.equipeMonstre[index2]
        joueur.equipeMonstre[index2] = temp

        println("Ordre mis à jour !")
    }

    /**
     * Affiche l'équipe du joueur et permet de naviguer dedans.
     * Le joueur peut taper le numéro d'un monstre pour voir son détail,
     * taper "m" pour modifier l'ordre de l'équipe,
     * ou taper "q" pour retourner au menu principal.
     */
    fun examineEquipe() {
        var continuer = true

        while (continuer) {
            // Affiche la liste de l'équipe avec leur position
            println("=== Équipe de ${joueur.nom} ===")
            joueur.equipeMonstre.forEachIndexed { index, monstre ->
                println("${index + 1}. ${monstre.nom} (Niveau ${monstre.niveau})")
            }

            println("Tapez le numéro d'un monstre pour voir son détail, 'm' pour modifier l'ordre, ou 'q' pour quitter")
            val choix = readlnOrNull()

            when {
                choix == "q" -> {
                    // Le joueur sort de la fonction
                    continuer = false
                }

                choix == "m" -> {
                    // Le joueur modifie l'ordre de l'équipe
                    modifierOrdreEquipe()
                }

                choix != null && choix.toIntOrNull() != null -> {
                    // Le joueur a tapé un numéro : on vérifie qu'il correspond à un monstre existant
                    val position = choix.toInt()
                    if (position in 1..joueur.equipeMonstre.size) {
                        val monstre = joueur.equipeMonstre[position - 1]
                        monstre.afficheDetail()
                        println(monstre.espece.afficheArt())
                    } else {
                        println("Numéro invalide.")
                    }
                }

                else -> {
                    println("Choix invalide.")
                }
            }
        }
    }


}