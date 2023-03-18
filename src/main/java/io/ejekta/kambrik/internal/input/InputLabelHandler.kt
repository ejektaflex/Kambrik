package io.ejekta.kambrik.internal.input

import io.ejekta.kambrik.input.KambrikKeybind
import net.minecraft.client.gui.screen.option.ControlsListWidget.KeyBindingEntry
import net.minecraft.text.Text

object InputLabelHandler {

    fun handle(kbe: KeyBindingEntry, newText: Text) {
        println("Adding control $kbe text $newText")
        //val elwa = controlsListWidget as EntryListWidgetAccessor


        val kb = kbe.binding

        if (kb is KambrikKeybind) {
            kbe.bindingName = kb.actualTranslation()
        } else {
            kbe.bindingName = newText
        }

        println("KBN: ${kbe.bindingName}")

    }

}