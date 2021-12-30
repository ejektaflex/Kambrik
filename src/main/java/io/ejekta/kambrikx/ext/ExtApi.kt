package io.ejekta.kambrikx.ext

import io.ejekta.kambrik.Kambrik
import io.ejekta.kambrikx.recipe.KambrikSpecialRecipeApi

private val specialRecipeApi = KambrikSpecialRecipeApi()

val Kambrik.SpecialRecipes: KambrikSpecialRecipeApi
    get() = specialRecipeApi