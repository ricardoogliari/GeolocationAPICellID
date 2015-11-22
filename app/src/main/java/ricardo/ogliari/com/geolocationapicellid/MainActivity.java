package ricardo.ogliari.com.geolocationapicellid;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends AppCompatActivity {

    private TextView txtMnc;
    private TextView txtMcc;
    private TextView txtLac;
    private TextView txtCid;
    private TextView txtMacAddress;
    private TextView txtSSID;
    private TextView txtLatLng;

    private int lac;
    private int cid;
    private String networkOperator;
    private String macAddress;

    private double latitude;
    private double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtLac = (TextView) findViewById(R.id.txtLac);
        txtMcc = (TextView) findViewById(R.id.txtMcc);
        txtMnc = (TextView) findViewById(R.id.txtMnc);
        txtCid = (TextView) findViewById(R.id.txtCid);
        txtMacAddress = (TextView) findViewById(R.id.txtMacAddress);
        txtSSID = (TextView) findViewById(R.id.txtSSID);
        txtLatLng = (TextView) findViewById(R.id.txtLatLng);

        final TelephonyManager t = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (t.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM) {
            final GsmCellLocation location = (GsmCellLocation) t.getCellLocation();
            if (location != null) {
                lac = location.getLac();
                cid = location.getCid();

                txtLac.setText("Lac: " + lac);
                txtCid.setText("Cid: " + cid);
                networkOperator = t.getNetworkOperator();
                txtMcc.setText("MCC: " + networkOperator.substring(0, 3));//MCCMNC
                txtMnc.setText("MNC: " + networkOperator.substring(3));//MCCMNC
            }
        }

        WifiManager mainWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo currentWifi = mainWifi.getConnectionInfo();
        if(currentWifi != null)
        {
            macAddress = currentWifi.getMacAddress();
            txtSSID.setText(currentWifi.getSSID());
            txtMacAddress.setText(macAddress);
        }

    }

    public void getPositionByCellId(View view){
        RestAdapter retrofit = new RestAdapter.Builder()
                .setEndpoint("https://www.googleapis.com")
                .build();

        CellIdService service = retrofit.create(CellIdService.class);
        service.geolocate("{\n" +
                "  \"cellTowers\": [\n" +
                "    {\n" +
                "      \"cellId\": "+cid+",\n" +
                "      \"locationAreaCode\": "+lac+",\n" +
                "      \"mobileCountryCode\": "+networkOperator.substring(0, 3)+",\n" +
                "      \"mobileNetworkCode\": "+networkOperator.substring(3)+"\n" +
                "    }\n" +
                "  ]\n" +
                "}", "AIzaSyD-x8ItqL7UF3vqSMYChlRARwrXuyCqny0", new Callback<CellId>() {

            @Override
            public void success(CellId cellId, Response response) {
                txtLatLng.setText(cellId.location.lat + ", " + cellId.location.lng);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e("TESTE", "ERRO: " + error.getMessage());
            }
        });
    }

    public void getPositionByWiFi(View view){
        RestAdapter retrofit = new RestAdapter.Builder()
                .setEndpoint("https://www.googleapis.com")
                .build();

        CellIdService service = retrofit.create(CellIdService.class);
        service.geolocate("{\n" +
                "  \"macAddress\": " + macAddress +
                "}", "AIzaSyD-x8ItqL7UF3vqSMYChlRARwrXuyCqny0", new Callback<CellId>() {

            @Override
            public void success(CellId cellId, Response response) {
                latitude = cellId.location.lat;
                longitude = cellId.location.lng;
                txtLatLng.setText(latitude + ", " + longitude);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e("TESTE", "ERRO: " + error.getMessage());
            }
        });
    }

    public void seeInMaps(View v){
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);
        startActivity(intent);
    }
}
