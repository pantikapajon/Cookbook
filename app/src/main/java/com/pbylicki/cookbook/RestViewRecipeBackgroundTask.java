package com.pbylicki.cookbook;

import com.pbylicki.cookbook.data.Recipe;
import com.pbylicki.cookbook.data.User;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.rest.RestService;


@EBean
public class RestViewRecipeBackgroundTask {
    @RootContext
    ViewRecipeActivity activity;
    @RestService
    CookbookRestClient restClient;
    @Background
    void addRecipe(User user, Recipe recipe) {
        try {
            restClient.setHeader("X-Dreamfactory-Application-Name", "cookbook");
            restClient.setHeader("X-Dreamfactory-Session-Token", user.sessionId);
            restClient.addRecipeEntry(recipe);
            publishResult();
        } catch (Exception e) {
            publishError(e);
        }
    }
    @UiThread
    void publishResult() {
        //activity.addSuccess();
    }
    @UiThread
    void publishError(Exception e) {
        //activity.addError(e);
    }
}
