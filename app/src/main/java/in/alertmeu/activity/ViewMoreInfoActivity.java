package in.alertmeu.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import in.alertmeu.R;

public class ViewMoreInfoActivity extends AppCompatActivity {
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;
    TextView titleTxt, dis, validity;
    ImageView image;
    String imagePath = "";
    Resources res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        res = getResources();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_more_info);
        preferences = getSharedPreferences("Prefrence", MODE_PRIVATE);
        prefEditor = preferences.edit();
        titleTxt = (TextView) findViewById(R.id.titleTxt);
        dis = (TextView) findViewById(R.id.dis);
        validity = (TextView) findViewById(R.id.validity);
        image = (ImageView) findViewById(R.id.image);
        Intent intent = getIntent();
        dis.setText(intent.getStringExtra("desc"));
        validity.setText(intent.getStringExtra("reportd"));
        imagePath = intent.getStringExtra("imagePath");
        try {
            Picasso.get().load(imagePath).into(image);
        } catch (Exception e) {

        }
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (preferences.getString("account_status", "").equals("1")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ViewMoreInfoActivity.this);
                    builder.setMessage(res.getString(R.string.jprqr))
                            .setCancelable(false)
                            .setPositiveButton(res.getString(R.string.logreg), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    Intent intent = new Intent(ViewMoreInfoActivity.this, RegisterNGetStartActivity.class);
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton(res.getString(R.string.helpCan), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //  Action for 'NO' Button
                                    dialog.cancel();
                                }
                            });

                    //Creating dialog box
                    AlertDialog alert = builder.create();
                    //Setting the title manually
                    alert.setTitle(res.getString(R.string.jur));
                    alert.show();

                } else {

                    Intent intent1 = new Intent(ViewMoreInfoActivity.this, FullScreenViewActivity.class);
                    intent1.putExtra("path", imagePath);
                    intent1.putExtra("flag", "1");
                    startActivity(intent1);
                }

            }
        });
    }
}