package sma.rhythmtapper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import sma.rhythmtapper.framework.Impl.RTGame;
import sma.rhythmtapper.framework.Screen;
import sma.rhythmtapper.game.LoadingScreen;
import sma.rhythmtapper.models.Difficulty;

public class GameActivity extends RTGame {
    private Difficulty _diff;

    @Override
    public Screen getInitScreen() {
        // get passed difficulty object
        _diff = (Difficulty)this.getIntent().getSerializableExtra("difficulty");
        return new LoadingScreen(this, _diff);
    }


    //*from cj
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            drumPadNumber= intent.getStringExtra("DrumPadNumber");
            Toast.makeText(getApplicationContext(), drumPadNumber, Toast.LENGTH_LONG).show();
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
    }
    //from cj

}
