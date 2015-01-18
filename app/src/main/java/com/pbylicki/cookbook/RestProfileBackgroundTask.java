package com.pbylicki.cookbook;

import android.text.TextUtils;

import com.pbylicki.cookbook.data.Like;
import com.pbylicki.cookbook.data.LikeList;
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

import java.util.HashSet;


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
    @Background
    void getLikeRecipeList(User user) {
        try{
            restClient.setHeader("X-Dreamfactory-Application-Name", "cookbook");
            restClient.setHeader("X-Dreamfactory-Session-Token", user.sessionId);
            //getLikeListForRecipe method applies to all kinds of filters
            LikeList likeList = restClient.getLikeListForRecipe("ownerId=" + Integer.toString(user.id));
            HashSet<Integer> uniqueLikes = new HashSet<Integer>();
            for(Like like : likeList.records) uniqueLikes.add(like.recipeId);
            String ids = TextUtils.join(",", uniqueLikes);
            RecipeList recipeList = restClient.getRecipeListForIds(ids);
            UserInfo userInfo; PictureList pictureList;
            for(Recipe recipe : recipeList.records){
                pictureList = restClient.getPictureListForRecipe("recipeId=" + Integer.toString(recipe.id));
                if(pictureList.records.size()>0) recipe.pictureBytes = pictureList.records.get(0).base64bytes;
                userInfo = restClient.getUserInfo(recipe.ownerId);
                recipe.author = userInfo.display_name;
            }
            /*for(Recipe recipe : recipeList.records){
                userInfo = restClient.getUserInfo(recipe.ownerId);
                recipe.author = userInfo.display_name;
            }*/
            publishLikeResult(recipeList);
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
    @UiThread
    void publishLikeResult(RecipeList recipeList) {
        activity.updateLikeList(recipeList);
    }
}
