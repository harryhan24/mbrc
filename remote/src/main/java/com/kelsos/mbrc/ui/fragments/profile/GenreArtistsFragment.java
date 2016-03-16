package com.kelsos.mbrc.ui.fragments.profile;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.ArtistAdapter;
import com.kelsos.mbrc.presenters.GenreArtistsPresenter;
import com.kelsos.mbrc.ui.dialogs.CreateNewPlaylistDialog;
import com.kelsos.mbrc.ui.dialogs.PlaylistDialogFragment;
import com.kelsos.mbrc.ui.views.GenreArtistView;
import roboguice.RoboGuice;

public class GenreArtistsFragment extends Fragment
    implements GenreArtistView, PlaylistDialogFragment.OnPlaylistSelectedListener,
    CreateNewPlaylistDialog.OnPlaylistNameSelectedListener {

  private static final String GENRE_ID = "genreId";
  @Inject private ArtistAdapter adapter;
  @Bind(R.id.genre_artists_recycler) RecyclerView recyclerView;
  @Inject private GenreArtistsPresenter presenter;

  public GenreArtistsFragment() {
    // Required empty public constructor
  }

  public static GenreArtistsFragment newInstance(long genreId) {
    GenreArtistsFragment fragment = new GenreArtistsFragment();
    Bundle args = new Bundle();
    args.putLong(GENRE_ID, genreId);
    fragment.setArguments(args);
    return fragment;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    RoboGuice.getInjector(getContext()).injectMembers(this);
    if (getArguments() != null) {
      presenter.load(getArguments().getLong(GENRE_ID));
    }
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    final View view = inflater.inflate(R.layout.fragment_genre_artists, container, false);
    ButterKnife.bind(this, view);
    RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity());
    recyclerView.setLayoutManager(manager);
    recyclerView.setAdapter(adapter);
    return view;
  }

  @Override public void onPlaylistNameSelected(String name) {

  }

  @Override public void onPlaylistSelected(String hash) {

  }

  @Override public void onNewPlaylistSelected() {

  }
}
