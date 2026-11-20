package com.example.textreader

/**
 * Состояния экрана чтения.
 * Sealed class гарантирует, что других состояний быть не может.
 */
sealed class ReaderState {
    /** Ничего не загружено */
    object Idle : ReaderState()

    /** Идёт загрузка файла */
    object Loading : ReaderState()

    /** Файл успешно прочитан */
    data class Success(val lines: List<String>) : ReaderState()

    /** Ошибка чтения */
    data class Error(val message: String) : ReaderState()
}