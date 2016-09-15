package pl.damianpi.notesender.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.Date;
import java.util.UUID;

import pl.damianpi.notesender.Note;
import pl.damianpi.notesender.database.NoteDbSchema.NoteTable;


public class NoteCursorWrapper extends CursorWrapper {
    public NoteCursorWrapper(Cursor cursor){
        super(cursor);
    }

    public Note getNote(){
        String uuidString = getString(getColumnIndex(NoteTable.Cols.UUID));
        String title = getString(getColumnIndex(NoteTable.Cols.MESSAGE));
        long date = getLong(getColumnIndex(NoteTable.Cols.DATE));

        Note note = new Note(UUID.fromString(uuidString));
        note.setTitle(title);
        note.setDate(new Date(date));

        return note;
    }
}
