package com.giraffe.imapp.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Adapter;

import com.giraffe.imapp.R;
import com.giraffe.imapp.fragment.ChatsFragment;
import com.giraffe.imapp.fragment.CommunityFragment;
import com.giraffe.imapp.fragment.ContactsFragment;
import com.giraffe.imapp.fragment.SettingFragment;
import com.giraffe.imapp.url.ViewPagerAdapter;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private MenuItem menuItem;
    private BottomNavigationViewEx bnve;
    private List<Fragment> list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bnve = findViewById(R.id.AM_bnve);
        viewPager = findViewById(R.id.AM_viewpager);
        bnve.enableAnimation(true);
        bnve.enableShiftingMode(false);
        bnve.setOnNavigationItemSelectedListener(
                new BottomNavigationViewEx.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_chats:
                                viewPager.setCurrentItem(0);
                                break;
                            case R.id.action_contacts:
                                viewPager.setCurrentItem(1);
                                break;
                            case R.id.action_community:
                                viewPager.setCurrentItem(2);
                                break;
                            case R.id.action_setting:
                                viewPager.setCurrentItem(3);
                                break;
                        }
                        return false;
                    }
                });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (menuItem != null) {
                    menuItem.setChecked(false);
                } else {
                    bnve.getMenu().getItem(0).setChecked(false);
                }
                menuItem = bnve.getMenu().getItem(position);
                menuItem.setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        setupViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        list = new ArrayList<>();
        list.add(new ChatsFragment());
        list.add(new ContactsFragment());
        list.add(new CommunityFragment());
        list.add(new SettingFragment());

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(),list);
        viewPager.setAdapter(adapter);
    }
}
