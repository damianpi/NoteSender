package pl.damianpi.notesender;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;


public class EmailSenderActivity extends AppCompatActivity {
    private static final String EXTRA_NOTE_TEXT_EMAIL = "pl.damianpi.notesender.note_email";

    private EditText mEmailText;
    private Button mSendEmail;
    private Spinner mAddressSpinner;

    public static Intent newIntent(Context context, String text) {
        Intent intent = new Intent(context, EmailSenderActivity.class);
        intent.putExtra(EXTRA_NOTE_TEXT_EMAIL, text);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.email_sender);

        mEmailText = (EditText) findViewById(R.id.email_sender_text);
        String text;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                text = "";
            } else {
                text = extras.getString(EXTRA_NOTE_TEXT_EMAIL);
            }
        } else {
            text = (String) savedInstanceState.getSerializable(EXTRA_NOTE_TEXT_EMAIL);
        }
        mEmailText.setText(text);

        mSendEmail = (Button) findViewById(R.id.send_email_button);
        mSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail();
            }
        });

        mAddressSpinner = (Spinner) findViewById(R.id.email_address_spinner);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,
                getNameEmailDetails());
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mAddressSpinner.setAdapter(spinnerAdapter);

    }


    private void sendEmail() {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{mAddressSpinner.getSelectedItem().toString()});

        try {
            i.putExtra(Intent.EXTRA_SUBJECT, "NoteSender email: " + getUserName());
        }catch(Exception e){
            i.putExtra(Intent.EXTRA_SUBJECT, "NoteSender email");
        }
        i.putExtra(Intent.EXTRA_TEXT, mEmailText.getText());
        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this,
                    "There are no email clients installed.",
                    Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(this, "Email has been created", Toast.LENGTH_SHORT).show();
    }

    private String getUserName(){
        Cursor c = getApplication().getContentResolver().query(
                ContactsContract.Profile.CONTENT_URI, null, null, null, null);
        c.moveToFirst();
        String name = c.getString(c.getColumnIndex("display_name"));
        c.close();

        return name;
    }

    private ArrayList<String> getNameEmailDetails() {
        ArrayList<String> emlRecs = new ArrayList<String>();
        HashSet<String> emlRecsHS = new HashSet<String>();
        Context context = this;
        ContentResolver cr = context.getContentResolver();
        String[] PROJECTION = new String[]{ContactsContract.RawContacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.PHOTO_ID,
                ContactsContract.CommonDataKinds.Email.DATA,
                ContactsContract.CommonDataKinds.Photo.CONTACT_ID};
        String order = "CASE WHEN "
                + ContactsContract.Contacts.DISPLAY_NAME
                + " NOT LIKE '%@%' THEN 1 ELSE 2 END, "
                + ContactsContract.Contacts.DISPLAY_NAME
                + ", "
                + ContactsContract.CommonDataKinds.Email.DATA
                + " COLLATE NOCASE";
        String filter = ContactsContract.CommonDataKinds.Email.DATA + " NOT LIKE ''";
        Cursor cur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                PROJECTION, filter, null, order);
        if (cur.moveToFirst()) {
            do {
                String name = cur.getString(1);
                String emlAddr = cur.getString(3);
                if (emlRecsHS.add(emlAddr.toLowerCase())) {
                    emlRecs.add(emlAddr);
                }
            } while (cur.moveToNext());
        }
        cur.close();
        return emlRecs;
    }


}
