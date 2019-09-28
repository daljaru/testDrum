package sma.rhythmtapper;

//from cj

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.midi.MidiManager;
import android.media.midi.MidiReceiver;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.mobileer.miditools.MidiFramer;
import com.mobileer.miditools.MidiOutputPortSelector;
import com.mobileer.miditools.MidiPortWrapper;

import java.io.IOException;
import java.util.LinkedList;
import java.util.StringTokenizer;

import sma.rhythmtapper.models.PadInfo;

//from cj
//import com.mobileer.example.midiscope.R;

public class MainActivity extends Activity implements ScopeLogger{
    //from cj
    public static Context testmContext;

    private String searchString = "NoteOn";
    private PadInfo padInformation = new PadInfo("0");
    //from cj

    //from midiScope
    private static final String TAG = "MidiScope";

    private TextView mLog;
    private ScrollView mScroller;
    private LinkedList<String> logLines = new LinkedList<String>();
    private static final int MAX_LINES = 100;
    //
    private MidiOutputPortSelector mLogSenderSelector;
    //
    private MidiManager mMidiManager;
    private MidiReceiver mLoggingReceiver;
    //
    private MidiFramer mConnectFramer;
    //
    private MyDirectReceiver mDirectReceiver;
    private boolean mShowRaw;
    //from midiScope

    private Button _startBtn;
    private Button _highscoreBtn;
    private Button _aboutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //from cj
        testmContext = this;
        //from cj

        this._startBtn = (Button)this.findViewById(R.id.main_btn_start);
        this._highscoreBtn = (Button)this.findViewById(R.id.main_btn_highscore);
        this._aboutBtn = (Button)this.findViewById(R.id.main_btn_about);

        this._startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, DifficultySelectionActivity.class);
                MainActivity.this.startActivity(i);
            }
        });

        this._highscoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, HighscoreActivity.class);
                MainActivity.this.startActivity(i);
            }
        });
        this._aboutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, AboutActivity.class);
                MainActivity.this.startActivity(i);
            }
        });

        //*from midiScope
        mLog = (TextView) findViewById(R.id.log);
        mScroller = (ScrollView) findViewById(R.id.scroll);

        // Setup MIDI
        mMidiManager = (MidiManager) getSystemService(MIDI_SERVICE);

        // Receiver that prints the messages.
        mLoggingReceiver = new LoggingReceiver(this);

        // Receivers that parses raw data into complete messages.
        mConnectFramer = new MidiFramer(mLoggingReceiver);

        // Setup a menu to select an input source.
        mLogSenderSelector = new MidiOutputPortSelector(mMidiManager, this,
                R.id.spinner_senders) {

            @Override
            public void onPortSelected(final MidiPortWrapper wrapper) {
                super.onPortSelected(wrapper);
                if (wrapper != null) {
                    log(MidiPrinter.formatDeviceInfo(wrapper.getDeviceInfo()));
                }
            }
        };

        mDirectReceiver = new MainActivity.MyDirectReceiver();
        mLogSenderSelector.getSender().connect(mDirectReceiver);

        // Tell the virtual device to log its messages here..
        MidiScope.setScopeLogger(this);
        //from MIDIScope
    }
    //from midiScope


    @Override
    public void onDestroy() {
        mLogSenderSelector.onClose();
        // The scope will live on as a service so we need to tell it to stop
        // writing log messages to this Activity.
        MidiScope.setScopeLogger(null);
        super.onDestroy();
    }

    class MyDirectReceiver extends MidiReceiver {
        @Override
        public void onSend(byte[] data, int offset, int count,
                           long timestamp) throws IOException {
            if (mShowRaw) {
                String prefix = String.format("0x%08X, ", timestamp);
                logByteArray(prefix, data, offset, count);
            }
            // Send raw data to be parsed into discrete messages.
            mConnectFramer.send(data, offset, count, timestamp);
        }
    }

    /**
     * @param string
     */
    @Override
    public void log(final String string) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                logFromUiThread(string);
            }
        });
    }

    //from cj
    public static boolean isNumeric(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch(NumberFormatException e) {
            return false;
        }
    }
    //from cj
    // Log a message to our TextView.
    // Must run on UI thread.


    public PadInfo getPadInformation(){
        return this.padInformation;
    }

    private void logFromUiThread(String s) {
        //from cj
        //Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
        boolean contains = s.contains(searchString);
        if(contains == true){
            StringTokenizer st = new StringTokenizer(s,", ");
            while(st.hasMoreTokens()) {
                String tokenString = st.nextToken();
                if(isNumeric(tokenString) == true){
                    padInformation.setPadNumber(tokenString);
                    sendMessage();
                    //Toast.makeText(getApplicationContext(), padInformation.getPadNumber(), Toast.LENGTH_LONG).show();
                    String tempString = padInformation.getPadNumber();


                    //Intent testIntent = new Intent(testIntentContext, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    //testIntentContext.startActivity(testIntent);

                    Intent intent = new Intent("DrumHitNumber");
                    intent.putExtra("DrumPadNumber", padInformation.getPadNumber());


                    LocalBroadcastManager.getInstance(testmContext).sendBroadcast(intent);

                    //break;
                }
            }
        }
        //from cj

        logLines.add(s);
        if (logLines.size() > MAX_LINES) {
            logLines.removeFirst();
        }
        // Render line buffer to one String.
        StringBuilder sb = new StringBuilder();
        for (String line : logLines) {
            sb.append(line).append('\n');
        }
        mLog.setText(sb.toString());
        mScroller.fullScroll(View.FOCUS_DOWN);
    }

    //*from cj
    private void sendMessage() {
        Log.d("sender", "Broadcasting message");
        Intent intent = new Intent("custom-event-name");
        // You can also include some extra data.
        intent.putExtra("message", "This is my message!");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
    //from cj

    private void logByteArray(String prefix, byte[] value, int offset, int count) {
        StringBuilder builder = new StringBuilder(prefix);
        for (int i = 0; i < count; i++) {
            builder.append(String.format("0x%02X", value[offset + i]));
            if (i != count - 1) {
                builder.append(", ");
            }
        }
        log(builder.toString());
    }
}
