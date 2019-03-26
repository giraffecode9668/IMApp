package com.giraffe.imapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.giraffe.imapp.R;
import com.giraffe.imapp.fragment.ChatsFragment;
import com.giraffe.imapp.fragment.CommunityFragment;
import com.giraffe.imapp.fragment.FriendsFragment;
import com.giraffe.imapp.fragment.SettingFragment;
import com.giraffe.imapp.pojo.User;
import com.giraffe.imapp.url.ViewPagerAdapter;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.v3.BmobUser;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements MenuItem.OnMenuItemClickListener {

    Boolean up = false;
    private Toolbar toolbar;//导航条
    private TextView tbtitle;//tbtitle:导航标题
    private CircleImageView civ_mavatar;//主页面的头像
    private DrawerLayout drawerLayout;//左拉窗
    private NavigationView navigationView;//左拉窗内容
    private CircleImageView civ_avatar;//左拉窗中的头像
    private TextView tv_nickname,tv_sign;//tv_nickname:左拉窗昵称；tv_sign:左拉窗个性签名
    private MenuItem it_username,it_sex,it_space,it_mood;//左拉窗菜单项
    private TextView tv_exit;//退出
    private MenuItem menuItem;//menuItem底部导航的子项
    private BottomNavigationViewEx bnve;//底部导航
    private ViewPager viewPager;
    private List<Fragment> list;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Main:","onCreate");

        initView();
        initUserIfm();
        initListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
        initUserIfm();
        Log.d("Main:","onStart");
    }

    @Override
    protected void onPause() {
        super.onPause();
        up = true;
        Log.d("Main:","onPause");
    }



    /* ********** */
    /* 初始化界面 */
    /* ********** */
    private void initView() {
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.AM_toolbar);//顶部导航栏
        tbtitle = findViewById(R.id.AM_toolbar_title);
        civ_mavatar = findViewById(R.id.AM_civ_avatar);

        drawerLayout = findViewById(R.id.AM_drawerlayout);
        navigationView = findViewById(R.id.AM_nv_navigationview);

        View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header);//左上角
        civ_avatar = headerLayout.findViewById(R.id.NH_civ_image);
        tv_nickname = headerLayout.findViewById(R.id.NH_tv_nickname);
        tv_sign = headerLayout.findViewById(R.id.NH_tv_sign);

        Menu nav_menu = navigationView.getMenu();//左下角
        it_username = nav_menu.findItem(R.id.nav_account);
        it_sex = nav_menu.findItem(R.id.nav_sex);
        it_space = nav_menu.findItem(R.id.nav_space);
        it_mood = nav_menu.findItem(R.id.nav_mood);
        tv_exit = findViewById(R.id.AM_tv_exit);

        bnve = findViewById(R.id.AM_bnve);//底部导航
        viewPager = findViewById(R.id.AM_viewpager);


        //支持使用toolbar，将title隐藏，因为使用的是自己写的textview设置居中效果
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //头像点击打开拉窗
        civ_mavatar.setOnClickListener(new View.OnClickListener() {//头像点击打开左边弹窗
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });


        bnve.enableAnimation(true);//底部导航：BottomNavigationViewEx的设置
        bnve.enableShiftingMode(false);
        bnve.setIconVisibility(true);
        bnve.setTextVisibility(true);
        setupViewPager(viewPager);//添加fragment页面

    }


    /* ************ */
    /* 初始化监听器 */
    /* ************ */
    private void initListener() {
        it_mood.setOnMenuItemClickListener(this);
        it_username.setOnMenuItemClickListener(this);
        it_space.setOnMenuItemClickListener(this);
        it_sex.setOnMenuItemClickListener(this);
        tv_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BmobUser.logOut();
                BmobIM.getInstance().disConnect();
                intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });


        //点开导航栏右边，展开工具
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.addfriend:
                        intent = new Intent(MainActivity.this,AddFriendsActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.addcom:
                        Toast.makeText(MainActivity.this,
                                "添加群聊",Toast.LENGTH_LONG).show();
                        break;
                    case R.id.confirmfriend:
                        intent = new Intent(MainActivity.this,ConfirmFriendActivity.class);
                        startActivity(intent);
                        break;
                }
                return true;
            }
        });


        //点击头像打开个人信息
        civ_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MainActivity.this,EditIfmActivity.class);
                startActivity(intent);
            }
        });


        //ViewPager点击监听
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


        //ViewPager页面监听
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
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

    }


    /* ******************** */
    /* 加载个人信息相关资料 */
    /* ******************** */
    private void initUserIfm(){

        if (BmobUser.getCurrentUser(User.class)==null){
            Toast.makeText(this,"用户未登陆",Toast.LENGTH_SHORT).show();
        }else{
            //填充头像内容
            if (BmobUser.getCurrentUser(User.class).getAvatar()!=null){//头像
                Glide.with(this).load(BmobUser.getCurrentUser(User.class).getAvatar().getUrl()).
                        error(R.mipmap.ic_launcher).thumbnail(0.1f).placeholder(R.mipmap.ic_launcher).
                        dontAnimate().into(civ_avatar);
                Glide.with(this).load(BmobUser.getCurrentUser(User.class).getAvatar().getUrl()).
                        error(R.mipmap.ic_launcher).thumbnail(0.1f).placeholder(R.mipmap.ic_launcher).
                        dontAnimate().into(civ_mavatar);
            }

            //填充个人信息
            tv_nickname.setText(BmobUser.getCurrentUser(User.class).getNickname()==null||
                    BmobUser.getCurrentUser(User.class).getNickname().equals("")?
                    "未命名":BmobUser.getCurrentUser(User.class).getNickname());//昵称
            tv_sign.setText(BmobUser.getCurrentUser(User.class).getSign()==null||
                    BmobUser.getCurrentUser(User.class).getSign().equals("")?
                    "这个人很懒，没有签名":BmobUser.getCurrentUser(User.class).getSign());//个性签名

            it_username.setTitle(BmobUser.getCurrentUser(User.class).getUsername());//账号
            it_sex.setTitle(BmobUser.getCurrentUser(User.class).getSex()==null||
                    BmobUser.getCurrentUser(User.class).getSex().equals("")?
                    "未设置": BmobUser.getCurrentUser(User.class).getSex());//性别
            it_space.setTitle(BmobUser.getCurrentUser(User.class).getSpace()==null||
                    BmobUser.getCurrentUser(User.class).getSpace().equals("")?
                    "未设置":BmobUser.getCurrentUser(User.class).getSpace());//地址
            it_mood.setTitle(BmobUser.getCurrentUser(User.class).getMood()==null||
                    BmobUser.getCurrentUser(User.class).getMood().equals("")?
                    "未设置":BmobUser.getCurrentUser(User.class).getMood());//心情

        }

    }



     /* ********************************************* */
     /* 解决Toolbar中Menu无法同时显示图标和文字的问题 */
     /* ********************************************* */
    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (menu != null) {
            if (menu.getClass().getSimpleName().equalsIgnoreCase("MenuBuilder")) {
                try {
                    Method method = menu.getClass()
                            .getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    method.setAccessible(true);
                    method.invoke(menu, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }



     /* ********************* */
     /* 创建Toolbar右边展开栏 */
     /* ********************* */
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tb_add, menu);
        return super.onCreateOptionsMenu(menu);
    }



    /* **************** */
    /* 添加fragment页面 */
    /* **************** */
    private void setupViewPager(ViewPager viewPager) {
        list = new ArrayList<>();
        list.add(new ChatsFragment());
        list.add(new FriendsFragment());
        list.add(new CommunityFragment());
        list.add(new SettingFragment());

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(),list);
        viewPager.setAdapter(adapter);
    }



    /* ************************ */
    /* 左下角导航子菜单点击事件 */
    /* ************************ */
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_account:
                Toast.makeText(this,"账号",Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_sex:
                Toast.makeText(this,"性别",Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_space:
                Toast.makeText(this,"地址",Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_mood:
                Toast.makeText(this,"心情",Toast.LENGTH_SHORT).show();
                break;
        }
        return false;
    }

}

