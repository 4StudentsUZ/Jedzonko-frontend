package com.fourstudents.jedzonko;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;

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
                    .replace(((ViewGroup) getView().getParent()).getId(), new AddRecipeFragment(), "AddRecipeFragment")
                    .replace(R.id.mainFrameLayout, new AddRecipeFragment(), "AddRecipeFragment")
                    .addToBackStack("AddRecipeFragment")
                    .commit()
            );
        view.findViewById(R.id.floatingActionButton_open_camera).setOnClickListener(v ->
                requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.mainFrameLayout, new CameraFragment(), "RecipeCameraView")
                    .addToBackStack("RecipeCameraView")
                    .commit()
        );
    }
}