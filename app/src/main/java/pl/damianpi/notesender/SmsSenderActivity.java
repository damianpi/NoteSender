package pl.damianpi.notesender;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class SmsSenderActivity extends AppCompatActivity {
    private static final String EXTRA_NOTE_TEXT_SMS = "pl.damianpi.notesender.note_text";
    private static final int PICK_CONTACT = 1;

    private EditText mSmsText;
    private EditText mSmsDestination;
    private Button mSendSms;
    private Button mChooseNumber;
    private String mChosenNumber;

    public static Intent newIntent(Context context, String text) {
        Intent intent = new Intent(context, SmsSenderActivity.class);
        intent.putExtra(EXTRA_NOTE_TEXT_SMS, text);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sms_sender);

        mSmsText = (EditText) findViewById(R.id.sms_sender_text);
        String text;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                text = "";
            } else {
                text = extras.getString(EXTRA_NOTE_TEXT_SMS);
            }
        } else {
            text = (String) savedInstanceState.getSerializable(EXTRA_NOTE_TEXT_SMS);
        }
        mSmsText.setText(text);

        mSmsDestination = (EditText) findViewById(R.id.sms_sender_destination);
        mSendSms = (Button) findViewById(R.id.send_sms_button);
        mSendSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String destination = mSmsDestination.getText().toString();
                String message = mSmsText.getText().toString();
                sendSMS(destination, message);
            }
        });

        mChooseNumber = (Button) findViewById(R.id.choose_number_button);
        mChooseNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, PICK_CONTACT);
            }
        });
    }

    private void sendSMS(String phoneNumber, String message) {
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, null, null);
        Toast.makeText(this, "SMS has been sent", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume(){
        super.onResume();
        this.mSmsDestination.setText(mChosenNumber);
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        switch (reqCode) {
            case (PICK_CONTACT) :
                if (resultCode == Activity.RESULT_OK) {

                    Uri contactData = data.getData();
                    Cursor c =  managedQuery(contactData, null, null, null, null);
                    if (c.moveToFirst()) {

                        String id =c.getString(
                                c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                        String hasPhone =c.getString(
                                c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                        if (hasPhone.equalsIgnoreCase("1")) {
                            Cursor phones = getContentResolver().query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ id,
                                    null, null);
                            phones.moveToFirst();
                            mChosenNumber = phones.getString(phones.getColumnIndex("data1"));
                            Log.d("SMS Sender", "Number is:" + mChosenNumber);
                            onNumberChosen(mChosenNumber);
                        }
                        String name = c.getString(c.getColumnIndex(
                                ContactsContract.Contacts.DISPLAY_NAME));
                    }
                }else {
                   Toast.makeText(this, "Wrong response code - have you chosen anything?",
                           Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                Toast.makeText(this, "Error getting response",
                        Toast.LENGTH_SHORT).show();
        }
    }

    private void onNumberChosen(String number){
        this.mChosenNumber = number;
        this.mSmsDestination.setText(mChosenNumber);
    }



}
