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
        val monstre1 = IndividuMonstre(1, "Springleaf", 0.0, especeSpringleaf)  //Crée le 1er starter
        val monstre2 = IndividuMonstre(2, "Flamkip", 0.0, especeFlamkip)   //Crée le 2eme starter
        val monstre3 = IndividuMonstre(3, "Aquamy", 0.0, especeAquamy)  //Crée le 3em estarter

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
        if (position1 == null || position2 == null ||    // Si l'une des deux saisies n'est pas un nombre
            position1 !in 1..joueur.equipeMonstre.size ||   // Ou si position1 est hors de la plage valide
            position2 !in 1..joueur.equipeMonstre.size   // Ou si position2 est hors de la plage valide
        ) {
            println("Position invalide.")
            return
        }

        // Conversion des positions (1-indexées côté utilisateur) en index de liste (0-indexées)
        val index1 = position1 - 1   // Convertit la position humaine en index de liste
        val index2 = position2 - 1


        // Échange des deux monstres dans la liste
        val temp = joueur.equipeMonstre[index1]   // Sauvegarde temporairement le monstre à index1
        joueur.equipeMonstre[index1] = joueur.equipeMonstre[index2]  // Met le monstre d'index2 à la place d'index1
        joueur.equipeMonstre[index2] = temp

        println("Ordre mis à jour !")
    }




}