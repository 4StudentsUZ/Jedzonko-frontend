package com.fourstudents.jedzonko.Database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.fourstudents.jedzonko.Database.Dao.ProductDao;
import com.fourstudents.jedzonko.Database.Dao.RecipeDao;
import com.fourstudents.jedzonko.Database.Dao.ShoppingDao;
import com.fourstudents.jedzonko.Database.Dao.TagDao;
import com.fourstudents.jedzonko.Database.Entities.Product;
import com.fourstudents.jedzonko.Database.Entities.Recipe;
import com.fourstudents.jedzonko.Database.Entities.RecipeProductCrossRef;
import com.fourstudents.jedzonko.Database.Entities.Shopping;
import com.fourstudents.jedzonko.Database.Entities.ShoppingProductCrossRef;
import com.fourstudents.jedzonko.Database.Entities.Tag;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
@Database(entities = {Recipe.class, Tag.class,Product.class, Shopping.class, ShoppingProductCrossRef.class, RecipeProductCrossRef.class}, version = 3, exportSchema = false)
public abstract class RoomDB extends RoomDatabase {
    private static volatile RoomDB database;
    private final static String DATABASE_NAME = "database";
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public synchronized static RoomDB getInstance(Context context){
        if(database ==null){
            database = Room.databaseBuilder(context.getApplicationContext(),
            RoomDB.class,DATABASE_NAME)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .addCallback(roomDatabaseCallback)
                    .build();
        }
        return database;
    }

    private static RoomDatabase.Callback roomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            databaseWriteExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    shoppingTemplate();
                    productTemplate();
                }
            });
        }
    };

    private static void shoppingTemplate() {
        ShoppingDao shoppingDao = database.shoppingDao();
        Shopping list = new Shopping();
        for (int i = 0; i < 4; i++) {
            list.setName("Lista zakupów - #" + (i+1));
            shoppingDao.insert(list);
        }
    }

    private static void productTemplate() {
        ProductDao productDao = database.productDao();
        Product product = new Product();
        product.setName("Pomidor");
        product.setBarcode("000");
        product.setData(new byte[] {0x00, 0x01});
        productDao.insert(product);
        product.setName("Banan");
        productDao.insert(product);
        product.setName("Mąka");
        productDao.insert(product);
    }

    public abstract RecipeDao recipeDao();
    public abstract TagDao tagDao();
    public abstract ProductDao productDao();
    public abstract ShoppingDao shoppingDao();
}
