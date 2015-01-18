package com.pbylicki.cookbook;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.pbylicki.cookbook.adapter.RecipeListAdapter;
import com.pbylicki.cookbook.data.Recipe;
import com.pbylicki.cookbook.data.RecipeList;
import com.pbylicki.cookbook.data.User;
import com.pbylicki.cookbook.data.UserInfo;

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

@EActivity(R.layout.activity_profile)
@OptionsMenu(R.menu.menu_browse)
public class ProfileActivity extends Activity {

    public static final int REQUESTCODE = 42;
    public static final int LOGIN_REQUESTCODE = 40;
    public static final String USER = "user";
    public static final String RECIPE = "recipe";
    public static final String RECIPELIST = "recipelist";
    public static final String LIKELIST = "likelist";
    public static final String USERINFO = "userinfo";

    @ViewById
    TextView username;
    @ViewById
    TextView email;
    @ViewById
    TextView created;
    @ViewById
    TextView last_modified;
    @ViewById
    ListView likelist;
    @ViewById
    ListView recipelist;
    @ViewById
    TextView recipeempty;
    @ViewById
    TextView likeempty;

    @Extra
    User user;

    @OptionsMenuItem(R.id.action_search)
    MenuItem menuSearch;

    @Bean
    RecipeListAdapter likeadapter;
    @Bean
    RecipeListAdapter recipeadapter;

    @Bean
    @NonConfigurationInstance
    RestProfileBackgroundTask restBackgroundTask;
    ProgressDialog ringProgressDialog;

    UserInfo userInfo;
    RecipeList recipeList;
    RecipeList likeList;

    @AfterViews
    void init() {
        likelist.setAdapter(likeadapter);
        recipelist.setAdapter(recipeadapter);
        ringProgressDialog = new ProgressDialog(this);
        ringProgressDialog.setMessage("Downloading recipes...");
        ringProgressDialog.setIndeterminate(true);
        ringProgressDialog.show();
        restBackgroundTask.getUserInfoRecipeList(user);
        restBackgroundTask.getLikeRecipeList(user);
    }

    public void showError(Exception e) {
        ringProgressDialog.dismiss();
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        e.printStackTrace();
    }

    public void updateRecipeList(RecipeList recipeList) {
        ringProgressDialog.dismiss();
        this.recipeList = recipeList;
        recipeadapter.update(recipeList);
        if(recipeadapter.getCount() == 0) recipeempty.setVisibility(View.VISIBLE);
        ViewGroup.LayoutParams params = this.recipelist.getLayoutParams();
        int dim = (int)getResources().getDimension(R.dimen.recipe_list_item_height);
        params.height = Math.max(recipeadapter.getCount() * dim, dim);
        this.recipelist.setLayoutParams(params);
    }
    public void updateLikeList(RecipeList recipeList) {
        ringProgressDialog.dismiss();
        this.likeList = recipeList;
        likeadapter.update(recipeList);
        if(likeadapter.getCount() == 0) likeempty.setVisibility(View.VISIBLE);
        ViewGroup.LayoutParams params = this.likelist.getLayoutParams();
        int dim = (int)getResources().getDimension(R.dimen.recipe_list_item_height);
        params.height = likeadapter.getCount() * dim;
        this.likelist.setLayoutParams(params);
    }
    public void updateUserInfo(UserInfo userInfo){
        this.userInfo = userInfo;
        username.setText(userInfo.display_name);
        email.setText(getString(R.string.profile_email) + userInfo.email);
        created.setText(getString(R.string.profile_created) + userInfo.created_date);
        last_modified.setText(getString(R.string.profile_last_modified) + userInfo.last_modified_date);
    }
/*    @Click
    void moveClicked(){
        Bundle bundle = new Bundle();
        bundle.putSerializable(USER, user);
        bundle.putSerializable(USERINFO, userInfo);
        bundle.putSerializable(RECIPELIST, recipeList);
        bundle.putSerializable(LIKELIST, likeList);
        Intent i = new Intent(this, MyActivity.class);
        i.putExtras(bundle);
        startActivity(i);
    }*/


    @ItemClick
    void recipelistItemClicked(Recipe item){
        if(item.id == null) return;
        //Toast.makeText(this, Integer.toString(item.id)+Integer.toString(item.ownerId) , Toast.LENGTH_LONG).show();
        Bundle bundle = new Bundle();
        bundle.putSerializable(USER, user);
        bundle.putSerializable(RECIPE, item);
        ViewRecipeActivity_.intent(this).bundle(bundle).start();
    }
    @ItemClick
    void likelistItemClicked(Recipe item){
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

/*    @OptionsItem(R.id.action_profile)
    void actionProfileSelected() {
        Toast.makeText(this, "View Profile", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra(USER, user);
        startActivity(intent);
    }*/

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode == RESULT_OK){
            user =(User)data.getSerializableExtra(LoginActivity.LOGINRESULT);
            switch (requestCode) {
                case REQUESTCODE:   AddRecipeActivity_.intent(this).user(user).start();
                                    break;
                case LOGIN_REQUESTCODE:     init();
                                            break;
                default:            break;
            }
        }
        if (resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "Login cancelled", Toast.LENGTH_LONG).show();
        }
    }
}
