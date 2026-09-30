package org.example.item

import org.example.IndividuMonstre
import org.example.joueur

/**
 * Représente un Kube, objet permettant de capturer un [IndividuMonstre] sauvage.
 * Hérite de [Item] pour les propriétés communes (id, nom, description),
 * et implémente [Utilisable] pour définir son effet lorsqu'il est utilisé.
 *
 * @property chanceCapture Pourcentage de chance de capture (ex : 50.0 pour 50%).
 */
class MonsterKube(
    id: Int,
    nom: String,
    description: String,
    var chanceCapture: Double,
) : Item(id, nom, description), Utilisable {

    /**
     * Tente de capturer le monstre cible.
     * Un monstre déjà possédé par un dresseur ne peut pas être capturé.
     *
     * @param cible Le [IndividuMonstre] que l'on tente de capturer.
     * @return `true` si la capture a réussi, `false` sinon.
     */
    override fun utiliser(cible: IndividuMonstre): Boolean {
        println("Vous lancez le Monstre Kube !")

        // Un monstre qui appartient déjà à un dresseur ne peut pas être capturé
        if (cible.entraineur != null) {
            println("Le monstre ne peut pas être capturé")
            return false
        }

        // Tirage aléatoire entre 0 et 100 pour déterminer si la capture réussit
        val nbAleatoire = (0..100).random()

        // Échec de la capture
        if (nbAleatoire >= chanceCapture) {
            println("Presque ! Le Kube n'a pas pu capturer le monstre !")
            return false
        }

        // Succès de la capture
        println("Le monstre est capturé !")

        // On propose de renommer le monstre capturé
        println("Donner un nouveau nom au monstre :")
        val nouveauNom = readLine()
        if (!nouveauNom.isNullOrEmpty()) {
            cible.nom = nouveauNom
        }

        // Si l'équipe du joueur a de la place (max 6), on y ajoute le monstre,
        // sinon on l'ajoute à la boîte
        if (joueur.equipeMonstre.size < 6) {
            joueur.equipeMonstre.add(cible)
        } else {
            joueur.boiteMonstre.add(cible)
        }

        // Le monstre appartient maintenant au joueur
        cible.entraineur = joueur

        return true
    }
}