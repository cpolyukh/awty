package edu.washington.cpolyukh.awty;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {
    private PendingIntent alarmIntent = null;
    private AlarmManager am = null;
    private String formattedNumber = "";
    private String desiredMessage = "";
    private BroadcastReceiver alarmReceiver = new AlarmReciever();

    public class AlarmReciever extends BroadcastReceiver {

        public void onReceive(Context c, Intent i) {
            //String alarmText = formattedNumber + ": Are we there yet?";

            Uri uri = Uri.parse("sms://" + formattedNumber);
            //Uri uri = Uri.parse("sms://5554");
            Intent it = new Intent(Intent.ACTION_VIEW, uri);
            it.putExtra("sms_body", desiredMessage);

            startActivity(it);
            //Toast.makeText(MainActivity.this, alarmText, Toast.LENGTH_SHORT).show();
        }
    }

    /*BroadcastReceiver alarmReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context c, Intent i) {
            Toast.makeText(MainActivity.this, alarmText, Toast.LENGTH_SHORT).show();
        }
    };*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText editMessage = (EditText) findViewById(R.id.editMessage);
        final EditText editPhone = (EditText) findViewById(R.id.editPhone);
        final EditText editMinutes = (EditText) findViewById(R.id.editMinutes);

        am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent i = new Intent();
        i.setAction("edu.washington.cpolyukh.awty");
        alarmIntent = PendingIntent.getBroadcast(this, 0, i, 0);

        final Button btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnSubmit.getText().toString().equals("Start")) {

                    String message = editMessage.getText().toString();
                    desiredMessage = message;
                    String phone = editPhone.getText().toString();
                    double minutesDouble = 0;
                    if (isNumeric(editMinutes.getText().toString())) {
                        minutesDouble = Double.parseDouble(editMinutes.getText().toString());
                    }

                    int minutes = (int) minutesDouble;

                    if (appropriateValues(message, phone, minutesDouble)) {
                        btnSubmit.setText("Stop");
                    }

                    registerReceiver(alarmReceiver, new IntentFilter("edu.washington.cpolyukh.awty"));
                    int timeInMillis = minutes * 60 * 1000;
                    am.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), timeInMillis, alarmIntent);
                    formattedNumber = formatPhoneNumber(phone);

                } else {
                    btnSubmit.setText("Start");

                    am.cancel(alarmIntent);
                }
            }
        });
    }


    private String formatPhoneNumber(String phone) {
        String formatted = phone.substring(0, 3) + "-" + phone.substring(3, 6) +
                "-" + phone.substring(6);
        return formatted;
    }

    private static boolean isNumeric(String str) {
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) return false;
        }
        return true;
    }

    private boolean appropriateValues(String message, String phone, double minutes) {
        return (message != null && phone.length() == 10 && isNumeric(phone) &&
                minutes > 0 && (int) minutes == minutes);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
