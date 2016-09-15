package pl.damianpi.notesender.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import pl.damianpi.notesender.database.NoteDbSchema.NoteTable;


public class NoteBaseHelper extends SQLiteOpenHelper{
    public static final int VERSION = 1;
    public static final String DATABASE_NAME = "noteBase.db";

    public NoteBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + NoteTable.NAME + "("
                        + " _id integer primary key autoincrement, "
                        + NoteTable.Cols.UUID + ", "
                        + NoteTable.Cols.MESSAGE + ", "
                        + NoteTable.Cols.DATE + ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
