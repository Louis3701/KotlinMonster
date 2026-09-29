package org.example.item

import org.example.dresseur.Entraineur

class Badge(
    id: Int,
    nom: String,
    description: String,
    var champion: Entraineur
) : Item(id, nom, description)