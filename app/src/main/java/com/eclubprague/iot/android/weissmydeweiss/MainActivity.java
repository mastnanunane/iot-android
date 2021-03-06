package com.eclubprague.iot.android.weissmydeweiss;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v4.widget.DrawerLayout;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.eclubprague.iot.android.weissmydeweiss.cloud.TokenWrapper;
import com.eclubprague.iot.android.weissmydeweiss.cloud.hubs.Hub;
import com.eclubprague.iot.android.weissmydeweiss.cloud.sensors.Sensor;
import com.eclubprague.iot.android.weissmydeweiss.cloud.sensors.supports.cloud_entities.AllSensors;
import com.eclubprague.iot.android.weissmydeweiss.tasks.GetSensorsDataTask;
import com.eclubprague.iot.android.weissmydeweiss.tasks.RegisterSensorTask;
import com.eclubprague.iot.android.weissmydeweiss.tasks.TestingTask;
import com.eclubprague.iot.android.weissmydeweiss.ui.SensorRegisterDialog;
import com.eclubprague.iot.android.weissmydeweiss.ui.SensorsExpandableListViewAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks,
        GetSensorsDataTask.TaskDelegate,
        TestingTask.TaskDelegate, SensorRegisterDialog.DialogDelegate {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    private TokenWrapper token;
    private String email;



    //private ArrayList<User> userRef = new ArrayList<>();
    private ArrayList<MainActivity> activityRef = new ArrayList<>();
    private ArrayList<GetSensorsDataTask.TaskDelegate> delegateRef = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        String tokenWrapperString = getIntent().getStringExtra("token");

        token = TokenWrapper.getTokenWrapperInstance(tokenWrapperString);
        Log.e("PSTOKEN", token.getAccess_token());

        email = getIntent().getStringExtra("email");

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));


        //userRef.add(new User(getIntent().getStringExtra("username"), getIntent().getStringExtra("password")));
        activityRef.add(this);
        delegateRef.add(this);
    }

    public static String getCurrentSsid(Context context) {
        String ssid = null;
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (networkInfo.isConnected()) {
            final WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            final WifiInfo connectionInfo = wifiManager.getConnectionInfo();
            if (connectionInfo != null && !TextUtils.isEmpty(connectionInfo.getSSID())) {
                ssid = connectionInfo.getSSID();
            }
        }
        return ssid;
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        Fragment fragment;
        FragmentManager fragmentManager = getSupportFragmentManager(); // For AppCompat use getSupportFragmentManager
        switch(position) {
            default:
            case 0:
                fragment = SensorsListFragment.newInstance();
                ((SensorsListFragment)(fragment)).setMainActivityRef(activityRef);
                break;
            case 1:
                fragment = BuiltInSensorsListFragment.newInstance();
                break;
            case 2:
                //fragment = HubsListFragment.newInstance();
                fragment = ProfileFragment.newInstance();
                ((ProfileFragment)(fragment)).setEmail(email);
        }

        // update the main content by replacing fragments
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Log.e("WIFI", getCurrentSsid(this));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Launch a QR code scanner.
     */
    public void launchQRScanner(View view) {
        Intent myIntent = new Intent(MainActivity.this, CameraActivity.class);
        //myIntent.putExtra("key", value); //Optional parameters
        MainActivity.this.startActivityForResult(myIntent, CameraActivity.REQUEST_SCAN_QR_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == CameraActivity.REQUEST_SCAN_QR_CODE) {
            if(resultCode == RESULT_OK) {
                try {
                    final String qrcode = data.getStringExtra(CameraActivity.RESULT_BARCODE);
                    // send the code to the audience
                    String[] qrCodeSplit = qrcode.split(";");
                    final String sensorId = qrCodeSplit[0].trim();
                    final int sensorType = Integer.parseInt(qrCodeSplit[1], 16);
                    final String sensorSecret = qrCodeSplit[2];
                    Log.e("QRCODE", sensorId + ", " + Integer.toString(sensorType) + ", " + sensorSecret);

                    new SensorRegisterDialog(this, sensorId, sensorType, sensorSecret);
                }
                catch(Exception e) {
                    Toast t2 = Toast.makeText(this, "Exception: NaN", Toast.LENGTH_SHORT);
                    t2.show();
                    Log.e("REG", e.toString());
                }
            }
        }
    }

    public void getSensorsData() {
        new GetSensorsDataTask(delegateRef, token.getAccess_token()).execute();
    }

    public TokenWrapper getToken() {
        return token;
    }

    public void testing() {
        new TestingTask(this).execute(token.getAccess_token());
    }

    @Override
    public void onGetSensorsDataTaskCompleted(AllSensors message) {
        refreshSensorsList(message);

    }

    public void refreshSensorsList(AllSensors message) {
        ExpandableListView sensorsList = (ExpandableListView) findViewById(R.id.sensors_expList);

        Hub hub1 = new Hub("My Sensors");
        Hub hub2 = new Hub("Public Sensors");
        Hub hub3 = new Hub("Shared with me");
        List<Hub> hubs = new ArrayList<>();
        hubs.add(hub1);
        hubs.add(hub2);
        hubs.add(hub3);


        HashMap<Hub, List<Sensor>> hubSensors = new LinkedHashMap<>();

        hubSensors.put(hub1, message.getMySensors());
        hubSensors.put(hub2, message.getPublicSensors());
        hubSensors.put(hub3, message.getBorrowedSensors());

        SensorsExpandableListViewAdapter adapter = new SensorsExpandableListViewAdapter(
                this, hubs, hubSensors);

        sensorsList.setAdapter(adapter);
    }


    //----------------------------------------------------------------

    @Override
    public void onBackPressed() {
        Log.d("CDA", "onBackPressed Called");
        Intent setIntent = new Intent(Intent.ACTION_MAIN);
        setIntent.addCategory(Intent.CATEGORY_HOME);
        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(setIntent);
    }

    @Override
    public void onTestingTaskCompleted(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSensorRegisterDialogSubmitted(Sensor sensor) {
        new RegisterSensorTask(sensor, this).execute(token.getAccess_token());
    }
}
