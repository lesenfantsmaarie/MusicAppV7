package be.evoliris.android.musicapp.task;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.widget.ListView;

import be.evoliris.android.musicapp.R;
import be.evoliris.android.musicapp.adapter.AlbumCursorAdapter;
import be.evoliris.android.musicapp.db.dao.AlbumDAO;

/**
 * Created by Evoliris on 29/08/2016.
 */
public class LoadAlbumsTask extends AsyncTask<Void, Void, Cursor> {

    private LoadAlbumsTaskCallback callback;

    private Context context;

    public LoadAlbumsTask (Context context){
        this.context=context;
    }

    public void setCallback(LoadAlbumsTaskCallback callback) {
        this.callback = callback;
    }

    @Override
    protected Cursor doInBackground(Void... voids) {
        AlbumDAO albumDAO = new AlbumDAO(context);
        return albumDAO.openReadable().readAllCursor();
       // AlbumCursorAdapter adapter = new AlbumCursorAdapter(context, c);
        // lvMainAlbums.setAdapter(adapter);
    }

    @Override
    protected void onPostExecute(Cursor cursor) {
        super.onPostExecute(cursor);
        if(callback != null){
            callback.postUpdateUI(cursor);
        }
    }

    public interface LoadAlbumsTaskCallback{
        void postUpdateUI(Cursor c);
    }
}
