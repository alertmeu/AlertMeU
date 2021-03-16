package in.alertmeu.adapter;


import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import in.alertmeu.R;
import in.alertmeu.utils.Contact;
import in.alertmeu.utils.Listener;


public class ShareAppConListAdpter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private LayoutInflater inflater;
    List<Contact> data;
    Contact current;
    String id, id1;
    int ID;
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;
    private static Listener mListener;


    // create constructor to innitilize context and data sent from MainActivity
    public ShareAppConListAdpter(Context context, List<Contact> data) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
        preferences = context.getSharedPreferences("Prefrence", Context.MODE_PRIVATE);
        prefEditor = preferences.edit();


    }

    // Inflate the layout when viewholder created
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.layout_sapp_details, parent, false);
        MyHolder holder = new MyHolder(view);

        return holder;
    }

    // Bind data
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final int pos = position;
        // Get current position of item in recyclerview to bind data and assign values from list
        final MyHolder myHolder = (MyHolder) holder;
        current = data.get(position);
        myHolder.tvName.setText(current.name);
        myHolder.tvName.setTag(position);
        myHolder.tvEmail.setText("");
        myHolder.tvEmail.setTag(position);
        myHolder.tvPhone.setText("");
        myHolder.tvPhone.setTag(position);
        if (current.numbers.size() > 0 && current.numbers.get(0) != null) {
            myHolder.tvPhone.setText(current.numbers.get(0).number);
        }
        if (current.emails.size() > 0 && current.emails.get(0) != null) {
            myHolder.tvEmail.setVisibility(View.VISIBLE);
            myHolder.tvEmail.setText(current.emails.get(0).address);
        } else {
            myHolder.tvEmail.setVisibility(View.GONE);
        }


        myHolder.chkBox.setTag(position);
        myHolder.chkBox.setChecked(data.get(position).isSelected());
        myHolder.chkBox.setTag(data.get(position));

        myHolder.chkBox.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;
                Contact contact = (Contact) cb.getTag();
                contact.setSelected(cb.isChecked());
                data.get(pos).setSelected(cb.isChecked());
            }
        });
    }


    // return total item from List
    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvEmail, tvPhone;
        CheckBox chkBox;
        ImageView subimage;

        // create constructor to get widget reference
        public MyHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvEmail = (TextView) itemView.findViewById(R.id.tvEmail);
            tvPhone = (TextView) itemView.findViewById(R.id.tvPhone);
            chkBox = (CheckBox) itemView.findViewById(R.id.chkBox);
            subimage = (ImageView) itemView.findViewById(R.id.subimage);

        }

    }


    // method to access in activity after updating selection
    public List<Contact> getSservicelist() {
        return data;
    }

    public static void bindListener(Listener listener) {
        mListener = listener;
    }


}
