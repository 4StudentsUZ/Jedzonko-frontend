package com.fourstudents.jedzonko.Fragments.Shop;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

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

        Button restaurantsButton = view.findViewById(R.id.restaurantsButton);
        Button groceriesButton = view.findViewById(R.id.groceriesButton);

        restaurantsButton.setOnClickListener(v -> openNearbyRestaurantsMap());
        groceriesButton.setOnClickListener(v -> openNearbyGroceries());
    }

    private void openNearbyRestaurantsMap() {
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=restaurants");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

    private void openNearbyGroceries() {
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=grocery stores");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }
}