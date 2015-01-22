package com.pbylicki.cookbook;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.pbylicki.cookbook.adapter.RecipeListAdapter;
import com.pbylicki.cookbook.data.Recipe;
import com.pbylicki.cookbook.data.RecipeList;
import com.pbylicki.cookbook.data.User;

import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.NonConfigurationInstance;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.HashSet;

@EActivity(R.layout.activity_browse)
@OptionsMenu(R.menu.menu_browse)
public class BrowseActivity extends Activity {

    public static final int REQUESTCODE = 42;
    public static final int LOGIN_REQUESTCODE = 40;
    public static final int PROFILE_REQUESTCODE = 38;
    public static final String USER = "user";
    public static final String RECIPE = "recipe";
    public static final int FILTER_THRESHOLD = 3;

    @ViewById
    ListView list;
    @ViewById
    AutoCompleteTextView filter;
    @ViewById
    TextView header;

    @Extra
    @InstanceState
    User user;

    RecipeList recipeList;
    RecipeList resultList;
    ArrayList<String> titles;
    ArrayAdapter<String> titlesAdapter;

    @Bean
    RecipeListAdapter adapter;
    @Bean
    @NonConfigurationInstance
    RestBrowseBackgroundTask restBackgroundTask;
    ProgressDialog ringProgressDialog;

    @AfterViews
    void init() {
        list.setAdapter(adapter);
        ringProgressDialog = new ProgressDialog(this);
        ringProgressDialog.setMessage("Downloading recipes...");
        ringProgressDialog.setIndeterminate(true);
        ringProgressDialog.show();
        restBackgroundTask.getRecipeList(user);


    }

    public void showError(Exception e) {
        ringProgressDialog.dismiss();
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        e.printStackTrace();
    }

    public void updateRecipeList(RecipeList recipeList) {
        ringProgressDialog.dismiss();
        this.recipeList = recipeList;
        adapter.update(recipeList);
        //Creates list of titles for autocompletetextview
        HashSet<String> titleHashset = new HashSet<String>();
        //for(Recipe recipe : recipeList.records){ if(recipe.title != null) titles.add(recipe.title);}
        for(Recipe recipe : recipeList.records){ if(recipe.title != null) titleHashset.add(recipe.title.toLowerCase().trim());}
        titles = new ArrayList<String>(titleHashset);
        titlesAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line, titles);
        filter.setAdapter(titlesAdapter);
        updateRecipeCount();
        filter.setVisibility(View.VISIBLE);
    }

    @AfterTextChange(R.id.filter)
    void afterTextChangedOnFilter(){
        String currentFilter = filter.getText().toString();
        if(currentFilter.length()>=FILTER_THRESHOLD){
            resultList = new RecipeList();
            for(Recipe recipe : recipeList.records){
                String title = "";
                if(recipe.title != null) title = recipe.title.toLowerCase();

                if(title.contains(currentFilter)) resultList.records.add(recipe);
            }
            adapter.update(resultList);
        } else {
            adapter.update(recipeList);
        }
        updateRecipeCount();
    }


    @ItemClick
    void listItemClicked(Recipe item){
        if(item.id == null) return;
        Bundle bundle = new Bundle();
        bundle.putSerializable(USER, user);
        bundle.putSerializable(RECIPE, item);
        ViewRecipeActivity_.intent(this).bundle(bundle).start();
    }

    @OptionsItem(R.id.action_login)
    void actionLoginSelected(){
        if(user == null) LoginActivity_.intent(this).startForResult(LOGIN_REQUESTCODE);
        else Toast.makeText(this, getString(R.string.user_already_logged_in), Toast.LENGTH_LONG).show();
    }

    @OptionsItem(R.id.action_add)
    void actionAddSelected() {
        //User must be logged in to start Activity
        if(user == null) LoginActivity_.intent(this).startForResult(REQUESTCODE);
        else AddRecipeActivity_.intent(this).user(user).start();

    }

    @OptionsItem(R.id.action_profile)
    void actionProfileSelected() {
        if(user == null) LoginActivity_.intent(this).startForResult(PROFILE_REQUESTCODE);
        else ProfileActivity_.intent(this).user(user).start();
    }
    @OptionsItem(R.id.action_browse)
    void actionBrowseSelected() {
        BrowseActivity_.intent(this).user(user).start();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode == RESULT_OK){
            user =(User)data.getSerializableExtra(LoginActivity.LOGINRESULT);
            switch (requestCode) {
                case REQUESTCODE:   AddRecipeActivity_.intent(this).user(user).start();
                                    break;
                case LOGIN_REQUESTCODE:     init();
                                            break;
                case PROFILE_REQUESTCODE:   ProfileActivity_.intent(this).user(user).start();
                                            break;
                default:            break;
            }
        }
        if (resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "Login cancelled", Toast.LENGTH_LONG).show();
        }
    }

    private void updateRecipeCount(){
        int count = adapter.getCount();
        //if( count == 1 && adapter.getItem(0).id == null) header.setText(getString(R.string.browse_header)+"(0)");
        //else header.setText(getString(R.string.browse_header)+"("+Integer.toString(count)+")");
        header.setText(getString(R.string.browse_header)+"("+Integer.toString(count)+")");
    }
}
