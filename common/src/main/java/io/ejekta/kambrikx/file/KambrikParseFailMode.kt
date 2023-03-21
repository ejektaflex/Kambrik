package io.ejekta.kambrikx.file

/**
 * Determines what to do when a file cannot be parsed correctly
 */
enum class KambrikParseFailMode {
    /**
     * If a file cannot be read, it will use the default data instead
     */
    LEAVE,

    /**
     * If a file cannot be read, it will overwrite the file with default data and then use it
     */
    OVERWRITE,

    /**
     * If a file cannot be read, it will issue an error
     */
    ERROR
}