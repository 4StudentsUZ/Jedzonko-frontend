package com.fourstudents.jedzonko;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fourstudents.jedzonko.Database.Entities.Product;
import com.fourstudents.jedzonko.Database.RoomDB;

import java.util.ArrayList;
import java.util.List;


public class AddProductFragment extends Fragment {
    EditText name;
    EditText barcode;
    Button addProductButton;
    RoomDB database;

    public AddProductFragment(){super(R.layout.fragment_add_product);}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_product, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar = getActivity().findViewById(R.id.custom_toolbar);
        toolbar.setTitle("Dodaj produkt");
        name = view.findViewById(R.id.productNameEditText);
        barcode = view.findViewById(R.id.productBarcodeEditText);
        addProductButton = view.findViewById(R.id.addProductButton);
        database = RoomDB.getInstance(getActivity());
        byte[] data = { 0x0F, 0x10, 0x0F, 0x11 };
        addProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkData()){
                    Product product = new Product();
                    product.setName(name.getText().toString().trim());
                    product.setBarcode(barcode.getText().toString().trim());
                    product.setData(data);
                    database.productDao().insert(product);
                    name.setText("");
                    barcode.setText("");
                    Toast.makeText(getContext(), "Dodano produkt",Toast.LENGTH_SHORT).show();
                    requireFragmentManager().popBackStack();
                }
            }
        });
    }
    boolean checkData(){
        if(name.getText().toString().equals("")|| barcode.getText().toString().equals("")){
            Toast.makeText(getContext(), R.string.missing_input_data, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}