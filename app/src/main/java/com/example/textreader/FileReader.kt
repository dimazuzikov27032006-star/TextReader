package com.example.textreader

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * Утилита для чтения файлов из хранилища.
 * Отвечает только за чтение — не знает про UI и ViewModel.
 */
object FileReader {

    /**
     * Возвращает имя файла по его URI.
     */
    fun getFileName(context: Context, uri: Uri): String? {
        return try {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (idx >= 0) cursor.getString(idx) else null
                } else null
            }
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Читает .txt файл и возвращает список строк.
     */
    fun readTxt(context: Context, uri: Uri): List<String> {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw IllegalStateException("Не удалось открыть файл")
        return inputStream.use { stream ->
            BufferedReader(InputStreamReader(stream, Charsets.UTF_8)).use { reader ->
                reader.readLines()
            }
        }
    }

    /**
     * Читает .fb2 файл через парсер и возвращает список строк.
     */
    fun readFb2(context: Context, uri: Uri): List<String> {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw IllegalStateException("Не удалось открыть файл")
        return inputStream.use { stream ->
            Fb2Parser.parse(stream)
        }
    }
}