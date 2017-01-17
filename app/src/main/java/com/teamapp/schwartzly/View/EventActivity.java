package com.teamapp.schwartzly.View;

import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;
import com.teamapp.schwartzly.Data.DataManager;
import com.teamapp.schwartzly.Data.EventPlayer;
import com.teamapp.schwartzly.R;
import com.teamapp.schwartzly.databinding.PlayerItemBinding;

import java.util.ArrayList;

public class EventActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final RecyclerView list = (RecyclerView) findViewById(R.id.playersList);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(new PlayersAdapter(DataManager.getInstance().getEventPlayers()));

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            Paint p = new Paint();
            Bitmap icon;
            float padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    16, getResources().getDisplayMetrics());

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                Log.e("QWER", "ON MOVE");
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                //Remove swiped item from list and notify the RecyclerView
                list.getAdapter().notifyItemChanged(viewHolder.getAdapterPosition());
                //clearView(list, viewHolder);

                Log.e("QWER", "clearView");
                //todo invoke status change

            }

            @Override
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                Log.e("QWER", "clearView");
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView,
                                    RecyclerView.ViewHolder viewHolder, float dX,
                                    float dY, int actionState, boolean isCurrentlyActive) {

                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    // Get RecyclerView item from the ViewHolder
                    View itemView = viewHolder.itemView;

                    if (dX > 0) {

                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.done);

                        /* Set your color for positive displacement */
                        p.setARGB(255, 76, 175, 80);

                        // Draw Rect with varying right side, equal to displacement dX
                        c.drawRect((float) itemView.getLeft(), (float) itemView.getTop(), dX,
                                (float) itemView.getBottom(), p);

                        // Set the image icon for Right swipe
                        c.drawBitmap(icon,
                                (float) itemView.getLeft() + padding,
                                (float) itemView.getTop() + ((float) itemView.getBottom() - (float) itemView.getTop() - icon.getHeight()) / 2,
                                p);
                    } else {
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.decline);

                        /* Set your color for negative displacement */
                        p.setARGB(255, 255, 82, 82);

                        // Draw Rect with varying left side, equal to the item's right side
                        // plus negative displacement dX
                        c.drawRect((float) itemView.getRight() + dX, (float) itemView.getTop(),
                                (float) itemView.getRight(), (float) itemView.getBottom(), p);

                        //Set the image icon for Left swipe
                        c.drawBitmap(icon,
                                (float) itemView.getRight() - padding - icon.getWidth(),
                                (float) itemView.getTop() + ((float) itemView.getBottom() - (float) itemView.getTop() - icon.getHeight()) / 2,
                                p);
                    }

                    // Fade out the view as it is swiped out of the parent's bounds
                    //final float alpha = 1 - Math.abs(dX) / (float) viewHolder.itemView.getWidth();
                    //viewHolder.itemView.setAlpha(alpha);
                    viewHolder.itemView.setTranslationX(dX);

                } else {
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(list);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

    public class PlayersAdapter extends RecyclerView.Adapter<PlayersAdapter.PlayerHolder> {

        private ArrayList<EventPlayer> mPlayers;

        public PlayersAdapter(ArrayList<EventPlayer> players) {
            mPlayers = players;
        }

        @Override
        public PlayerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            PlayerItemBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.player_item, parent, false);
            return new PlayerHolder(binding);
        }

        @Override
        public void onBindViewHolder(PlayerHolder holder, int position) {
            holder.bindPlayer(mPlayers.get(position));
        }

        @Override
        public int getItemCount() {
            return mPlayers.size();
        }

        public class PlayerHolder extends RecyclerView.ViewHolder {

            private final PlayerItemBinding mBinding;

            public PlayerHolder(PlayerItemBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
            }

            public void bindPlayer(EventPlayer player) {

                Picasso.with(mBinding.getRoot().getContext())
                        .load(player.player.image)
                        .transform(new CircleTransform())
                        .into(mBinding.thumbnail);

                mBinding.name.setText(player.player.name);
            }
        }
    }
}
