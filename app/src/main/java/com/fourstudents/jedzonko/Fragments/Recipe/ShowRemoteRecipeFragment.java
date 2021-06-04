package com.fourstudents.jedzonko.Fragments.Recipe;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fourstudents.jedzonko.Adapters.Recipe.RecipeCommentsAdapter;
import com.fourstudents.jedzonko.Adapters.Shared.ShowIngredientItemAdapter;
import com.fourstudents.jedzonko.Database.Entities.Product;
import com.fourstudents.jedzonko.Database.Entities.Recipe;
import com.fourstudents.jedzonko.Database.RoomDB;
import com.fourstudents.jedzonko.MainActivity;
import com.fourstudents.jedzonko.Network.JedzonkoService;
import com.fourstudents.jedzonko.Network.Responses.AverageRateResponse;
import com.fourstudents.jedzonko.Network.Responses.CommentResponse;
import com.fourstudents.jedzonko.Network.Responses.RecipeResponse;
import com.fourstudents.jedzonko.Network.Responses.UserRateResponse;
import com.fourstudents.jedzonko.Other.CommentItem;
import com.fourstudents.jedzonko.Other.Ingredient;
import com.fourstudents.jedzonko.Other.IngredientItem;
import com.fourstudents.jedzonko.R;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowRemoteRecipeFragment extends Fragment implements ShowIngredientItemAdapter.OnIngredientItemListener {
    RoomDB database;
    RatingBar ratingBar;
    RatingBar averageRatingBar;
    Button rateButton;
    RecipeResponse remoteRecipe;
    ShowIngredientItemAdapter showIngredientItemAdapter;
    RecipeCommentsAdapter recipeCommentsAdapter;
    List<IngredientItem> ingredientItemList = new ArrayList<>();
    JedzonkoService api;
    MainActivity activity;
    double userRate;
    RecyclerView commentsRV;
    List<CommentItem> commentsList = new ArrayList<>();

    public ShowRemoteRecipeFragment() {
        super(R.layout.fragment_show_remote_recipe);
    }

    private void initToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.custom_toolbar);
        toolbar.setTitle("Przepis");
        toolbar.inflateMenu(R.menu.show_recipe);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.getMenu().getItem(1).setVisible(false);
        if (remoteRecipe.getAuthor().getId() == ((MainActivity)requireActivity()).userid) {
            toolbar.getMenu().getItem(2).setVisible(true);
        }
        else {
            toolbar.getMenu().getItem(2).setVisible(false);
        }
        toolbar.setNavigationOnClickListener(v -> getParentFragmentManager().popBackStack());

        toolbar.setOnMenuItemClickListener(clickedItem -> {
            if(clickedItem.getItemId()==R.id.action_delete_recipe){
                onDeleteRecipe();
            }
            return false;
        });
    }

    private void onDeleteRecipe() {
        androidx.appcompat.app.AlertDialog.Builder alertBuilder = new androidx.appcompat.app.AlertDialog.Builder(requireContext());
        alertBuilder.setTitle(R.string.dialog_delete_recipe_title);
        alertBuilder.setMessage(R.string.dialog_delete_recipe_message);
        alertBuilder.setPositiveButton(R.string.delete_confirm, (dialog, which) -> deleteRecipe());
        alertBuilder.setNegativeButton(R.string.delete_not_confirm, (dialog, which) -> {});
        alertBuilder.show();
    }

    private void deleteRecipe() {
        Call<String> call = api.deleteRecipe((long) remoteRecipe.getId());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
            }
        });

        Recipe localRecipe = database.recipeDao().findByRemoteId((long) remoteRecipe.getId());
        if (localRecipe != null) {
            database.recipeDao().deleteIngredients(localRecipe.getRecipeId());
            database.recipeDao().deleteTags(localRecipe.getRecipeId());
            database.recipeDao().delete(localRecipe);
        }
        getParentFragmentManager().popBackStack();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_show_remote_recipe, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        assert bundle != null;
        remoteRecipe = (RecipeResponse) bundle.getSerializable("remoteRecipe");
        database = RoomDB.getInstance(getActivity());
        initToolbar(view);
        userRate = 0;
        ratingBar = view.findViewById(R.id.ratingBar);
        averageRatingBar = view.findViewById(R.id.averageRatingBar);
        TextView recipeTitle = view.findViewById(R.id.showRecipeTitle);
        TextView recipeTags = view.findViewById(R.id.showRecipeTagsString);
        TextView recipeDescription = view.findViewById(R.id.showRecipeDescription);
        TextView rateTitle = view.findViewById(R.id.showRecipeRateTitle);
        Button commentButton = view.findViewById(R.id.commentButton);
        EditText commentText = view.findViewById(R.id.showRecipeCommentEdiText);
        ImageView recipeImage = view.findViewById(R.id.imageView);
        recipeTitle.setText(remoteRecipe.getTitle());
        recipeDescription.setText(remoteRecipe.getDescription());
        activity = ((MainActivity) requireActivity());
        api = ((MainActivity) requireActivity()).api;

        RecyclerView ingredientRV = view.findViewById(R.id.showRecipeIngredientRV);
        showIngredientItemAdapter = new ShowIngredientItemAdapter(getContext(), this);

        commentsRV = view.findViewById(R.id.showRecipeCommentsRV);
        recipeCommentsAdapter = new RecipeCommentsAdapter(getContext());
        commentsRV.setLayoutManager(new LinearLayoutManager(getContext()));
        commentsRV.setAdapter(recipeCommentsAdapter);

        List<String> quantites = remoteRecipe.getQuantities();
        int pos = 0;
        for (Ingredient ingredient : remoteRecipe.getIngredients()) {
            IngredientItem ingredientItem = new IngredientItem();
            Product product = new Product();
            product.setName(ingredient.getName());
            ingredientItem.setProduct(product);
            ingredientItem.setQuantity(quantites.get(pos));
            pos++;
            ingredientItemList.add(ingredientItem);
        }

        StringBuilder concatTags = new StringBuilder();
        for (String tag : remoteRecipe.getTags()) {
            concatTags.append(" ").append(tag);
        }
        recipeTags.setText(concatTags.toString());
        setAverageRatingBar();

        ingredientRV.setLayoutManager(new LinearLayoutManager(getContext()));
        ingredientRV.setAdapter(showIngredientItemAdapter);
        showIngredientItemAdapter.submitList(ingredientItemList);


        byte[] decoded;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            decoded = Base64.getDecoder().decode(remoteRecipe.getImage());
        } else {
            decoded = android.util.Base64.decode(remoteRecipe.getImage(), 0);
        }

        Bitmap recipePhoto = BitmapFactory.decodeByteArray(decoded, 0, decoded.length);
        recipeImage.setImageBitmap(recipePhoto);

        rateButton = view.findViewById(R.id.rateButton);
        if (activity.token.length() > 0) {
            rateButton.setVisibility(View.VISIBLE);
            ratingBar.setVisibility(View.VISIBLE);
            rateTitle.setVisibility(View.VISIBLE);
            commentButton.setVisibility(View.VISIBLE);
            commentText.setVisibility(View.VISIBLE);
            Call<UserRateResponse> userRateCall = api.getUserRate(remoteRecipe.getId());
            userRateCall.enqueue(new Callback<UserRateResponse>() {
                @Override
                public void onResponse(@NotNull Call<UserRateResponse> call, @NotNull Response<UserRateResponse> response) {
                    if (response.body() != null) {
                        userRate = response.body().getRating();
                        ratingBar.setRating((float) userRate);
                        if (userRate > 0) rateButton.setText("ZMIEŃ");
                    }
                }

                @Override
                public void onFailure(@NotNull Call<UserRateResponse> call, @NotNull Throwable t) {
                }
            });
        }
        rateButton.setOnClickListener(v -> {
            if (ratingBar.getRating() == 0) {
                Toast.makeText(getContext(), "Brak oceny", Toast.LENGTH_SHORT).show();
            } else {
                String r = String.valueOf(ratingBar.getRating());
                JsonObject object = new JsonObject();
                object.addProperty("recipeId", remoteRecipe.getId());
                object.addProperty("rating", r);
                Call<UserRateResponse> callRate = api.addRate(object);
                callRate.enqueue(new Callback<UserRateResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<UserRateResponse> call, @NotNull Response<UserRateResponse> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(), "Oceniono na: " + r, Toast.LENGTH_SHORT).show();
                            setAverageRatingBar();
                            rateButton.setText("ZMIEŃ");
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<UserRateResponse> call, @NotNull Throwable t) {

                    }
                });

            }

        });

        commentButton.setOnClickListener(v -> {
            String comment = commentText.getText().toString();
            if (!comment.equals("")) {
                JsonObject object = new JsonObject();
                object.addProperty("content", comment);
                object.addProperty("recipeId", remoteRecipe.getId());
                Call<CommentResponse> commentCall = api.addComment(object);
                commentCall.enqueue(new Callback<CommentResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<CommentResponse> call, @NotNull Response<CommentResponse> response) {
                        Toast.makeText(getContext(), "Dodano komentarz", Toast.LENGTH_SHORT).show();
                        commentText.setText("");
                        CommentItem commentItem = new CommentItem();
                        assert response.body() != null;
                        if (response.body().getAuthor().getFirstName().isEmpty() || response.body().getAuthor().getLastName().isEmpty()) {
                            commentItem.setAuthor("Anonimowy");
                        } else
                            commentItem.setAuthor(response.body().getAuthor().getFirstName() + " " + response.body().getAuthor().getLastName());
                        commentItem.setComment(response.body().getContent().trim());
                        LocalDateTime data = LocalDateTime.parse(response.body().getCreationDate());
                        String dateString = data.getDayOfMonth() + "-" + data.getMonthValue() + "-" + data.getYear() + " " + data.getHour() + ":" + data.getMinute() + ":" + data.getSecond();
                        commentItem.setDate(dateString);
                        commentsList.add(commentItem);
                        recipeCommentsAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(@NotNull Call<CommentResponse> call, @NotNull Throwable t) {
                        Toast.makeText(getContext(), t.toString(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        Call<List<CommentResponse>> commentsCall = api.getCommentsForRecipe(remoteRecipe.getId());
        commentsCall.enqueue(new Callback<List<CommentResponse>>() {
            @Override
            public void onResponse(@NotNull Call<List<CommentResponse>> call, @NotNull Response<List<CommentResponse>> response) {
                assert response.body() != null;
                setCommentsList(response.body());
                recipeCommentsAdapter.submitList(commentsList);
            }

            @Override
            public void onFailure(@NotNull Call<List<CommentResponse>> call, @NotNull Throwable t) {

            }
        });
    }

    private void setAverageRatingBar() {
        Call<AverageRateResponse> averageRateResponse = api.getAverageRate(remoteRecipe.getId());
        averageRateResponse.enqueue(new Callback<AverageRateResponse>() {
            @Override
            public void onResponse(@NotNull Call<AverageRateResponse> call, @NotNull Response<AverageRateResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    averageRatingBar.setRating((float) response.body().getAverage());
                }
            }

            @Override
            public void onFailure(@NotNull Call<AverageRateResponse> call, @NotNull Throwable t) {

            }
        });
    }


    private void setCommentsList(List<CommentResponse> commentResponseList) {
        for (CommentResponse commentResponse : commentResponseList) {
            CommentItem commentItem = new CommentItem();
            if (commentResponse.getAuthor().getFirstName().isEmpty() || commentResponse.getAuthor().getLastName().isEmpty()) {
                commentItem.setAuthor("Anonimowy");
            } else
                commentItem.setAuthor(commentResponse.getAuthor().getFirstName() + " " + commentResponse.getAuthor().getLastName());
            commentItem.setComment(commentResponse.getContent().trim());
            LocalDateTime data = LocalDateTime.parse(commentResponse.getCreationDate());
            String dateString = data.getDayOfMonth() + "-" + data.getMonthValue() + "-" + data.getYear() + " " + data.getHour() + ":" + data.getMinute() + ":" + data.getSecond();
            commentItem.setDate(dateString);
            commentsList.add(commentItem);
        }
    }

    @Override
    public void onIngredientItemClick(int position) {

    }
}