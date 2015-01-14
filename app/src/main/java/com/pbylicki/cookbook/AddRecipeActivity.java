package com.pbylicki.cookbook;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.pbylicki.cookbook.adapter.RecipeListAdapter;
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
import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_add_recipe)
@OptionsMenu(R.menu.menu_browse)
public class AddRecipeActivity extends Activity {

    public static final int REQUESTCODE = 42;

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

    @Extra
    User user;

    @Bean
    @NonConfigurationInstance
    RestAddRecipeBackgroundTask restBackgroundTask;
    ProgressDialog ringProgressDialog;

    @AfterViews
    void init() {
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
        Recipe recipe = new Recipe();
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

    @OptionsItem(R.id.action_add)
    void actionAddSelected() {

        if(user == null) LoginActivity_.intent(this).startForResult(REQUESTCODE);
        else AddRecipeActivity_.intent(this).user(user).start();

    }

    @OptionsItem(R.id.action_profile)
    void actionProfileSelected() {
        Toast.makeText(this, "View Profile", Toast.LENGTH_LONG).show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUESTCODE) {
            if(resultCode == RESULT_OK){
                user =(User)data.getSerializableExtra(LoginActivity.LOGINRESULT);
                AddRecipeActivity_.intent(this).user(user).start();
            }
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Login cancelled", Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean isEmpty(EditText editText) {
        return editText.getText().toString().trim().length() == 0;
    }
    private void showToastRequired(EditText editText){
        Toast.makeText(this, getString(R.string.add_recipe_toast_required) + editText.getHint(), Toast.LENGTH_LONG).show();
    }
}
