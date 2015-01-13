package com.pbylicki.cookbook;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.pbylicki.cookbook.adapter.RecipeListAdapter;
import com.pbylicki.cookbook.data.RecipeList;
import com.pbylicki.cookbook.data.User;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.NonConfigurationInstance;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_browse)
@OptionsMenu(R.menu.menu_browse)
public class BrowseActivity extends Activity {

    public static final int REQUESTCODE = 42;
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
        restBackgroundTask.getRecipeList();
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

    @OptionsItem(R.id.action_add)
    void actionAddSelected() {

        if(user == null) LoginActivity_.intent(this).startForResult(REQUESTCODE);
        else Toast.makeText(this, "Add Recipe", Toast.LENGTH_LONG).show();

    }

    @OptionsItem(R.id.action_profile)
    void actionProfileSelected() {
        Toast.makeText(this, "View Profile", Toast.LENGTH_LONG).show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUESTCODE) {
            if(resultCode == RESULT_OK){
                user =(User)data.getSerializableExtra(LoginActivity.LOGINRESULT);
                Toast.makeText(this, user.sessionId, Toast.LENGTH_LONG).show();
                //Start next Activity
            }
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Login cancelled", Toast.LENGTH_LONG).show();
            }
        }
    }
}
