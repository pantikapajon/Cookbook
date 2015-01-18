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
public class RestProfileBackgroundTask {
    @RootContext
    ProfileActivity activity;
    @RestService
    CookbookRestClient restClient;
    @Background
    void getUserInfoRecipeList(User user) {
        try {
            restClient.setHeader("X-Dreamfactory-Application-Name", "cookbook");
            restClient.setHeader("X-Dreamfactory-Session-Token", user.sessionId);
            UserInfo userInfo = restClient.getUserInfo(user.id);
            RecipeList recipeList = restClient.getRecipeListForOwner("ownerId=" + Integer.toString(user.id));
            for(Recipe recipe : recipeList.records){
                PictureList pictureList = restClient.getPictureListForRecipe("recipeId=" + Integer.toString(recipe.id));
                if(pictureList.records.size()>0) recipe.pictureBytes = pictureList.records.get(0).base64bytes;
                recipe.author = userInfo.display_name;
            }
            publishResult(recipeList, userInfo);
        } catch (Exception e) {
            publishError(e);
        }
    }

    @UiThread
    void publishResult(RecipeList recipeList, UserInfo userInfo) {
        activity.updateRecipeList(recipeList);
        activity.updateUserInfo(userInfo);
    }
    @UiThread
    void publishError(Exception e) {
        activity.showError(e);
    }
}
