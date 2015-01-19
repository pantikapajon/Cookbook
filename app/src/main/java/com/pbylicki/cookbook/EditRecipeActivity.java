package com.pbylicki.cookbook;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pbylicki.cookbook.data.Recipe;
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

import java.io.ByteArrayOutputStream;

@EActivity(R.layout.activity_add_recipe)
@OptionsMenu(R.menu.menu_browse)
public class EditRecipeActivity extends Activity {

    public static final int REQUESTCODE = 42;

    @ViewById
    TextView header;
    @ViewById
    EditText title;
    @ViewById
    EditText introduction;
    @ViewById
    EditText servings;
    @ViewById
    EditText preparationMinutes;
    @ViewById
    EditText cookingMinutes;
    @ViewById
    EditText ingredients;
    @ViewById
    EditText steps;
    @ViewById
    ImageView image;

    @Extra
    Bundle bundle;
    Recipe recipe;
    User user;
    int recipeId;
    String oldPictureBytes;

    @Bean
    @NonConfigurationInstance
    RestEditRecipeBackgroundTask restBackgroundTask;
    ProgressDialog ringProgressDialog;

    @AfterViews
    void init() {
        //Gets User and Selected Recipe
        if(user == null) user = (User)bundle.getSerializable(BrowseActivity.USER);
        recipe = (Recipe) bundle.getSerializable(BrowseActivity.RECIPE);
        recipeId = recipe.id;
        //Populates View
        header.setText(getString(R.string.edit_recipe_header));
        title.setText(recipe.title);
        introduction.setText(recipe.introduction);
        if(recipe.servings != null) servings.setText(Integer.toString(recipe.servings));
        if(recipe.preparationMinutes != null) preparationMinutes.setText(Integer.toString(recipe.preparationMinutes));
        if(recipe.cookingMinutes != null) cookingMinutes.setText(Integer.toString(recipe.cookingMinutes));
        ingredients.setText(recipe.ingredients);
        steps.setText(recipe.steps);
        if(recipe.pictureBytes != null) recipe.decodeAndSetImage(image);
        oldPictureBytes = recipe.pictureBytes;

        ringProgressDialog = new ProgressDialog(this);
        ringProgressDialog.setMessage("Saving recipe...");
        ringProgressDialog.setIndeterminate(true);

    }

    @Click
    public void addbuttonClicked(){
        //Checks if required fields are filled
        if(isEmpty(title)) { showToastRequired(title); return; }
        if(isEmpty(servings)) { showToastRequired(servings); return; }
        if(isEmpty(ingredients)) { showToastRequired(ingredients); return; }
        if(isEmpty(steps)) { showToastRequired(steps); return; }

        //passes edited object to Rest client
        ringProgressDialog.show();
        //recipe = new Recipe();
        recipe.id = recipeId;
        recipe.title = title.getText().toString();
        recipe.introduction = introduction.getText().toString();
        recipe.servings = Integer.parseInt(servings.getText().toString());
        if(!isEmpty(preparationMinutes)) recipe.preparationMinutes = Integer.parseInt(preparationMinutes.getText().toString());
        else recipe.preparationMinutes = null;
        if(!isEmpty(cookingMinutes)) recipe.cookingMinutes = Integer.parseInt(cookingMinutes.getText().toString());
        else recipe.cookingMinutes = null;
        recipe.ingredients = ingredients.getText().toString();
        recipe.steps = steps.getText().toString();
        recipe.ownerId = user.id;
        restBackgroundTask.editRecipe(user, recipe, oldPictureBytes);

    }

    @Click
    void imagebuttonClicked(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, AddRecipeActivity_.CAPTURE_IMAGE_THUMBNAIL_ACTIVITY_REQUEST_CODE);
    }

    public void editError(Exception e) {
        ringProgressDialog.dismiss();
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        e.printStackTrace();
    }

    public void editSuccess() {
        ringProgressDialog.dismiss();
        Toast.makeText(this, getString(R.string.edit_recipe_toast_edited), Toast.LENGTH_LONG).show();
        BrowseActivity_.intent(this).user(user).start();
    }

    @OptionsItem(R.id.action_login)
    void actionLoginSelected(){
        if(user == null) LoginActivity_.intent(this).startForResult(BrowseActivity_.LOGIN_REQUESTCODE);
        else Toast.makeText(this, getString(R.string.user_already_logged_in), Toast.LENGTH_LONG).show();
    }

    @OptionsItem(R.id.action_add)
    void actionAddSelected() {

        if(user == null) LoginActivity_.intent(this).startForResult(REQUESTCODE);
        else AddRecipeActivity_.intent(this).user(user).start();

    }

    @OptionsItem(R.id.action_profile)
    void actionProfileSelected() {
        if(user == null) LoginActivity_.intent(this).startForResult(BrowseActivity_.PROFILE_REQUESTCODE);
        else ProfileActivity_.intent(this).user(user).start();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode == RESULT_OK){
            if (user == null) user =(User)data.getSerializableExtra(LoginActivity.LOGINRESULT);
            switch (requestCode) {
                case REQUESTCODE:   AddRecipeActivity_.intent(this).user(user).start();
                                    break;
                case BrowseActivity_.LOGIN_REQUESTCODE: init();
                                                        break;
                case BrowseActivity_.PROFILE_REQUESTCODE:   ProfileActivity_.intent(this).user(user).start();
                                                            break;
                case AddRecipeActivity_.CAPTURE_IMAGE_THUMBNAIL_ACTIVITY_REQUEST_CODE:  getAndEncodeImage(data);
                                                                                        break;
                default:            break;
            }
        }
        if (resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "Login cancelled", Toast.LENGTH_LONG).show();
        }
    }

    private boolean isEmpty(EditText editText) {
        return editText.getText().toString().trim().length() == 0;
    }
    private void showToastRequired(EditText editText){
        Toast.makeText(this, getString(R.string.add_recipe_toast_required) + editText.getHint(), Toast.LENGTH_LONG).show();
    }
    private void getAndEncodeImage(Intent data){
        if(data == null) return;
        Bitmap bitmap = (Bitmap)data.getExtras().get("data");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        recipe.pictureBytes = Base64.encodeToString(b, Base64.DEFAULT);
        image.setImageDrawable(null);
        recipe.decodeAndSetImage(image);
        image.setAlpha(255);
    }
}
