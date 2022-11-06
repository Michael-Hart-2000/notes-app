package models

data class Note(var noteTitle: String,
                var noteContent: String,
                var noteStatus: String,
                var notePriority: Int,
                var noteCategory: String,
                var isNoteArchived: Boolean){
}