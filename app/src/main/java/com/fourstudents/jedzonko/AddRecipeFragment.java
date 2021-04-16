package com.fourstudents.jedzonko;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.*;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.fourstudents.jedzonko.Database.Entities.Ingredient;
import com.fourstudents.jedzonko.Database.Entities.Product;
import com.fourstudents.jedzonko.Database.Entities.Recipe;
import com.fourstudents.jedzonko.Database.Relations.IngredientsWithRecipes;
import com.fourstudents.jedzonko.Database.RoomDB;
import com.google.mlkit.vision.common.InputImage;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class AddRecipeFragment extends Fragment {
    RecyclerView recyclerView;
    public AddRecipeFragment(){super(R.layout.fragment_add_recipe);}
    RoomDB database;
    List<Product> productList = new ArrayList<>();
    List<Product> ingredientList = new ArrayList<>();
    ProductRecyclerViewAdapter adapter1;
    ProductRecyclerViewAdapter adapter;
    Button addIngredientButton;
    Button addRecipeButton;
    Dialog dialog;
    RecyclerView recyclerView1;
    EditText title;
    EditText description;
    ImageView imageView;

    private void initToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.custom_toolbar);
        toolbar.setTitle("Dodaj przepis");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireFragmentManager().popBackStack();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (((MainActivity) requireActivity()).imageData != null) {
            byte[] bytes = ((MainActivity) requireActivity()).imageData;
            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            Bitmap scaledBmp = Bitmap.createScaledBitmap(bmp, bmp.getWidth()/10, bmp.getHeight()/10, true);
//            Bitmap scaledBmp = Bitmap.createScaledBitmap(bmp, imageViewWidth, imageViewHeight, true);
            Bitmap rotatedBmp = HarryHelperClass.rotateBitmapByAngle(scaledBmp, ((MainActivity) requireActivity()).imageRotation);

//            Log.i("Harry onResume", "bmp width="+bmp.getWidth());
//            Log.i("Harry onResume", "bmp height="+bmp.getHeight());
//            Log.i("Harry onResume", "byte size="+bytes.length);
//            Log.i("Harry onResume", "rotatedbmp width="+rotatedBmp.getWidth());
//            Log.i("Harry onResume", "rotatedbmp height="+rotatedBmp.getHeight());
            imageView.setImageBitmap(rotatedBmp);

        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        ((MainActivity) requireActivity()).imageData = null;
        ((MainActivity) requireActivity()).imageRotation = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initToolbar(view);

        imageView = view.findViewById(R.id.imageView);
        addIngredientButton = (Button) view.findViewById(R.id.addIngredientButton);
        addRecipeButton = view.findViewById(R.id.addRecipeButton);
        database = RoomDB.getInstance(getActivity());
        title = view.findViewById(R.id.editTextTitle);
        description = view.findViewById(R.id. editTextDescription);
        recyclerView1 = view.findViewById(R.id.recyclerView);


        int width = WindowManager.LayoutParams.MATCH_PARENT;
        int height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_add_ingredient);
        dialog.getWindow().setLayout(width,height);
        recyclerView=dialog.findViewById(R.id.recyclerViewProduct);

        productList.addAll(database.productDao().getAll());
        adapter= new ProductRecyclerViewAdapter(getContext(), productList, false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        view.findViewById(R.id.floatingActionButton_open_camera).setOnClickListener(v ->
                requireActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainFrameLayout, new CameraFragment(), "RecipeCameraView")
                        .addToBackStack("RecipeCameraView")
                        .commit()
        );

        addIngredientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                Button addProductButton = dialog.findViewById(R.id.addProductButton);
                Button addIngredientsButton = dialog.findViewById(R.id.addIngredientsButton);
                addIngredientsButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ingredientList=adapter.ingredientList;
                        adapter1= new ProductRecyclerViewAdapter(getContext(),ingredientList, true);
                        recyclerView1.setLayoutManager(new LinearLayoutManager(getContext()));
                        recyclerView1.setAdapter(adapter1);
                        recyclerView.setAdapter(adapter);
                        dialog.dismiss();
                    }
                });
                addProductButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        requireActivity()
                                .getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.mainFrameLayout, new AddProductFragment(), "AddProductFragment")
                                .addToBackStack("AddProductFragment")
                                .commit();
                        dialog.dismiss();
                        productList.clear();
                    }
                });
            }
        });
        addRecipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkData()) {
                    byte[] data;
                    BitmapDrawable bitmapDrawable = (BitmapDrawable) imageView.getDrawable();
                    Bitmap bmp = bitmapDrawable.getBitmap();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    data = stream.toByteArray();

                    Recipe recipe = new Recipe();
                    recipe.setTitle(title.getText().toString().trim());
                    recipe.setDescription(description.getText().toString().trim());
                    recipe.setData(data);
                    recipe.setAuthor("Me");
                    database.recipeDao().insert(recipe);
                    int recipeId = database.recipeDao().getLastId();
                    int size = ingredientList.size();

                    for (int i = 0; i < size; i++) {
                        Ingredient ingredient = new Ingredient();
                        ingredient.setProductId(ingredientList.get(i).getProductId());
                        ingredient.setRecipeId(recipeId);
                        database.ingredientDao().insert(ingredient);
                    }
                    title.setText("");
                    description.setText("");
                    ingredientList.clear();
                    adapter1.notifyItemRangeRemoved(0, size);
                    ((MainActivity) requireActivity()).imageData = null;
                    imageView.setImageResource(R.drawable.test_drawable);
                    Toast.makeText(getContext(), "Dodano przepis", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    boolean checkData(){
        if(title.getText().toString().equals("") || description.getText().toString().equals("")|| ingredientList.size()==0 ){
            Toast.makeText(getContext(), R.string.missing_input_data, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

}

