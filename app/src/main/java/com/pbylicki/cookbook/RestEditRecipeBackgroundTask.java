package com.pbylicki.cookbook;

import com.pbylicki.cookbook.data.Picture;
import com.pbylicki.cookbook.data.Recipe;
import com.pbylicki.cookbook.data.User;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.rest.RestService;


@EBean
public class RestEditRecipeBackgroundTask {
    @RootContext
    EditRecipeActivity activity;
    @RestService
    CookbookRestClient restClient;
    @Background
    void editRecipe(User user, Recipe recipe, String oldPictureBytes) {
        try {
            restClient.setHeader("X-Dreamfactory-Application-Name", "cookbook");
            restClient.setHeader("X-Dreamfactory-Session-Token", user.sessionId);
            restClient.editRecipeEntry(recipe);
            if(oldPictureBytes != recipe.pictureBytes){
                restClient.deletePictureEntryForRecipe("recipeId=" + Integer.toString(recipe.id));
                Picture picture = new Picture();
                picture.base64bytes = recipe.pictureBytes;
                picture.recipeId = recipe.id;
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
        activity.editSuccess();
    }
    @UiThread
    void publishError(Exception e) {
        activity.editError(e);
    }
}
