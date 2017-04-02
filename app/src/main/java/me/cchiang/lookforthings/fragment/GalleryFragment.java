package me.cchiang.lookforthings.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import me.cchiang.lookforthings.R;
import me.cchiang.lookforthings.adapter.GalleryAdapter;
import me.cchiang.lookforthings.data.Constant;
import me.cchiang.lookforthings.data.GlobalVariable;
import me.cchiang.lookforthings.data.Tools;
import me.cchiang.lookforthings.model.GalleryImage;

public class GalleryFragment extends Fragment {

    private View view;
    private RecyclerView recyclerView;
    private GalleryAdapter mGridAdapter;
//    private FeedListAdapter mListAdapter;
    private GlobalVariable global;
    private int selected_pos = 0;
    private List<GalleryImage> items = new ArrayList<>();
    private MenuItem menuItem;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_gallery, container, false);

        // activate fragment menu
        setHasOptionsMenu(true);
        global = (GlobalVariable) getActivity().getApplication();
        items = Constant.getRandomExploreFeed(getActivity());
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        if (global.isGrid_mode()) {
            switchGridMode();
        } else {
            switchListMode();
        }
        return view;
    }

    private void switchGridMode() {
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), Tools.getGridExplorerCount(getActivity())));
        recyclerView.setHasFixedSize(true);
        recyclerView.setPadding(2, 2, 2, 2);

        //set data and list adapter
        mGridAdapter = new GalleryAdapter(getActivity(), items);
        recyclerView.setAdapter(mGridAdapter);
        mGridAdapter.setOnItemClickListener(new GalleryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, GalleryImage obj, int position) {
//                selected_pos = position;
//                switchListMode();
//                menuItem.setIcon(R.drawable.ic_menu_grid);
            }
        });
        global.setGrid_mode(true);
    }

    private void switchListMode() {
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        recyclerView.setHasFixedSize(true);
        recyclerView.setPadding(0, 0, 0, 0);
        recyclerView.scrollToPosition(selected_pos);
//
//        mListAdapter = new FeedListAdapter(getActivity(), items);
//        recyclerView.setAdapter(mListAdapter);
//        global.setGrid_mode(false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
//        this.menuItem = menu.getItem(1);
//        if (global.isGrid_mode()) {
//            menuItem.setIcon(R.drawable.ic_menu_list);
//        } else {
//            menuItem.setIcon(R.drawable.ic_menu_grid);
//        }
        super.onCreateOptionsMenu(menu, inflater);
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int itemId = item.getItemId();
//        if (itemId == R.id.action_mode) {
//            if (global.isGrid_mode()) {
//                switchListMode();
//                item.setIcon(R.drawable.ic_menu_grid);
//            } else {
//                switchGridMode();
//                item.setIcon(R.drawable.ic_menu_list);
//            }
//        } else {
//            Snackbar.make(view, item.getTitle() + " Clicked", Snackbar.LENGTH_SHORT).show();
//        }
//        return super.onOptionsItemSelected(item);
//    }

}
