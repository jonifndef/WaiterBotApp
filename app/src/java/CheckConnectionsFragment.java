package se.ju.frjo1425student.waiterbotapp;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;

/**
 * Created by jonas on 2017-11-20.
 */

public class CheckConnectionsFragment extends Fragment
{
    private static final String TAG = "CHECK_CONNECTIONS_FRG";

    private Button mleScanButton;
    private Button mGoAutomaticButton;
    private TextView mConnectionStatusView;
    private Spinner mUpdateSpinner;

    //Interface for "connection" to main activity
    public interface CheckConnectionsListener
    {
        void leScan();
        boolean getLeConnected();
    }
    CheckConnectionsListener activityCommander;
    MainActivity mActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance)
    {
        View RootView = inflater.inflate(R.layout.check_connections_fragment, container, false);

        this.mActivity = (MainActivity) getActivity();

        mGoAutomaticButton = (Button) RootView.findViewById(R.id.goAutomatic);
        mleScanButton = (Button) RootView.findViewById(R.id.leScanButton);
        mConnectionStatusView = (TextView) RootView.findViewById(R.id.connection_status_textview);

        //Spinner used to set server update rate
        mUpdateSpinner = (Spinner) RootView.findViewById(R.id.updateSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.spinner_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mUpdateSpinner.setAdapter(adapter);

        //Setting spinner to remembered user choice
        SharedPreferences sharedPref = mActivity.getSharedPreferences("updateServerChoice", 0);
        mUpdateSpinner.setSelection(sharedPref.getInt("spinnerChoice", 0));

        //Listener för spinner clicks
        mUpdateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id)
            {
                Log.d(TAG, "Spinner index: " + position);

                //Using sharedPreferences to remember users last choice //getContext() / mActivity?
                SharedPreferences sharedPref = ContextClass.getContext().getSharedPreferences("updateServerChoice", 0);
                SharedPreferences.Editor prefEditor = sharedPref.edit();
                prefEditor.putInt("spinnerChoice", position);
                prefEditor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView)
            {
                //setOnItemSelectedListener must implement onNothingSelected()
            }
        });

        if (!mActivity.getLeConnected())
        {
            mleScanButton.setText("Connect");
        }
        else
        {
            mleScanButton.setText("Disconnect");
        }

        mleScanButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                leScanButtonClicked(view);
            }
        });

        mGoAutomaticButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                goAutomaticButtonClicked(view);
            }
        });

        //check if connected!
        if (activityCommander.getLeConnected())
            mConnectionStatusView.setText(R.string.connected);
        else
            mConnectionStatusView.setText(R.string.not_connected);

        return RootView;
    }

    //onAttach
    @TargetApi(23)
    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        try
        {
            activityCommander = (CheckConnectionsListener) context;
        }
        catch (ClassCastException exception)
        {
            throw new ClassCastException(context.toString());
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
        {
            try
            {
                activityCommander = (CheckConnectionsListener) activity;
            }
            catch (ClassCastException exception)
            {
                throw new ClassCastException(activity.toString());
            }
        }
    }

    public void leScanButtonClicked(View view)
    {
        activityCommander.leScan();
    }

    public void goAutomaticButtonClicked(View view)
    {
        byte[] bytes = {2, 0, 0};
        if (activityCommander.getLeConnected())
        {
            mActivity.sendData(bytes);
        }
        else
        {
            Toast.makeText(ContextClass.getContext(), "Not connected to WaiterBot!",
                    Toast.LENGTH_LONG).show();
        }
    }

    public void setStatusText(boolean connected)
    {
        if (connected)
        {
            mConnectionStatusView.setText(R.string.connected);
            mleScanButton.setText("Disconnect");
        }
        else
        {
            mConnectionStatusView.setText(R.string.not_connected);
            mleScanButton.setText("Connect");
        }
    }
}

