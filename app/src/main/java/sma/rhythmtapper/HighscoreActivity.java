package sma.rhythmtapper;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TextView;

import sma.rhythmtapper.models.Difficulty;
import sma.rhythmtapper.models.PadInfo;




public class HighscoreActivity extends Activity {

    public static String PREF_FILE = "HighscorePrefFile";
    private SharedPreferences _prefs;

    private TextView _easyTxtView;
    private TextView _medTxtView;
    private TextView _hardTxtView;

    private TextView testText;

    private PadInfo padTest;
    private String testString;


    public PadInfo getReceivedPadInfo(){
        return ((MainActivity) MainActivity.testmContext).getPadInformation();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscore);
        //_highscoreView = (ListView)this.findViewById(R.id.highscore_list_view);

        //*from cj
        //LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, new IntentFilter("DrumHitNumber"));
        //from cj

        _easyTxtView = (TextView)this.findViewById(R.id.highscore_txt_score_easy);
        _medTxtView = (TextView)this.findViewById(R.id.highscore_txt_score_medium);
        _hardTxtView = (TextView)this.findViewById(R.id.highscore_txt_score_hard);

        //from cj
        testText = (TextView)this.findViewById(R.id.testTextView);
        padTest = getReceivedPadInfo();
        testString = padTest.getPadNumber();
        //testText.setText(testString);
        //from cj


        // load highscores
        _prefs = PreferenceManager.getDefaultSharedPreferences(this);

        String easyMode = String.valueOf(_prefs.getInt(Difficulty.EASY_TAG, 0));
        String mediumMode = String.valueOf(_prefs.getInt(Difficulty.MED_TAG, 0));
        String hardMode = String.valueOf(_prefs.getInt(Difficulty.HARD_TAG, 0));

        _easyTxtView.setText(easyMode);
        _medTxtView.setText(mediumMode);
        _hardTxtView.setText(hardMode);


    }

    //*from cj
   /* private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String data1 = intent.getStringExtra("DrumPadNumber");
            testText.setText(data1);
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
    }*/
    //from cj
}
