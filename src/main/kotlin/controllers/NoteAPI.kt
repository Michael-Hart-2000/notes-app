package controllers

import models.Note

class NoteAPI {
    private var notes = ArrayList<Note>()

    fun add(note: Note): Boolean {
        return notes.add(note)
    }

    fun listAllNotes(): String {
        return if (notes.isEmpty()) {
            "No notes stored"
        } else {
            var listofNotes = ""
            for (i in notes.indices) {
                listofNotes += "${i}: ${notes[i]} \n"
            }
            listofNotes
        }
    }

}