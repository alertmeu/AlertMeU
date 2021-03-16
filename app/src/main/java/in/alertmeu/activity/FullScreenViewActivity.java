package in.alertmeu.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import in.alertmeu.R;


public class FullScreenViewActivity extends AppCompatActivity {
    ImageView imgDisplay;
    String path = "", flag = "";
    Resources res;
    private static final String FILE_NAME = "file_lang";
    private static final String KEY_LANG = "key_lang";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLanguage();
        res = getResources();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_full_screen_view);
        Intent intent = getIntent();
        path = intent.getStringExtra("path");
        flag = intent.getStringExtra("flag");
        imgDisplay = (ImageView) findViewById(R.id.imgDisplay);
        try {
            if (flag.equals("1")) {
                if (path.contains("http")) {
                    try {
                        Picasso.get().load(path).into(imgDisplay);
                    } catch (Exception e) {

                    }
                } else {
                    imgDisplay.setImageDrawable(getResources().getDrawable(R.drawable.defaultbusimg));
                }
                //  Picasso.get().load(path).into(imgDisplay);
            } else {
                //    tid.setVisibility(View.VISIBLE);
                String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/AlertMeU" + File.separator + path;
                Bitmap bmp = BitmapFactory.decodeFile(filePath);
                imgDisplay.setImageBitmap(bmp);
            }
        } catch (Exception e) {
        }

    }


    private void loadLanguage() {
        Locale locale = new Locale(getLangCode());
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }

    private String getLangCode() {
        SharedPreferences preferences = getSharedPreferences(FILE_NAME, MODE_PRIVATE);
        String langCode = preferences.getString(KEY_LANG, "en");
        return langCode;
    }
}
