package com.pbylicki.cookbook;

import com.pbylicki.cookbook.data.PictureList;
import com.pbylicki.cookbook.data.Recipe;
import com.pbylicki.cookbook.data.RecipeList;
import com.pbylicki.cookbook.data.User;
import com.pbylicki.cookbook.data.UserInfo;

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
    void getRecipeList(User user) {
        try {
            restClient.setHeader("X-Dreamfactory-Application-Name", "cookbook");
            RecipeList recipeList = restClient.getRecipeList();
            for(Recipe recipe : recipeList.records){
                PictureList pictureList = restClient.getPictureListForRecipe("recipeId=" + Integer.toString(recipe.id));
                if(pictureList.records.size()>0) recipe.pictureBytes = pictureList.records.get(0).base64bytes;
            }
            if(user != null){
                restClient.setHeader("X-Dreamfactory-Session-Token", user.sessionId);
                for(Recipe recipe : recipeList.records){
                    UserInfo userInfo = restClient.getUserInfo(recipe.ownerId);
                    recipe.author = userInfo.display_name;
                }
            }
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
