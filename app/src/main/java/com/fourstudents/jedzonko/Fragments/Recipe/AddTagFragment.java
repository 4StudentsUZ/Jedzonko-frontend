package com.fourstudents.jedzonko.Fragments.Recipe;

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
import android.widget.Toast;

import com.fourstudents.jedzonko.Database.Entities.Product;
import com.fourstudents.jedzonko.Database.Entities.Tag;
import com.fourstudents.jedzonko.Database.RoomDB;
import com.fourstudents.jedzonko.R;


public class AddTagFragment extends Fragment {
    EditText name;
    Button addProductButton;
    RoomDB database;

    public AddTagFragment(){super(R.layout.fragment_add_tag);}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_tag, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar = getActivity().findViewById(R.id.custom_toolbar);
        toolbar.setTitle("Dodaj tag");
        name = view.findViewById(R.id.tagNameEditText);
        addProductButton = view.findViewById(R.id.addTagButton);
        database = RoomDB.getInstance(getActivity());
        addProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkData()){
                    Tag tag = new Tag();
                    tag.setName(name.getText().toString().trim());
                    database.tagDao().insert(tag);
                    name.setText("");
                    Toast.makeText(getContext(), "Dodano produkt",Toast.LENGTH_SHORT).show();
                    requireFragmentManager().popBackStack();
                }
            }
        });
    }
    boolean checkData(){
        if(name.getText().toString().equals("")){
            Toast.makeText(getContext(), R.string.missing_input_data, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}