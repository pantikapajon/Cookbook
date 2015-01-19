package com.pbylicki.cookbook;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.EditText;
import android.widget.ImageView;
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
public class AddRecipeActivity extends Activity {

    public static final int REQUESTCODE = 42;
    public static final int CAPTURE_IMAGE_THUMBNAIL_ACTIVITY_REQUEST_CODE = 1939;

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
    User user;

    Recipe recipe;

    @Bean
    @NonConfigurationInstance
    RestAddRecipeBackgroundTask restBackgroundTask;
    ProgressDialog ringProgressDialog;

    @AfterViews
    void init() {
        recipe = new Recipe();
        ringProgressDialog = new ProgressDialog(this);
        ringProgressDialog.setMessage("Adding recipe...");
        ringProgressDialog.setIndeterminate(true);
    }

    @Click
    public void addbuttonClicked(){
        //Checks if required fields are filled
        if(isEmpty(title)) { showToastRequired(title); return; }
        if(isEmpty(servings)) { showToastRequired(servings); return; }
        if(isEmpty(ingredients)) { showToastRequired(ingredients); return; }
        if(isEmpty(steps)) { showToastRequired(steps); return; }
        //Creates new Recipe object and passes it to Rest client
        ringProgressDialog.show();
        //Recipe recipe = new Recipe();
        recipe.title = title.getText().toString();
        recipe.introduction = introduction.getText().toString();
        recipe.servings = Integer.parseInt(servings.getText().toString());
        if(!isEmpty(preparationMinutes)) recipe.preparationMinutes = Integer.parseInt(preparationMinutes.getText().toString());
        if(!isEmpty(cookingMinutes)) recipe.cookingMinutes = Integer.parseInt(cookingMinutes.getText().toString());
        recipe.ingredients = ingredients.getText().toString();
        recipe.steps = steps.getText().toString();
        recipe.ownerId = user.id;
        restBackgroundTask.addRecipe(user, recipe);

    }

    @Click
    void imagebuttonClicked(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAPTURE_IMAGE_THUMBNAIL_ACTIVITY_REQUEST_CODE);
    }


    public void addError(Exception e) {
        ringProgressDialog.dismiss();
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        e.printStackTrace();
    }

    public void addSuccess() {
        ringProgressDialog.dismiss();
        Toast.makeText(this, getString(R.string.add_recipe_toast_added), Toast.LENGTH_LONG).show();
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
                case CAPTURE_IMAGE_THUMBNAIL_ACTIVITY_REQUEST_CODE: getAndEncodeImage(data);
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
