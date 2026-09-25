package org.example.monde

import org.example.EspeceMonstre

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


}