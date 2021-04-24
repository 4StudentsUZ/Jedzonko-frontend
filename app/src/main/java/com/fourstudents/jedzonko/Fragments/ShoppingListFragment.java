package com.fourstudents.jedzonko.Fragments;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fourstudents.jedzonko.Adapters.ShoppingRecyclerViewAdapter;
import com.fourstudents.jedzonko.Database.Entities.Shopping;
import com.fourstudents.jedzonko.Fragments.AddShoppingListFragment;
import com.fourstudents.jedzonko.R;
import com.fourstudents.jedzonko.ViewModels.ShoppingViewModel;

import java.util.List;

public class ShoppingListFragment extends Fragment {

    public ShoppingListFragment() {
        super(R.layout.fragment_slist);
    }

    RecyclerView shoppingRV;
//    List<Shopping> shoppingListList = new ArrayList<>();
//    RoomDB database;
    ShoppingRecyclerViewAdapter shoppingAdapter;

    private void initToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.custom_toolbar);
        toolbar.setTitle(R.string.title_slist);
        toolbar.inflateMenu(R.menu.slist);
    }

    @Override
    public void onResume() {
        super.onResume();
        shoppingAdapter.notifyDataSetChanged();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initToolbar(view);

//        database = RoomDB.getInstance(getActivity());
        shoppingRV = view.findViewById(R.id.shoppingRV);
        shoppingAdapter = new ShoppingRecyclerViewAdapter();
        shoppingRV.setAdapter(shoppingAdapter);
        shoppingRV.setLayoutManager(new LinearLayoutManager(getContext()));

        ShoppingViewModel shoppingViewModel = new ViewModelProvider(this).get(ShoppingViewModel.class);
        shoppingViewModel.getAllLiveDataShoppingList().observe(getViewLifecycleOwner(), new Observer<List<Shopping>>() {
            @Override
            public void onChanged(List<Shopping> shoppings) {
                if (shoppings != null) {
                    shoppingAdapter.setProductList(shoppings);
                }
            }
        });

//        shoppingListList.clear();
//        shoppingListList.addAll(database.shoppingDao().getAll());


        view.findViewById(R.id.floatingActionButton_add_sList).setOnClickListener(v ->
                requireActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainFrameLayout, new AddShoppingListFragment(), "AddShoppingListFragment")
                        .addToBackStack("AddShoppingListFragment")
                        .commit()
        );
    }

}