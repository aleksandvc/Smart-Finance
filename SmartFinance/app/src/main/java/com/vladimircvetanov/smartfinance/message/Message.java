package com.vladimircvetanov.smartfinance.message;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by vladimircvetanov on 04.04.17.
 */
/**
 * Class with a static method which prints Toast with the context and message..
 */
public class Message {

    public static void message(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

    }
}
