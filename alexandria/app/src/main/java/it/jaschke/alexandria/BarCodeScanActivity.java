package it.jaschke.alexandria;

import com.google.zxing.Result;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import it.jaschke.alexandria.utils.Constants;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * A simple activity for bar code scan
 */
public class BarCodeScanActivity extends Activity implements ZXingScannerView.ResultHandler {
    private static final String TAG = BarCodeScanActivity.class.getSimpleName();

    private ZXingScannerView scannerView;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);
    }

    @Override
    public void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }

    @Override
    public void onResume() {
        super.onResume();
        scannerView.setResultHandler(this);
        scannerView.startCamera();
    }

    @Override
    public void handleResult(Result result) {
        saveBarcode(result.getText());
    }

    private void saveBarcode(String barcode) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.SCANNED_BARCODE_KEY, barcode);
        editor.apply();
    }
}
