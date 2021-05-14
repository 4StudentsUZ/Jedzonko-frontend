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

import com.fourstudents.jedzonko.Adapters.Recipe.RecipeTagAdapter;
import com.fourstudents.jedzonko.Adapters.Recipe.TagAdapter;
import com.fourstudents.jedzonko.Adapters.Shared.IngredientItemAdapter;
import com.fourstudents.jedzonko.Adapters.Shared.ProductAdapter;
import com.fourstudents.jedzonko.Database.Entities.Ingredient;
import com.fourstudents.jedzonko.Database.Entities.IngredientProductCrossRef;
import com.fourstudents.jedzonko.Database.Entities.Product;
import com.fourstudents.jedzonko.Database.Entities.Recipe;
import com.fourstudents.jedzonko.Database.Entities.RecipeTagCrossRef;
import com.fourstudents.jedzonko.Database.Entities.Tag;
import com.fourstudents.jedzonko.Database.Relations.IngredientsWithProducts;
import com.fourstudents.jedzonko.Database.Relations.RecipeWithIngredientsAndProducts;
import com.fourstudents.jedzonko.Database.Relations.RecipesWithTags;
import com.fourstudents.jedzonko.Database.RoomDB;
import com.fourstudents.jedzonko.Fragments.Shared.AddProductFragment;
import com.fourstudents.jedzonko.Fragments.Shared.CameraFragment;
import com.fourstudents.jedzonko.MainActivity;
import com.fourstudents.jedzonko.Other.HarryHelperClass;
import com.fourstudents.jedzonko.Other.IngredientItem;
import com.fourstudents.jedzonko.R;
import com.fourstudents.jedzonko.ViewModels.Shared.IngredientItemViewModel;
import com.fourstudents.jedzonko.ViewModels.Shared.TagViewModel;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;


public class EditRecipeFragment extends Fragment implements ProductAdapter.OnProductListener, IngredientItemAdapter.OnIngredientItemListener, TagAdapter.OnTagListener, RecipeTagAdapter.OnRecipeTagListener {
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
    long recipeId;


    public EditRecipeFragment() {super(R.layout.fragment_edit_recipe);}

    private void initToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.custom_toolbar);
        toolbar.setTitle("Edytuj przepis");
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
                    actionEditRecipe();
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

        Bundle bundle = getArguments();
        recipeId= bundle.getLong("recipeId");

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
        getRecipeData();
    }

    private void actionEditRecipe() {
        if (checkData()) {
            database.recipeDao().deleteIngredients(recipeId);
            database.recipeDao().deleteTags(recipeId);
            Recipe updatedRecipe = new Recipe();
            List<Recipe> recipes =database.recipeDao().getAll();
            for (Recipe recipe:recipes) {
                if(recipe.getRecipeId()==recipeId){
                    updatedRecipe=recipe;
                    break;
                }
            }

            byte[] data;
            BitmapDrawable bitmapDrawable = (BitmapDrawable) imageView.getDrawable();
            Bitmap bmp = bitmapDrawable.getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            data = stream.toByteArray();


            updatedRecipe.setTitle(title.getText().toString().trim());
            updatedRecipe.setDescription(description.getText().toString().trim());
            updatedRecipe.setData(data);
            database.recipeDao().update(updatedRecipe);
            int recipeId = database.recipeDao().getLastId();

            List<IngredientItem> ingredientItems= ingredientItemViewModel.getIngredientItemsList();

            for (IngredientItem ingredientItem: ingredientItems) {
                Ingredient ingredient = new Ingredient();
                ingredient.setRecipeOwnerId(recipeId);
                ingredient.setQuantity(ingredientItem.quantity);
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
            Toast.makeText(getContext(), "Zapisano zmiany", Toast.LENGTH_SHORT).show();
            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.mainFrameLayout, new RecipeFragment(), "RecipeFragment")
                    .addToBackStack("RecipeFragment")
                    .commit();
        }
        productRV.setAdapter(productAdapter);

    }
    boolean checkData(){
        if(title.getText().toString().equals("") || description.getText().toString().equals("")|| ingredientItemViewModel.getIngredientItemsListSize()==0 || !ingredientItemViewModel.isQuantityFilled() ){
            Toast.makeText(getContext(), R.string.missing_input_data, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    private void getRecipeData(){
        List<RecipesWithTags> recipesWithTags = database.recipeDao().getRecipesWithTags();
        List<RecipeWithIngredientsAndProducts> recipesWithIngredientsAndProducts  =database.recipeDao().getRecipesWithIngredientsAndProducts();
        for (RecipesWithTags recipeWithTag: recipesWithTags) {
            if(recipeWithTag.recipe.getRecipeId()==recipeId){
                title.setText(recipeWithTag.recipe.getTitle());
                description.setText(recipeWithTag.recipe.getDescription());
                byte[] data = recipeWithTag.recipe.getData();
                Bitmap recipePhoto = BitmapFactory.decodeByteArray(data,0,data.length);
                imageView.setImageBitmap(recipePhoto);
                for (Tag tag: recipeWithTag.tags) {
                    tagViewModel.addTag(tag);
                }
                break;
            }
        }

        for (RecipeWithIngredientsAndProducts recipeWithIngredientsAndProducts: recipesWithIngredientsAndProducts) {
            if(recipeWithIngredientsAndProducts.recipe.getRecipeId()==recipeId){
                for (IngredientsWithProducts ingredientsWithProducts: recipeWithIngredientsAndProducts.ingredients) {
                    IngredientItem ingredientItem = new IngredientItem();
                    ingredientItem.setQuantity(ingredientsWithProducts.ingredient.getQuantity());
                    for (Product product: ingredientsWithProducts.products) {
                        ingredientItem.setProduct(product);
                        ingredientItemViewModel.addIngredientItem(ingredientItem);
                    }
                }
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        ingredientItemViewModel.clearIngredientItemList();
        tagViewModel.clearTagList();
    }

    @Override
    public void onRecipeTagDeleteClick(int position) {
        Tag tag =tagViewModel.getTag(position);
        tagViewModel.removeTag(tag);
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

    @Override
    public void onIngredientItemDeleteClick(int position) {
        IngredientItem ingredientItem = ingredientItemViewModel.getIngredientItem(position);
        ingredientItem.setQuantity("");
        ingredientItemViewModel.removeIngredientItem(ingredientItem);
    }

    @Override
    public void onTextChange(int position, CharSequence s) {
        IngredientItem ingredientItem = ingredientItemViewModel.getIngredientItem(position);
        if (ingredientItem != null) {
            ingredientItem.setQuantity(s.toString());
        }
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
}