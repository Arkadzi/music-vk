package me.gumenniy.arkadiy.vkmusic.app;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.gumenniy.arkadiy.vkmusic.R;
import me.gumenniy.arkadiy.vkmusic.model.Song;
import me.gumenniy.arkadiy.vkmusic.presenter.PlaybackPresenter;
import me.gumenniy.arkadiy.vkmusic.presenter.SongListPresenter;
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
    @Nullable
    private Fragment fragment;
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            Player service = ((MusicService.MusicBinder) binder).getService();
            presenter.setPlayer(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            presenter.setPlayer(null);
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
        initSeekBar();
        presenter.bindView(this);
        startService();
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment == null) {
            startFragment(SongListFragment.newInstance(SongListPresenter.CURRENT_USER, getString(R.string.my_music), false));
            navigationView.setCheckedItem(R.id.my_music_item);
        }
    }

    private void initSeekBar() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) { }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                presenter.onProgressChanged(seekBar.getProgress());
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, connection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(connection);
        presenter.setPlayer(null);
    }

    private void initSlidingPanel() {
        panel.addPanelSlideListener(new SlidingUpPanelLayout.SimplePanelSlideListener());
        panel.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
    }

    private void startService() {
        Intent intent = new Intent(this, MusicService.class);
        startService(intent);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.bindView(null);
        if (isFinishing()) {
            Intent intent = new Intent(this, MusicService.class);
            stopService(intent);
        }
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

        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onRequest() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (panel.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
            panel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
            OnBackPressListener fragment = (OnBackPressListener) getSupportFragmentManager()
                    .findFragmentById(R.id.fragment_container);
            if (fragment == null || !fragment.backPressHandled()) {
                super.onBackPressed();
            }
        }
    }

    @Override
    public void setQueue(List<Song> queue) {

    }

    @Override
    public void setPosition(int position) {

    }

    public void enableControlPanel() {
        if (panel.getPanelState() == SlidingUpPanelLayout.PanelState.HIDDEN) {
            panel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        }
    }

    @Override
    public void renderSong(@NotNull Song song) {
        songNameView.setText(song.getTitle());
        artistNameView.setText(song.getArtist());
        seekBar.setMax(song.getDuration());
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
