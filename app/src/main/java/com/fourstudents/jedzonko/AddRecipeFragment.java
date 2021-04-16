package com.fourstudents.jedzonko;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.fourstudents.jedzonko.Database.Entities.Ingredient;
import com.fourstudents.jedzonko.Database.Entities.Product;
import com.fourstudents.jedzonko.Database.Entities.Recipe;
import com.fourstudents.jedzonko.Database.RoomDB;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class AddRecipeFragment extends Fragment {
    RoomDB database;
    List<Product> ingredientList = new ArrayList<>();
    List<Product> productList = new ArrayList<>();
    ProductRecyclerViewAdapter ingredientAdapter;
    ProductRecyclerViewAdapter productAdapter;
    Button addIngredientButton;
    Dialog dialog;
    RecyclerView ingredientRV;
    RecyclerView productRV;
    EditText title;
    EditText description;
    ImageView imageView;

    public AddRecipeFragment(){super(R.layout.fragment_add_recipe);}

    private void initToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.custom_toolbar);
        toolbar.setTitle("Dodaj przepis");
        toolbar.inflateMenu(R.menu.add_recipe);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireFragmentManager().popBackStack();
            }
        });
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if(item.getItemId()==R.id.action_save_note)
                {
                    actionSaveRecipe();
                }


                return false;
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initToolbar(view);

        imageView = view.findViewById(R.id.imageView);
        addIngredientButton = view.findViewById(R.id.addIngredientButton);
        database = RoomDB.getInstance(getActivity());
        title = view.findViewById(R.id.editTextTitle);
        description = view.findViewById(R.id. editTextDescription);
        ingredientRV = view.findViewById(R.id.ingredientRV);

        int width = WindowManager.LayoutParams.MATCH_PARENT;
        int height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_add_ingredient);
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setLayout(width, height);

        productList.addAll(database.productDao().getAll());
        productAdapter = new ProductRecyclerViewAdapter(getContext(), productList, false);
        productRV = dialog.findViewById(R.id.productRV);
        productRV.setLayoutManager(new LinearLayoutManager(getContext()));
        productRV.setAdapter(productAdapter);

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
//                if (productAdapter.productList.size() == 0) {
//                }
//                productAdapter.notifyDataSetChanged();
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
//                productList.clear();
//                productAdapter.notifyDataSetChanged();
            }
        });

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
//                productList.clear();
//                ingredientAdapter.notifyDataSetChanged();
            }
        });

        view.findViewById(R.id.floatingActionButton_open_camera).setOnClickListener(v -> {
//            dialog.dismiss();
            productList.clear();
            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.mainFrameLayout, new CameraFragment(), "RecipeCameraView")
                    .addToBackStack("RecipeCameraView")
                    .commit();
                }

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
                        ingredientList = productAdapter.ingredientList;
                        ingredientAdapter = new ProductRecyclerViewAdapter(getContext(), ingredientList, true);
                        ingredientRV.setLayoutManager(new LinearLayoutManager(getContext()));
                        ingredientRV.setAdapter(ingredientAdapter);
                        productRV.setAdapter(productAdapter);
                        dialog.dismiss();
                    }
                });
                addProductButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        productList.clear();
                        dialog.dismiss();
                        requireActivity()
                                .getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.mainFrameLayout, new AddProductFragment(), "AddProductFragment")
                                .addToBackStack("AddProductFragment")
                                .commit();
                    }
                });
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

    private void actionSaveRecipe() {
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
            ingredientAdapter.notifyItemRangeRemoved(0, size);
            ((MainActivity) requireActivity()).imageData = null;
            imageView.setImageResource(R.drawable.test_drawable);
            Toast.makeText(getContext(), "Dodano przepis", Toast.LENGTH_SHORT).show();
        }
    }

}

