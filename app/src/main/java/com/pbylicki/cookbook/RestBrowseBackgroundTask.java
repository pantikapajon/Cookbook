package com.pbylicki.cookbook;

import com.pbylicki.cookbook.data.PhoneBook;
import com.pbylicki.cookbook.data.RecipeList;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.rest.RestService;


@EBean
public class RestBrowseBackgroundTask {
    @RootContext
    BrowseActivity activity;
    @RestService
    CookbookRestClient restClient;
    @Background
    void getRecipeList() {
        try {
            restClient.setHeader("X-Dreamfactory-Application-Name", "cookbook");
            RecipeList recipeList = restClient.getRecipeList();
            publishResult(recipeList);
        } catch (Exception e) {
            publishError(e);
        }
    }
    @UiThread
    void publishResult(RecipeList recipeList) {
        activity.updateRecipeList(recipeList);
    }
    @UiThread
    void publishError(Exception e) {
        activity.showError(e);
    }
}
