package io.ejekta.kambrik.gui.draw.reactor

sealed class EventReactor(defaultCanPass: Boolean) {
    /**
     * Whether widgets behind this widget can be clicked on.
     */
    var canPassThrough: () -> Boolean = { defaultCanPass }
}