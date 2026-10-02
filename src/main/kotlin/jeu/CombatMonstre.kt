package org.example.jeu

import org.example.IndividuMonstre
import org.example.joueur
import org.example.item.Utilisable

/**
 * Représente un combat entre un individu monstre du joueur et un individu monstre sauvage.
 */
class CombatMonstre(
    var monstreJoueur: IndividuMonstre,
    val monstreSauvage: IndividuMonstre,
    val afficheCombat: () -> Unit
) {

    // Numéro du round en cours, commence à 1
    var round: Int = 1

    /**
     * Vérifie si le joueur a perdu le combat.
     *
     * Condition de défaite :
     * - Aucun monstre de l'équipe du joueur n'a de PV > 0.
     *
     * @return `true` si le joueur a perdu, sinon `false`.
     */
    fun gameOver(): Boolean {
        return joueur.equipeMonstre.none { it.pv > 0 }
    }

    /**
     * Indique si le joueur a gagné le combat.
     *
     * Il y a 2 façons de gagner :
     * - Capturer le monstre sauvage (son entraîneur devient le joueur)
     * - Amener les pv du monstre sauvage à 0
     *
     * Le monstre du joueur gagne de l'expérience seulement dans le deuxième cas.
     *
     * @return `true` si le joueur a gagné, `false` sinon.
     */
    fun joueurGagne(): Boolean {
        if (monstreSauvage.pv <= 0) {
            println("${joueur.nom} a gagné !")

            val gainExp = monstreSauvage.exp * 0.20
            monstreJoueur.exp += gainExp

            println("${monstreJoueur.nom} gagne $gainExp exp")
            return true
        }

        if (monstreSauvage.entraineur == joueur) {
            println("${monstreSauvage.nom} a été capturé !")
            return true
        }

        return false
    }

    /**
     * Fait agir le monstre sauvage lors de son tour.
     * Si le monstre sauvage a encore des pv, il attaque le monstre du joueur.
     */
    fun actionAdversaire() {
        if (monstreSauvage.pv > 0) {
            monstreSauvage.attaquer(monstreJoueur)
        }
    }

    /**
     * Gère le tour d'action du joueur pendant le combat.
     *
     * Actions possibles :
     * 1. Attaquer le monstre sauvage
     * 2. Utiliser un objet du sac
     * 3. Changer de monstre actif
     * 4. Fuir le combat
     * 5. Attendre (ne rien faire ce tour-ci)
     *
     * @return `true` si le combat doit continuer, `false` sinon.
     */
    fun actionJoueur(): Boolean {
        if (gameOver()) {
            return false
        }

        println("1. Attaquer\n2. Utiliser un objet\n3. Changer de monstre\n4. Fuir\n5. Attendre")
        val choixAction = readlnOrNull()?.toIntOrNull()

        when (choixAction) {
            1 -> {
                monstreJoueur.attaquer(monstreSauvage)
            }

            2 -> {
                println("Sac à items :")
                joueur.sacAItems.forEachIndexed { index, item ->
                    println("$index. ${item.nom}")
                }

                val indexChoix = readlnOrNull()?.toIntOrNull()
                val objetChoisi = indexChoix?.let { joueur.sacAItems.getOrNull(it) }

                if (objetChoisi is Utilisable) {
                    val captureReussie = objetChoisi.utiliser(monstreSauvage)
                    if (captureReussie) {
                        return false
                    }
                } else {
                    println("Objet non utilisable")
                }
            }

            3 -> {
                println("Équipe de monstres :")
                val monstresDisponibles = joueur.equipeMonstre.filter { it.pv > 0 }
                monstresDisponibles.forEachIndexed { index, monstre ->
                    println("$index. ${monstre.nom}")
                }

                val indexChoix = readlnOrNull()?.toIntOrNull()
                val choixMonstre = indexChoix?.let { monstresDisponibles.getOrNull(it) }

                if (choixMonstre != null) {
                    if (choixMonstre.pv <= 0) {
                        println("Impossible ! Ce monstre est KO")
                    } else {
                        println("${choixMonstre.nom} remplace ${monstreJoueur.nom}")
                        monstreJoueur = choixMonstre
                    }
                }
            }

            4 -> {
                println("${joueur.nom} prend la fuite !")
                return false
            }

            5 -> {
                println("${monstreJoueur.nom} attend...")
            }

            else -> {
                println("Choix invalide")
            }
        }

        return true
    }

    /**
     * Fait jouer un round de combat entre les deux monstres.
     * Le monstre le plus rapide agit en premier.
     * Si l'action du joueur indique que le combat doit s'arrêter
     * (fuite, capture, victoire), la méthode s'arrête immédiatement.
     */
    fun jouer() {
        // Détermine qui est le plus rapide entre les deux monstres
        val joueurPlusRapide = monstreJoueur.vitesse >= monstreSauvage.vitesse

        // Affiche l'état du combat avant l'action
        afficheCombat()

        if (joueurPlusRapide) {
            // Le joueur agit en premier
            val continuer = actionJoueur()
            if (!continuer) {
                return
            }
            // Puis le monstre sauvage agit
            actionAdversaire()
        } else {
            // Le monstre sauvage agit en premier
            actionAdversaire()

            // On vérifie si le combat est déjà terminé après l'attaque adverse
            if (!gameOver()) {
                val continuer = actionJoueur()
                if (!continuer) {
                    return
                }
            }
        }
    }

    /**
     * Lance le combat et gère les rounds jusqu'à la victoire ou la défaite.
     *
     * Affiche un message de fin si le joueur perd et restaure les PV
     * de tous ses monstres.
     */
    fun lanceCombat() {
        while (!gameOver() && !joueurGagne()) {
            this.jouer()
            println("======== Fin du Round : $round ========")
            round++
        }
        if (gameOver()) {
            joueur.equipeMonstre.forEach { it.pv = it.pvMax }
            println("Game Over !")
        }
    }






}