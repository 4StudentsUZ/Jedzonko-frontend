package com.fourstudents.jedzonko.Fragments.Shared;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.fourstudents.jedzonko.Database.Entities.Product;
import com.fourstudents.jedzonko.Database.RoomDB;
import com.fourstudents.jedzonko.MainActivity;
import com.fourstudents.jedzonko.Other.HarryHelperClass;
import com.fourstudents.jedzonko.R;

import java.io.ByteArrayOutputStream;


public class AddProductFragment extends Fragment {
    EditText name;
    EditText barcode;
    Button addImageButton;
    Button scanBarcodeButton;
    RoomDB database;
    ImageView imageView;

    MainActivity activity;

    public AddProductFragment() {
        super(R.layout.fragment_add_product);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (activity.productImageData != null) {
            byte[] bytes = activity.productImageData;
            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            Bitmap scaledBmp = Bitmap.createScaledBitmap(bmp, bmp.getWidth()/10, bmp.getHeight()/10, true);
            Bitmap rotatedBmp = HarryHelperClass.rotateBitmapByAngle(scaledBmp, activity.productImageRotation);
            imageView.setImageBitmap(rotatedBmp);
        }
        if (activity.scannedBarcode != null) {
            barcode.setText(activity.scannedBarcode);
            Toast.makeText(requireContext(), R.string.camera_barcode_scanned, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activity.productImageData = null;
        activity.productImageRotation = null;
        activity.scannedBarcode = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity = ((MainActivity) requireActivity());

        initToolbar(view);
        initViews(view);
        database = RoomDB.getInstance(requireActivity());
    }

    private void initToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.custom_toolbar);
        toolbar.setTitle("Dodaj produkt");
        toolbar.inflateMenu(R.menu.add_product);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(v -> getParentFragmentManager().popBackStack());
        toolbar.setOnMenuItemClickListener(clickedMenuItem -> {
            if (clickedMenuItem.getItemId() == R.id.action_save_recipe)
                actionSaveRecipe();
            return false;
        });
    }

    private void initViews(View view) {
        name = view.findViewById(R.id.productNameEditText);
        barcode = view.findViewById(R.id.productBarcodeEditText);
        addImageButton = view.findViewById(R.id.addImageButton);
        scanBarcodeButton = view.findViewById(R.id.scanBarcodeButton);
        imageView = view.findViewById(R.id.imageView);

        addImageButton.setOnClickListener(v -> {
            CameraFragment cameraFragment = new CameraFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("mode", HarryHelperClass.CameraModes.Product);
            cameraFragment.setArguments(bundle);

            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.mainFrameLayout, cameraFragment, "ProductCameraView")
                    .addToBackStack("ProductCameraView")
                    .commit();
        });

        scanBarcodeButton.setOnClickListener(v -> {
            CameraFragment cameraFragment = new CameraFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("mode", HarryHelperClass.CameraModes.Barcode);
            cameraFragment.setArguments(bundle);

            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.mainFrameLayout, cameraFragment, "ProductCameraView")
                    .addToBackStack("ProductCameraView")
                    .commit();
        });

    }

    private void actionSaveRecipe() {
        if(checkData()){
            byte[] data;
            BitmapDrawable bitmapDrawable = (BitmapDrawable) imageView.getDrawable();
            Bitmap bmp = bitmapDrawable.getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            data = stream.toByteArray();

            Product product = new Product();
            product.setName(name.getText().toString().trim());
            product.setBarcode(barcode.getText().toString().trim());
            product.setData(data);


            database.productDao().insert(product);
            name.setText("");
            barcode.setText("");
            Toast.makeText(getContext(), "Dodano produkt",Toast.LENGTH_SHORT).show();
            getParentFragmentManager().popBackStack();
        }
    }

    boolean checkData(){
        if(name.getText().toString().equals("")|| barcode.getText().toString().equals("")){
            Toast.makeText(getContext(), R.string.missing_input_data, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}