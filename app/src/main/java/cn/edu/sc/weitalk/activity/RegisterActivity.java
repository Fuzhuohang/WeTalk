package cn.edu.sc.weitalk.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.edu.sc.weitalk.R;

public class RegisterActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{
    private EditText edtName;
    private EditText edtPassword;
    private EditText edtPassword2;
    private EditText edtPhone;
    private EditText edtEmail;
    private EditText edtlocation;
    private Button btnRegister;

    //生日选择
    private static DatePickerDialog datePickerDialog;
    private static Calendar calendar;
    private Button btnDate;
    private TextView txtDate;
    Integer themeID=2;

    //头像选择
    private Button btnHead;
    private String mCurrentPhotoPath;
    private int REQUEST_IMAGE_CAPTURE=0x100;
    private int REQUEST_IMAGE_GET=0x200;
    private ImageView head;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        btnDate=findViewById(R.id.btnDate);
        txtDate=findViewById(R.id.txtDate);
        head=findViewById(R.id.head);
        edtName=findViewById(R.id.edtName);
        edtPassword=findViewById(R.id.edtPassword);
        edtPassword2=findViewById(R.id.edtPassword2);
        edtPhone=findViewById(R.id.edtPhone);
        edtEmail=findViewById(R.id.edtEmail);
        edtlocation=findViewById(R.id.edtLocation);
        btnRegister=findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
















        btnDate.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                init();
            }
        });

        btnHead=findViewById(R.id.btnhead);
        initPhotoError();

        //预加载默认头像
        try{
            FileInputStream fs=new FileInputStream("/storage/emulated/0/Pictures/JPEG_20200616_024201_1704357333750184055.jpg");
            Bitmap bitmap=BitmapFactory.decodeStream(fs);
            head.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        btnHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

    }
    @SuppressLint("ResourceType")
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void init(){
        calendar=Calendar.getInstance();
        datePickerDialog=new DatePickerDialog(RegisterActivity.this,themeID,this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String dt=String.valueOf(new StringBuffer()
                .append(year)
                .append("-")
                .append((month+1)<10?"0"+(month+1):(month+1))
                .append("-")
                .append((dayOfMonth<10)?"0"+dayOfMonth:dayOfMonth));
        txtDate.setText(dt);

    }

    public void selectImage(){
        Intent intent=new Intent(Intent.ACTION_PICK);
        if(intent.resolveActivity(getPackageManager())!=null){
            File photoFile=null;
            try{
                photoFile=createImageFile();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
    private File createImageFile() throws IOException{
        String timeStamp=new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imgFileName="JPEG_"+timeStamp+"_";
        File storageDir= Environment.getExternalStoragePublicDirectory
                (Environment.DIRECTORY_PICTURES);
        File image=File.createTempFile(
                imgFileName,
                ".jpg",
                storageDir
        );
        mCurrentPhotoPath=image.getAbsolutePath();
        System.out.println(mCurrentPhotoPath);
        return image;
    }

    private void initPhotoError(){
        // android 7.0系统解决拍照的问题
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
    }


    private void dispatchTakePictureIntent(){
        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getPackageManager())!=null){
            File photoFile=null;
            try{
                photoFile=createImageFile();
            }catch (IOException e){
                e.printStackTrace();
            }
            if(photoFile!=null){
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(intent,REQUEST_IMAGE_CAPTURE);
            }
        }else{
            System.out.println("无法启动相机");
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 回调成功
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            String filePath = null;
            //判断是哪一个的回调
            if (requestCode == REQUEST_IMAGE_GET) {
                //返回的是content://的样式
                filePath = getFilePathFromContentUri(data.getData(), this);
            } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
                if (mCurrentPhotoPath != null) {
                    filePath = mCurrentPhotoPath;
                }
            }
            if (!TextUtils.isEmpty(filePath)) {
                // 自定义大小，防止OOM
                Bitmap bitmap = getSmallBitmap(filePath, 200, 200);
                head.setImageBitmap(bitmap);
            }
        }
    }
    /**
     * @param uri     content:// 样式
     * @param context
     * @return real file path
     */
    public static String getFilePathFromContentUri(Uri uri, Context context) {
        String filePath;
        String[] filePathColumn = {MediaStore.MediaColumns.DATA};
        Cursor cursor = context.getContentResolver().query(uri, filePathColumn, null, null, null);
        if (cursor == null) return null;
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        filePath = cursor.getString(columnIndex);
        cursor.close();
        return filePath;
    }
    /**
     * 获取小图片，防止OOM
     *
     * @param filePath
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static Bitmap getSmallBitmap(String filePath, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        try {
            BitmapFactory.decodeFile(filePath, options);
        } catch (Exception e) {
            e.printStackTrace();
        }
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }
    /**
     * 计算图片缩放比例
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }
}