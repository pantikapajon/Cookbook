package com.pbylicki.cookbook;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.pbylicki.cookbook.data.Recipe;
import com.pbylicki.cookbook.data.User;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.NonConfigurationInstance;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_view_recipe)
public class ViewRecipeActivity extends Activity {

    @Extra
    Bundle bundle;

    @ViewById
    TextView title;
    @ViewById
    ImageView image;
    @ViewById
    TextView created;
    @ViewById
    TextView introduction;
    @ViewById
    TextView servings;
    @ViewById
    TextView preparationMinutes;
    @ViewById
    TextView cookingMinutes;
    @ViewById
    TextView ingredients;
    @ViewById
    TextView steps;
    @ViewById
    EditText newcomment;
    @ViewById
    ListView commentlist;

    @Bean
    @NonConfigurationInstance
    RestViewRecipeBackgroundTask restBackgroundTask;
    Recipe recipe;
    User user;
    ProgressDialog ringProgressDialog;

    @AfterViews
    void init(){
        //Gets User and Selected Recipe
        user = (User)bundle.getSerializable(BrowseActivity.USER);
        recipe = (Recipe) bundle.getSerializable(BrowseActivity.RECIPE);
        //Populates View
        title.setText(recipe.title);
        created.setText(getString(R.string.add_recipe_created_date)+recipe.getCreatedDate().toString());
        introduction.setText(recipe.introduction);
        servings.setText(getString(R.string.add_recipe_servings)+": " +Integer.toString(recipe.servings));
        if(recipe.preparationMinutes != null) preparationMinutes.setText(getString(R.string.add_recipe_preparationMinutes)+": " +Integer.toString(recipe.preparationMinutes));
        if(recipe.cookingMinutes != null) cookingMinutes.setText(getString(R.string.add_recipe_cookingMinutes)+": " +Integer.toString(recipe.cookingMinutes));
        ingredients.setText(recipe.ingredients);
        steps.setText(recipe.steps);

        ringProgressDialog = new ProgressDialog(this);
        ringProgressDialog.setMessage("Downloading data...");
        ringProgressDialog.setIndeterminate(true);
    }
}
