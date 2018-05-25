package se.ju.frjo1425student.waiterbotapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

/**
 * Created by jonas on 2017-11-18.
 */

public class ControlBotFragment extends Fragment
{
    private static final String TAG = "CONTROL_BOT_FRAGMENT";
    private SeekBar mLeftSeekBar;
    private SeekBar mRightSeekBar;
    private int mLeftSeekBarVal;
    private int mRightSeekBarVal;
    private int mStartPos;
    private MainActivity mActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance)
    {
        View RootView = inflater.inflate(R.layout.control_bot_fragment, container, false);
        mActivity = (MainActivity) getActivity();

        mLeftSeekBar = (SeekBar) RootView.findViewById(R.id.LeftSeekBar);
        mRightSeekBar = (SeekBar) RootView.findViewById(R.id.RightSeekBar);
        mStartPos = mRightSeekBar.getMax() / 2;

        mLeftSeekBarVal = mStartPos;
        mRightSeekBarVal = mStartPos;

        mLeftSeekBar.setProgress(mStartPos);
        mLeftSeekBar.setProgress(mStartPos);

        mLeftSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                if (Math.abs(progress - mLeftSeekBarVal) > 16)
                {
                    Log.d(TAG, "Left seekbar: " + progress);
                    mLeftSeekBarVal = progress;
                    sendProgress();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {
                mLeftSeekBar.setProgress(mStartPos);
            }
        });

        mRightSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                if (Math.abs(progress - mRightSeekBarVal) > 16)
                {
                    Log.d(TAG, "Right seekbar: " + progress);
                    mRightSeekBarVal = progress;
                    sendProgress();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {
                mRightSeekBar.setProgress(mStartPos);
            }
        });
        return RootView;
    }

    private void sendProgress()
    {
        // '1' for control command
        // This enables to send values between 0 - 255 on each seekbar
        byte[] bytes = { (byte)1, (byte)mLeftSeekBarVal, (byte)mRightSeekBarVal };
        Log.d(TAG, "Left: " + mLeftSeekBarVal + " Right: " + mRightSeekBarVal);
        mActivity.sendData(bytes);
    }

}
