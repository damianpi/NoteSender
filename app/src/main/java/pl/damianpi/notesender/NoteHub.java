package pl.damianpi.notesender;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import pl.damianpi.notesender.database.NoteCursorWrapper;
import pl.damianpi.notesender.database.NoteDbSchema;
import pl.damianpi.notesender.database.NoteDbSchema.NoteTable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import pl.damianpi.notesender.database.NoteBaseHelper;

public class NoteHub {
    private static NoteHub sNoteHub;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static NoteHub get(Context context){
        if(sNoteHub == null){
            sNoteHub = new NoteHub(context);
        }
        return sNoteHub;
    }

    private NoteHub(Context context){
        mContext = context.getApplicationContext();
        mDatabase = new NoteBaseHelper(mContext).getWritableDatabase();


    }

    public void addNote(Note n){
        ContentValues values = getContentValues(n);
        mDatabase.insert(NoteTable.NAME, null, values);
    }

    public void deleteNote(UUID id){
        mDatabase.delete(NoteTable.NAME, NoteTable.Cols.UUID + "='" + id + "'", null);
    }

    public List<Note> getNotes() {
        List<Note> notes = new ArrayList<>();
        NoteCursorWrapper cursor = queryNotes(null, null);

        try{
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                notes.add(cursor.getNote());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return notes;
    }

    public Note getNote(UUID id){
        NoteCursorWrapper cursor = queryNotes(
                NoteTable.Cols.UUID + " = ?",
                new String[] {id.toString()}
        );

        try{
            if(cursor.getCount() == 0){
                return null;
            }

            cursor.moveToFirst();
            return cursor.getNote();
        } finally {
            cursor.close();
        }
    }

    public void updateNote(Note note){
        String uuidString = note.getId().toString();
        ContentValues values = getContentValues(note);

        mDatabase.update(NoteTable.NAME, values,
                NoteTable.Cols.UUID + " = ?",
                new String[] {uuidString});
    }

    private static ContentValues getContentValues(Note note){
        ContentValues values = new ContentValues();
        values.put(NoteTable.Cols.UUID, note.getId().toString());
        values.put(NoteTable.Cols.MESSAGE, note.getTitle());
        values.put(NoteTable.Cols.DATE, note.getDate().getTime());
        return values;
    }

    private NoteCursorWrapper queryNotes(String whereClause, String[] whereArgs){
        Cursor cursor = mDatabase.query(
                NoteTable.NAME,
                null, // Columns - null selects all columns
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null // orderBy
        );

        return new NoteCursorWrapper(cursor);
    }
}
