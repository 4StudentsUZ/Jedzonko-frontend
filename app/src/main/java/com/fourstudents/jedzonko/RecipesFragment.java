package com.fourstudents.jedzonko;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

public class RecipesFragment extends Fragment {

    public RecipesFragment() {
        super(R.layout.fragment_recipes);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar = view.findViewById(R.id.custom_toolbar);
        toolbar.setTitle(R.string.title_recipes);

        view.findViewById(R.id.floatingActionButton_add_recipe).setOnClickListener(v ->
                getActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    //.replace(((ViewGroup) getView().getParent()).getId(), new AccountFragment(), "AddRecipeFragment")
                    .replace(R.id.mainFrameLayout, new AccountFragment(), "AddRecipeFragment")
                    .addToBackStack("AddRecipeFragment")
                    .commit()
            );

    }
}