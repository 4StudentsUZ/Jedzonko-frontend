package com.fourstudents.jedzonko.Fragments.ShoppingList;

import android.Manifest;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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

import com.fourstudents.jedzonko.Adapters.Shared.BluetoothDeviceAdapter;
import com.fourstudents.jedzonko.Adapters.Shared.ShowIngredientItemAdapter;
import com.fourstudents.jedzonko.Database.Entities.Product;
import com.fourstudents.jedzonko.Database.Entities.Shopping;
import com.fourstudents.jedzonko.Database.Relations.ShopitemsWithProducts;
import com.fourstudents.jedzonko.Database.Relations.ShoppingWithShopitemsAndProducts;
import com.fourstudents.jedzonko.Database.RoomDB;
import com.fourstudents.jedzonko.Fragments.Shared.ShowProductFragment;
import com.fourstudents.jedzonko.Other.BluetoothAcceptThread;
import com.fourstudents.jedzonko.Other.BluetoothConnectedThread;
import com.fourstudents.jedzonko.Other.BluetoothSendThread;
import com.fourstudents.jedzonko.Other.BluetoothServiceClass;
import com.fourstudents.jedzonko.Other.IngredientItem;
import com.fourstudents.jedzonko.R;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static android.app.Activity.RESULT_CANCELED;

public class ShowShoppingListFragment extends Fragment implements ShowIngredientItemAdapter.OnIngredientItemListener, BluetoothDeviceAdapter.OnDeviceListener{
    RoomDB database;
    ShowIngredientItemAdapter showIngredientItemAdapter;
    List<IngredientItem> ingredientItemList = new ArrayList<>();
    Shopping shopping;

    BluetoothAdapter bluetoothAdapter;
    Set<BluetoothDevice> pairedDevices;
    BluetoothDeviceAdapter allDevicesAdapter;
    Dialog bluetoothDialog;
    RecyclerView devicesRV;
    List<BluetoothDevice> deviceList = new ArrayList<>();

    TextView shoppingListTitle;

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
                deviceList.add(device);
                allDevicesAdapter.notifyDataSetChanged();
            }
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                bluetoothDialog.findViewById(R.id.bluetoothProgressBar).setVisibility(View.VISIBLE);
            }
            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                bluetoothDialog.findViewById(R.id.bluetoothProgressBar).setVisibility(View.INVISIBLE);
            }
        }
    };

    private void beginSendOperation() {

        pairedDevices = bluetoothAdapter.getBondedDevices();

        bluetoothDialog = new Dialog(requireContext());
        bluetoothDialog.setContentView(R.layout.dialog_bluetooth_device);
        bluetoothDialog.setCanceledOnTouchOutside(true);
        bluetoothDialog.getWindow().setLayout(
                        WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.WRAP_CONTENT
        );

        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress();
                Log.i("Harry1", deviceName + " " + deviceHardwareAddress);
                deviceList.add(device);
            }
        }

        devicesRV = bluetoothDialog.findViewById(R.id.dialog_bluetooth_recyclerView);
        allDevicesAdapter = new BluetoothDeviceAdapter(deviceList, this);
        devicesRV.setLayoutManager(new LinearLayoutManager(requireContext()));
        devicesRV.setAdapter(allDevicesAdapter);

        bluetoothDialog.findViewById(R.id.dialog_bluetooth_close).setOnClickListener(v -> {
            bluetoothDialog.dismiss();
            deviceList.clear();
        });
        
        bluetoothDialog.findViewById(R.id.dialog_bluetooth_search).setOnClickListener(v -> bluetoothAdapter.startDiscovery());
        bluetoothDialog.show();

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
        if (bundle == null)
            return;
        shopping = (Shopping) bundle.getSerializable("shoppingList");
        getShoppingListData(shopping, view);
        shoppingListTitle = view.findViewById(R.id.showShoppingListTitle);
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
        FragmentTransaction ft =  getParentFragmentManager().beginTransaction();
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

    private void makeConnection(BluetoothDevice device) {
        try {
            device.createRfcommSocketToServiceRecord(BluetoothServiceClass.bluetoothUUID);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Handler handler = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                switch (msg.what) {
                    case BluetoothServiceClass.MESSAGE_READY:
                        sendData(msg.obj);
                        break;
                    case BluetoothServiceClass.MESSAGE_WRITE:
                        Log.i("Harry", "WRITE");
                        byte[] writeBuf = (byte[]) msg.obj;
                        String writeMessage = new String(writeBuf, 0, msg.arg1);
                        Log.i("Harry", writeMessage);
                        break;
//                    case BluetoothServiceClass.MESSAGE_READ:
//                        Log.i("Harry", "READ");
//                        byte[] readBuf = (byte[]) msg.obj;
//                        String readMessage = new String(readBuf, 0, msg.arg1);
//                        Log.i("Harry", readMessage);
//                        break;
                    case 99:
                        Toast.makeText(requireContext(), "Połączenie nawiązane", Toast.LENGTH_SHORT).show();
                        break;
                }
                super.handleMessage(msg);
            }
        };

        BluetoothSendThread thread = new BluetoothSendThread(device, handler);
        thread.start();
    }

    private void sendData(Object object) {
        BluetoothConnectedThread thread = (BluetoothConnectedThread) object;
        JsonObject jsonObject = new JsonObject();
        JsonArray jsonArray = new JsonArray();
        for (IngredientItem item : ingredientItemList) {
            JsonObject itemObject = new JsonObject();
            itemObject.addProperty("item_name", item.product.getName());
            itemObject.addProperty("item_barcode", item.product.getBarcode());
            itemObject.addProperty("item_quantity", item.quantity);
            byte[] data = item.product.getData();
            String dataString = android.util.Base64.encodeToString(data, 0);
            itemObject.addProperty("item_data", dataString);
            jsonArray.add(itemObject);
        }
        jsonObject.addProperty("list_name", shoppingListTitle.getText().toString().trim());
        jsonObject.add("items", jsonArray);
        String str = jsonObject.toString();
        Log.i("HarrySendData", str);
        byte[] testBytes = str.getBytes();
        String readMessage = new String(testBytes, 0, testBytes.length);

        JsonObject jsonObjectTest = new Gson().fromJson(readMessage, JsonObject.class);
        Log.i("HarrySendData2", jsonObjectTest.toString());
        thread.write(testBytes);
    }

    private void sendData2(Object object) {
        BluetoothConnectedThread thread = (BluetoothConnectedThread) object;
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        for (IngredientItem item : ingredientItemList) {
            JSONObject itemObject = new JSONObject();
            try {
                itemObject.put("item_name", item.product.getName());
                itemObject.put("item_barcode", item.product.getBarcode());
                itemObject.put("item_quantity", item.quantity);
                byte[] data = item.product.getData();
                String dataString = android.util.Base64.encodeToString(data, 0);
                itemObject.put("item_data", dataString);
                jsonArray.put(itemObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        try {
            jsonObject.put("list_name", shoppingListTitle.getText().toString().trim());
            jsonObject.put("items", jsonArray);
            thread.write(jsonObject.toString().getBytes());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDeviceClick(int position) {
        BluetoothDevice device = deviceList.get(position);
        bluetoothDialog.dismiss();
        deviceList.clear();
        bluetoothAdapter.cancelDiscovery();
        makeConnection(device);
    }
}
