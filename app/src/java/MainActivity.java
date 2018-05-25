package se.ju.frjo1425student.waiterbotapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.Nullable;
import android.content.Intent;
import android.bluetooth.BluetoothAdapter;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.support.v4.app.Fragment;

import java.util.Arrays;

/**
 * Created by jonas on 2017-11-08.
 */

public class MainActivity extends AppCompatActivity
        implements CheckConnectionsFragment.CheckConnectionsListener,
        OrdersFragment.OrdersListener
{
    private static final String TAG = "MainActivity";

    private static final int REQUEST_ENABLE_BT = 1;
    private static boolean mLeConnected = false;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolbar;
    private NavigationView mNavigationView;
    private BluetoothHandler mBluetoothHandler;
    private FragmentManager mFragmentManager;
    private String mStatusString ="";
    private String mBatteryString ="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstance)
    {
        super.onCreate(savedInstance);
        setContentView(R.layout.layout_main);

        mBluetoothHandler = new BluetoothHandler(this);
        mFragmentManager = getSupportFragmentManager();

        //If not activated, prompt the user to activate bluetooth on their device
        if (mBluetoothHandler.getBluetoothAdapter() == null ||
                !mBluetoothHandler.getBluetoothAdapter().isEnabled())
        {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        //If not activated, prompt the user to activate location services
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION))
            {
                Log.d(TAG, "Requesting permission for ACCESS_FINE_LOCATION");
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }

        //get reference to the views and set up bluetooth adapter
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mToolbar = (Toolbar) findViewById(R.id.nav_actionbar_id);
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);

        //initialize custom action bar
        setSupportActionBar(mToolbar);
        mToggle = new ActionBarDrawerToggle(this,
                                            mDrawerLayout,
                                            R.string.drawer_open,
                                            R.string.drawer_close);

        mToggle.syncState();
        mDrawerLayout.addDrawerListener(mToggle);

        //initialize the first fragment on startup
        Fragment ordersFragment = new OrdersFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, ordersFragment, "handle_orders").commit();
        setTitle(R.string.nav_drawer_handle_orders);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Listen for click events
        mNavigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem)
            {
                //FragmentManager fragmentManager = getSupportFragmentManager();
                switch(menuItem.getItemId())
                {
                    case(R.id.menu_item_handle_orders):
                        mFragmentManager.beginTransaction().replace(R.id.content_frame,
                                new OrdersFragment(), "handle_orders").commit();
                        setTitle(R.string.nav_drawer_handle_orders);
                        break;

                    case(R.id.menu_item_control_bot):
                        mFragmentManager.beginTransaction().replace(R.id.content_frame,
                                new ControlBotFragment(), "control_bot").commit();
                        setTitle(R.string.nav_drawer_control_bot);
                        break;

                    case(R.id.menu_item_check_connection):
                        mFragmentManager.beginTransaction().replace(R.id.content_frame,
                                new CheckConnectionsFragment(), "check_connections").commit();
                        setTitle(R.string.nav_drawer_check_connections);
                        break;
                }

                mNavigationView.setCheckedItem(R.id.menu_item_handle_orders);
                mDrawerLayout.closeDrawer(mNavigationView);
                return true;
            }
        });
    }

    @Override
    protected void onDestroy()
    {
        Log.d(TAG, "onDestroy called in MainActivity.");
        super.onDestroy();
        mBluetoothHandler.closeConnection();
        mLeConnected = false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (mToggle.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setTitle(CharSequence title)
    {
        getSupportActionBar().setTitle(title);
    }

    @Override
    public void leScan()
    {
        if (!mLeConnected)
            mBluetoothHandler.scanForDevice();
        else
        {
            mBluetoothHandler.closeConnection();
            mLeConnected = false;
        }
    }

    @Override
    public void sendTableData(int table)
    {
        byte[] bytes = {(byte)0, (byte)table, (byte)0};
        sendData(bytes);

        /*
        // Test to demonstrate. Gives a bytearray = [0, 2, 2]
        int x = 0;
        byte y = (byte)(x + 2);
        int two = Integer.parseInt("2");
        byte[] bytes = {(byte)x, y, (byte)two};
        */
    }

    public void leIsConnectedMain(boolean connected)
    {
        Log.d(TAG, "leIsConnectedMain was called from bluetoothhandler callback function.");
        mLeConnected = connected;
        Log.d(TAG, "Trying to find fragment and call setStatsText on it.");
        CheckConnectionsFragment frag = (CheckConnectionsFragment)
                mFragmentManager.findFragmentByTag("check_connections");
        if (frag == null)
            return;

        frag.setStatusText(mLeConnected);
    }

    @Override
    public boolean getLeConnected()
    {
        return mLeConnected;
    }

    public void sendData(byte[] data)
    {
        if (mLeConnected)
        {
            Log.d(TAG, "Sending data from MainActivity. Data: " + Arrays.toString(data));
            mBluetoothHandler.sendLeData(data);
        }
        else
        {
            Log.d(TAG, "Trying to send, but not connected! Data: " + Arrays.toString(data));
        }
    }

    public void updateStatus(String data)
    {
        if (data.startsWith("Battery"))
        {
            mBatteryString = data;
        }
        else if (data.startsWith("Status:"))
        {
            mStatusString = data;
        }
        else
        {
            //Log.d(TAG, "data did not start with subString");
        }
        OrdersFragment frag = (OrdersFragment)
                mFragmentManager.findFragmentByTag("handle_orders");
        if (frag == null)
            return;
        else
            frag.updateStatusBox(mStatusString, mBatteryString);
    }
}
