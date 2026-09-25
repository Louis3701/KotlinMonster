package org.example

import org.example.dresseur.Entraineur
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.random.Random

/**
 * Représente un individu monstre, c'est-à-dire une instance concrète d'une espèce de monstre
 * avec laquelle le joueur va interagir (monstre sauvage, monstre de l'équipe du joueur,
 * ou monstre appartenant à un autre dresseur).
 * Plusieurs individus peuvent appartenir à la même espèce, par exemple plusieurs Canaros.
 */
class IndividuMonstre(
    val id: Int,
    var nom: String,
    val espece: EspeceMonstre,
    var entraineur: Entraineur?,
    expInit: Double
) {

    var niveau: Int = 1

    var attaque: Int = espece.baseAttaque + Random.nextInt(-2, 3)
    var defense: Int = espece.baseDefense + Random.nextInt(-2, 3)
    var vitesse: Int = espece.baseVitesse + Random.nextInt(-2, 3)
    var attaqueSpe: Int = espece.baseAttaqueSpe + Random.nextInt(-2, 3)
    var defenseSpe: Int = espece.baseDefenseSpe + Random.nextInt(-2, 3)

    var pvMax: Int = espece.basePv + Random.nextInt(-5, 6)

    var potentiel: Double = Random.nextDouble(0.5, 2.0 + 0.0001)

    var exp: Double = 0.0
        get() = field
        set(value) {
            field = value
        }

    /**
     * @property pv Points de vie actuels.
     * Ne peut pas être inférieur à 0 ni supérieur à [pvMax].
     */
    var pv: Int = pvMax
        get() = field
        set(nouveauPv) {
            field = when {
                nouveauPv < 0 -> 0
                nouveauPv > pvMax -> pvMax
                else -> nouveauPv
            }
        }

    init {
        exp = expInit
    }

    /**
     * Calcule l'expérience totale nécessaire pour atteindre un niveau donné.
     *
     * @param niveau Niveau cible.
     * @return Expérience cumulée nécessaire pour atteindre ce niveau.
     */
    fun palierExp(niveau: Int): Double {
        return 100 * (niveau - 1).toDouble().pow(2.0)
    }

    /**
     * Augmente le niveau de l'individu et recalcule ses caractéristiques
     * (attaque, défense, vitesse, attaqueSpe, defenseSpe, pvMax) en fonction
     * du potentiel de l'individu et des modificateurs de son espèce.
     * Le nombre de pv est également augmenté du gain de pvMax obtenu.
     */
    fun levelUp() {
        niveau++

        val gainAttaque = (espece.modAttaque * potentiel).roundToInt() + Random.nextInt(-2, 3)
        val gainDefense = (espece.modDefense * potentiel).roundToInt() + Random.nextInt(-2, 3)
        val gainVitesse = (espece.modVitesse * potentiel).roundToInt() + Random.nextInt(-2, 3)
        val gainAttaqueSpe = (espece.modAttaqueSpe * potentiel).roundToInt() + Random.nextInt(-2, 3)
        val gainDefenseSpe = (espece.modDefenseSpe * potentiel).roundToInt() + Random.nextInt(-2, 3)
        val gainPvMax = (espece.modPv * potentiel).roundToInt() + Random.nextInt(-5, 6)

        attaque += gainAttaque
        defense += gainDefense
        vitesse += gainVitesse
        attaqueSpe += gainAttaqueSpe
        defenseSpe += gainDefenseSpe

        pvMax += gainPvMax
        pv += gainPvMax
    }
}