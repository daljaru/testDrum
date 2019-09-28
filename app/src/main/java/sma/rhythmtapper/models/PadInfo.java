package sma.rhythmtapper.models;

import sma.rhythmtapper.MainActivity;

public class PadInfo {
    private String padNumber;
    public PadInfo(){}
    public PadInfo(String padNumber){
        this.padNumber = padNumber;
    }

    public String getPadNumber() {
        return padNumber;
    }

    public void setPadNumber(String padNumber) {
        this.padNumber = padNumber;
    }
}
