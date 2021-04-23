package com.fourstudents.jedzonko;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fourstudents.jedzonko.Database.Entities.Product;
import com.fourstudents.jedzonko.Database.Entities.Shopping;
import com.fourstudents.jedzonko.ViewModels.ProductViewModel;
import com.fourstudents.jedzonko.ViewModels.ShoppingViewModel;

import java.util.List;

public class AddShoppingListFragment extends Fragment {
    EditText editTextName;
    RecyclerView shoppingProductRV;
    Button editShoppingProductsBtn;
    Dialog editShoppingProductDialog;
    ProductRecyclerViewAdapter2VM productAdapter;
    RecyclerView productRV;

    public AddShoppingListFragment(){super(R.layout.fragment_add_slist);}

    private void initToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.custom_toolbar);
        toolbar.setTitle(R.string.title_add_slist);
        toolbar.inflateMenu(R.menu.add_recipe);
        toolbar.getMenu().getItem(0).setTitle(R.string.save_button);
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

                if (item.getItemId() == R.id.action_save_note)
                {
                    menuSaveButtonPressed();
                }
                return false;

            }
        });
    }

    private void initLayout(View view) {
        editTextName = view.findViewById(R.id.editTextName);
        shoppingProductRV = view.findViewById(R.id.shoppingProductRV);
        editShoppingProductsBtn = view.findViewById(R.id.editShoppingProductsBtn);
        editShoppingProductsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editShoppingProductDialog.show();
                Button addProductButton = editShoppingProductDialog.findViewById(R.id.addProductButton);
                Button addIngredientsButton = editShoppingProductDialog.findViewById(R.id.addIngredientsButton);
                addIngredientsButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editShoppingProductDialog.dismiss();
                    }
                });
                addProductButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        productList.clear();
                        editShoppingProductDialog.dismiss();
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

    private void initDialog() {
        editShoppingProductDialog = new Dialog(getContext());
        editShoppingProductDialog.setContentView(R.layout.dialog_add_ingredient);
        editShoppingProductDialog.setCanceledOnTouchOutside(true);
        editShoppingProductDialog.getWindow()
                .setLayout(
                        WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.WRAP_CONTENT
                );
    }

    private void initRV() {
        productAdapter = new ProductRecyclerViewAdapter2VM();
        productRV = editShoppingProductDialog.findViewById(R.id.productRV);
        productRV.setAdapter(productAdapter);
        productRV.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void initVM() {
        ProductViewModel productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);
        productViewModel.getAllLiveDataProductList().observe(getViewLifecycleOwner(), new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> products) {
                if (products != null) {
                    productAdapter.setProductList(products);
                }
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initToolbar(view);
        initLayout(view);
        initDialog();
        initRV();
        initVM();
    }

    boolean checkData(){
        return true;
    }

    private void menuSaveButtonPressed() {

    }

}

