package com.fourstudents.jedzonko.Fragments.Recipe;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.fourstudents.jedzonko.Adapters.Shared.IngredientItemAdapter;
import com.fourstudents.jedzonko.Adapters.Shared.ProductAdapter;
import com.fourstudents.jedzonko.Adapters.Recipe.RecipeTagAdapter;
import com.fourstudents.jedzonko.Adapters.Recipe.TagAdapter;
import com.fourstudents.jedzonko.Database.Entities.Ingredient;
import com.fourstudents.jedzonko.Database.Entities.IngredientProductCrossRef;
import com.fourstudents.jedzonko.Database.Entities.Product;
import com.fourstudents.jedzonko.Database.Entities.Recipe;
import com.fourstudents.jedzonko.Database.Entities.RecipeTagCrossRef;
import com.fourstudents.jedzonko.Database.Entities.Tag;
import com.fourstudents.jedzonko.Database.RoomDB;
import com.fourstudents.jedzonko.Fragments.Shared.CameraFragment;
import com.fourstudents.jedzonko.Fragments.Shared.AddProductFragment;
import com.fourstudents.jedzonko.Other.HarryHelperClass;
import com.fourstudents.jedzonko.Other.IngredientItem;
import com.fourstudents.jedzonko.MainActivity;
import com.fourstudents.jedzonko.R;
import com.fourstudents.jedzonko.ViewModels.Shared.IngredientItemViewModel;
import com.fourstudents.jedzonko.ViewModels.Shared.TagViewModel;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class AddRecipeFragment extends Fragment implements ProductAdapter.OnProductListener, IngredientItemAdapter.OnIngredientItemListener, TagAdapter.OnTagListener, RecipeTagAdapter.OnRecipeTagListener {
    RoomDB database;
    List<Product> productList = new ArrayList<>();
    List<Tag> tagList= new ArrayList<>();
    IngredientItemAdapter ingredientItemAdapter;
    RecipeTagAdapter recipeTagAdapter;
    ProductAdapter productAdapter;
    TagAdapter tagAdapter;
    Button addIngredientButton;
    Button addTagButton;
    Dialog ingredientDialog;
    Dialog tagDialog;
    RecyclerView ingredientRV;
    RecyclerView productRV;
    RecyclerView tagRV;
    RecyclerView recipeTagRV;
    EditText title;
    EditText description;
    ImageView imageView;
    IngredientItemViewModel ingredientItemViewModel;
    TagViewModel tagViewModel;

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

                if(item.getItemId()==R.id.action_save_recipe)
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
        ingredientItemViewModel = new ViewModelProvider(requireActivity()).get(IngredientItemViewModel.class);
        tagViewModel = new ViewModelProvider(requireActivity()).get(TagViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initToolbar(view);

        imageView = view.findViewById(R.id.imageView);
        addIngredientButton = view.findViewById(R.id.showRecipeDescription);
        addTagButton = view.findViewById(R.id.addTagButton);
        database = RoomDB.getInstance(getActivity());
        title = view.findViewById(R.id.editTextTitle);
        description = view.findViewById(R.id. editTextDescription);
        ingredientRV = view.findViewById(R.id.ingredientRV);
        recipeTagRV = view.findViewById(R.id.showRecipeIngredientRV);

        int width = WindowManager.LayoutParams.MATCH_PARENT;
        int height = WindowManager.LayoutParams.WRAP_CONTENT;

        ingredientDialog = new Dialog(getContext());
        ingredientDialog.setContentView(R.layout.dialog_add_ingredient);
        ingredientDialog.setCanceledOnTouchOutside(true);
        ingredientDialog.getWindow().setLayout(width, height);

        tagDialog = new Dialog(getContext());
        tagDialog.setContentView(R.layout.dialog_add_tag);
        tagDialog.setCanceledOnTouchOutside(true);
        tagDialog.getWindow().setLayout(width, height);

        ingredientItemAdapter = new IngredientItemAdapter(getContext(), this);
        ingredientRV.setLayoutManager(new LinearLayoutManager(getContext()));
        ingredientRV.setAdapter(ingredientItemAdapter);

        recipeTagAdapter = new RecipeTagAdapter(getContext(),this);
        recipeTagRV.setLayoutManager(new LinearLayoutManager(getContext()));
        recipeTagRV.setAdapter(recipeTagAdapter);


        productList.addAll(database.productDao().getAll());
        productAdapter = new ProductAdapter(getContext(), productList, this);
        productRV = ingredientDialog.findViewById(R.id.productRV);
        productRV.setLayoutManager(new LinearLayoutManager(getContext()));
        productRV.setAdapter(productAdapter);

        tagList.addAll(database.tagDao().getAll());
        tagAdapter = new TagAdapter(getContext(), tagList, this );
        tagRV = tagDialog.findViewById(R.id.showRecipeIngredientRV);
        tagRV.setLayoutManager(new LinearLayoutManager(getContext()));
        tagRV.setAdapter(tagAdapter);

        ingredientItemViewModel.getIngredientItemList().observe(getViewLifecycleOwner(), ingredientItems -> {
            ingredientItemAdapter.submitList(ingredientItems);
            ingredientItemAdapter.notifyDataSetChanged();
        });

        tagViewModel.getTagList().observe(getViewLifecycleOwner(), tags -> {
            recipeTagAdapter.submitList(tags);
            recipeTagAdapter.notifyDataSetChanged();
        });


        view.findViewById(R.id.addPhotoButton).setOnClickListener(v -> {
//            dialog.dismiss();
            productList.clear();
            tagList.clear();
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
                ingredientDialog.show();
                Button addProductButton = ingredientDialog.findViewById(R.id.addProductButton);
                Button addIngredientsButton = ingredientDialog.findViewById(R.id.addIngredientsButton);
                addIngredientsButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ingredientDialog.dismiss();
                    }
                });
                addProductButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        productList.clear();
                        ingredientDialog.dismiss();
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

        addTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tagDialog.show();
                Button addTagButton = tagDialog.findViewById(R.id.addTagButton);
                Button addTagsButton = tagDialog.findViewById(R.id.addTagsButton);
                addTagsButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tagDialog.dismiss();
                    }
                });
                addTagButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    tagList.clear();
                    tagDialog.dismiss();
                        requireActivity()
                                .getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.mainFrameLayout, new AddTagFragment(), "AddTagFragment")
                                .addToBackStack("AddTagFragment")
                                .commit();

                    }
                });
            }
        });

    }
    boolean checkData(){
        if(title.getText().toString().equals("") || description.getText().toString().equals("")|| ingredientItemViewModel.getIngredientItemsListSize()==0 || !ingredientItemViewModel.isQuantityFilled() ){
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
            recipe.setRemoteId(-1);
            database.recipeDao().insert(recipe);
            int recipeId = database.recipeDao().getLastId();

            List<IngredientItem> ingredientItems= ingredientItemViewModel.getIngredientItemsList();

            for (IngredientItem ingredientItem: ingredientItems) {
                Ingredient ingredient = new Ingredient();
                ingredient.setRecipeOwnerId(recipeId);
                ingredient.setQuantity(ingredientItem.getQuantity());
                database.ingredientDao().insert(ingredient);

                IngredientProductCrossRef ingredientProductCrossRef = new IngredientProductCrossRef();
                ingredientProductCrossRef.setIngredientId(database.ingredientDao().getLastId());
                ingredientProductCrossRef.setProductId(ingredientItem.product.getProductId());
                database.ingredientDao().insertIngredientWithProduct(ingredientProductCrossRef);
            }
            List<Tag> tags = tagViewModel.getTagsList();

            for (Tag tag: tags) {
                RecipeTagCrossRef recipeTagCrossRef= new RecipeTagCrossRef();
                recipeTagCrossRef.setTagId(tag.getTagId());
                recipeTagCrossRef.setRecipeId(recipeId);
                database.recipeDao().insertRecipeWithTag(recipeTagCrossRef);
            }

            title.setText("");
            description.setText("");
            ingredientItemViewModel.clearIngredientItemList();
            tagViewModel.clearTagList();
            ((MainActivity) requireActivity()).imageData = null;
            imageView.setImageResource(R.drawable.test_drawable);
            Toast.makeText(getContext(), "Dodano przepis", Toast.LENGTH_SHORT).show();
            getParentFragmentManager().popBackStack();
        }
        productRV.setAdapter(productAdapter);
    }

    @Override
    public void onProductClick(int position) {
        IngredientItem ingredientItem= new IngredientItem();
             ingredientItem.product = productList.get(position);
        if (ingredientItemViewModel.hasIngredientItem(ingredientItem)) {
            Toast.makeText(getContext(), "Produkt już jest na liście", Toast.LENGTH_SHORT).show();
        } else {
            ingredientItemViewModel.addIngredientItem(ingredientItem);
            Toast.makeText(getContext(), "Dodano produkt", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onIngredientItemDeleteClick(int position) {
       IngredientItem ingredientItem = ingredientItemViewModel.getIngredientItem(position);
       ingredientItemViewModel.removeIngredientItem(ingredientItem);
    }

    @Override
    public void onRecipeTagDeleteClick(int position) {
        Tag tag =tagViewModel.getTag(position);
        tagViewModel.removeTag(tag);
    }

    @Override
    public void onTextChange(int position, CharSequence s) {
        IngredientItem ingredientItem = ingredientItemViewModel.getIngredientItem(position);
        if (ingredientItem != null) {
            ingredientItem.setQuantity(s.toString());
        }
    }

    @Override
    public void onTagClick(int position) {
        Tag tag = tagList.get(position);
        if (tagViewModel.hasTag(tag)) {
            Toast.makeText(getContext(), "Tag już jest na liście", Toast.LENGTH_SHORT).show();
        } else {
           tagViewModel.addTag(tag);
            Toast.makeText(getContext(), "Dodano tag", Toast.LENGTH_SHORT).show();
        }
    }
}

