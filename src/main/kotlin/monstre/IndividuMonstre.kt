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
    val id: Int,                              // Identifiant unique de l'individu
    var nom: String,                          // Nom donné à ce monstre (peut être renommé par le joueur)
    expInit: Double,                          // Simple paramètre (pas une propriété) utilisé une fois dans le init
    val espece: EspeceMonstre,                // L'espèce à laquelle appartient cet individu (ex: Springleaf)
    var entraineur: Entraineur? = null        // Le dresseur qui possède ce monstre, null par défaut (monstre sauvage)
) {

    // Niveau de départ de tout individu, fixé à 1 par défaut
    var niveau: Int = 1

    // Caractéristiques de combat, initialisées à partir des valeurs de base de l'espèce,
    // avec une variation aléatoire de -2 à +2 pour rendre chaque individu unique
    var attaque: Int = espece.baseAttaque + Random.nextInt(-2, 3)
    var defense: Int = espece.baseDefense + Random.nextInt(-2, 3)
    var vitesse: Int = espece.baseVitesse + Random.nextInt(-2, 3)
    var attaqueSpe: Int = espece.baseAttaqueSpe + Random.nextInt(-2, 3)
    var defenseSpe: Int = espece.baseDefenseSpe + Random.nextInt(-2, 3)

    // Points de vie maximum, calculés à partir du basePv de l'espèce +/- 5 (aléatoire)
    var pvMax: Int = espece.basePv + Random.nextInt(-5, 6)

    // Potentiel individuel : multiplicateur aléatoire entre 0.5 et 2.0 (inclus)
    // qui influence la progression des caractéristiques lors des montées de niveau
    var potentiel: Double = Random.nextDouble(0.5, 2.0 + 0.0001)

    // Expérience actuelle de l'individu. Le setter contrôle automatiquement
    // le passage de niveau dès que le palier d'expérience est atteint.
    var exp: Double = 0.0
        get() = field
        set(value) {
            // On stocke d'abord la nouvelle valeur d'expérience
            field = value

            // On mémorise si l'individu était niveau 1 AVANT de commencer à monter de niveau
            // (permet de ne pas afficher de message lors de la création initiale du monstre)
            val estNiveau1 = (niveau == 1)

            // Tant que l'expérience actuelle dépasse le palier requis pour le niveau courant,
            // on continue à monter de niveau (gère le cas d'un gros gain d'exp d'un coup)
            while (field >= palierExp(niveau)) {
                levelUp()
                // On n'affiche le message que si le monstre n'était pas niveau 1 au départ
                if (!estNiveau1) {
                    println("Le monstre $nom est maintenant niveau $niveau !")
                }
            }
        }

    /**
     * @property pv Points de vie actuels.
     * Ne peut pas être inférieur à 0 ni supérieur à [pvMax].
     */
    var pv: Int = pvMax
        get() = field
        set(nouveauPv) {
            // On borne la valeur entre 0 et pvMax pour éviter des PV négatifs ou excessifs
            field = when {
                nouveauPv < 0 -> 0
                nouveauPv > pvMax -> pvMax
                else -> nouveauPv
            }
        }

    // Bloc d'initialisation exécuté à la création de chaque IndividuMonstre
    init {
        // On passe par le setter (et non directement par field) pour déclencher
        // automatiquement un éventuel levelUp si expInit correspond à un niveau > 1
        this.exp = expInit
    }

    /**
     * Calcule l'expérience totale nécessaire pour atteindre un niveau donné.
     *
     * @param niveau Niveau cible.
     * @return Expérience cumulée nécessaire pour atteindre ce niveau.
     */
    fun palierExp(niveau: Int): Double {
        // Formule : 100 * (niveau - 1)^2
        return 100 * (niveau - 1).toDouble().pow(2.0)
    }

    /**
     * Augmente le niveau de l'individu et recalcule ses caractéristiques
     * (attaque, défense, vitesse, attaqueSpe, defenseSpe, pvMax) en fonction
     * du potentiel de l'individu et des modificateurs de son espèce.
     * Le nombre de pv est également augmenté du gain de pvMax obtenu.
     */
    fun levelUp() {
        // Incrémente le niveau de l'individu
        niveau++

        // Calcul du gain de chaque caractéristique :
        // (modificateur de l'espèce * potentiel de l'individu), arrondi,
        // plus une petite variation aléatoire entre -2 et +2
        val gainAttaque = (espece.modAttaque * potentiel).roundToInt() + Random.nextInt(-2, 3)
        val gainDefense = (espece.modDefense * potentiel).roundToInt() + Random.nextInt(-2, 3)
        val gainVitesse = (espece.modVitesse * potentiel).roundToInt() + Random.nextInt(-2, 3)
        val gainAttaqueSpe = (espece.modAttaqueSpe * potentiel).roundToInt() + Random.nextInt(-2, 3)
        val gainDefenseSpe = (espece.modDefenseSpe * potentiel).roundToInt() + Random.nextInt(-2, 3)

        // Même formule pour les PV max, mais avec une variation aléatoire plus large (-5 à +5)
        val gainPvMax = (espece.modPv * potentiel).roundToInt() + Random.nextInt(-5, 6)

        // Application des gains sur les caractéristiques
        attaque += gainAttaque
        defense += gainDefense
        vitesse += gainVitesse
        attaqueSpe += gainAttaqueSpe
        defenseSpe += gainDefenseSpe

        // Le pvMax augmente, et les pv actuels augmentent du même montant
        // (le setter de pv se charge de ne pas dépasser le nouveau pvMax)
        pvMax += gainPvMax
        pv += gainPvMax
    }

    /**
     * Attaque un autre [IndividuMonstre] et inflige des dégâts.
     *
     * Les dégâts sont calculés de manière très simple pour le moment :
     * `dégâts = attaque - (défense / 2)` (minimum 1 dégât).
     *
     * @param cible Monstre cible de l'attaque.
     */
    fun attaquer(cible: IndividuMonstre) {
        // Calcul brut des dégâts à partir de l'attaque de l'attaquant
        var degatBrut = this.attaque

        // On soustrait la moitié de la défense de la cible
        var degatTotal = degatBrut - (cible.defense / 2)

        // Les dégâts ne peuvent jamais être inférieurs à 1
        if (degatTotal < 1) {
            degatTotal = 1
        }

        // On enregistre les pv de la cible avant l'attaque
        val pvAvant = cible.pv

        // Application des dégâts sur la cible (passe par le setter, donc borné entre 0 et pvMax)
        cible.pv -= degatTotal

        // On enregistre les pv de la cible après l'attaque
        val pvApres = cible.pv

        // Affichage du résultat de l'attaque
        println("$nom inflige ${pvAvant - pvApres} dégâts à ${cible.nom}")
    }

    /**
     * Demande au joueur de renommer le monstre.
     * Si l'utilisateur entre un texte vide, le nom n'est pas modifié.
     */
    fun renommer() {
        // Affiche la demande de renommage avec le nom actuel
        println("Renommer $nom ?")

        // Lecture de la saisie utilisateur
        val nouveauNom = readLine()

        // Si la saisie n'est pas vide (ni null), on met à jour le nom
        if (!nouveauNom.isNullOrEmpty()) {
            this.nom = nouveauNom
        }
        // Sinon, on ne fait rien : le nom reste inchangé
    }

    /**
     * Affiche les caractéristiques détaillées du monstre dans la console
     * (nom, niveau, expérience, pv, et statistiques de combat).
     */

}