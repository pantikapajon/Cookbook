package com.pbylicki.cookbook;

import com.pbylicki.cookbook.data.Comment;
import com.pbylicki.cookbook.data.CommentList;
import com.pbylicki.cookbook.data.EmailAndPassword;
import com.pbylicki.cookbook.data.Like;
import com.pbylicki.cookbook.data.LikeList;
import com.pbylicki.cookbook.data.Picture;
import com.pbylicki.cookbook.data.PictureList;
import com.pbylicki.cookbook.data.Recipe;
import com.pbylicki.cookbook.data.RecipeList;
import com.pbylicki.cookbook.data.User;
import com.pbylicki.cookbook.data.UserInfo;

import org.androidannotations.annotations.rest.Delete;
import org.androidannotations.annotations.rest.Get;
import org.androidannotations.annotations.rest.Post;
import org.androidannotations.annotations.rest.Put;
import org.androidannotations.annotations.rest.RequiresHeader;
import org.androidannotations.annotations.rest.Rest;
import org.androidannotations.api.rest.RestClientHeaders;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

@Rest(rootUrl = "http://pegaz.wzr.ug.edu.pl:8080/rest", converters = { MappingJackson2HttpMessageConverter.class })
@RequiresHeader({"X-Dreamfactory-Application-Name"})
public interface CookbookRestClient extends RestClientHeaders {
    @Get("/db/recipes")
    RecipeList getRecipeList();
    @Get("/db/recipes?filter={path}")
    RecipeList getRecipeListForOwner(String path);
    @Get("/db/recipes?ids={path}")
    RecipeList getRecipeListForIds(String path);
    @Post("/db/recipes")
    @RequiresHeader({"X-Dreamfactory-Session-Token","X-Dreamfactory-Application-Name" })
    Recipe addRecipeEntry(Recipe recipe);
    @Put("/db/recipes")
    @RequiresHeader({"X-Dreamfactory-Session-Token","X-Dreamfactory-Application-Name" })
    void editRecipeEntry(Recipe recipe);
    @Delete("/db/recipes/{id}")
    @RequiresHeader({"X-Dreamfactory-Session-Token","X-Dreamfactory-Application-Name" })
    void deleteRecipeEntry(Integer id);

    @Get("/db/comments")
    CommentList getCommentList();
    @Get("/db/comments?filter={path}")
    CommentList getCommentListForRecipe(String path);
    @Post("/db/comments")
    @RequiresHeader({"X-Dreamfactory-Session-Token","X-Dreamfactory-Application-Name" })
    void addCommentEntry(Comment comment);
    @Put("/db/comments")
    @RequiresHeader({"X-Dreamfactory-Session-Token","X-Dreamfactory-Application-Name" })
    void editCommentEntry(Comment comment);
    @Delete("/db/comments/{id}")
    @RequiresHeader({"X-Dreamfactory-Session-Token","X-Dreamfactory-Application-Name" })
    void deleteCommentEntry(Integer id);
    @Delete("/db/comments?filter={path}")
    @RequiresHeader({"X-Dreamfactory-Session-Token","X-Dreamfactory-Application-Name" })
    void deleteCommentEntryForRecipe(String path);

    @Get("/db/likes")
    LikeList getLikeList();
    @Get("/db/likes?filter={path}")
    LikeList getLikeListForRecipe(String path);
    @Post("/db/likes")
    @RequiresHeader({"X-Dreamfactory-Session-Token","X-Dreamfactory-Application-Name" })
    void addLikeEntry(Like like);
    @Put("/db/likes")
    @RequiresHeader({"X-Dreamfactory-Session-Token","X-Dreamfactory-Application-Name" })
    void editLikeEntry(Like like);
    @Delete("/db/likes/{id}")
    @RequiresHeader({"X-Dreamfactory-Session-Token","X-Dreamfactory-Application-Name" })
    void deleteLikeEntry(Integer id);
    @Delete("/db/likes?filter={path}")
    @RequiresHeader({"X-Dreamfactory-Session-Token","X-Dreamfactory-Application-Name" })
    void deleteLikeEntryForRecipe(String path);

    @Get("/db/pictures/{id}")
    Picture getPictureById(int id);
    @Get("/db/pictures?filter={path}")
    PictureList getPictureListForRecipe(String path);
    @Post("/db/pictures")
    @RequiresHeader({"X-Dreamfactory-Session-Token","X-Dreamfactory-Application-Name" })
    void addPictureEntry(Picture picture);
    @Delete("/db/pictures?filter={path}")
    @RequiresHeader({"X-Dreamfactory-Session-Token","X-Dreamfactory-Application-Name" })
    void deletePictureEntryForRecipe(String path);

    @Get("/system/user/{id}")
    @RequiresHeader({"X-Dreamfactory-Session-Token","X-Dreamfactory-Application-Name" })
    UserInfo getUserInfo(Integer id);

    @Post("/user/session")
    User login(EmailAndPassword emailAndPassword);
}
