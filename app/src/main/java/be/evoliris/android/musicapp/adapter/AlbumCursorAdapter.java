package be.evoliris.android.musicapp.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.Calendar;

import be.evoliris.android.musicapp.R;
import be.evoliris.android.musicapp.db.dao.AlbumDAO;
import be.evoliris.android.musicapp.model.Album;

public class AlbumCursorAdapter extends CursorAdapter {

    private LayoutInflater inflater;

    public AlbumCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return inflater.inflate(R.layout.album_list_item, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView tvId = (TextView) view.findViewById(R.id.tv_item_id);
        TextView tvTitle = (TextView) view.findViewById(R.id.tv_item_title);
        TextView tvGenre = (TextView) view.findViewById(R.id.tv_item_genre);
        TextView tvYear = (TextView) view.findViewById(R.id.tv_item_year);
        RatingBar rbRating = (RatingBar) view.findViewById(R.id.rb_item_rating);
        CheckBox cbFavorite = (CheckBox) view.findViewById(R.id.cb_item_favorite);
        ImageView ivImage = (ImageView) view.findViewById(R.id.iv_item_image);

        Album a = AlbumDAO.cursorToAlbum(cursor);

        tvId.setText(String.valueOf(a.getId()));
        tvTitle.setText(a.getTitle());

        if (a.isFavorite()) {
            tvTitle.setTextColor(context.getResources().getColor(R.color.colorAccent));
        }

        tvGenre.setText(a.getGenre());
        Calendar c = Calendar.getInstance();
        c.setTime(a.getOutDate());
        tvYear.setText(String.valueOf(c.get(Calendar.YEAR)));
        rbRating.setRating(a.getRating());
        cbFavorite.setChecked(a.isFavorite());
        // TODO ivImage
    }
}
