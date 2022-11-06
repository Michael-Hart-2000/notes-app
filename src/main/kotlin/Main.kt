import controllers.NoteAPI
import models.Note
import mu.KotlinLogging
import persistence.JSONSerializer
import persistence.XMLSerializer
import utils.ScannerInput
import utils.ScannerInput.readNextInt
import utils.ScannerInput.readNextLine
import java.io.File
import java.lang.System.exit

val yellow = "\u001B[33m"
val red = "\u001b[31m"
val green = "\u001B[32m"

fun mainMenu() : Int {
    return ScannerInput.readNextInt( yellow +""" 
         > --------------------------------------
         > |           NOTE KEEPER APP          |
         > --------------------------------------
         > | NOTE MENU                          |
         > |   1) Add a note                    |
         > |   2) List all notes                |
         > |   3) Update a note                 |
         > |   4) Remove a note                 |
         > |   5) Archive a note                |
         > |   6) Search note(by description)   |
         > --------------------------------------
         > |   20) Save notes                   |
         > |   21) Load notes                   |
         > |   0) Exit                          |
         > --------------------------------------
         > ==>> """.trimMargin(">"))
}

fun runMenu() {
    do {
        val option = mainMenu()
        when (option) {
            1  -> addNote()
            2  -> listNotes()
            3  -> updateNote()
            4  -> deleteNote()
            5 -> archiveNote()
            6 -> searchNotes()
            20  -> save()
            21  -> load()
            0  -> exitApp()
            else -> println( red +"Invalid option entered: ${option}")
        }
    } while (true)
}

fun addNote(){
    //logger.info { "addNote() function invoked" }
    val noteTitle = readNextLine( green +"Enter a title for the note: ")
    val notePriority = readNextInt( green +"Enter a priority (1-low, 2, 3, 4, 5-high): ")
    val noteCategory = readNextLine( green +"Enter a category for the note: ")
    val isAdded = noteAPI.add(Note(noteTitle, notePriority, noteCategory, false))

    if (isAdded) {
        println( green +"Added Successfully")
    } else {
        println( red +"Add Failed")
    }
}

fun listNotes() {
    if (noteAPI.numberOfNotes() > 0) {
        val option = readNextInt( yellow +
            """
                  > --------------------------------
                  > |   1) View ALL notes          |
                  > |   2) View ACTIVE notes       |
                  > |   3) View ARCHIVED notes     |
                  > --------------------------------
         > ==>> """.trimMargin(">"))

        when (option) {
            1 -> listAllNotes();
            2 -> listActiveNotes();
            3 -> listArchivedNotes();
            else -> println( red +"Invalid option entered: " + option);
        }
    } else {
        println( red +"Option Invalid - No notes stored");
    }
}

fun updateNote() {
    //logger.info { "updateNotes() function invoked" }
    listNotes()
    if (noteAPI.numberOfNotes() > 0) {
        //only ask the user to choose the note if notes exist
        val indexToUpdate = readNextInt( green +"Enter the index of the note to update: ")
        if (noteAPI.isValidIndex(indexToUpdate)) {
            val noteTitle = readNextLine( green +"Enter a title for the note: ")
            val notePriority = readNextInt( green +"Enter a priority (1-low, 2, 3, 4, 5-high): ")
            val noteCategory = readNextLine( green +"Enter a category for the note: ")

            //pass the index of the note and the new note details to NoteAPI for updating and check for success.
            if (noteAPI.updateNote(indexToUpdate, Note(noteTitle, notePriority, noteCategory, false))){
                println( green +"Update Successful")
            } else {
                println( red +"Update Failed")
            }
        } else {
            println( red +"There are no notes for this index number")
        }
    }
}

fun deleteNote(){
    //logger.info { "deleteNotes() function invoked" }
    listNotes()
    if (noteAPI.numberOfNotes() > 0) {
        //only ask the user to choose the note to delete if notes exist
        val indexToDelete = readNextInt( green +"Enter the index of the note to delete: ")
        //pass the index of the note to NoteAPI for deleting and check for success.
        val noteToDelete = noteAPI.deleteNote(indexToDelete)
        if (noteToDelete != null) {
            println( green +"Delete Successful! Deleted note: ${noteToDelete.noteTitle}")
        } else {
            println( red +"Delete NOT Successful")
        }
    }
}

fun save() {
    try {
        noteAPI.store()
    } catch (e: Exception) {
        System.err.println( red +"Error writing to file: $e")
    }
}

fun load() {
    try {
        noteAPI.load()
    } catch (e: Exception) {
        System.err.println( red +"Error reading from file: $e")
    }
}

fun listActiveNotes() {
    println(noteAPI.listActiveNotes())
}

fun archiveNote() {
    listActiveNotes()
    if (noteAPI.numberOfActiveNotes() > 0) {
        //only ask the user to choose the note to archive if active notes exist
        val indexToArchive = readNextInt( green +"Enter the index of the note to archive: ")
        //pass the index of the note to NoteAPI for archiving and check for success.
        if (noteAPI.archiveNote(indexToArchive)) {
            println( green +"Archive Successful!")
        } else {
            println( red +"Archive NOT Successful")
        }
    }
}

fun listAllNotes() {
    println(noteAPI.listAllNotes())
}

fun listArchivedNotes() {
    println(noteAPI.listArchivedNotes())
}

fun exitApp(){
    println("Exiting...bye")
    exit(0)
}

fun searchNotes() {
    val searchTitle = readNextLine( green +"Enter the description to search by: ")
    val searchResults = noteAPI.searchByTitle(searchTitle)
    if (searchResults.isEmpty()) {
        println( red +"No notes found")
    } else {
        println(searchResults)
    }
}

private val logger = KotlinLogging.logger {}

fun main(args: Array<String>) {
    runMenu()
}

//private val noteAPI = NoteAPI(XMLSerializer(File("notes.xml")))
private val noteAPI = NoteAPI(JSONSerializer(File("notes.json")))


