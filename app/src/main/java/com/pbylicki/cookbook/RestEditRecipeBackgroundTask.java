package com.pbylicki.cookbook;

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
    void editRecipe(User user, Recipe recipe) {
        try {
            restClient.setHeader("X-Dreamfactory-Application-Name", "cookbook");
            restClient.setHeader("X-Dreamfactory-Session-Token", user.sessionId);
            restClient.editRecipeEntry(recipe);
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
