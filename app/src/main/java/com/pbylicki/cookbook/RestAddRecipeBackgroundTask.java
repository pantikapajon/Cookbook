package com.pbylicki.cookbook;

import com.pbylicki.cookbook.data.Picture;
import com.pbylicki.cookbook.data.Recipe;
import com.pbylicki.cookbook.data.RecipeList;
import com.pbylicki.cookbook.data.User;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.rest.RestService;


@EBean
public class RestAddRecipeBackgroundTask {
    @RootContext
    AddRecipeActivity activity;
    @RestService
    CookbookRestClient restClient;
    @Background
    void addRecipe(User user, Recipe recipe) {
        try {
            restClient.setHeader("X-Dreamfactory-Application-Name", "cookbook");
            restClient.setHeader("X-Dreamfactory-Session-Token", user.sessionId);

            Recipe response = restClient.addRecipeEntry(recipe);

            if(recipe.pictureBytes != null){
                Picture picture = new Picture();
                picture.base64bytes = recipe.pictureBytes;
                picture.recipeId = response.id;
                picture.ownerId = user.id;
                restClient.addPictureEntry(picture);
            }
            publishResult();
        } catch (Exception e) {
            publishError(e);
        }
    }
    @UiThread
    void publishResult() {
        activity.addSuccess();
    }
    @UiThread
    void publishError(Exception e) {
        activity.addError(e);
    }
}
