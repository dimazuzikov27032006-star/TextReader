package com.example.textreader

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel хранит состояние экрана и управляет чтением файла.
 * Благодаря ViewModel данные не теряются при повороте экрана.
 */
class ReaderViewModel : ViewModel() {

    var state by mutableStateOf<ReaderState>(ReaderState.Idle)
        private set

    fun loadFile(context: Context, uri: Uri) {
        state = ReaderState.Loading

        viewModelScope.launch {
            try {
                val lines = withContext(Dispatchers.IO) {
                    val fileName = FileReader.getFileName(context, uri) ?: ""
                    val mimeType = context.contentResolver.getType(uri) ?: ""

                    when {
                        fileName.endsWith(".fb2", true) || mimeType.contains("fb2") ->
                            FileReader.readFb2(context, uri)

                        fileName.endsWith(".txt", true) || mimeType.startsWith("text/") ->
                            FileReader.readTxt(context, uri)

                        else -> throw IllegalArgumentException(
                            "Неподдерживаемый формат. Доступны .txt и .fb2"
                        )
                    }
                }

                state = if (lines.isEmpty() || lines.all { it.isBlank() }) {
                    ReaderState.Error("Файл пустой")
                } else {
                    ReaderState.Success(lines)
                }

            } catch (e: Exception) {
                state = ReaderState.Error("Ошибка: ${e.message ?: "неизвестная ошибка"}")
            }
        }
    }
}