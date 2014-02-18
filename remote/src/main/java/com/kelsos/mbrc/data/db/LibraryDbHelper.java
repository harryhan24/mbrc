package com.kelsos.mbrc.data.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import com.kelsos.mbrc.BuildConfig;
import com.kelsos.mbrc.data.dbdata.*;

import java.util.List;

public class LibraryDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "TrackLibrary.db";
    public static final String IS = " IS ?";
    public static final String ASC = " ASC";
    public static final String EQUALS = " = ?";
    private final Context mContext;

    public LibraryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = context;
    }

    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL(Album.CREATE_TABLE);
        db.execSQL(Artist.CREATE_TABLE);
        db.execSQL(Cover.CREATE_TABLE);
        db.execSQL(Genre.CREATE_TABLE);
        db.execSQL(Track.CREATE_TABLE);
        db.execSQL(NowPlayingTrack.CREATE_TABLE);
        db.execSQL(Playlist.CREATE_TABLE);
        db.execSQL(PlaylistTrack.CREATE_TABLE);
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(Album.DROP_TABLE);
        db.execSQL(Artist.DROP_TABLE);
        db.execSQL(Cover.DROP_TABLE);
        db.execSQL(Genre.DROP_TABLE);
        db.execSQL(Track.DROP_TABLE);
        db.execSQL(NowPlayingTrack.DROP_TABLE);
        db.execSQL(Playlist.DROP_TABLE);
        db.execSQL(PlaylistTrack.DROP_TABLE);
        onCreate(db);
    }

    @Override public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            db.execSQL("PRAGMA foreign_keys=ON");
        }
    }

    public synchronized Cursor getAlbumCursor(final long id) {
        final SQLiteDatabase db = this.getReadableDatabase();
        return db != null ? db.query(Album.TABLE_NAME, Album.FIELDS,
                Album._ID + IS, new String[]{Long.toString(id)}, null, null, null, null) : null;
    }

    public synchronized long insertAlbum(final Album album) {
        album.setId(getAlbumId(album.getAlbumName()));
        if (album.getId() > 0) {
            return album.getId();
        }
        album.setArtistId(insertArtist(new Artist(album.getArtist(), "")));
        SQLiteDatabase db = this.getWritableDatabase();
        return db != null ? db.insert(Album.TABLE_NAME, null, album.getContentValues()) : 0;
    }

    public synchronized long getAlbumId(final String albumName) {
        long id = -1;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db != null ? db.query(true, Album.TABLE_NAME, new String[]{Album._ID},
                Album.ALBUM_NAME + EQUALS, new String[]{albumName},
                null, null, null, null) : null;

        if (c != null) {
            if (c.moveToFirst()) {
                id = c.getInt(c.getColumnIndex(Album._ID));
            }
            c.close();
        }

        return id;
    }

    public synchronized Artist getArtist(final long id) {
        final Cursor cursor = getArtistCursor(id);
        final Artist artist;
        if (cursor.moveToFirst()) {
            artist = new Artist(cursor);
        } else {
            artist = null;
        }
        cursor.close();
        return artist;
    }

    public synchronized Cursor getArtistCursor(final long id) {
        final SQLiteDatabase db = this.getReadableDatabase();
        return db != null ? db.query(Artist.TABLE_NAME, Artist.FIELDS,
                Artist._ID + IS, new String[]{Long.toString(id)},
                null, null, Artist.ARTIST_NAME + ASC, null) : null;
    }

    public synchronized long getArtistId(final String artistName) {
        long id = -1;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db != null ? db.query(true, Artist.TABLE_NAME, new String[]{Artist._ID},
                Artist.ARTIST_NAME + EQUALS, new String[]{artistName},
                null, null, null, null) : null;
        if (c != null) {
            if (c.moveToFirst()) {
                id = c.getInt(c.getColumnIndex(Artist._ID));
            }
            c.close();
        }

        return id;
    }

    public synchronized Cursor getAllArtistsCursor(final String selection,
                                                   final String[] args) {
        final SQLiteDatabase db = this.getReadableDatabase();
        return db != null ? db.query(Artist.TABLE_NAME, Artist.FIELDS, selection,
                args, null, null, Artist.ARTIST_NAME + ASC) : null;
    }

    public synchronized long insertArtist(final Artist artist) {
        long id = getArtistId(artist.getArtistName());
        if (id > 0) {
            return id;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        id = db != null ? db.insert(Artist.TABLE_NAME, null, artist.getContentValues()) : 0;
        return id;
    }

    public synchronized long insertGenre(final Genre genre) {
        genre.setId(getGenreId(genre.getGenreName()));
        if (genre.getId() > 0) {
            return genre.getId();
        }

        SQLiteDatabase db = this.getWritableDatabase();
        return db != null ? db.insert(Genre.TABLE_NAME, null, genre.getContentValues()) : 0;
    }

    public synchronized long getGenreId(final String genreName) {
        long id = -1;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db != null ? db.query(true, Genre.TABLE_NAME, new String[]{Genre._ID},
                Genre.GENRE_NAME + EQUALS, new String[]{genreName},
                null, null, null, null) : null;

        if (c != null) {
            if (c.moveToFirst()) {
                id = c.getInt(c.getColumnIndex(Genre._ID));
            }
            c.close();
        }

        return id;
    }

    public synchronized Cursor getGenreCursor(final long id) {
        final SQLiteDatabase db = this.getReadableDatabase();
        return db != null ? db.query(Genre.TABLE_NAME, Genre.FIELDS,
                Genre._ID + IS, new String[]{Long.toString(id)},
                null, null, Genre.GENRE_NAME + ASC, null) : null;
    }

    public synchronized Cursor getAllGenresCursor(final String selection,
                                                  final String[] args) {
        final SQLiteDatabase db = this.getReadableDatabase();
        return db != null ? db.query(Genre.TABLE_NAME, Genre.FIELDS,
                selection, args, null, null, Genre.GENRE_NAME + ASC) : null;
    }

    public synchronized long getCoverId(final String hash) {
        long id = -1;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db != null ? db.query(true, Cover.TABLE_NAME, new String[]{Cover._ID},
                Cover.COVER_HASH + EQUALS, new String[]{hash},
                null, null, null, null) : null;

        if (c != null) {
            if (c.moveToFirst()) {
                id = c.getInt(c.getColumnIndex(Cover._ID));
            }
            c.close();
        }

        return id;
    }

    public synchronized long insertCover(final Cover cover) {
        cover.setId(getCoverId(cover.getCoverHash()));
        if (cover.getId() > 0) {
            return cover.getId();
        }

        SQLiteDatabase db = this.getWritableDatabase();
        return db != null ? db.insert(Cover.TABLE_NAME, null, cover.getContentValues()) : 0;
    }

    public synchronized Cursor getCoverCursor(final long id) {
        final SQLiteDatabase db = this.getReadableDatabase();
        return db != null ? db.query(Cover.TABLE_NAME, Cover.FIELDS, Cover._ID + IS,
                new String[]{Long.toString(id)}, null, null, null, null) : null;
    }

    public synchronized Cursor getAllCoversCursor(final String selection,
                                                  final String[] args,
                                                  final String sortOrder) {
        final SQLiteDatabase db = this.getReadableDatabase();
        return db != null ? db.query(Cover.TABLE_NAME, Cover.FIELDS, selection, args,
                null, null, sortOrder, null) : null;
    }

    /**
     * Used to batch insert now playlist data in the library
     * @param list A list of now playing tracks
     */
    public synchronized void batchNowPlayingInsert(final List<NowPlayingTrack> list) {
        SQLiteDatabase mDb = getWritableDatabase();
        if (mDb != null) {
            mDb.beginTransaction();
            SQLiteStatement stm = mDb.compileStatement(NowPlayingTrack.INSERT);
            for (NowPlayingTrack track : list) {
                if (stm != null) {
                    stm.bindString(1, track.getArtist());
                    stm.bindString(2, track.getTitle());
                    stm.bindString(3, track.getSrc());
                    stm.bindLong(4, track.getPosition());
                    stm.execute();
                    stm.clearBindings();
                }
            }
            mDb.setTransactionSuccessful();
            mDb.endTransaction();
            mDb.close();
        }
    }

    /**
     * Used to batch insert playlists in the sqlite database
     * @param list The list of playlists
     */
    public synchronized void batchInsertPlaylists(final List<Playlist> list) {
        final SQLiteDatabase mDb = getWritableDatabase();
        if (mDb != null) {
            mDb.beginTransaction();
            SQLiteStatement stm = mDb.compileStatement(Playlist.INSERT);
            for (Playlist playlist : list) {
                if (stm != null) {
                    stm.bindString(1, playlist.getName());
                    stm.bindString(2, playlist.getHash());
                    stm.bindLong(3, playlist.getTracks());
                    stm.execute();
                    stm.clearBindings();
                }
            }
            mDb.setTransactionSuccessful();
            mDb.endTransaction();
            mDb.close();
        }
        mContext.getContentResolver().notifyChange(Playlist.getContentUri(), null, false);
    }

    /**
     * Used to batch insert the playlist tracks in the database
     * @param list The list of playlist tracks
     */
    public synchronized void batchInsertPlaylistTracks(final List<PlaylistTrack> list) {
        final SQLiteDatabase mDb = getWritableDatabase();
        if (mDb != null) {
            mDb.beginTransaction();
            SQLiteStatement stm = mDb.compileStatement(PlaylistTrack.INSERT);
            for (PlaylistTrack track: list) {
                if (stm != null) {
                    stm.bindString(1, track.getArtist());
                    stm.bindString(2, track.getTitle());
                    stm.bindLong(3, track.getIndex());
                    stm.bindString(4, track.getHash());
                    stm.bindString(5, track.getPlaylistHash());
                    stm.execute();
                    stm.clearBindings();
                }
            }
            mDb.setTransactionSuccessful();
            mDb.endTransaction();
            mDb.close();
        }
        mContext.getContentResolver().notifyChange(PlaylistTrack.getContentUri(), null, false);
    }

    /**
     * Receives a batch list of track meta data in json format and inserts them
     * in the database using compiled statements
     * @param list A list containing track items
     */
    public synchronized void processBatch(final List<Track> list) {
        SQLiteDatabase mDb = getWritableDatabase();
        if (mDb != null) {
            mDb.beginTransaction();
            SQLiteStatement artistStatement = mDb.compileStatement(Artist.INSERT);
            SQLiteStatement genreStatement = mDb.compileStatement(Genre.INSERT);
            SQLiteStatement albumStatement = mDb.compileStatement(Album.INSERT);
            SQLiteStatement trackStatement = mDb.compileStatement(Track.INSERT);

            for(Track track : list) {
                if (artistStatement != null) {
                    artistStatement.bindString(1, track.getArtist());
                    artistStatement.execute();
                    artistStatement.clearBindings();

                    artistStatement.bindString(1, track.getAlbumArtist());
                    artistStatement.execute();
                    artistStatement.clearBindings();
                }

                if (genreStatement != null) {
                    genreStatement.bindString(1, track.getGenre());
                    genreStatement.execute();
                    genreStatement.clearBindings();
                }


                if (albumStatement != null) {
                    albumStatement.bindString(1, track.getAlbum());
                    albumStatement.bindString(2, track.getAlbumArtist());
                    albumStatement.execute();
                    albumStatement.clearBindings();
                }

                if (trackStatement != null) {
                    trackStatement.bindString(1, track.getHash());
                    trackStatement.bindString(2, track.getTitle());
                    trackStatement.bindString(3, track.getGenre());
                    trackStatement.bindString(4, track.getArtist());
                    trackStatement.bindString(5, track.getAlbum());
                    trackStatement.bindString(6, track.getYear());
                    trackStatement.bindLong(7, track.getTrackNo());
                    trackStatement.execute();
                    trackStatement.clearBindings();
                }
            }
            mDb.setTransactionSuccessful();
            mDb.endTransaction();
            mDb.close();
        }
    }

    public synchronized Cursor getAllPlaylistsCursor(final String sortOrder) {
        final SQLiteDatabase db = this.getReadableDatabase();
        return db != null
                ? db.query(Playlist.TABLE_NAME,
                new String[]{
                        Playlist._ID,
                        Playlist.PLAYLIST_NAME,
                        Playlist.PLAYLIST_HASH,
                        Playlist.PLAYLIST_TRACKS
                },
                null, null,
                null, null,
                sortOrder, null)
                : null;
    }

    public synchronized long insertTrack(final Track track) {
        track.setArtistId(insertArtist(new Artist(track.getArtist(), track.getArtistImageUrl())));
        track.setGenreId(insertGenre(new Genre(track.getGenre())));
        track.setCoverId(insertCover(new Cover(track.getCoverHash())));
        track.setAlbumId(insertAlbum(new Album(track.getAlbum(), track.getAlbumArtist())));
        SQLiteDatabase db = this.getWritableDatabase();
        long id = db != null ? db.insert(Track.TABLE_NAME, null, track.getContentValues()) : 0;
        Log.d(BuildConfig.PACKAGE_NAME, "inserting track with id " + id + " and title" + track.getTitle());
        return id;
    }

    public synchronized Cursor getTrackCursor(final long id) {
        final SQLiteDatabase db = this.getReadableDatabase();
        return db != null ? db.rawQuery(Track.SELECT_TRACK, new String[]{Long.toString(id)}) : null;
    }

    public synchronized Cursor getAllTracksCursor(final String[] args) {
        final SQLiteDatabase db = this.getReadableDatabase();
        return db != null ? db.rawQuery(Track.SELECT_TRACKS, args) : null;
    }

}
