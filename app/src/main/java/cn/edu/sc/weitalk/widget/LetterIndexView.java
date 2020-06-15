package cn.edu.sc.weitalk.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import cn.edu.sc.weitalk.R;

public class LetterIndexView extends View {

    //当前手指滑动到的位置
    private int choosedPosition = -1;
    //画文字的画笔
    private Paint paint;
    //右边的所有文字
    private String[] letters = new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L",
            "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "#"};
    //页面正中央的TextView，用来显示手指当前滑动到的位置的文本
    private TextView textViewDialog;
    //接口变量，该接口主要用来实现当手指在右边的滑动控件上滑动时ListView能够跟着滚动
    private OnLetterSelectedListener onLetterSelectedListener;

    public LetterIndexView(Context context) {
        super(context);
        paint = new Paint();        //文字的画笔
        paint.setAntiAlias(true);   //抗锯齿
        paint.setTextSize(24);  //字的大小
    }

    public LetterIndexView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();        //文字的画笔
        paint.setAntiAlias(true);   //抗锯齿
        paint.setTextSize(24);  //字的大小
    }

    public LetterIndexView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint = new Paint();        //文字的画笔
        paint.setAntiAlias(true);   //抗锯齿
        paint.setTextSize(24);  //字的大小
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int perTextHeight = getHeight() / letters.length;
        for (int i = 0; i < letters.length; i++) {
            if (i == choosedPosition) {
                paint.setColor(Color.RED);
            } else {
                paint.setColor(Color.parseColor("#0C7CD1"));
            }
            //1.字母  2.x  3.y            文字rightPadding = 文字宽度 / 2
            canvas.drawText(letters[i], (getWidth() - paint.measureText(letters[i])) / 2, (i + 1) * perTextHeight, paint);
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        int perTextHeight = getHeight() / letters.length;
        float y = event.getY();
        int currentPosition = (int) (y / perTextHeight);
        if (!(currentPosition > -1 && currentPosition < letters.length))
            return false;
        String letter = letters[currentPosition];
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:     //手指抬起时
                setBackgroundColor(Color.TRANSPARENT);
                if (textViewDialog != null) {
                    textViewDialog.setVisibility(View.GONE);
                }
                break;
            default:        //其他情况
                setBackgroundResource(R.drawable.letter_index); //setBackgroundColor(Color.parseColor("#cccccc"));
                if (currentPosition > -1 && currentPosition < letters.length) {
                    if (textViewDialog != null) {
                        textViewDialog.setVisibility(View.VISIBLE);
                        textViewDialog.setText(letter);
                    }
                    if (onLetterSelectedListener != null) {
                        onLetterSelectedListener.onLetterSelected(letter);
                    }
                    choosedPosition = currentPosition;
                }
                break;
        }
        invalidate();
        return true;
    }

    //当左边列表滑动时，列表顶端item所在的分组发生变化，调用此函数改变红色Letter的显示
    public void updateLetterIndexView(int currentChar) {
        for (int i = 0; i < letters.length; i++) {
            if (currentChar == letters[i].charAt(0)) {
                choosedPosition = i;
                invalidate();
                break;
            }
        }
    }

    //当某个字母被选中时
    public interface OnLetterSelectedListener{
        void onLetterSelected(String currentChar);
    }

    //设置放大字母的TextView
    public void setTextViewDialog(TextView textViewDialog){
        this.textViewDialog = textViewDialog;
    }

    //设置OnLetterSelectedListener
    public void setOnLetterSelectedListener(OnLetterSelectedListener listener){
        this.onLetterSelectedListener = listener;
    }
}
