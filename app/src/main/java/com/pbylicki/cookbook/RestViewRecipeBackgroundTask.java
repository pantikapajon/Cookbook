package com.pbylicki.cookbook;

import com.pbylicki.cookbook.data.Comment;
import com.pbylicki.cookbook.data.CommentList;
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
    void getComments(Recipe recipe) {
        try {
            restClient.setHeader("X-Dreamfactory-Application-Name", "cookbook");
            CommentList commentList = restClient.getCommentListForRecipe("recipeId=" + Integer.toString(recipe.id));
            publishResult(commentList);
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
    @UiThread
    void publishResult(CommentList commentList) {
        activity.updateCommentList(commentList);
    }
    @UiThread
    void publishError(Exception e) {
        activity.showError(e);
    }
    @UiThread
    void publishResultPost() {
        activity.updateCommentListAfterPost();
    }
}
