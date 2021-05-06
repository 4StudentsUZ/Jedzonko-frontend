package com.fourstudents.jedzonko.Fragments.Shop;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.fourstudents.jedzonko.Fragments.Recipe.AddTagFragment;
import com.fourstudents.jedzonko.Fragments.Recipe.EditRecipeFragment;
import com.fourstudents.jedzonko.R;

public class ShopsFragment extends Fragment {

    public ShopsFragment() {
        super(R.layout.fragment_shop);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar = view.findViewById(R.id.custom_toolbar);
        toolbar.setTitle(R.string.title_shops);
    }
}