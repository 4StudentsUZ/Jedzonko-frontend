package com.fourstudents.jedzonko;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fourstudents.jedzonko.Database.Entities.Shopping;
import com.fourstudents.jedzonko.Database.RoomDB;

import java.util.ArrayList;
import java.util.List;

public class ShoppingListFragment extends Fragment {

    public ShoppingListFragment() {
        super(R.layout.fragment_slist);
    }

    RecyclerView recyclerView;
    List<Shopping> shoppingListList = new ArrayList<>();
    ShoppingRecyclerViewAdapter adapter;
    RoomDB database;

    private void initToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.custom_toolbar);
        toolbar.setTitle("Listy zakupów");
        toolbar.inflateMenu(R.menu.slist);

    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initToolbar(view);

        database = RoomDB.getInstance(getActivity());
        recyclerView = view.findViewById(R.id.sListList);

        shoppingListList.clear();
        shoppingListList.addAll(database.shoppingDao().getAll());
        adapter= new ShoppingRecyclerViewAdapter(getContext(), shoppingListList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);


        view.findViewById(R.id.floatingActionButton_add_sList).setOnClickListener(v ->
                getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(((ViewGroup) getView().getParent()).getId(), new AddRecipeFragment(), "AddRecipeFragment")
                        .replace(R.id.mainFrameLayout, new AddRecipeFragment(), "AddRecipeFragment")
                        .addToBackStack("AddRecipeFragment")
                        .commit()
        );
    }
}