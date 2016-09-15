package pl.damianpi.notesender;

import java.util.Date;
import java.util.UUID;

public class Note {
    private UUID mId;
    private String mMessage;
    private Date mDate;
    private String mText;

    public Note(){
        this(UUID.randomUUID());
    }

    public Note(UUID id){
        mId = id;
        mDate = new Date();
    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mMessage;
    }

    public void setTitle(String title) {
        mMessage = title;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }


    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }
}
