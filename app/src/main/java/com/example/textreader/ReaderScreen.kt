package com.example.textreader

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * Экран чтения файла. Отвечает ТОЛЬКО за отрисовку.
 * Вся логика — в ViewModel.
 */
@Composable
fun ReaderScreen(viewModel: ReaderViewModel = viewModel()) {
    val context = LocalContext.current

    // Лаунчер системного выбора файла
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) viewModel.loadFile(context, uri)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        TopBar(onOpenClick = {
            launcher.launch(
                arrayOf("text/plain", "application/fb2", "*/*")
            )
        })

        Box(modifier = Modifier.fillMaxSize().padding(8.dp)) {
            when (val state = viewModel.state) {
                is ReaderState.Idle -> IdleView()
                is ReaderState.Loading -> LoadingView()
                is ReaderState.Success -> SuccessView(state.lines)
                is ReaderState.Error -> ErrorView(state.message)
            }
        }
    }
}

@Composable
private fun TopBar(onOpenClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1976D2))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = onOpenClick,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White)
        ) {
            Text("Открыть файл", color = Color(0xFF1976D2), fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun IdleView() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Выберите файл .txt или .fb2", color = Color.Gray, fontSize = 18.sp)
    }
}

@Composable
private fun LoadingView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
        Spacer(Modifier.height(16.dp))
        Text("Чтение файла...", color = Color.Gray)
    }
}

@Composable
private fun SuccessView(lines: List<String>) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(lines) { line ->
            Text(
                text = line,
                fontSize = 16.sp,
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            )
        }
    }
}

@Composable
private fun ErrorView(message: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(message, color = Color.Red, fontSize = 18.sp)
    }
}