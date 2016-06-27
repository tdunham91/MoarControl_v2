package com.wubydax.romcontrol;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wubydax.romcontrol.utils.Constants;

import de.hdodenhof.circleimageview.CircleImageView;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(PreferenceManager.getDefaultSharedPreferences(Constants.CONTEXT).getInt(Constants.THEME_PREF_KEY, 0) == 0 ? R.style.AppTheme_NoActionBar : R.style.AppTheme_NoActionBar_Dark);
        setContentView(R.layout.activity_about);
        Toolbar toolbar = (Toolbar) findViewById(R.id.aboutToolbar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setUpAboutItems();
        setUpTeamItems();
        setUpCreditsItems();
    }

    private void setUpCreditsItems() {
        String[] textItems = getResources().getStringArray(R.array.about_credits_names);
        TypedArray typedArray = getResources().obtainTypedArray(R.array.about_credits_drawables);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.aboutCreditsContainer);
        assert linearLayout != null;
        for (int i = 0; i < textItems.length; i++) {
            View itemView = LayoutInflater.from(this).inflate(R.layout.about_people_item, linearLayout, false);

            ((CircleImageView) itemView.findViewById(R.id.aboutPeopleIcon)).setImageResource(typedArray.getResourceId(i, -1));
            ((TextView) itemView.findViewById(R.id.aboutPeopleText)).setText(textItems[i]);
            linearLayout.addView(itemView, i);
        }
        typedArray.recycle();
    }

    private void setUpTeamItems() {
        String[] textItems = getResources().getStringArray(R.array.about_team_names);
        TypedArray typedArray = getResources().obtainTypedArray(R.array.about_team_drawables);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.aboutTeamContainer);
        assert linearLayout != null;
        for (int i = 0; i < textItems.length; i++) {
            View itemView = LayoutInflater.from(this).inflate(R.layout.about_people_item, linearLayout, false);

            ((CircleImageView) itemView.findViewById(R.id.aboutPeopleIcon)).setImageResource(typedArray.getResourceId(i, -1));
            ((TextView) itemView.findViewById(R.id.aboutPeopleText)).setText(textItems[i]);
            linearLayout.addView(itemView, i);
        }
        typedArray.recycle();

    }

    private void setUpAboutItems() {
        String[] textItems = getResources().getStringArray(R.array.about_contact_us_text);
        TypedArray typedArray = getResources().obtainTypedArray(R.array.about_contact_us_drawables);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.aboutContactUsContainer);
        assert linearLayout != null;
        for (int i = 0; i < textItems.length; i++) {
            View itemView = LayoutInflater.from(this).inflate(R.layout.about_contact_us_item, linearLayout, false);

            ((CircleImageView) itemView.findViewById(R.id.contactUsImage)).setImageResource(typedArray.getResourceId(i, -1));
            ((TextView) itemView.findViewById(R.id.contactUsText)).setText(textItems[i]);
            linearLayout.addView(itemView, i);
        }
        typedArray.recycle();
    }
}
