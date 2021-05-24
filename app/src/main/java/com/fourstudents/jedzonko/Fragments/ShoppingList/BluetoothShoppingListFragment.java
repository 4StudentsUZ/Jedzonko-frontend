package com.fourstudents.jedzonko.Fragments.ShoppingList;

import android.Manifest;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.SyncStateContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
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
import com.fourstudents.jedzonko.Database.RoomDB;
import com.fourstudents.jedzonko.Fragments.Shared.AddProductFragment;
import com.fourstudents.jedzonko.Other.BluetoothAcceptThread;
import com.fourstudents.jedzonko.Other.BluetoothConnectedThread;
import com.fourstudents.jedzonko.Other.BluetoothServiceClass;
import com.fourstudents.jedzonko.Other.IngredientItem;
import com.fourstudents.jedzonko.R;
import com.fourstudents.jedzonko.ViewModels.Shared.IngredientItemViewModel;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.stream.MalformedJsonException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class BluetoothShoppingListFragment extends Fragment implements ProductAdapter.OnProductListener, IngredientItemAdapter.OnIngredientItemListener {
    EditText name;
    Button addIngredientButton;
    Dialog ingredientDialog;
    RoomDB database;

    RecyclerView ingredientRV;
    IngredientItemAdapter ingredientItemAdapter;
    IngredientItemViewModel ingredientItemViewModel;

    RecyclerView productRV;
    ProductAdapter productAdapter;
    List<Product> productList = new ArrayList<>();
    List<Product> addedProducts = new ArrayList<>();
    boolean wasSaved = false;

    BluetoothAdapter bluetoothAdapter;
    Context safeContext;
    String resultReadData;

    final int REQUEST_CODE_PERMISSIONS = 10;
    final String[] REQUIRED_PERMISSIONS = {Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.ACCESS_FINE_LOCATION};
    final int REQUEST_ENABLE_BT = 11;
    final int REQUEST_DISCOVER_BT = 12;

    public BluetoothShoppingListFragment(){super(R.layout.fragment_bluetooth_shopping_list);}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resultReadData = "";
        ingredientItemViewModel = new ViewModelProvider(requireActivity()).get(IngredientItemViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initToolbar(view);
        initLayout(view);
        initDialog();
        database = RoomDB.getInstance(getActivity());
        initRV();
//        initVM();
        initVM();
        initBT();
    }

    private void initToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.custom_toolbar);
        toolbar.setTitle(R.string.title_bluetooth_slist);
        toolbar.inflateMenu(R.menu.bluetooth_slist);
        toolbar.getMenu().getItem(0).setTitle(R.string.save_button);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(v -> getParentFragmentManager().popBackStack());

        toolbar.setOnMenuItemClickListener(clickedItem -> {
            if (clickedItem.getItemId() == R.id.action_save_slist)
                menuSaveButtonPressed();
            return true;
        });
    }

    private void initLayout(View view) {
        name = view.findViewById(R.id.editTextName);
        ingredientRV = view.findViewById(R.id.ingredientRV);
        addIngredientButton = view.findViewById(R.id.editShoppingProductsBtn);
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
                getParentFragmentManager().popBackStack();
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
            enableDiscoverBT();
        }
    }

    private void enableDiscoverBT() {
        Intent enableBtDiscoverIntent = new Intent((BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE));
        enableBtDiscoverIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 120);
        startActivityForResult(enableBtDiscoverIntent, REQUEST_DISCOVER_BT);
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
        filter.addAction(android.bluetooth.);
        safeContext.registerReceiver(receiver, filter);
    }



    @Override
    public void onPause() {
        super.onPause();
        safeContext.unregisterReceiver(receiver);
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i("Harry9", action);
            if (BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.i("Harry9", device.getName());
            }
        }
    };

    private void waitForData() {
        Handler handler = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                switch (msg.what) {
//                    case BluetoothServiceClass.MESSAGE_WRITE:
//                        Log.i("Harry", "WRITE");
//                        byte[] writeBuf = (byte[]) msg.obj;
//                        String writeMessage = new String(writeBuf, 0, msg.arg1);
//                        Log.i("Harry", writeMessage);
//                        break;
                    case BluetoothServiceClass.MESSAGE_READ:
                        Log.i("Harry", "READ");
                        readData(msg);
                        break;
                    case 99:
                        Toast.makeText(requireContext(), "Połączenie nawiązane", Toast.LENGTH_SHORT).show();
                        break;
                }
                super.handleMessage(msg);
            }
        };
        BluetoothAcceptThread thread = new BluetoothAcceptThread(bluetoothAdapter, handler);
        thread.start();
    }

    private void readData(Message msg) {
        byte[] readBuf = (byte[]) msg.obj;
        String readMessage = new String(readBuf, 0, msg.arg1);
        Log.i("HarryTest", readMessage);
        resultReadData += readMessage;
        JsonObject jsonObject;
        try {
            jsonObject = new Gson().fromJson(resultReadData, JsonObject.class);
        } catch (Exception e) {
            return;
        }

        name.setText(jsonObject.get("list_name").getAsString());
        JsonArray jsonArray = jsonObject.getAsJsonArray("items");
        for (int i = 0; i < jsonArray.size(); i++) {
            JsonObject object = jsonArray.get(i).getAsJsonObject();
            Product product = new Product();
            product.setName(object.get("item_name").getAsString());
            product.setBarcode(object.get("item_barcode").getAsString());
            product.setData(android.util.Base64.decode(object.get("item_data").getAsString(), 0));
//            product.setData(new byte[] {1,1});
            database.productDao().insert(product);
            int lastProductId = database.productDao().getLastId();
            product.setProductId(lastProductId);
            addedProducts.add(product);
            IngredientItem item = new IngredientItem();
            item.setProduct(product);
            item.setQuantity(object.get("item_quantity").getAsString());
            ingredientItemViewModel.addIngredientItem(item);
        }
        Log.i("HarryReadData", readMessage);
    }

    private void readData2(Message msg) {
        byte[] readBuf = (byte[]) msg.obj;
        String readMessage = new String(readBuf, 0, msg.arg1);
        try {
            JSONObject jsonObject = new JSONObject(readMessage);
            name.setText(jsonObject.getString("list_name"));
            JSONArray jsonArray = jsonObject.getJSONArray("items");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                Product product = new Product();
                product.setName(object.getString("item_name"));
                product.setBarcode(object.getString("item_barcode"));
                product.setData(android.util.Base64.decode(object.getString("item_data"), 0));
//                product.setData(new byte[] {1,1});
                database.productDao().insert(product);
                int lastProductId = database.productDao().getLastId();
                product.setProductId(lastProductId);
                addedProducts.add(product);
                IngredientItem item = new IngredientItem();
                item.setProduct(product);
                item.setQuantity(object.getString("item_quantity"));
                ingredientItemViewModel.addIngredientItem(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("Harry", readMessage);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode != RESULT_CANCELED) {
                enableDiscoverBT();
            } else {
                Toast.makeText(requireContext(), "Bluetooth wyłączony", Toast.LENGTH_LONG).show();
                getParentFragmentManager().popBackStack();
            }
        }
        if (requestCode == REQUEST_DISCOVER_BT) {
            if (resultCode != RESULT_CANCELED) {
                waitForData();
            } else {
                Toast.makeText(requireContext(), "Urządzenie musi być widoczne", Toast.LENGTH_LONG).show();
                getParentFragmentManager().popBackStack();
            }
        }
    }

    private void initDialog() {
        ingredientDialog = new Dialog(getContext());
        ingredientDialog.setContentView(R.layout.dialog_add_ingredient);
        ingredientDialog.setCanceledOnTouchOutside(true);
        ingredientDialog.getWindow()
                .setLayout(
                        WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.WRAP_CONTENT
                );
    }

    private void initRV() {
        ingredientItemAdapter = new IngredientItemAdapter(getContext(), this);
        ingredientRV.setLayoutManager(new LinearLayoutManager(getContext()));
        ingredientRV.setAdapter(ingredientItemAdapter);

        productList.addAll(database.productDao().getAll());
        productAdapter = new ProductAdapter(getContext(), productList, this);
        productRV = ingredientDialog.findViewById(R.id.productRV);
        productRV.setLayoutManager(new LinearLayoutManager(getContext()));
        productRV.setAdapter(productAdapter);
    }

    private void initVM() {
        ingredientItemViewModel.getIngredientItemList().observe(getViewLifecycleOwner(), ingredientItems -> {
            ingredientItemAdapter.submitList(ingredientItems);
            ingredientItemAdapter.notifyDataSetChanged();
        });
    }

    boolean checkData(){
        if (TextUtils.isEmpty(name.getText().toString()) ||
            ingredientItemViewModel.getIngredientItemsListSize() == 0 ||
            !ingredientItemViewModel.isQuantityFilled()
        ) {
            Toast.makeText(getContext(), R.string.missing_input_data, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    private void menuSaveButtonPressed() {
        if (checkData()) {
            Shopping shopping = new Shopping();
            shopping.setName(name.getText().toString().trim());
            database.shoppingDao().insert(shopping);
            int shoppingId = database.shoppingDao().getLastId();

            List<IngredientItem> ingredientItems = ingredientItemViewModel.getIngredientItemsList();

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

            name.setText("");
            ingredientItemViewModel.clearIngredientItemList();
            Toast.makeText(getContext(), "Dodano listę", Toast.LENGTH_SHORT).show();
            wasSaved = true;
            getParentFragmentManager().popBackStack();
        }
        productRV.setAdapter(productAdapter);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (!wasSaved) {
            for (Product product : addedProducts) {
                database.productDao().delete(product);
            }
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

    @Override
    public void onIngredientItemDeleteClick(int position) {
        IngredientItem ingredientItem = ingredientItemViewModel.getIngredientItem(position);
        ingredientItemViewModel.removeIngredientItem(ingredientItem);
    }

    @Override
    public void onTextChange(int position, CharSequence s) {
        IngredientItem ingredientItem = ingredientItemViewModel.getIngredientItem(position);
        if (ingredientItem != null) {
            ingredientItem.setQuantity(s.toString());
        }
    }
}

