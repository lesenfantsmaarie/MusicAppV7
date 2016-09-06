package be.evoliris.android.musicapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import be.evoliris.android.musicapp.adapter.AlbumCursorAdapter;
import be.evoliris.android.musicapp.db.dao.AlbumDAO;
import be.evoliris.android.musicapp.model.Album;
import be.evoliris.android.musicapp.task.LoadAlbumsTask;

public class MainActivity extends AppCompatActivity implements LoadAlbumsTask.LoadAlbumsTaskCallback{

    private Button btnMainAdd;
    private ListView lvMainAlbums;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // findViewById

        btnMainAdd = (Button) findViewById(R.id.btn_main_add);
        lvMainAlbums = (ListView) findViewById(R.id.lv_main_albums);

        // Listeners

        btnMainAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toAddAlbum = new Intent(MainActivity.this, AddAlbumActivity.class);
                startActivityForResult(toAddAlbum, AddAlbumActivity.REQUEST_CODE);
            }
        });

        lvMainAlbums.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView tv = (TextView) view;
                Toast.makeText(MainActivity.this,
                        "Click on " + tv.getText().toString(),
                        Toast.LENGTH_SHORT).show();
            }
        });

        // ListView

        AlbumCursorAdapter adapter = new AlbumCursorAdapter(MainActivity.this, null);
        lvMainAlbums.setAdapter(adapter);

        updateListView();

        registerForContextMenu(lvMainAlbums);

        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(MainActivity.this);

        if (prefs.getBoolean("news", false)) {
            String email = prefs.getString("email", "");
            Toast.makeText(MainActivity.this, "Sending a mail to " + email,
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case AddAlbumActivity.REQUEST_CODE:

                switch (resultCode) {
                    case RESULT_OK:
                        updateListView();
                        Toast.makeText(MainActivity.this,
                                "Album was successfully added",
                                Toast.LENGTH_SHORT).show();
                        break;

                    case RESULT_CANCELED:
                        Toast.makeText(MainActivity.this, "Canceled",
                                Toast.LENGTH_SHORT).show();
                        break;

                    default:
                        Log.e(getLocalClassName(), "Unknown resultCode");
                        break;
                }

                break;

            default:
                Log.e(getLocalClassName(), "Unknown requestCode");
                break;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.delete_all:
                AlbumDAO dao = new AlbumDAO(MainActivity.this);
                dao.openWritable().deleteAll();
                dao.close();
                updateListView();
                break;

            case R.id.settings:
                Intent toPreferences =
                        new Intent(MainActivity.this, AppPreferenceActivity.class);
                startActivity(toPreferences);
                break;

            default:
                // TODO
                break;
        }
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.menu_main_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        RelativeLayout rl = (RelativeLayout) info.targetView;

        TextView tvId = (TextView) rl.findViewById(R.id.tv_item_id);
        long id = Long.parseLong(tvId.getText().toString());

        switch (item.getItemId()) {
            case R.id.context_menu_edit:
                // TODO
                break;

            case R.id.context_menu_delete:
                AlbumDAO dao = new AlbumDAO(MainActivity.this);
                dao.openWritable().deleteById(id);
                dao.close();
                updateListView();
                break;
        }
        return true;
    }

    @Override
    public void postUpdateUI(Cursor c) {
        //va fournir à l'adapter de la listview le curseur créé
        AlbumCursorAdapter adapter = (AlbumCursorAdapter) lvMainAlbums.getAdapter();
                //ligne suivante ne marche que si un adapter existe déjà
        adapter.changeCursor(c);
    }

    private void updateListView(){
        LoadAlbumsTask task = new LoadAlbumsTask(MainActivity.this);
        //va exécuter une seule fois la tâche! sinon refaire un new
        task.setCallback(this);
        task.execute();
    }
}
