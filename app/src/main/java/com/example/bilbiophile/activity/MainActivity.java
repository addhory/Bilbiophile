package com.example.bilbiophile.activity;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.example.bilbiophile.R;
import com.example.bilbiophile.fragment.BookFragment;
import com.example.bilbiophile.fragment.FavoriteFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "___MainActivity";

    private Toolbar mToolbar;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;

    // holds reference to tab tags
    private Hashtable<Integer, String> mTabFragmentTags = new Hashtable<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);


        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // setup viewpager
        mViewPager = findViewById(R.id.viewpager);
        setupViewPager();

        // setup tabs
        mTabLayout = findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mViewPager);
        setupTabIcons();
    }

    private void setupViewPager() {
        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(pagerAdapter);
    }

    private void setupTabIcons() {
        final int[] icons = new int[]{
                R.drawable.ic_book_white_24dp,
                R.drawable.ic_favorite_border_black_24dp,

        };

        final int[] selectedIcons = new int[]{
                R.drawable.ic_book_selected_24dp,
                R.drawable.ic_favorite_black_24dp,

        };

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mTabLayout.setTabTextColors(getColor(R.color.colorInactive), getColor(R.color.colorAccent));
                tab.setIcon(selectedIcons[tab.getPosition()]);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.setIcon(icons[tab.getPosition()]);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        mTabLayout.getTabAt(0).setIcon(selectedIcons[0]);
        mTabLayout.getTabAt(1).setIcon(icons[1]);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        ComponentName cn = new ComponentName(this, SearchActivity.class);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(cn));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private List<String> mTitleList = new ArrayList<>(Arrays.asList(getString(R.string.tab_new),
                getString(R.string.favorite)));


        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new BookFragment();

                case 1:
                    return new FavoriteFragment();

                default:
                    return null;
            }
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            // get tag created by fragment manager
            String tag = fragment.getTag();
            // save this tag for later use
            mTabFragmentTags.put(position, tag);

            return fragment;
        }

        @Override
        public int getCount() {
            return mTitleList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitleList.get(position);
        }
    }
}
