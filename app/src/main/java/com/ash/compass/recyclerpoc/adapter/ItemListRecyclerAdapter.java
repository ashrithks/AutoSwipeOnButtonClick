package com.ash.compass.recyclerpoc.adapter;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ash.compass.recyclerpoc.interfaces.IOnSwipe;
import com.ash.compass.recyclerpoc.model.ItemModel;
import com.ash.compass.recyclerpoc.R;
import com.ash.compass.recyclerpoc.helper.SwipeRunnable;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ashrith on 30/5/16.
 */
public class ItemListRecyclerAdapter extends RecyclerView.Adapter<ItemListRecyclerAdapter.myViewHolder> {
    Context context;
    private List<ItemModel> itemList = new ArrayList<>();
    private LayoutInflater inflater;
    private IOnSwipe iOnSwipe;
    private Handler mHandler = new Handler();
    public static final int SWIPE_LEFT = -1;
    public static final int SWIPE_RIGHT = 1;
    public static final int TIME_POST_DELAYED = 1000; // in ms
    private final ArrayList<SwipeRunnable> mRunnables = new ArrayList<>();

    @Override
    public myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listadapter_item, parent, false);

        return new myViewHolder(itemView);

    }

    public ItemListRecyclerAdapter(Context context, List<ItemModel> itemList, IOnSwipe iOnSwipe) {
        this.context = context;
        this.itemList = itemList;
        this.iOnSwipe = iOnSwipe;

        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public void onBindViewHolder(final myViewHolder holder, final int position) {
        holder.text.setText(itemList.get(position).getText());
        holder.text.setTag(position);
        if (itemList.get(position).isItem) {
            holder.itemLayout.setVisibility(View.VISIBLE);
            holder.undoLayout.setVisibility(View.GONE);
            holder.undoignoreLayout.setVisibility(View.GONE);
        }

        if (itemList.get(position).isUndo) {
            holder.itemLayout.setVisibility(View.GONE);
            holder.undoLayout.setVisibility(View.VISIBLE);
            holder.undoignoreLayout.setVisibility(View.GONE);
        }
        if (itemList.get(position).isUndoIgnore) {
            holder.itemLayout.setVisibility(View.GONE);
            holder.undoLayout.setVisibility(View.GONE);
            holder.undoignoreLayout.setVisibility(View.VISIBLE);
        }

        holder.addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = holder.getAdapterPosition();
                try {
                    itemList.get(adapterPosition).setUndo(true);
                    itemList.get(adapterPosition).setItem(false);
                    itemList.get(adapterPosition).setUndoIgnore(false);
                    holder.itemLayout.setVisibility(View.GONE);
                    holder.undoignoreLayout.setVisibility(View.GONE);
                    holder.undoLayout.setVisibility(View.VISIBLE);
                    holder.undoLayout.startAnimation(inFromRightAnimation());  
                } catch (Exception e) {

                }
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    try {
                        final SwipeRunnable runnable = new SwipeRunnable() {
                            @Override
                            public void run() {
                                synchronized (mRunnables) {
                                    iOnSwipe.onSwipe(mRunnables.indexOf(this), SWIPE_RIGHT);
                                }
                            }
                        };
                        synchronized (mRunnables) {
                            mRunnables.set(adapterPosition, runnable);
                            mHandler.postDelayed(runnable, TIME_POST_DELAYED);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        holder.undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = holder.getAdapterPosition();
                
                try {
                    itemList.get(adapterPosition).setUndoIgnore(false);
                    itemList.get(adapterPosition).setUndo(false);
                    itemList.get(adapterPosition).setItem(true);
                    holder.itemLayout.setVisibility(View.VISIBLE);
                    holder.undoLayout.setVisibility(View.GONE);
                    holder.undoignoreLayout.setVisibility(View.GONE);
                    holder.undoLayout.startAnimation(outToLeftAnimation());
                    
                } catch (Exception e) {
                }
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    try {
                        synchronized (mRunnables) {

                            Runnable runnable = mRunnables.set(adapterPosition, null);
                            mHandler.removeCallbacks(runnable);
                        }
                    } catch (Exception e) {
                    }
                }
            }
        });

        holder.ignore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = holder.getAdapterPosition();
                
                try {
                     itemList.get(adapterPosition).setUndo(false);
                    itemList.get(adapterPosition).setItem(false);
                    itemList.get(adapterPosition).setUndoIgnore(true);
                    holder.itemLayout.setVisibility(View.GONE);
                    holder.undoLayout.setVisibility(View.GONE);
                    holder.undoignoreLayout.setVisibility(View.VISIBLE);
                    holder.undoignoreLayout.startAnimation(inFromLeftAnimation());
                   
                } catch (Exception e) {

                }
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    try {
                        final SwipeRunnable runnable = new SwipeRunnable() {
                            @Override
                            public void run() {
                                synchronized (mRunnables) {
                                    iOnSwipe.onSwipe(mRunnables.indexOf(this), SWIPE_LEFT);
                                }
                            }
                        };
                        synchronized (mRunnables) {
                            mRunnables.set(adapterPosition, runnable);
                            mHandler.postDelayed(runnable, TIME_POST_DELAYED);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        holder.undoIgnore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = holder.getAdapterPosition();             
                try {
                    itemList.get(adapterPosition).setUndo(false);
                    itemList.get(adapterPosition).setItem(true);
                    itemList.get(adapterPosition).setUndoIgnore(false);
                    holder.itemLayout.setVisibility(View.VISIBLE);
                    holder.undoLayout.setVisibility(View.GONE);
                    holder.undoignoreLayout.setVisibility(View.GONE);
                    holder.undoignoreLayout.startAnimation(outToRightAnimation());
                    
                } catch (Exception e) {
                }
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    try {
                        synchronized (mRunnables) {

                            Runnable runnable = mRunnables.set(adapterPosition, null);
                            mHandler.removeCallbacks(runnable);
                        }
                    } catch (Exception e) {
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }


    public class myViewHolder extends RecyclerView.ViewHolder {


        @Bind(R.id.text)
        TextView text;
        @Bind(R.id.additem)
        Button addItem;
        @Bind(R.id.itemlayout)
        RelativeLayout itemLayout;
        @Bind(R.id.undolayout)
        RelativeLayout undoLayout;
        @Bind(R.id.undo)
        TextView undo;
        @Bind(R.id.undoignorelayout)
        RelativeLayout undoignoreLayout;
        @Bind(R.id.undo_ignore)
        TextView undoIgnore;
        @Bind(R.id.ignore)
        Button ignore;

        public myViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


    private Animation inFromRightAnimation() {

        Animation inFromRight = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, +1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromRight.setDuration(250);
        inFromRight.setInterpolator(new AccelerateInterpolator());
        return inFromRight;
    }

    private Animation outToLeftAnimation() {
        Animation outtoLeft = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        outtoLeft.setDuration(250);
        outtoLeft.setInterpolator(new AccelerateInterpolator());
        return outtoLeft;
    }

    private Animation inFromLeftAnimation() {
        Animation inFromLeft = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromLeft.setDuration(250);
        inFromLeft.setInterpolator(new AccelerateInterpolator());
        return inFromLeft;
    }

    private Animation outToRightAnimation() {
        Animation outtoRight = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, +1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        outtoRight.setDuration(250);
        outtoRight.setInterpolator(new AccelerateInterpolator());
        return outtoRight;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        if (mRunnables.size() == 0) {
            int count = getItemCount();
            for (int i = 0; i < count; i++) {
                mRunnables.add(null);
            }
        }
        registerAdapterDataObserver(new SwipeAdapterDataObserver());
    }

    private class SwipeAdapterDataObserver extends RecyclerView.AdapterDataObserver {
        public void onChanged() {
            synchronized (mRunnables) {
                int size = mRunnables.size();
                int itemCount = getItemCount();
                if (itemCount > size) {
                    onItemRangeChanged(0, size);
                    onItemRangeInserted(size, itemCount - size);
                } else {
                    onItemRangeChanged(0, itemCount);
                    onItemRangeRemoved(itemCount, size - itemCount);
                }
            }
        }

        public void onItemRangeChanged(int positionStart, int itemCount) {
            synchronized (mRunnables) {
                for (int i = 0; i < itemCount; i++) {
                    Runnable r = mRunnables.set(positionStart + i, null);
                    if (r != null) {
                        mHandler.removeCallbacks(r);
                    }
                }
            }
        }

        public void onItemRangeInserted(int positionStart, int itemCount) {
            synchronized (mRunnables) {
                for (int i = 0; i < itemCount; i++) {
                    mRunnables.add(positionStart, null);
                }
            }
        }

        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            synchronized (mRunnables) {
                for (int i = 0; i < itemCount; i++) {
                    int c = fromPosition > toPosition ? i : 0;
                    mRunnables.set(toPosition + c, mRunnables.remove(fromPosition + c));
                }
            }
        }

        public void onItemRangeRemoved(int positionStart, int itemCount) {
            synchronized (mRunnables) {
                for (int i = 0; i < itemCount; i++) {
                    Runnable r = mRunnables.remove(positionStart);
                    if (r != null) {
                        mHandler.removeCallbacks(r);
                    }
                }
            }
        }
    }


    public void remove(int position) {
        try {
            Toast.makeText(context, itemList.get(position).getText() + " removed", Toast.LENGTH_SHORT).show();
            itemList.remove(position);
            notifyItemRemoved(position);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
