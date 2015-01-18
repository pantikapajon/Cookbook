package com.pbylicki.cookbook;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.pbylicki.cookbook.adapter.CommentListAdapter;
import com.pbylicki.cookbook.data.Comment;
import com.pbylicki.cookbook.data.CommentList;
import com.pbylicki.cookbook.data.Like;
import com.pbylicki.cookbook.data.Recipe;
import com.pbylicki.cookbook.data.User;
import com.pbylicki.cookbook.data.UserInfo;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.NonConfigurationInstance;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;

import java.util.HashSet;

@EActivity(R.layout.activity_view_recipe)
@OptionsMenu(R.menu.menu_browse)
public class ViewRecipeActivity extends Activity {

    public static final int COMMENT_REQUESTCODE = 44;
    public static final int DELETE_RECIPE_REQUESTCODE = 46;
    public static final int EDIT_RECIPE_REQUESTCODE = 48;
    public static final int LIKE_RECIPE_REQUESTCODE = 50;
    @Extra
    Bundle bundle;

    @ViewById
    TextView title;
    @ViewById
    ImageView image;
    @ViewById
    TextView created;
    @ViewById
    TextView author;
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
    @ViewById
    ImageButton likebutton;

    @Bean
    CommentListAdapter adapter;
    @Bean
    @NonConfigurationInstance
    RestViewRecipeBackgroundTask restBackgroundTask;
    Recipe recipe;
    User user;
    //Stores unique likes' ownerIds for recipe
    HashSet<Integer> likeHash;
    ProgressDialog ringProgressDialog;

    @AfterViews
    void init(){
        //Gets User and Selected Recipe
        if(user == null) user = (User)bundle.getSerializable(BrowseActivity.USER);
        recipe = (Recipe) bundle.getSerializable(BrowseActivity.RECIPE);
        //Populates View
        if(recipe.pictureBytes != null) recipe.decodeAndSetImage(image);
        title.setText(recipe.title);
        created.setText(getString(R.string.add_recipe_created_date)+recipe.getCreatedDate().toString());
        introduction.setText(recipe.introduction);
        if(recipe.servings != null) servings.setText(getString(R.string.add_recipe_servings)+": " +Integer.toString(recipe.servings));
        if(recipe.preparationMinutes != null) preparationMinutes.setText(getString(R.string.add_recipe_preparationMinutes)+": " +Integer.toString(recipe.preparationMinutes));
        if(recipe.cookingMinutes != null) cookingMinutes.setText(getString(R.string.add_recipe_cookingMinutes)+": " +Integer.toString(recipe.cookingMinutes));
        ingredients.setText(recipe.ingredients);
        steps.setText(recipe.steps);
        if(recipe.author != null) author.setText(getString(R.string.view_recipe_author)+ " " + recipe.author);
        else author.setText(getString(R.string.view_recipe_author)+" User " + Integer.toString(recipe.ownerId));

        commentlist.setAdapter(adapter);
        ringProgressDialog = new ProgressDialog(this);
        ringProgressDialog.setMessage("Downloading data...");
        ringProgressDialog.setIndeterminate(true);
        ringProgressDialog.show();
        restBackgroundTask.getComments(recipe, user);
    }

    public void showError(Exception e) {
        ringProgressDialog.dismiss();
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        e.printStackTrace();
    }

    public void updateCommentList(CommentList commentList) {
        ringProgressDialog.dismiss();
        adapter.update(commentList);
        ViewGroup.LayoutParams params = this.commentlist.getLayoutParams();
        int dim = (int)getResources().getDimension(R.dimen.comment_list_item_height);
        params.height = adapter.getCount() * dim;
        this.commentlist.setLayoutParams(params);
    }
    public void updateCommentListAfterPost() {
        newcomment.setText("");
        restBackgroundTask.getComments(recipe, user);
    }
    public void deleteRecipeSuccess(){
        ringProgressDialog.dismiss();
        Toast.makeText(this, getString(R.string.view_recipe_recipe_deleted), Toast.LENGTH_LONG).show();
        BrowseActivity_.intent(this).user(user).start();
    }
    public void setLikeHash(HashSet<Integer> likeHash){
        this.likeHash = likeHash;
        if (user != null) switchLikeButton(likeHash.contains(user.id));
    }
    public void setRecipeAuthor(UserInfo userInfo){
        recipe.author = userInfo.display_name;
        author.setText(getString(R.string.view_recipe_author)+recipe.author);
    }
    public void postLikeSuccess(){
        likeHash.add(user.id);
        switchLikeButton(likeHash.contains(user.id));
        Toast.makeText(this, getString(R.string.view_recipe_like_added), Toast.LENGTH_LONG).show();
    }
    public void deleteLikeSuccess(){
        likeHash.remove(user.id);
        switchLikeButton(likeHash.contains(user.id));
        Toast.makeText(this, getString(R.string.view_recipe_like_deleted), Toast.LENGTH_LONG).show();
    }

    @Click
    void likebuttonClicked(){
        if(user == null) {
            LoginActivity_.intent(this).startForResult(LIKE_RECIPE_REQUESTCODE);
        } else if (!likeHash.contains(user.id)) {
            Like like = new Like();
            like.ownerId = user.id;
            like.recipeId = recipe.id;
            restBackgroundTask.postLike(user, like);
        } else {
            restBackgroundTask.deleteLike(user, recipe);
        }
    }

    @Click
    void editbuttonClicked(){
        if(user == null) {
            LoginActivity_.intent(this).startForResult(EDIT_RECIPE_REQUESTCODE);
        } else {
            if (Integer.toString(user.id) == Integer.toString(recipe.ownerId)) {
                Bundle editBundle = new Bundle();
                editBundle.putSerializable(BrowseActivity_.USER, user);
                editBundle.putSerializable(BrowseActivity_.RECIPE, recipe);
                EditRecipeActivity_.intent(this).bundle(editBundle).start();
            } else {
                Toast.makeText(this, getString(R.string.view_recipe_action_denied), Toast.LENGTH_LONG).show();
            }
        }
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
    @OptionsItem(R.id.action_login)
    void actionLoginSelected(){
        if(user == null) LoginActivity_.intent(this).startForResult(BrowseActivity_.LOGIN_REQUESTCODE);
        else Toast.makeText(this, getString(R.string.user_already_logged_in), Toast.LENGTH_LONG).show();
    }

    @OptionsItem(R.id.action_add)
    void actionAddSelected() {
        //User must be logged in to start Activity
        if(user == null) LoginActivity_.intent(this).startForResult(BrowseActivity_.REQUESTCODE);
        else AddRecipeActivity_.intent(this).user(user).start();
    }

    @OptionsItem(R.id.action_profile)
    void actionProfileSelected() {
        if(user == null) LoginActivity_.intent(this).startForResult(BrowseActivity_.PROFILE_REQUESTCODE);
        else ProfileActivity_.intent(this).user(user).start();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode == RESULT_OK){
            user =(User)data.getSerializableExtra(LoginActivity.LOGINRESULT);
            switchLikeButton(likeHash.contains(user.id));
            switch (requestCode) {
                case BrowseActivity_.REQUESTCODE:   AddRecipeActivity_.intent(this).user(user).start();
                                                    break;
                case BrowseActivity_.LOGIN_REQUESTCODE: init();
                                                        break;
                case BrowseActivity_.PROFILE_REQUESTCODE:   ProfileActivity_.intent(this).user(user).start();
                                                            break;
                case COMMENT_REQUESTCODE:   ringProgressDialog.show();
                                            restBackgroundTask.postComment(user, getNewComment());
                                            break;
                case DELETE_RECIPE_REQUESTCODE: deletebuttonClicked();
                                                break;
                case EDIT_RECIPE_REQUESTCODE:   editbuttonClicked();
                                                break;
                case LIKE_RECIPE_REQUESTCODE:   likebuttonClicked();
                                                break;
                default:                        break;
            }
        }
        if (resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "Login cancelled", Toast.LENGTH_LONG).show();
        }
    }

    private Comment getNewComment(){
        Comment comment = new Comment();
        comment.ownerId = user.id;
        comment.recipeId = recipe.id;
        comment.text = newcomment.getText().toString();
        return comment;
    }
    private void switchLikeButton(Boolean test){
        if(test) likebutton.setImageResource(R.drawable.ic_action_bad);
        else likebutton.setImageResource(R.drawable.ic_action_good);
    }
}