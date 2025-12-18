package com.canarda.notesapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NotesAppTheme {
                NotesScreen()
            }
        }
    }
}

@Composable
fun NotesAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color(0xFF6200EE),
            background = Color(0xFFF5F5F5),
            surface = Color.White
        ),
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen() {
    var notes by remember { mutableStateOf(listOf<Note>()) }
    var showDialog by remember { mutableStateOf(false) }
    var noteTitle by remember { mutableStateOf("") }
    var noteContent by remember { mutableStateOf("") }
    var editingNote by remember { mutableStateOf<Note?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Notes App",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF6200EE),
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingNote = null
                    noteTitle = ""
                    noteContent = ""
                    showDialog = true
                },
                containerColor = Color(0xFF6200EE)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add Note",
                    tint = Color.White
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF5F5F5))
        ) {
            if (notes.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "No notes yet!",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                        Text(
                            "Tap + to create a note",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(notes) { note ->
                        NoteItem(
                            note = note,
                            onDelete = {
                                notes = notes.filter { it.id != note.id }
                            },
                            onEdit = {
                                editingNote = note
                                noteTitle = note.title
                                noteContent = note.content
                                showDialog = true
                            }
                        )
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
                editingNote = null
                noteTitle = ""
                noteContent = ""
            },
            title = { Text(if (editingNote == null) "Add Note" else "Edit Note") },
            text = {
                Column {
                    OutlinedTextField(
                        value = noteTitle,
                        onValueChange = { noteTitle = it },
                        label = { Text("Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = noteContent,
                        onValueChange = { noteContent = it },
                        label = { Text("Content") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        maxLines = 5
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (noteTitle.isNotBlank() && noteContent.isNotBlank()) {
                            if (editingNote != null) {
                                // Edit existing note
                                notes = notes.map { note ->
                                    if (note.id == editingNote!!.id) {
                                        note.copy(
                                            title = noteTitle,
                                            content = noteContent,
                                            timestamp = System.currentTimeMillis()
                                        )
                                    } else {
                                        note
                                    }
                                }
                            } else {
                                // Add new note
                                val newNote = Note(
                                    id = (notes.maxOfOrNull { it.id } ?: 0) + 1,
                                    title = noteTitle,
                                    content = noteContent
                                )
                                notes = notes + newNote
                            }
                            noteTitle = ""
                            noteContent = ""
                            editingNote = null
                            showDialog = false
                        }
                    }
                ) {
                    Text(if (editingNote == null) "Add" else "Save")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDialog = false
                    editingNote = null
                    noteTitle = ""
                    noteContent = ""
                }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun NoteItem(note: Note, onDelete: () -> Unit, onEdit: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEdit() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = note.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6200EE),
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.Red
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = note.content,
                fontSize = 16.sp,
                color = Color.DarkGray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = formatDate(note.timestamp),
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}

fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}