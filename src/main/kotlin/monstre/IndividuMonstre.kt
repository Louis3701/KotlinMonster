package org.example

import org.example.dresseur.Entraineur
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.random.Random

/**
 * Représente un individu monstre : une instance concrète d'une espèce
 * avec laquelle le joueur va interagir (sauvage, dans l'équipe, ou chez un autre dresseur).
 * Plusieurs individus peuvent appartenir à la même espèce.
 */
class IndividuMonstre(
    val id: Int,
    var nom: String,
    expInit: Double,
    val espece: EspeceMonstre,
    var entraineur: Entraineur? = null
) {

    // Niveau de départ
    var niveau: Int = 1

    // Statistiques de combat
    var attaque: Int = espece.baseAttaque + Random.nextInt(-2, 3)
    var defense: Int = espece.baseDefense + Random.nextInt(-2, 3)
    var vitesse: Int = espece.baseVitesse + Random.nextInt(-2, 3)
    var attaqueSpe: Int = espece.baseAttaqueSpe + Random.nextInt(-2, 3)
    var defenseSpe: Int = espece.baseDefenseSpe + Random.nextInt(-2, 3)

    // PV maximum
    var pvMax: Int = espece.basePv + Random.nextInt(-5, 6)

    // Potentiel individuel
    var potentiel: Double = Random.nextDouble(0.5, 2.0 + 0.0001)

    // Expérience actuelle
    var exp: Double = 0.0
        set(value) {
            field = value

            // Permet de ne pas afficher de message lors de la création
            val estNiveau1 = niveau == 1

            // Permet de gérer plusieurs niveaux gagnés d'un coup
            while (field >= palierExp(niveau)) {
                levelUp()

                if (!estNiveau1) {
                    println("Le monstre $nom est maintenant niveau $niveau !")
                }
            }
        }

    // PV actuels
    var pv: Int = pvMax
        set(nouveauPv) {
            field = nouveauPv.coerceIn(0, pvMax)
        }

    // Initialisation
    init {
        this.exp = expInit
    }

    /**
     * Calcule l'expérience nécessaire pour atteindre un niveau.
     * Formule :
     * 100 * (niveau - 1)^2
     */
    fun palierExp(niveau: Int): Double {
        return 100 * (niveau - 1).toDouble().pow(2.0)
    }

    /**
     * Fait passer le monstre au niveau supérieur.
     */
    fun levelUp() {
        niveau++

        attaque += (espece.modAttaque * potentiel).roundToInt() +
                Random.nextInt(-2, 3)

        defense += (espece.modDefense * potentiel).roundToInt() +
                Random.nextInt(-2, 3)

        vitesse += (espece.modVitesse * potentiel).roundToInt() +
                Random.nextInt(-2, 3)

        attaqueSpe += (espece.modAttaqueSpe * potentiel).roundToInt() +
                Random.nextInt(-2, 3)

        defenseSpe += (espece.modDefenseSpe * potentiel).roundToInt() +
                Random.nextInt(-2, 3)

        // Augmentation des PV maximum
        val gainPvMax =
            (espece.modPv * potentiel).roundToInt() +
                    Random.nextInt(-5, 6)

        pvMax += gainPvMax

        // Les PV actuels augmentent également
        pv += gainPvMax
    }

    /**
     * Attaque un autre monstre.
     *
     * Dégâts :
     * attaque - défense de la cible / 2
     *
     * Minimum : 1 dégât.
     */
    fun attaquer(cible: IndividuMonstre) {

        val degats = maxOf(
            1,
            attaque - cible.defense / 2
        )

        val pvAvant = cible.pv

        cible.pv -= degats

        println(
            "$nom inflige ${pvAvant - cible.pv} dégâts à ${cible.nom}"
        )
    }

    /**
     * Permet au joueur de renommer le monstre.
     */
    fun renommer() {
        println("Renommer $nom ?")
        val nouveauNom = readln()
        if (nouveauNom != "") {
            nom = nouveauNom
        }
    }

    /**
     * Affiche les détails du monstre et son art ASCII.
     */
    fun afficheDetail() {

        println("==============================")
        println("Nom : $nom")
        println("Niveau : $niveau")
        println("Exp : $exp")
        println("PV : $pv / $pvMax")
        println("Attaque : $attaque")
        println("Défense : $defense")
        println("Vitesse : $vitesse")
        println("Attaque spéciale : $attaqueSpe")
        println("Défense spéciale : $defenseSpe")
        println("==============================")

        println(espece.afficheArt())
    }
}