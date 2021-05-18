package com.fourstudents.jedzonko.Fragments.Shop;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.fourstudents.jedzonko.R;

import org.jetbrains.annotations.NotNull;

public class ShopsFragment extends Fragment {

    private static final int locationRequestId = 24572;
    private LocationManager locationManager;

    public ShopsFragment() {
        super(R.layout.fragment_shop);
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLocationWithPermissionRequest();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar = view.findViewById(R.id.custom_toolbar);
        toolbar.setTitle(R.string.title_shops);

        Button restaurantsButton = view.findViewById(R.id.restaurantsButton);
        Button groceriesButton = view.findViewById(R.id.groceriesButton);

        restaurantsButton.setOnClickListener(v -> queryMaps("restaurants"));
        groceriesButton.setOnClickListener(v -> queryMaps("grocery stores"));
    }

    private void queryMaps(String query) {
        Location location = getLastLocation();
        if (location == null) {
            Toast.makeText(requireActivity(), "Trwa ładowanie lokalizacji...", Toast.LENGTH_SHORT).show();
            return;
        }

        Double latitude = location.getLatitude();
        Double longitude = location.getLongitude();

        @SuppressLint("DefaultLocale")
        Uri gmmIntentUri = Uri.parse(String.format("geo:%f,%f?q=%s", latitude, longitude, query));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

    private void initLocationWithPermissionRequest() {
        if (locationManager != null) return;

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, locationRequestId);
        } else {
            initLocation();
        }
    }

    private void initLocation() {
        if (locationManager != null) return;
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                1000,
                25,
                location -> {}
        );
    }

    private Location getLastLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        initLocationWithPermissionRequest();
        return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        if (requestCode == locationRequestId) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initLocation();
            } else {
                Toast.makeText(requireActivity(), "Brak pozwolenia na użycie lokalizacji.", Toast.LENGTH_SHORT).show();
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}