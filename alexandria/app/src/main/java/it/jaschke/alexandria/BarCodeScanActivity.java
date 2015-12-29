package it.jaschke.alexandria;

import com.google.zxing.Result;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import it.jaschke.alexandria.utils.Constants;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * A simple activity for bar code scan
 */
public class BarCodeScanActivity extends Activity implements ZXingScannerView.ResultHandler {
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
        Intent resultIntent = new Intent();
        resultIntent.putExtra(Constants.SCANNED_BARCODE_KEY, result.toString());
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
}
