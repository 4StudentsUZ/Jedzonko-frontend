package com.fourstudents.jedzonko.Fragments.ShoppingList;

import android.app.Dialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fourstudents.jedzonko.Adapters.Shared.IngredientItemAdapter;
import com.fourstudents.jedzonko.Adapters.Shared.ProductAdapter;
import com.fourstudents.jedzonko.Database.Entities.Product;
import com.fourstudents.jedzonko.Database.Entities.Shopitem;
import com.fourstudents.jedzonko.Database.Entities.ShopitemProductCrossRef;
import com.fourstudents.jedzonko.Database.Entities.Shopping;
import com.fourstudents.jedzonko.Database.Relations.ShopitemsWithProducts;
import com.fourstudents.jedzonko.Database.Relations.ShoppingWithShopitemsAndProducts;
import com.fourstudents.jedzonko.Database.RoomDB;
import com.fourstudents.jedzonko.Fragments.Shared.AddProductFragment;
import com.fourstudents.jedzonko.Other.IngredientItem;
import com.fourstudents.jedzonko.R;
import com.fourstudents.jedzonko.ViewModels.Shared.IngredientItemViewModel;

import java.util.ArrayList;
import java.util.List;

public class EditShoppingListFragment extends Fragment implements ProductAdapter.OnProductListener, IngredientItemAdapter.OnIngredientItemListener {
    RoomDB database;
    List<Product> productList = new ArrayList<>();
    IngredientItemAdapter ingredientItemAdapter;
    ProductAdapter productAdapter;
    Button addIngredientButton;
    Dialog ingredientDialog;
    RecyclerView ingredientRV;
    RecyclerView productRV;
    EditText title;
    IngredientItemViewModel ingredientItemViewModel;
    long shoppingId;


    public EditShoppingListFragment() {super(R.layout.fragment_edit_shopping_list);}

    private void initToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.custom_toolbar);
        toolbar.setTitle("Edytuj listę zakupów");
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

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        ingredientItemViewModel = new ViewModelProvider(requireActivity()).get(IngredientItemViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initToolbar(view);

        Bundle bundle = getArguments();
        shoppingId= bundle.getLong("shoppingId");

        addIngredientButton = view.findViewById(R.id.addShopItem);
        database = RoomDB.getInstance(getActivity());
        title = view.findViewById(R.id.editTextTitle);
        ingredientRV = view.findViewById(R.id.ingredientRV);

        int width = WindowManager.LayoutParams.MATCH_PARENT;
        int height = WindowManager.LayoutParams.WRAP_CONTENT;

        ingredientDialog = new Dialog(getContext());
        ingredientDialog.setContentView(R.layout.dialog_add_ingredient);
        ingredientDialog.setCanceledOnTouchOutside(true);
        ingredientDialog.getWindow().setLayout(width, height);

        ingredientItemAdapter = new IngredientItemAdapter(getContext(), this);
        ingredientRV.setLayoutManager(new LinearLayoutManager(getContext()));
        ingredientRV.setAdapter(ingredientItemAdapter);

        productList.addAll(database.productDao().getAll());
        productAdapter = new ProductAdapter(getContext(), productList, this);
        productRV = ingredientDialog.findViewById(R.id.productRV);
        productRV.setLayoutManager(new LinearLayoutManager(getContext()));
        productRV.setAdapter(productAdapter);

        ingredientItemViewModel.getIngredientItemList().observe(getViewLifecycleOwner(), ingredientItems -> {
            ingredientItemAdapter.submitList(ingredientItems);
            ingredientItemAdapter.notifyDataSetChanged();
        });

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
        getShoppingListData();
    }

    private void actionEditRecipe() {
        if (checkData()) {
            database.shoppingDao().deleteShopitems(shoppingId);
            Shopping updatedShopping = new Shopping();
            List<Shopping> shoppings =database.shoppingDao().getAll();
            for (Shopping list:shoppings) {
                if(list.getShoppingId()==shoppingId){
                    updatedShopping=list;
                    break;
                }
            }

            updatedShopping.setName(title.getText().toString().trim());
            database.shoppingDao().update(updatedShopping);

            List<IngredientItem> ingredientItems= ingredientItemViewModel.getIngredientItemsList();

            for (IngredientItem ingredientItem: ingredientItems) {
                Shopitem shopitem = new Shopitem();
                shopitem.setShoppingOwnerId(shoppingId);
                shopitem.setQuantity(ingredientItem.quantity);
                database.shopitemDao().insert(shopitem);

                ShopitemProductCrossRef shopitemProductCrossRef = new ShopitemProductCrossRef();
                shopitemProductCrossRef.setShopitemId(database.shopitemDao().getLastId());
                shopitemProductCrossRef.setProductId(ingredientItem.product.getProductId());
                database.shopitemDao().insertShopitemWithProduct(shopitemProductCrossRef);
            }

            title.setText("");
            ingredientItemViewModel.clearIngredientItemList();

            Toast.makeText(getContext(), "Zapisano zmiany", Toast.LENGTH_SHORT).show();
            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.mainFrameLayout, new ShoppingListFragment(), "ShoppingListFragment")
                    .addToBackStack("ShoppingListFragment")
                    .commit();
        }
        productRV.setAdapter(productAdapter);

    }
    boolean checkData(){
        if(title.getText().toString().equals("") || ingredientItemViewModel.getIngredientItemsListSize()==0 || !ingredientItemViewModel.isQuantityFilled() ){
            Toast.makeText(getContext(), R.string.missing_input_data, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void getShoppingListData(){
        List<ShoppingWithShopitemsAndProducts> shoppingWithShopitemsAndProducts =  database.shoppingDao().getShoppingsWithShopitemsAndProducts();
        for(ShoppingWithShopitemsAndProducts list : shoppingWithShopitemsAndProducts){
            if(list.shopping.getShoppingId() == shoppingId){
                title.setText(list.shopping.getName());
                for(ShopitemsWithProducts shopitemsWithProducts: list.shopitems)
                {
                    IngredientItem ingredientItem = new IngredientItem();
                    ingredientItem.quantity = shopitemsWithProducts.shopitem.getQuantity();
                    for(Product product: shopitemsWithProducts.products){
                        ingredientItem.setProduct(product);
                        ingredientItemViewModel.addIngredientItem(ingredientItem);
                        //ingredientItemList.add(ingredientItem);
                    }
                }
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        ingredientItemViewModel.clearIngredientItemList();
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