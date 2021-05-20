package com.fourstudents.jedzonko.Fragments.ShoppingList;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fourstudents.jedzonko.Adapters.Shared.ShowIngredientItemAdapter;
import com.fourstudents.jedzonko.Database.Entities.Product;
import com.fourstudents.jedzonko.Database.Entities.Shopping;
import com.fourstudents.jedzonko.Database.Relations.ShopitemsWithProducts;
import com.fourstudents.jedzonko.Database.Relations.ShoppingWithShopitemsAndProducts;
import com.fourstudents.jedzonko.Database.RoomDB;
import com.fourstudents.jedzonko.Fragments.Shared.CameraFragment;
import com.fourstudents.jedzonko.Fragments.Shared.ShowProductFragment;
import com.fourstudents.jedzonko.Other.HarryHelperClass;
import com.fourstudents.jedzonko.Other.IngredientItem;
import com.fourstudents.jedzonko.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static android.app.Activity.RESULT_CANCELED;

public class ShowShoppingListFragment extends Fragment implements ShowIngredientItemAdapter.OnIngredientItemListener{
    RoomDB database;
    ShowIngredientItemAdapter showIngredientItemAdapter;
    List<IngredientItem> ingredientItemList = new ArrayList<>();
    Shopping shopping;

    BluetoothAdapter bluetoothAdapter;
    Set<BluetoothDevice> pairedDevices;

    Context safeContext;
    final int REQUEST_CODE_PERMISSIONS = 10;
    final int REQUEST_ENABLE_BT = 11;
    final String[] REQUIRED_PERMISSIONS = {Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    public ShowShoppingListFragment(){super(R.layout.fragment_show_recipe);}

    private void initToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.custom_toolbar);
        toolbar.setTitle("Lista zakupów");
        toolbar.inflateMenu(R.menu.show_slist);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(v -> getParentFragmentManager().popBackStack());
        toolbar.setOnMenuItemClickListener(clickedItem -> {

//            FragmentTransaction ft = getParentFragmentManager().beginTransaction();
//            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

            Bundle shoppingBundle = new Bundle();
            long shoppingId = shopping.getShoppingId();
            shoppingBundle.putLong("shoppingId", shoppingId);

            if(clickedItem.getItemId()==R.id.action_edit_slist) {
                EditShoppingListFragment editShoppingListFragment = new EditShoppingListFragment();
                editShoppingListFragment.setArguments(shoppingBundle);

                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainFrameLayout, editShoppingListFragment, "EditShoppingListFragment")
                        .addToBackStack("EditShoppingListFragment")
                        .commit();
            } else if (clickedItem.getItemId() == R.id.action_send_bluetooth) {
                initBT();
            }
            return false;
        });
    }

    private void initBT() {
        if (allPermissionsGranted()) {
            enableBT();
        } else {
            requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                enableBT();
            } else {
                Toast.makeText(requireContext(), R.string.permission_not_granted, Toast.LENGTH_LONG).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private boolean allPermissionsGranted() {
        for (String permission: REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(requireContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void enableBT() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            return;
        }
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            beginSendOperation();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode != RESULT_CANCELED) {
                beginSendOperation();
            } else {
                Toast.makeText(requireContext(), "Bluetooth wyłączony", Toast.LENGTH_LONG).show();
                getParentFragmentManager().popBackStack();
            }
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        safeContext = context;

    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        safeContext.registerReceiver(receiver, filter);
        Log.i("Harry2", "hallo1");
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i("Harry2", action);

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress();
                Log.i("Harry2", deviceName + " " + deviceHardwareAddress);
            }
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                Log.i("Harry2", "started");
            }
            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Log.i("Harry2", "finished");
            }
        }
    };

    private void beginSendOperation() {
        pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress();
                Log.i("Harry1", deviceName + " " + deviceHardwareAddress);
            }
        }
        bluetoothAdapter.startDiscovery();
        Log.i("Harry5", "isDiscover " + bluetoothAdapter.isDiscovering());

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_show_shopping_list, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initToolbar(view);
        database = RoomDB.getInstance(getActivity());
        showIngredientItemAdapter = new ShowIngredientItemAdapter(getContext(), this);
        RecyclerView ingredientRV = view.findViewById(R.id.showShoppingListProductsRV);
        Bundle bundle = getArguments();
        shopping = (Shopping) bundle.getSerializable("shoppingList");
        getShoppingListData(shopping, view);
        TextView shoppingListTitle = view.findViewById(R.id.showShoppingListTitle);
        shoppingListTitle.setText(shopping.getName());
        ingredientRV.setLayoutManager(new LinearLayoutManager(getContext()));
        ingredientRV.setAdapter(showIngredientItemAdapter);
        showIngredientItemAdapter.submitList(ingredientItemList);

    }

    public void getShoppingListData(Shopping shopping, View view){
        List<ShoppingWithShopitemsAndProducts> shoppingWithShopitemsAndProducts =  database.shoppingDao().getShoppingsWithShopitemsAndProducts();
        for(ShoppingWithShopitemsAndProducts list : shoppingWithShopitemsAndProducts){
            if(list.shopping.getShoppingId() == shopping.getShoppingId()){
                for(ShopitemsWithProducts shopitemsWithProducts: list.shopitems)
                {
                    IngredientItem ingredientItem = new IngredientItem();
                    ingredientItem.quantity = shopitemsWithProducts.shopitem.getQuantity();
                    for(Product product: shopitemsWithProducts.products){
                        ingredientItem.setProduct(product);
                        ingredientItemList.add(ingredientItem);
                    }
                }
            }
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        ingredientItemList.clear();
        safeContext.unregisterReceiver(receiver);
    }

    @Override
    public void onIngredientItemClick(int position) {
        //Toast.makeText(requireContext(),"a", Toast.LENGTH_LONG).show();
        FragmentTransaction ft =  getActivity().getSupportFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ShowProductFragment showProductFragment = new ShowProductFragment();
        Bundle bundle = new Bundle();
        Product product = ingredientItemList.get(position).product;
        bundle.putSerializable("product", product);
        showProductFragment.setArguments(bundle);
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.mainFrameLayout, showProductFragment, "ShowProductFragment")
                .addToBackStack("ShowSProductFragment")
                .commit();
    }


}
