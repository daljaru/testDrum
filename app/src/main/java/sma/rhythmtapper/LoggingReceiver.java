package sma.rhythmtapper;

import android.media.midi.MidiReceiver;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

public class LoggingReceiver extends MidiReceiver{
    public static final String TAG = "MidiScope";
    private static final long NANOS_PER_MILLISECOND = 1000000L;
    private static final long NANOS_PER_SECOND = NANOS_PER_MILLISECOND * 1000L;
    private long mStartTime;
    private ScopeLogger mLogger;
    private long mLastTimeStamp = 0;


    public LoggingReceiver(ScopeLogger logger) {
        mStartTime = System.nanoTime();
        mLogger = logger;
    }

    /*
     * @see android.media.midi.MidiReceiver#onSend(byte[], int, int, long)
     */
    @Override
    public void onSend(byte[] data, int offset, int count, long timestamp)
            throws IOException {
        StringBuilder sb = new StringBuilder();

        StringBuilder testSb = new StringBuilder();

        if (timestamp == 0) {
            sb.append(String.format("-----0----: "));
        } else {
            long monoTime = timestamp - mStartTime;
            long delayTimeNanos = timestamp - System.nanoTime();
            int delayTimeMillis = (int)(delayTimeNanos / NANOS_PER_MILLISECOND);
            double seconds = (double) monoTime / NANOS_PER_SECOND;
            // Mark timestamps that are out of order.
            sb.append((timestamp < mLastTimeStamp) ? "*" : " ");
            mLastTimeStamp = timestamp;
            sb.append(String.format("%10.3f (%2d): ", seconds, delayTimeMillis));
        }
        sb.append(MidiPrinter.formatBytes(data, offset, count));
        sb.append(": ");


        sb.append(MidiPrinter.formatMessage(data, offset, count));
        String text = sb.toString();
        mLogger.log(text);
        Log.i(TAG, text);
    }
}
