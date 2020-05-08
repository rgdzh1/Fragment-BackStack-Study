package com.example.fragmentbackstack;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import com.example.fragmentbackstack.databinding.ActivityMain1Binding;
import com.example.fragmentbackstack.fragment.FirstFragment;
import com.example.fragmentbackstack.fragment.FourthFragment;
import com.example.fragmentbackstack.fragment.SecondFragment;
import com.example.fragmentbackstack.fragment.ThirdFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private ActivityMain1Binding binding;
    private FirstFragment firstFragment;
    private SecondFragment secondFragment;
    private ThirdFragment thirdFragment;
    private FourthFragment fourthFragment;
    private ArrayList<Fragment> mFragmentBackStack;
    private Map<Fragment, Integer> mFragmentMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMain1Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.bnv.setOnNavigationItemSelectedListener(this);
        initFragmentBackStack();
        addFragments();
        showFragment(firstFragment);
        // Fragment被new出来的时候不会走生命周期,只有当commit()之后会走生命周期.
        // 如果将所有的Fragment一次性添加进事务中再commit()会导致第一次加载卡顿.
        // 所以不建议这样,虽然懒加载可以缓解这种卡顿.但是还是不建议.
        //        getSupportFragmentManager()
        //                .beginTransaction()
        //                .add(R.id.fl, firstFragment, "firstFragment")
        //                .add(R.id.fl, secondFragment, "secondFragment")
        //                .add(R.id.fl, thirdFragment, "thirdFragment")
        //                .add(R.id.fl, fourthFragment, "fourthFragment")
        //                .hide(secondFragment)
        //                .hide(thirdFragment)
        //                .hide(fourthFragment)
        //                .commit();
    }

    private void initFragmentBackStack() {
        mFragmentBackStack = new ArrayList<>();
    }

    private void addFragments() {
        firstFragment = new FirstFragment();
        secondFragment = new SecondFragment();
        thirdFragment = new ThirdFragment();
        fourthFragment = new FourthFragment();
        mFragmentMap = new HashMap<>();
        mFragmentMap.put(firstFragment, R.id.first);
        mFragmentMap.put(secondFragment, R.id.second);
        mFragmentMap.put(thirdFragment, R.id.third);
        mFragmentMap.put(fourthFragment, R.id.four);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.first:
                showFragment(firstFragment);
                break;
            case R.id.second:
                showFragment(secondFragment);
                break;
            case R.id.third:
                showFragment(thirdFragment);
                break;
            case R.id.four:
                showFragment(fourthFragment);
                break;
        }
        return true;
    }

    private void showFragment(Fragment showFragment) {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        // 查找该Fragment是否已经添加到容器中
        Fragment mIsAddFragment = supportFragmentManager.findFragmentByTag(showFragment.getClass().getName());
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        if (mIsAddFragment == null) {
            // 如果为null,则该容器没有被添加入容器中.
            for (Fragment mFragment : mFragmentMap.keySet()) {
                // showFragment需要展示的Fragment未添加进Fragment中,所以不需要再次查找然后判断隐藏.
                if (showFragment != mFragment) {
                    // 遍历查看Fragment是否加入容器
                    Fragment mTagFargment = supportFragmentManager.findFragmentByTag(mFragment.getClass().getName());
                    // 说明该Fragment已经加入容器中了
                    if (mTagFargment != null) {
                        fragmentTransaction.hide(mTagFargment);
                    }
                }
            }
            fragmentTransaction
                    .add(R.id.fl, showFragment, showFragment.getClass().getName())// 将需要显示的Fragment加入容器中
                    .show(showFragment)
                    .commit();
        } else {
            // 该Fragment已经添加到容器中了
            for (Fragment mFragment : mFragmentMap.keySet()) {
                // 如果该Fragment不是要显示的Fragemnt, 并且该Fragment已经被添加到容器中, 那么就隐藏该Fragment.
                if (showFragment != mFragment) {
                    Fragment mTagFargment = supportFragmentManager.findFragmentByTag(mFragment.getClass().getName());
                    // 说明该Fragment已经加入容器中了
                    if (mTagFargment != null) {
                        fragmentTransaction.hide(mTagFargment);
                    }
                } else {
                    fragmentTransaction.show(showFragment);
                }
            }
            fragmentTransaction.commit();//提交Fragment
        }
        addFragmentBackStack(showFragment);
    }

    /**
     * 将要显示的Fragment添加进模拟回退栈
     *
     * @param showFragment
     */
    private void addFragmentBackStack(Fragment showFragment) {
        if (mFragmentBackStack.contains(showFragment)) {
            // 如果回退栈中已经有了这个Fragment,则先将老的引用移除,再增加新引用.这样可以更改这个Fragment在回退栈中的索引位置.
            mFragmentBackStack.remove(showFragment);
            mFragmentBackStack.add(showFragment);
        } else {
            // 如果回退栈中不存在这个Fragment,则直接增加引用就行了.
            mFragmentBackStack.add(showFragment);
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        // 将点击返回按钮动作接管
        if (mFragmentBackStack.size() > 1) {
            // 删除页面当前最上层Fragment
            mFragmentBackStack.remove(mFragmentBackStack.size() - 1);
            // 将下面的一个Fragment显示出来
            Fragment mShowFragment = mFragmentBackStack.get(mFragmentBackStack.size() - 1);
            showFragment(mShowFragment);
            switchBnvItem(mShowFragment);
        } else {
            // 当回退栈中只存在一个Fragment时,就关闭当前Activity.
            finish();
        }
    }

    /**
     * 切换底部导航栏图标
     *
     * @param mShowFragment
     */
    public void switchBnvItem(Fragment mShowFragment) {
        Integer bnvId = mFragmentMap.get(mShowFragment);
        binding.bnv.setSelectedItemId(bnvId);
    }
}
