package sma.rhythmtapper;

import android.media.midi.MidiDeviceService;
import android.media.midi.MidiDeviceStatus;
import android.media.midi.MidiReceiver;

import com.mobileer.miditools.MidiFramer;

import java.io.IOException;

public class MidiScope extends MidiDeviceService{
    private static final String TAG = "MidiScope";

    private static ScopeLogger mScopeLogger;
    private MidiReceiver mInputReceiver = new MyReceiver();
    private static MidiFramer mDeviceFramer;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public MidiReceiver[] onGetInputPortReceivers() {
        return new MidiReceiver[] { mInputReceiver };
    }

    public static ScopeLogger getScopeLogger() {
        return mScopeLogger;
    }

    public static void setScopeLogger(ScopeLogger logger) {
        if (logger != null) {
            // Receiver that prints the messages.
            LoggingReceiver loggingReceiver = new LoggingReceiver(logger);
            mDeviceFramer = new MidiFramer(loggingReceiver);
        }
        mScopeLogger = logger;
    }

    class MyReceiver extends MidiReceiver {
        @Override
        public void onSend(byte[] data, int offset, int count,
                           long timestamp) throws IOException {
            if (mScopeLogger != null) {
                // Send raw data to be parsed into discrete messages.
                mDeviceFramer.send(data, offset, count, timestamp);
            }
        }
    }

    /**
     * This will get called when clients connect or disconnect.
     * Log device information.
     */
    @Override
    public void onDeviceStatusChanged(MidiDeviceStatus status) {
        if (mScopeLogger != null) {
            if (status.isInputPortOpen(0)) {
                mScopeLogger.log("=== connected ===");
                String text = MidiPrinter.formatDeviceInfo(
                        status.getDeviceInfo());
                mScopeLogger.log(text);
            } else {
                mScopeLogger.log("--- disconnected ---");
            }
        }
    }
}
