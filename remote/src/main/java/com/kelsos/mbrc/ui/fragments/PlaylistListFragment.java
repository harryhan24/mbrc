package com.kelsos.mbrc.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.PlaylistListAdapter;
import com.kelsos.mbrc.adapters.PlaylistListAdapter.OnPlaylistPlayPressedListener;
import com.kelsos.mbrc.domain.Playlist;
import com.kelsos.mbrc.presenters.PlaylistPresenter;
import com.kelsos.mbrc.ui.views.PlaylistListView;
import java.util.List;
import roboguice.fragment.RoboFragment;

public class PlaylistListFragment extends RoboFragment implements PlaylistListView, OnPlaylistPlayPressedListener {

  @Bind(R.id.playlist_recycler) RecyclerView recyclerView;

  @Inject private PlaylistListAdapter adapter;
  @Inject private PlaylistPresenter presenter;

  public static PlaylistListFragment newInstance() {
    return new PlaylistListFragment();
  }

  @Override public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    final View view = inflater.inflate(R.layout.ui_fragment_playlist, container, false);
    ButterKnife.bind(this, view);
    presenter.bind(this);
    adapter.setOnPlaylistPlayPressedListener(this);

    RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity());
    recyclerView.setLayoutManager(manager);
    recyclerView.setAdapter(adapter);
    return view;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override public void onStart() {
    super.onStart();
    presenter.load();
  }

  @Override public void update(List<Playlist> playlists) {
    adapter.updateData(playlists);
  }

  @Override public void playlistPlayPressed(Playlist playlist, int position) {
    presenter.play(playlist.getPath());
  }
}
