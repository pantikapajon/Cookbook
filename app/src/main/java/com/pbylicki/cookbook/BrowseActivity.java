package com.pbylicki.cookbook;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.pbylicki.cookbook.adapter.RecipeListAdapter;
import com.pbylicki.cookbook.data.Recipe;
import com.pbylicki.cookbook.data.RecipeList;
import com.pbylicki.cookbook.data.User;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.NonConfigurationInstance;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_browse)
@OptionsMenu(R.menu.menu_browse)
public class BrowseActivity extends Activity {

    public static final int REQUESTCODE = 42;
    public static final int LOGIN_REQUESTCODE = 40;
    public static final int PROFILE_REQUESTCODE = 38;
    public static final String USER = "user";
    public static final String RECIPE = "recipe";
    @ViewById
    ListView list;

    @Extra
    User user;

    @OptionsMenuItem(R.id.action_search)
    MenuItem menuSearch;

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
        adapter.update(recipeList);
    }


    @ItemClick
    void listItemClicked(Recipe item){
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
}
