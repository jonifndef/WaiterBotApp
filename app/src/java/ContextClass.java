package se.ju.frjo1425student.waiterbotapp;

import android.app.Application;
import android.content.Context;

/**
 * Created by jonas on 2017-12-13.
 */

public class ContextClass extends Application
{
    public static Context mContext;
    @Override
    public void onCreate()
    {
        super.onCreate();
        mContext = this;
    }

    public static Context getContext()
    {
        return mContext;
    }
}
