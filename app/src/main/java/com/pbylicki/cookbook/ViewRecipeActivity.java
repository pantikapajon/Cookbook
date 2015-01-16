package com.pbylicki.cookbook;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.pbylicki.cookbook.adapter.CommentListAdapter;
import com.pbylicki.cookbook.data.Comment;
import com.pbylicki.cookbook.data.CommentList;
import com.pbylicki.cookbook.data.Recipe;
import com.pbylicki.cookbook.data.RecipeList;
import com.pbylicki.cookbook.data.User;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.NonConfigurationInstance;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_view_recipe)
@OptionsMenu(R.menu.menu_browse)
public class ViewRecipeActivity extends Activity {

    public static final int COMMENT_REQUESTCODE = 44;
    public static final int DELETE_RECIPE_REQUESTCODE = 46;
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
    CommentListAdapter adapter;
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

        commentlist.setAdapter(adapter);
        ringProgressDialog = new ProgressDialog(this);
        ringProgressDialog.setMessage("Downloading data...");
        ringProgressDialog.setIndeterminate(true);
        ringProgressDialog.show();
        restBackgroundTask.getComments(recipe);
    }

    public void showError(Exception e) {
        ringProgressDialog.dismiss();
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        e.printStackTrace();
    }

    public void updateCommentList(CommentList commentList) {
        ringProgressDialog.dismiss();
        adapter.update(commentList);
    }
    public void updateCommentListAfterPost() {
        newcomment.setText("");
        restBackgroundTask.getComments(recipe);
    }
    public void deleteRecipeSuccess(){
        ringProgressDialog.dismiss();
        Toast.makeText(this, getString(R.string.view_recipe_recipe_deleted), Toast.LENGTH_LONG).show();
        BrowseActivity_.intent(this).user(user).start();
    }

    @Click
    void deletebuttonClicked(){
        if(user == null) {
            LoginActivity_.intent(this).startForResult(DELETE_RECIPE_REQUESTCODE);
        } else {
            if (Integer.toString(user.id) == Integer.toString(recipe.ownerId)) {
                ringProgressDialog.setMessage("Removing Recipe...");
                ringProgressDialog.show();
                restBackgroundTask.deleteRecipe(user, recipe);
            } else {
                Toast.makeText(this, getString(R.string.view_recipe_action_denied), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Click
    void commentbtnClicked(){
        if(newcomment.getText().toString().trim().length() == 0){
            Toast.makeText(this, getString(R.string.view_recipe_comment_nonempty), Toast.LENGTH_LONG).show();
            return;
        }
        //User must be logged in to start Activity
        if(user == null) LoginActivity_.intent(this).startForResult(COMMENT_REQUESTCODE);
        else {
            ringProgressDialog.show();
            restBackgroundTask.postComment(user, getNewComment());
        }

    }

    @OptionsItem(R.id.action_add)
    void actionAddSelected() {
        //User must be logged in to start Activity
        if(user == null) LoginActivity_.intent(this).startForResult(BrowseActivity_.REQUESTCODE);
        else AddRecipeActivity_.intent(this).user(user).start();

    }

    @OptionsItem(R.id.action_profile)
    void actionProfileSelected() {
        Toast.makeText(this, "View Profile", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra(BrowseActivity_.USER, user);
        startActivity(intent);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == BrowseActivity_.REQUESTCODE) {
            if(resultCode == RESULT_OK){
                user =(User)data.getSerializableExtra(LoginActivity.LOGINRESULT);
                AddRecipeActivity_.intent(this).user(user).start();
            }
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Login cancelled", Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == COMMENT_REQUESTCODE) {
            if(resultCode == RESULT_OK){
                user =(User)data.getSerializableExtra(LoginActivity.LOGINRESULT);
                ringProgressDialog.show();
                restBackgroundTask.postComment(user, getNewComment());
            }
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Login cancelled", Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == DELETE_RECIPE_REQUESTCODE) {
            if(resultCode == RESULT_OK){
                user =(User)data.getSerializableExtra(LoginActivity.LOGINRESULT);
                deletebuttonClicked();
            }
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Login cancelled", Toast.LENGTH_LONG).show();
            }
        }
    }

    private Comment getNewComment(){
        Comment comment = new Comment();
        comment.ownerId = user.id;
        comment.recipeId = recipe.id;
        comment.text = newcomment.getText().toString();
        return comment;
    }
}
