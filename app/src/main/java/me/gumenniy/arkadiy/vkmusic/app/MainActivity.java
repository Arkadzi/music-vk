package me.gumenniy.arkadiy.vkmusic.app;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.gumenniy.arkadiy.vkmusic.R;
import me.gumenniy.arkadiy.vkmusic.app.adapter.ArtworkAdapter;
import me.gumenniy.arkadiy.vkmusic.app.view.SimplePagerListener;
import me.gumenniy.arkadiy.vkmusic.model.Song;
import me.gumenniy.arkadiy.vkmusic.presenter.PlaybackPresenter;
import me.gumenniy.arkadiy.vkmusic.presenter.SongListPresenter;
import me.gumenniy.arkadiy.vkmusic.utils.Settings;
import me.gumenniy.arkadiy.vkmusic.view.FriendListFragment;
import me.gumenniy.arkadiy.vkmusic.view.GroupListFragment;
import me.gumenniy.arkadiy.vkmusic.view.OnBackPressListener;
import me.gumenniy.arkadiy.vkmusic.view.PlaybackView;
import me.gumenniy.arkadiy.vkmusic.view.PopularSongsFragment;
import me.gumenniy.arkadiy.vkmusic.view.SearchFragment;
import me.gumenniy.arkadiy.vkmusic.view.SongListFragment;

public class MainActivity extends AppCompatActivity implements RequestTokenListener, PlaybackView {

    @Bind(R.id.tool_bar)
    Toolbar toolbar;
    @Bind(R.id.drawer)
    DrawerLayout drawerLayout;
    @Bind(R.id.navigation_view)
    NavigationView navigationView;
    @Bind(R.id.sliding_up_panel)
    SlidingUpPanelLayout panel;
    @Bind(R.id.pager)
    ViewPager pager;
    @Bind(R.id.prev_button)
    ImageButton prevButton;
    @Bind(R.id.pp_button)
    ImageButton ppButton;
    @Bind(R.id.next_button)
    ImageButton nextButton;
    @Bind(R.id.artist_name)
    TextView artistNameView;
    @Bind(R.id.song_name)
    TextView songNameView;
    @Bind(R.id.seek_bar)
    SeekBar seekBar;
    @Inject
    PlaybackPresenter presenter;
    private ArtworkAdapter adapter;
    @Nullable
    private Fragment fragment;
    private boolean isUserInteraction;
    @Nullable
    private MusicService service;

    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            service = ((MusicService.MusicBinder) binder).getService();
            presenter.setPlayer(service);

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            presenter.setPlayer(null);
            service = null;
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        MusicApplication.getApp(this).getComponent().inject(this);
        setSupportActionBar(toolbar);
        prepareDrawer();
        initSlidingPanel();
        preparePager();
        initSeekBar();
        presenter.bindView(this);
//        startService();
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment == null) {
            startFragment(SongListFragment.newInstance(SongListPresenter.CURRENT_USER, getString(R.string.my_music), false));
            navigationView.setCheckedItem(R.id.my_music_item);
        }
    }

//    private void check() {
//        Log.e("aaaaaa",getCacheDir() + " " + getFilesDir());
//        try {
//
//            File file = new File(getExternalCacheDir() + File.separator + "asd");
//            FileOutputStream os = new FileOutputStream(file);
//            String word = "word";
//            for (char c : word.toCharArray())
//            os.write(c);
//            os.close();
//        } catch (Exception e) {
//            Log.e("exception", String.valueOf(e));
//        }
//    }

    private void preparePager() {
        adapter = new ArtworkAdapter(this, presenter);
        pager.setAdapter(adapter);
        pager.addOnPageChangeListener(new SimplePagerListener() {
            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_DRAGGING) {
                    isUserInteraction = true;
                }
            }

            @Override
            public void onPageSelected(int position) {
                if (isUserInteraction) {
                    presenter.onPageSelected(position);
                    isUserInteraction = false;
                }
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof SearchView.SearchAutoComplete) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                    SearchFragment fragment = (SearchFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                    if (fragment != null) {
                        fragment.clearFocus();
                    }
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private void initSeekBar() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                presenter.onStartTracking();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                presenter.onProgressChanged(seekBar.getProgress());
            }
        });
    }

    private void initSlidingPanel() {
        panel.addPanelSlideListener(new SlidingUpPanelLayout.SimplePanelSlideListener() {
            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                if (newState == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    songNameView.setSelected(true);
                    artistNameView.setSelected(true);
                }
            }
        });
        panel.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, MusicService.class);
        startService(Settings.Notification.ACTION.END_FOREGROUND_ACTION);
        bindService(intent, connection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        if (service != null) {
            unbindService(false, service.isPrepared() || service.isShouldStart());
            service = null;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (service != null) {
            Log.e("action", isFinishing() + " " + service.isPlaying());
            unbindService(isFinishing() && !service.isPlaying(), isFinishing() && service.isPlaying());
            service = null;
        }
    }


    private void unbindService(boolean canStopService, boolean canBeginForeground) {
        unbindService(connection);
        presenter.setPlayer(null);
        if (canBeginForeground) {
            startService(Settings.Notification.ACTION.BEGIN_FOREGROUND_ACTION);
        } else if (canStopService) {
            startService(Settings.Notification.ACTION.STOP_SERVICE_ACTION);
        }
    }

    private void startService(String action) {
        Intent intent = new Intent(this, MusicService.class);
        intent.setAction(action);
        startService(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.bindView(null);
    }

    private void startFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void prepareDrawer() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                drawerLayout.closeDrawers();
                FragmentManager fm = getSupportFragmentManager();
                for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                    fm.popBackStack();
                }
                switch (menuItem.getItemId()) {
                    case R.id.my_music_item:
                        fragment = SongListFragment.newInstance(SongListPresenter.CURRENT_USER, getString(R.string.my_music), false);
                        break;
                    case R.id.friends_item:
                        fragment = FriendListFragment.newInstance(getString(R.string.friends));
                        break;
                    case R.id.groups_item:
                        fragment = GroupListFragment.newInstance(getString(R.string.groups));
                        break;
                    case R.id.recommend_item:
                        fragment = SongListFragment.newInstance(SongListPresenter.CURRENT_USER, getString(R.string.recommended), true);
                        break;
                    case R.id.popular_item:
                        fragment = PopularSongsFragment.newInstance(getString(R.string.popular));
                        break;
                    case R.id.search_item:
                        fragment = SearchFragment.newInstance(getString(R.string.search));
                        break;
                }

                return true;
            }
        });

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.open_menu, R.string.close_menu) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (fragment != null) {
                    startFragment(fragment);
                    fragment = null;
                }
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onRequest() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }


    public boolean handleBackPress() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        } else if (panel.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
            panel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (!handleBackPress()) {
            OnBackPressListener fragment = (OnBackPressListener) getSupportFragmentManager()
                    .findFragmentById(R.id.fragment_container);

            if (!(fragment != null && fragment.backPressHandled())) {
                super.onBackPressed();
            }
        }
    }

    @Override
    public void setQueue(@NonNull List<Song> queue) {
        adapter.setData(queue);
    }

    public void enableControlPanel() {
        if (panel.getPanelState() == SlidingUpPanelLayout.PanelState.HIDDEN) {
            panel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        }
    }

    @Override
    public void renderSong(int position, @NonNull Song song) {
        songNameView.setText(song.getTitle());
        artistNameView.setText(song.getArtist());
        seekBar.setMax(song.getDuration());
        if (position != pager.getCurrentItem()) {
            isUserInteraction = false;
            pager.setCurrentItem(position);
        }
        setBufferProgress(0, 0);
        enableControlPanel();
    }

    @Override
    public void updatePlaybackButtonImage(boolean started) {
        ppButton.setImageResource(started ?
                R.drawable.ic_pause_white_48dp :
                R.drawable.ic_play_arrow_white_48dp);
    }

    @Override
    public void setBufferProgress(int percent, int progress) {
        seekBar.setSecondaryProgress(seekBar.getMax() * percent / 100);
        seekBar.setProgress(progress);
    }

    @Override
    public void renderImage(@NonNull Song song, @NonNull String url) {
        for (int i = 0; i < pager.getChildCount(); i++) {
            View child = pager.getChildAt(i);
            String tag = (String) child.getTag();
            if (song.getKey().equals(tag)) {
                adapter.updateView(child, url);
            }
        }
//        View view = pager.findViewWithTag(song.getKey());
//        if (view != null) {
//            adapter.updateView(view, url);
//        }
    }

    @OnClick(R.id.prev_button)
    public void onPrevButtonClick() {
        presenter.handleClick(PlaybackPresenter.PREV);
    }

    @OnClick(R.id.next_button)
    public void onNextButtonClick() {
        presenter.handleClick(PlaybackPresenter.NEXT);
    }

    @OnClick(R.id.pp_button)
    public void onPPButtonClick() {
        presenter.handleClick(PlaybackPresenter.PAUSE_PLAY);
    }
}
