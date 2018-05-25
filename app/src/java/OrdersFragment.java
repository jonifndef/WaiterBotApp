package se.ju.frjo1425student.waiterbotapp;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by jonas on 2017-11-17.
 */

public class OrdersFragment extends Fragment
{
    private static final String TAG = "ORDERS_FRAGMENT";
    private static List<OrderItem> mOrderItemList;
    private static ListView mListView;
    private static TextView mStatusText;
    private static TextView mBatteryStatus;
    private static String mStatusString;
    private static String mBatteryString;
    private static MainActivity mActivity;
    Button mSendButton;

    public interface OrdersListener
    {
        void sendTableData(int table);
        boolean getLeConnected();
    }
    OrdersListener activityCommander;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance)
    {
        View RootView = inflater.inflate(R.layout.handle_orders_fragment, container, false);

        //start polling server
        new dbHandler.getDbRows().execute();

        //mActivity = (MainActivity) ContextClass.getContext();

        mSendButton = (Button) RootView.findViewById(R.id.sendButton);
        mSendButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                sendButtonClicked(view);
            }
        });

        mStatusText = (TextView) RootView.findViewById(R.id.statusTextView);
        mBatteryStatus = (TextView) RootView.findViewById(R.id.batteryTextView);

        mListView = (ListView) RootView.findViewById(R.id.ordersListView);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                handleOrderClick(position);
            }
        });

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
            activityCommander = (OrdersFragment.OrdersListener) context;
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
                activityCommander = (OrdersFragment.OrdersListener) activity;
            }
            catch (ClassCastException exception)
            {
                throw new ClassCastException(activity.toString());
            }
        }
    }

    private void sendButtonClicked(View view)
    {
        activityCommander.sendTableData(2);
        //////
        //String str = "";
        //activityCommander.sendDataButton();
        //////

        //trying to update row in db via http request
        //new dbHandler.setDbDelivered().execute("5");
    }

    public static void updateOrderList(List<OrderItem> orders)
    {
        //Log.d(TAG, "Updating list in ordersFragment!");
        mOrderItemList = orders;

        OrderItemsAdapter adapter = new OrderItemsAdapter(ContextClass.getContext(), mOrderItemList);
        mListView.setAdapter(adapter);
    }

    private void handleOrderClick(int position)
    {
        if (activityCommander.getLeConnected())
        {
            OrderItem item = mOrderItemList.get(position);
            Log.d(TAG, "Statustext:" + mStatusString);

            if (mStatusString.equals("Status: Standby"))
            {
                Log.d(TAG, "it equals Status: Standby");
                showDialog(item);
            } else
                showErrorDialog();
        }
        else
        {
            Toast.makeText(getActivity(), "Not connected to WaiterBot!",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void showDialog(OrderItem sentItem)
    {
        final OrderItem item = sentItem;
        new AlertDialog.Builder(getActivity())
                .setTitle("Send Order")
                .setMessage("Do you want to send order nr " + item.getOrderNumber() + "?")
                .setPositiveButton(
                        "YES",
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int whichButton)
                            {
                                activityCommander.sendTableData(item.getTableNumber());
                                String id = Integer.toString(item.getOrderNumber());
                                new dbHandler.setDbDelivered().execute(id);
                            }
                        }
                ).setNegativeButton(
                        "NO",
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int whichButton)
                            {
                                // Do not delete it.
                            }
                        }
                ).show();
    }

    private void showErrorDialog()
    {
        new AlertDialog.Builder(getActivity())
                .setTitle("Send Order")
                .setMessage("Error: Cannot send order until WaiterBot is at dock.")
                .setPositiveButton(
                        "OK",
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int whichButton)
                            {
                                //Do nothing
                            }
                        }
                ).show();
    }

    public void updateStatusBox(String status, String battery)
    {
        //Log.d(TAG, "Updating statusbox in ordersfragment.");
        mStatusString = status;
        mBatteryString = battery;

        mStatusText.setText(status);
        mBatteryStatus.setText(battery);
    }
}
