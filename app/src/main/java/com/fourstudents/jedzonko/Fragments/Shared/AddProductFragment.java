package com.fourstudents.jedzonko.Fragments.Shared;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
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
import java.io.FileNotFoundException;
import java.io.InputStream;

import static android.app.Activity.RESULT_OK;
import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;


public class AddProductFragment extends Fragment {
    private static final int RESULT_SELECT_FROM_GALLERY = 1;

    EditText name;
    EditText barcode;
    Button makePhotoButton;
    Button photoFromGalleryButton;
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
            ////Bitmap scaledBmp = Bitmap.createScaledBitmap(bmp, bmp.getWidth()/10, bmp.getHeight()/10, true);
Bitmap scaledBmp = Bitmap.createScaledBitmap(bmp, 256, 256, true);
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
        makePhotoButton = view.findViewById(R.id.addImageCameraButton);
        photoFromGalleryButton = view.findViewById(R.id.addImageGalleryButton);
        scanBarcodeButton = view.findViewById(R.id.scanBarcodeButton);
        imageView = view.findViewById(R.id.imageView);

        makePhotoButton.setOnClickListener(v -> {
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

        photoFromGalleryButton.setOnClickListener(v -> {
            ((MainActivity) requireActivity()).productImageData = null;
            ((MainActivity) requireActivity()).productImageRotation = null;

            Intent photoPickerIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, RESULT_SELECT_FROM_GALLERY);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_SELECT_FROM_GALLERY) {
            if (resultCode == RESULT_OK) {
                try {
                    assert data != null;
                    final Uri imageUri = data.getData();
                    assert imageUri != null;

                    requireActivity().getContentResolver().takePersistableUriPermission(imageUri, FLAG_GRANT_READ_URI_PERMISSION);
                    final InputStream imageStream = requireActivity().getContentResolver().openInputStream(imageUri);
                    final Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                    updatePicture(bitmap);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(requireActivity(), R.string.couldnt_load_image_from_gallery, Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(requireActivity(), R.string.couldnt_load_image_from_gallery,Toast.LENGTH_LONG).show();
            }
        }
    }

    private void updatePicture(Bitmap bmp) {
        //Bitmap scaledBmp = Bitmap.createScaledBitmap(bmp, bmp.getWidth()/10, bmp.getHeight()/10, true);
Bitmap scaledBmp = Bitmap.createScaledBitmap(bmp, 256, 256, true);
        imageView.setImageBitmap(scaledBmp);
    }
}