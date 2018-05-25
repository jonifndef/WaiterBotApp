package se.ju.frjo1425student.waiterbotapp;

import android.app.Activity;
import android.widget.Button;

/**
 * Created by jonas on 2017-12-06.
 */

public class OrderItem
{
    private int mOrderNumber;
    private int mTableNumber;
    private String mFoodOrder;

    public OrderItem(int orderNumber, int tableNumber, String foodOrder)
    {
        mOrderNumber = orderNumber;
        mTableNumber = tableNumber;
        mFoodOrder = foodOrder;

        //mDeliverButton = (Button) activity.findViewById();
    }

    public int getOrderNumber()
    {
        return mOrderNumber;
    }

    public int getTableNumber()
    {
        return mTableNumber;
    }

    public String getFoodOrder()
    {
        return mFoodOrder;
    }
}
