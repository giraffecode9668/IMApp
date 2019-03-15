package com.giraffe.imapp.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.giraffe.imapp.R;
import com.giraffe.imapp.fragment.ChatsFragment;
import com.giraffe.imapp.fragment.CommunityFragment;
import com.giraffe.imapp.fragment.ContactsFragment;
import com.giraffe.imapp.fragment.SettingFragment;
import com.giraffe.imapp.url.CircleDrawable;
import com.giraffe.imapp.url.ViewPagerAdapter;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //页面组件初始化
    private Toolbar toolbar;
    private TextView tbtitle;
    private DrawerLayout drawerLayout;
    private ViewPager viewPager;
    private List<Fragment> list;
    private BottomNavigationViewEx bnve;

    private MenuItem menuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //定位组件
        bnve = findViewById(R.id.AM_bnve);
        toolbar = findViewById(R.id.AM_toolbar);
        viewPager = findViewById(R.id.AM_viewpager);
        tbtitle = findViewById(R.id.AM_toolbar_title);
        drawerLayout = findViewById(R.id.AM_drawerlayout);





        /*         *********        */
        /*          顶部导航        */
        /*          ********        */

        //支持使用toolbar，将title隐藏，因为使用的是自己写的textview设置居中效果
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //Toolbar_NavigationIcon：头像使用自定义组件，画一个圆
        Resources resources = MainActivity.this.getResources();
        Drawable drawable = resources.getDrawable(R.drawable.giraffecode);
        int size = 44;
        CircleDrawable circleDrawable = new CircleDrawable(drawable, MainActivity.this, size);
        toolbar.setNavigationIcon(circleDrawable);

        //头像点击，展开左边抽屉
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        //展开工具
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                Intent intent;
                switch (id) {
                    case R.id.addfriend:
                        Toast.makeText(MainActivity.this,"添加好友",Toast.LENGTH_LONG).show();
                        break;
                    case R.id.addcom:
                        Toast.makeText(MainActivity.this,"添加群聊",Toast.LENGTH_LONG).show();
                        break;
                }
                return true;
            }
        });

        /*  ------------------------------------------------------------------------------------------    */



        /*         *********        */
        /*          底部导航        */
        /*          ********        */

        //底部导航：BottomNavigationViewEx的设置
        bnve.enableAnimation(true);
        bnve.enableShiftingMode(false);
        bnve.setIconVisibility(true);
        bnve.setTextVisibility(true);
        //添加fragment页面
        setupViewPager(viewPager);

        //点击监听
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

        //页面监听
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
                switch (position){
                    case 0:
                        tbtitle.setText("消息");
                        break;
                    case 1:
                        tbtitle.setText("通讯录");
                        break;
                    case 2:
                        tbtitle.setText("发现");
                        break;
                    case 3:
                        tbtitle.setText("设置");
                        break;
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        /*  ------------------------------------------------------------------------------------------    */
    }

    /**
     * 创建Toolbar右边展开栏
     **/
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tb_add, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * 解决Toolbar中Menu无法同时显示图标和文字的问题
     * */
    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (menu != null) {
            if (menu.getClass().getSimpleName().equalsIgnoreCase("MenuBuilder")) {
                try {
                    Method method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    method.setAccessible(true);
                    method.invoke(menu, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }

    /*
    *添加fragment页面
    **/
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
