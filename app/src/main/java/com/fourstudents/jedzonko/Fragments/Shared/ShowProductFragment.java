package com.fourstudents.jedzonko.Fragments.Shared;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.fourstudents.jedzonko.Adapters.Shared.ShowIngredientItemAdapter;
import com.fourstudents.jedzonko.Database.Entities.Product;
import com.fourstudents.jedzonko.Database.RoomDB;
import com.fourstudents.jedzonko.MainActivity;
import com.fourstudents.jedzonko.Other.HarryHelperClass;
import com.fourstudents.jedzonko.R;


public class ShowProductFragment extends Fragment implements ShowIngredientItemAdapter.OnIngredientItemListener{
    RoomDB database;
    Product product;
    MainActivity activity;
    String barcode;
    Button scanBarcodeButton;
    public ShowProductFragment(){super(R.layout.fragment_show_product);}

    @Override
    public void onResume() {
        super.onResume();

        if (activity.scannedBarcode != null) {
            if(barcode.equals(activity.scannedBarcode)){
                Toast.makeText(requireContext(),"Kod jest identyczny", Toast.LENGTH_LONG).show();
            }else
            {
                Toast.makeText(requireContext(),"Kod jest inny", Toast.LENGTH_LONG).show();
            }
            activity.scannedBarcode = null;
        }
    }
    private void initToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.custom_toolbar);
        toolbar.setTitle("Produkt");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });
        activity = ((MainActivity) requireActivity());

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return false;
            }
        });
    }

    private void initViews(View view) {
        scanBarcodeButton = view.findViewById(R.id.compareButton);

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_show_product, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        product = (Product) bundle.getSerializable("product");
        initToolbar(view);
        initViews(view);
        barcode = product.getBarcode();
        database = RoomDB.getInstance(getActivity());
        TextView productTitle = view.findViewById(R.id.showProductTitle);
        ImageView productImage = view.findViewById(R.id.imageView);
        productTitle.setText(product.getName());
        byte[] data = product.getData();
        Bitmap productPhoto = BitmapFactory.decodeByteArray(data,0,data.length);
        productImage.setImageBitmap(productPhoto);


    }

    @Override
    public void onIngredientItemClick(int position) {

    }
}