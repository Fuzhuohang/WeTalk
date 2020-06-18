package cn.edu.sc.weitalk.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import cn.edu.sc.weitalk.R;

public class MyDialog extends Dialog {

    private int width;
    private int height;

    public MyDialog(@NonNull Context context, int width, int height) {
        super(context, R.style.customDialogTheme);
        this.width = width;
        this.height = height;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.width = width;
        attributes.height = height;
        window.setAttributes(attributes);
    }
}
