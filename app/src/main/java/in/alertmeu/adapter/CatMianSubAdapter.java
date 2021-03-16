package in.alertmeu.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.DataSetObserver;

import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import in.alertmeu.R;
import in.alertmeu.activity.DisplayAllActiveAdsActivity;
import in.alertmeu.models.ExMainSubCatDAO;


public class CatMianSubAdapter implements ExpandableListAdapter {

    private Context context;
    private List<ExMainSubCatDAO> catmainsub = new ArrayList<ExMainSubCatDAO>();
    Resources res;

    public CatMianSubAdapter(Context context, List<ExMainSubCatDAO> catmainsub) {
        this.context = context;
        this.catmainsub = catmainsub;
        res = context.getResources();
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getGroupCount() {
        return catmainsub.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return catmainsub.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return catmainsub.get(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        ParentHolder parentHolder = null;

        ExMainSubCatDAO brand = (ExMainSubCatDAO) getGroup(groupPosition);

        if (convertView == null) {
            LayoutInflater userInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = userInflater.inflate(R.layout.item_parent, null);
            convertView.setHorizontalScrollBarEnabled(true);

            parentHolder = new ParentHolder();
            convertView.setTag(parentHolder);

        } else {
            parentHolder = (ParentHolder) convertView.getTag();
        }

        parentHolder.brandName = (TextView) convertView.findViewById(R.id.text_brand);
        parentHolder.brandName.setText(brand.catMainSubName);
        parentHolder.viewall = (TextView) convertView.findViewById(R.id.viewall);
        SpannableString content = new SpannableString(res.getString(R.string.xvall));
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        parentHolder.viewall.setText(content);
        parentHolder.viewall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DisplayAllActiveAdsActivity.class);
                intent.putExtra("name", brand.catMainSubName);
                intent.putExtra("mainCatId", brand.mainCatId);
                context.startActivity(intent);
               // Toast.makeText(context, brand.mainCatId, Toast.LENGTH_SHORT).show();
            }
        });
        parentHolder.indicator = (ImageView) convertView.findViewById(R.id.image_indicator);

        if (isExpanded) {
            // parentHolder.indicator.setImageResource(R.drawable.ic_keyboard_arrow_up_black_18dp);
        } else {
            //  parentHolder.indicator.setImageResource(R.drawable.ic_keyboard_arrow_down_black_18dp);
        }

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        ChildHolder childHolder = null;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_group_child, parent, false);
            childHolder = new ChildHolder();
            convertView.setTag(childHolder);
        } else {
            childHolder = (ChildHolder) convertView.getTag();
        }

        childHolder.horizontalListView = (RecyclerView) convertView.findViewById(R.id.mobiles);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        childHolder.horizontalListView.setLayoutManager(layoutManager);

        AdvertisementsAdapter horizontalListAdapter = new AdvertisementsAdapter(context, catmainsub.get(groupPosition).advertisements);
        childHolder.horizontalListView.setAdapter(horizontalListAdapter);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public void onGroupExpanded(int groupPosition) {

    }

    @Override
    public void onGroupCollapsed(int groupPosition) {

    }

    @Override
    public long getCombinedChildId(long groupId, long childId) {
        return 0;
    }

    @Override
    public long getCombinedGroupId(long groupId) {
        return 0;
    }

    private static class ChildHolder {
        static RecyclerView horizontalListView;
    }

    private static class ParentHolder {
        TextView brandName, viewall;
        ImageView indicator;
    }
}
