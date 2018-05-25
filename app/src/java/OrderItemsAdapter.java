package se.ju.frjo1425student.waiterbotapp;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;

/**
 * Created by jonas on 2017-12-13.
 */

public class OrderItemsAdapter extends ArrayAdapter<OrderItem>
{
    public OrderItemsAdapter(Context context, List<OrderItem> orderList)
    {
        super(context, 0, orderList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        OrderItem orderItem = getItem(position);

        if (convertView == null)
        {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.order_item, parent, false);
        }
        TextView orderIdText = (TextView) convertView.findViewById(R.id.order_id_text);
        TextView foodText = (TextView) convertView.findViewById(R.id.food_text);
        TextView tableNumText = (TextView) convertView.findViewById(R.id.tableNum_text);

        orderIdText.setText("Nr: " + orderItem.getOrderNumber());
        foodText.setText("Food: " + orderItem.getFoodOrder());
        tableNumText.setText("Table: " + orderItem.getTableNumber());

        return convertView;
    }
}