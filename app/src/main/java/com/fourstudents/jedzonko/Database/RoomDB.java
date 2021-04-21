package com.fourstudents.jedzonko.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.fourstudents.jedzonko.Database.Dao.IngredientDao;
import com.fourstudents.jedzonko.Database.Dao.ProductDao;
import com.fourstudents.jedzonko.Database.Dao.RecipeDao;
import com.fourstudents.jedzonko.Database.Dao.ShoppingDao;
import com.fourstudents.jedzonko.Database.Dao.TagDao;
import com.fourstudents.jedzonko.Database.Entities.Ingredient;
import com.fourstudents.jedzonko.Database.Entities.Product;
import com.fourstudents.jedzonko.Database.Entities.Recipe;
import com.fourstudents.jedzonko.Database.Entities.Shopping;
import com.fourstudents.jedzonko.Database.Entities.ShoppingProductCrossRef;
import com.fourstudents.jedzonko.Database.Entities.Tag;

@Database(entities = {Recipe.class, Tag.class, Ingredient.class, Product.class, Shopping.class, ShoppingProductCrossRef.class}, version = 2, exportSchema = false)
public abstract class RoomDB extends RoomDatabase {
    private static com.fourstudents.jedzonko.Database.RoomDB database;
    private static String DATABASE_NAME = "database";

    public synchronized static com.fourstudents.jedzonko.Database.RoomDB getInstance(Context context){
        if(database ==null){
            database = Room.databaseBuilder(context.getApplicationContext(),
            com.fourstudents.jedzonko.Database.RoomDB.class,DATABASE_NAME)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return database;
    }
    public abstract RecipeDao recipeDao();
    public abstract TagDao tagDao();
    public abstract IngredientDao ingredientDao();
    public abstract ProductDao productDao();
    public abstract ShoppingDao shoppingDao();
}
