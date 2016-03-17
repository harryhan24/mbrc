package com.kelsos.mbrc.ui.fragments.profile;

import android.app.ActionBar;
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
import com.kelsos.mbrc.domain.Artist;
import com.kelsos.mbrc.presenters.GenreArtistsPresenter;
import com.kelsos.mbrc.ui.dialogs.CreateNewPlaylistDialog;
import com.kelsos.mbrc.ui.dialogs.PlaylistDialogFragment;
import com.kelsos.mbrc.ui.views.GenreArtistView;
import java.util.List;
import roboguice.RoboGuice;

public class GenreArtistsFragment extends Fragment
    implements GenreArtistView, PlaylistDialogFragment.OnPlaylistSelectedListener,
    CreateNewPlaylistDialog.OnPlaylistNameSelectedListener {

  private static final String GENRE_ID = "genre_id";
  private static final String GENRE_NAME = "genre_name";

  @Inject private ArtistAdapter adapter;
  @Bind(R.id.genre_artists_recycler) RecyclerView recyclerView;
  @Inject private GenreArtistsPresenter presenter;

  public GenreArtistsFragment() {
    // Required empty public constructor
  }

  public static GenreArtistsFragment newInstance(long genreId, String genreName) {
    GenreArtistsFragment fragment = new GenreArtistsFragment();
    Bundle args = new Bundle();
    args.putLong(GENRE_ID, genreId);
    args.putString(GENRE_NAME, genreName);
    fragment.setArguments(args);
    return fragment;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    RoboGuice.getInjector(getContext()).injectMembers(this);
    presenter.bind(this);
    Bundle bundle = getArguments();
    if (bundle != null) {
      presenter.load(bundle.getLong(GENRE_ID));
      ActionBar actionBar = getActivity().getActionBar();
      if (actionBar != null) {
        actionBar.setTitle(bundle.getString(GENRE_NAME, ""));
      }
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

  @Override public void update(List<Artist> data) {
    adapter.updateData(data);
  }
}
