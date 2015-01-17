package com.pbylicki.cookbook;

import com.pbylicki.cookbook.data.Comment;
import com.pbylicki.cookbook.data.CommentList;
import com.pbylicki.cookbook.data.Like;
import com.pbylicki.cookbook.data.LikeList;
import com.pbylicki.cookbook.data.Recipe;
import com.pbylicki.cookbook.data.User;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.rest.RestService;

import java.util.HashSet;


@EBean
public class RestViewRecipeBackgroundTask {
    @RootContext
    ViewRecipeActivity activity;
    @RestService
    CookbookRestClient restClient;
    @Background
    void getComments(Recipe recipe) {
        try {
            restClient.setHeader("X-Dreamfactory-Application-Name", "cookbook");
            CommentList commentList = restClient.getCommentListForRecipe("recipeId=" + Integer.toString(recipe.id));
            LikeList likeList = restClient.getLikeListForRecipe("recipeId=" + Integer.toString(recipe.id));
            publishResult(commentList, likeList.getLikesForRecipe());
        } catch (Exception e) {
            publishError(e);
        }
    }
    @Background
    void postComment(User user, Comment comment) {
        try {
            restClient.setHeader("X-Dreamfactory-Application-Name", "cookbook");
            restClient.setHeader("X-Dreamfactory-Session-Token", user.sessionId);
            restClient.addCommentEntry(comment);
            publishResultPost();
        } catch (Exception e) {
            publishError(e);
        }
    }
    @Background
    void deleteRecipe(User user, Recipe recipe) {
        try {
            restClient.setHeader("X-Dreamfactory-Application-Name", "cookbook");
            restClient.setHeader("X-Dreamfactory-Session-Token", user.sessionId);
            //delete likes and picture
            restClient.deleteLikeEntryForRecipe("recipeId=" + Integer.toString(recipe.id));
            restClient.deleteCommentEntryForRecipe("recipeId=" + Integer.toString(recipe.id));
            restClient.deleteRecipeEntry(recipe.id);
            publishResultDelete();
        } catch (Exception e) {
            publishError(e);
        }
    }
    @Background
    void postLike(User user, Like like) {
        try {
            restClient.setHeader("X-Dreamfactory-Application-Name", "cookbook");
            restClient.setHeader("X-Dreamfactory-Session-Token", user.sessionId);
            restClient.addLikeEntry(like);
            publishResultLike();
        } catch (Exception e) {
            publishError(e);
        }
    }
    @Background
    void deleteLike(User user, Recipe recipe) {
        try {
            restClient.setHeader("X-Dreamfactory-Application-Name", "cookbook");
            restClient.setHeader("X-Dreamfactory-Session-Token", user.sessionId);
            restClient.deleteLikeEntryForRecipe("recipeId=" + Integer.toString(recipe.id)+ " AND ownerId="+ Integer.toString(user.id));
            publishResultLikeDelete();
        } catch (Exception e) {
            publishError(e);
        }
    }
    @UiThread
    void publishResult(CommentList commentList, HashSet<Integer> likeHash) {
        activity.updateCommentList(commentList);
        activity.setLikeHash(likeHash);
    }
    @UiThread
    void publishError(Exception e) {
        activity.showError(e);
    }
    @UiThread
    void publishResultPost() {
        activity.updateCommentListAfterPost();
    }
    @UiThread
    void publishResultDelete() {
        activity.deleteRecipeSuccess();
    }
    @UiThread
    void publishResultLike() {
        activity.postLikeSuccess();
    }
    @UiThread
    void publishResultLikeDelete() {
        activity.deleteLikeSuccess();
    }
}
