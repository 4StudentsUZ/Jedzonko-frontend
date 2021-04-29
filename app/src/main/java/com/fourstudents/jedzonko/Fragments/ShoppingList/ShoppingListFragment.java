package com.fourstudents.jedzonko.Fragments.ShoppingList;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fourstudents.jedzonko.Adapters.ShoppingList.ShoppingAdapter;
import com.fourstudents.jedzonko.Database.Entities.Shopping;
import com.fourstudents.jedzonko.R;
import com.fourstudents.jedzonko.ViewModels.ShoppingList.ShoppingViewModel;

import java.util.List;

public class ShoppingListFragment extends Fragment {

    public ShoppingListFragment() {
        super(R.layout.fragment_shopping_list);
    }

    RecyclerView shoppingRV;
    ShoppingAdapter shoppingAdapter;

    private void initToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.custom_toolbar);
        toolbar.setTitle(R.string.title_slist);
        toolbar.inflateMenu(R.menu.slist);
        toolbar.getMenu();
        MenuItem search = toolbar.getMenu().findItem(R.id.action_search);
        SearchView searchView = (SearchView) search.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                shoppingAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        initToolbar(requireView());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        shoppingRV = view.findViewById(R.id.shoppingRV);
        ShoppingViewModel shoppingViewModel = new ViewModelProvider(this).get(ShoppingViewModel.class);
        shoppingAdapter = new ShoppingAdapter();
        shoppingRV.setAdapter(shoppingAdapter);
        shoppingRV.setLayoutManager(new LinearLayoutManager(getContext()));
        shoppingViewModel.getAllLiveDataShoppingList().observe(getViewLifecycleOwner(), new Observer<List<Shopping>>() {
            @Override
            public void onChanged(List<Shopping> shoppings) {
                if (shoppings != null) {
                    shoppingAdapter.setProductList(shoppings);
                }
            }
        });

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