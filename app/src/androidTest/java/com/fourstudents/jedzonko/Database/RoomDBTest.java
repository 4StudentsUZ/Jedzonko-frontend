package com.fourstudents.jedzonko.Database;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.fourstudents.jedzonko.Database.Dao.IngredientDao;
import com.fourstudents.jedzonko.Database.Dao.ProductDao;
import com.fourstudents.jedzonko.Database.Dao.RecipeDao;
import com.fourstudents.jedzonko.Database.Dao.ShopitemDao;
import com.fourstudents.jedzonko.Database.Dao.ShoppingDao;
import com.fourstudents.jedzonko.Database.Dao.TagDao;
import com.fourstudents.jedzonko.Database.Entities.Ingredient;
import com.fourstudents.jedzonko.Database.Entities.IngredientProductCrossRef;
import com.fourstudents.jedzonko.Database.Entities.Product;
import com.fourstudents.jedzonko.Database.Entities.Recipe;
import com.fourstudents.jedzonko.Database.Entities.RecipeTagCrossRef;
import com.fourstudents.jedzonko.Database.Entities.Shopitem;
import com.fourstudents.jedzonko.Database.Entities.ShopitemProductCrossRef;
import com.fourstudents.jedzonko.Database.Entities.Shopping;
import com.fourstudents.jedzonko.Database.Entities.Tag;
import com.fourstudents.jedzonko.Database.Relations.RecipeWithIngredientsAndProducts;
import com.fourstudents.jedzonko.Database.Relations.RecipesWithTags;
import com.fourstudents.jedzonko.Database.Relations.ShoppingWithShopitemsAndProducts;
import com.fourstudents.jedzonko.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;
@RunWith(AndroidJUnit4.class)
public class RoomDBTest {
    private RecipeDao recipeDao;
    private ProductDao productDao;
    private IngredientDao ingredientDao;
    private TagDao tagDao;
    private ShoppingDao shoppingDao;
    private ShopitemDao shopitemDao;
    private RoomDB db;
    private byte[] data;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, RoomDB.class).build();
        recipeDao = db.recipeDao();
        productDao = db.productDao();
        ingredientDao = db.ingredientDao();
        shoppingDao = db.shoppingDao();
        shopitemDao = db.shopitemDao();
        tagDao = db.tagDao();
        data = new byte[]{0x00};
    }

    @After
    public void closeDb()  {
        db.close();
    }

    @Test
    public void InsertRecipeTest()  {
        Recipe recipe = new Recipe();
        recipe.setDescription("OpisTest");
        recipe.setTitle("TytulTest");
        recipe.setRemoteId(0);
        recipe.setData(data);
        recipeDao.insert(recipe);
        recipe.setRecipeId(1);
        List<Recipe> recipes = recipeDao.getAll();
        assertFalse(recipes.contains(recipe));
    }

    @Test
    public void GetLastRecipeIdTest(){
        Recipe recipe = new Recipe();
        recipe.setDescription("OpisTest");
        recipe.setTitle("TytulTest");
        recipe.setRemoteId(0);
        recipe.setData(data);
        recipeDao.insert(recipe);
        int id = recipeDao.getLastId();
        assertEquals(id,1);
    }

    @Test
    public void InsertProductAndIngredientTest(){
        Recipe recipe = new Recipe();
        recipe.setDescription("OpisTest");
        recipe.setTitle("TytulTest");
        recipe.setRemoteId(0);
        recipe.setData(data);
        recipeDao.insert(recipe);

        Product product = new Product();
        product.setData(data);
        product.setBarcode("1234");
        product.setName("TestProdukt");
        productDao.insert(product);

        Ingredient ingredient = new Ingredient();
        ingredient.setQuantity("50ml");
        ingredient.setRecipeOwnerId(1);
        ingredientDao.insert(ingredient);

        IngredientProductCrossRef ingredientProductCrossRef= new IngredientProductCrossRef();
        ingredientProductCrossRef.setIngredientId(1);
        ingredientProductCrossRef.setProductId(1);
        ingredientDao.insertIngredientWithProduct(ingredientProductCrossRef);

        List<RecipeWithIngredientsAndProducts> list =  recipeDao.getRecipesWithIngredientsAndProducts();

        assertEquals(list.get(0).ingredients.get(0).products.get(0).getName(), "TestProdukt");
    }

    @Test
    public void GetLastIngredientIdTest(){
        Product product = new Product();
        product.setData(data);
        product.setBarcode("1234");
        product.setName("TestProdukt");
        productDao.insert(product);

        Ingredient ingredient = new Ingredient();
        ingredient.setQuantity("50ml");
        ingredient.setRecipeOwnerId(1);
        ingredientDao.insert(ingredient);

        assertEquals(ingredientDao.getLastId(),1);
    }

    @Test
    public void InsertTagTest(){
        Recipe recipe = new Recipe();
        recipe.setDescription("OpisTest");
        recipe.setTitle("TytulTest");
        recipe.setRemoteId(0);
        recipe.setData(data);
        recipeDao.insert(recipe);

        Tag tag = new Tag();
        tag.setName("TestTag");
        tagDao.insert(tag);

        RecipeTagCrossRef recipeTagCrossRef = new RecipeTagCrossRef();
        recipeTagCrossRef.setRecipeId(1);
        recipeTagCrossRef.setTagId(1);
        recipeDao.insertRecipeWithTag(recipeTagCrossRef);
        List<RecipesWithTags> list = recipeDao.getRecipesWithTags();
        assertEquals(list.get(0).tags.get(0).getName(), "TestTag");
    }

    @Test
    public void InsertShoppingTest(){
        Product product = new Product();
        product.setData(data);
        product.setBarcode("1234");
        product.setName("TestProdukt");
        productDao.insert(product);

        Shopping shopping = new Shopping();
        shopping.setName("TestShopping");
        shoppingDao.insert(shopping);

        Shopitem shopitem = new Shopitem();
        shopitem.setQuantity("50ml");
        shopitem.setShoppingOwnerId(1);
        shopitemDao.insert(shopitem);

        ShopitemProductCrossRef shopitemProductCrossRef = new ShopitemProductCrossRef();
        shopitemProductCrossRef.setProductId(1);
        shopitemProductCrossRef.setShopitemId(1);

        shopitemDao.insertShopitemWithProduct(shopitemProductCrossRef);
        List<ShoppingWithShopitemsAndProducts> list = shoppingDao.getShoppingsWithShopitemsAndProducts();
        assertEquals(list.get(0).shopitems.get(0).products.get(0).getBarcode(), "1234");
    }
    
}