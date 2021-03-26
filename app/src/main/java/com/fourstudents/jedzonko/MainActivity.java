package com.fourstudents.jedzonko;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        ArrayList<Todo> todos = new ArrayList<>();
//        todos.add(new Todo("Item 1", true));
//        todos.add(new Todo("Item 2", false));
//        todos.add(new Todo("Item 3", false));
//        todos.add(new Todo("Item 4", true));
//        todos.add(new Todo("Item 5", false));
//        todos.add(new Todo("Item 6", true));
//
//        TodoAdapter adapter = new TodoAdapter(todos);
//        RecyclerView rvTodo = findViewById(R.id.rvTodo);
//        rvTodo.setAdapter(adapter);
//        rvTodo.setLayoutManager(new LinearLayoutManager(this));
//
//        (findViewById(R.id.btnAddTodo)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String title = ((EditText) findViewById(R.id.etTodo)).getText().toString();
//                Todo todo = new Todo(title, false);
//                todos.add(todo);
//                adapter.notifyItemInserted(todos.size() - 1);
//            }
//        });

        Fragment searchFragment = new SearchFragment();
        Fragment shoppingListFragment = new ShoppingListFragment();
        Fragment recipesFragment = new RecipesFragment();
        Fragment shopsFragment = new ShopsFragment();
        Fragment accountFragment = new AccountFragment();

//        setCurrentFragment(recipesFragment);

        ((BottomNavigationView) findViewById(R.id.bottomNavigationView)).setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navSearch:
                    setCurrentFragment(searchFragment);
                    break;
                case R.id.navShoppingList:
                    setCurrentFragment(shoppingListFragment);
                    break;
                case R.id.navRecipes:
                    setCurrentFragment(recipesFragment);
                    break;
                case R.id.navShops:
                    setCurrentFragment(shopsFragment);
                    break;
                case R.id.navAccount:
                    setCurrentFragment(accountFragment);
                    break;
            }
            return true;
        });

        ((BottomNavigationView) findViewById(R.id.bottomNavigationView)).setSelectedItemId(R.id.navRecipes);

    }

    private void setCurrentFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.mainFrameLayout, fragment);
        transaction.commit();
    }
}