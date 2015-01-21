package com.pbylicki.cookbook;

import android.text.TextUtils;

import com.pbylicki.cookbook.data.Picture;
import com.pbylicki.cookbook.data.PictureList;
import com.pbylicki.cookbook.data.Recipe;
import com.pbylicki.cookbook.data.RecipeList;
import com.pbylicki.cookbook.data.User;
import com.pbylicki.cookbook.data.UserInfo;
import com.pbylicki.cookbook.data.UserInfoList;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.rest.RestService;

import java.util.HashSet;
import java.util.Hashtable;


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
            PictureList pictureList = restClient.getPictureListForRecipe("recipeId>0");
            Hashtable<Integer, Picture> pictureHashtable = new Hashtable<Integer, Picture>();
            for(Picture picture : pictureList.records) pictureHashtable.put(picture.recipeId, picture);
            for(Recipe recipe : recipeList.records){
                if(pictureHashtable.containsKey(recipe.id)) recipe.pictureBytes = pictureHashtable.get(recipe.id).base64bytes;
            }
            if(user != null){
                restClient.setHeader("X-Dreamfactory-Session-Token", user.sessionId);
                HashSet<Integer> uniqueUsers = new HashSet<Integer>();
                for(Recipe recipe : recipeList.records) uniqueUsers.add(recipe.ownerId);
                String ids = TextUtils.join(",", uniqueUsers);
                UserInfoList userInfoList = restClient.getUserInfoForIds(ids);
                Hashtable<Integer, UserInfo> userInfoHashtable = new Hashtable<Integer, UserInfo>();
                for(UserInfo userInfo : userInfoList.records) userInfoHashtable.put(userInfo.id, userInfo);
                for(Recipe recipe : recipeList.records){
                    if(userInfoHashtable.containsKey(recipe.ownerId)) recipe.author = userInfoHashtable.get(recipe.ownerId).display_name;
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
