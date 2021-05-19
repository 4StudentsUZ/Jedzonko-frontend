package com.fourstudents.jedzonko.Fragments.Shop;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.fourstudents.jedzonko.R;

import org.jetbrains.annotations.NotNull;

public class ShopsFragment extends Fragment implements LocationListener {

    private static final int locationRequestId = 24572;
    private Location lastLocation = null;
    private LocationManager locationManager;

    private TextView statusText;
    private Button requestPermissionsButton;
    Button restaurantsButton;
    Button groceriesButton;

    public ShopsFragment() {
        super(R.layout.fragment_shop);
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        if (locationManager != null) {
            locationManager.removeUpdates(this);
            locationManager = null;
        }

        super.onDestroy();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar = view.findViewById(R.id.custom_toolbar);
        toolbar.setTitle(R.string.title_shops);

        restaurantsButton = view.findViewById(R.id.restaurantsButton);
        groceriesButton = view.findViewById(R.id.groceriesButton);

        restaurantsButton.setEnabled(false);
        restaurantsButton.setOnClickListener(v -> queryMaps("restaurants"));

        groceriesButton.setEnabled(false);
        groceriesButton.setOnClickListener(v -> queryMaps("grocery stores"));

        statusText = view.findViewById(R.id.locationStatus);

        requestPermissionsButton = view.findViewById(R.id.requestPermissions);
        requestPermissionsButton.setVisibility(View.INVISIBLE);
        requestPermissionsButton.setOnClickListener(v -> initLocationWithPermissionRequest());

        initLocationWithPermissionRequest();
    }

    private void queryMaps(String query) {
        if (lastLocation == null) {
            Toast.makeText(requireContext(), "Nie odnaleziono lokalizacji!", Toast.LENGTH_SHORT).show();
            return;
        }

        Double latitude = lastLocation.getLatitude();
        Double longitude = lastLocation.getLongitude();

        @SuppressLint("DefaultLocale")
        Uri gmmIntentUri = Uri.parse(String.format("geo:%f,%f?q=%s", latitude, longitude, query));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        try {
            startActivity(mapIntent);
        } catch (Exception e) {
            checkGoogleMapsAvailability();
        }
    }

    private void initLocationWithPermissionRequest() {
        if (locationManager != null) return;

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, locationRequestId);
            } else {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Brak uprawnień")
                        .setMessage("Brak pozwolenia na użycie lokalizacji, upewnij się, że aplikacja ma nadane odpowiednie uprawnienia w ustawieniach systemowych.")
                        .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                        })
                        .show();

                onLocationPermissionDenied();
            }
        } else {
            initLocation();
        }
    }

    private boolean checkGoogleMapsAvailability() {
        if (isGoogleMapsInstalled()) {
            return true;
        } else {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Brak apllikacji Google Maps")
                    .setMessage("Do korzystania z funkcji szukania sklepów i restauracji, konieczna jest instalacja aplikacji Google Maps.")
                    .setPositiveButton("Zainstaluj", (dialog, which) -> openGoogleMapsInstaller())
                    .setNegativeButton("Anuluj", null)
                    .show();
            return false;
        }
    }

    public boolean isGoogleMapsInstalled() {
        try
        {
            requireActivity().getPackageManager().getApplicationInfo("com.google.android.apps.maps", 0);
            return true;
        }
        catch(PackageManager.NameNotFoundException e)
        {
            return false;
        }
    }

    private void openGoogleMapsInstaller() {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.android.apps.maps"));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Nie odnaleziono sklepu Google Play", Toast.LENGTH_LONG).show();
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
                3000,
                0,
                this
        );

        statusText.setText("Status:\nTrwa wyszukiwanie lokalizacji...\nJeśli to trwa zbyt długo, upewnij się, że włączyłeś usługę lokalizacji w telefonie.");
        requestPermissionsButton.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        if (requestCode == locationRequestId) {
            if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                onLocationPermissionDenied();
            } else {
                requestPermissionsButton.setVisibility(View.VISIBLE);
                initLocation();
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        lastLocation = location;
        restaurantsButton.setEnabled(true);
        groceriesButton.setEnabled(true);
        statusText.setText("Status:\nOdnaleziono lokalizację!");
    }

    private void onLocationPermissionDenied() {
        statusText.setText("Status:\nBrak pozwolenia na użycie lokalizacji...");
        requestPermissionsButton.setVisibility(View.VISIBLE);

        restaurantsButton.setEnabled(false);
        groceriesButton.setEnabled(false);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
}