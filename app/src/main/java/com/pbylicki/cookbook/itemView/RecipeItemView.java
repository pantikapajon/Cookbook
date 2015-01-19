package com.pbylicki.cookbook.itemView;

import android.content.Context;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pbylicki.cookbook.R;
import com.pbylicki.cookbook.data.Recipe;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

@EViewGroup(R.layout.list_item_recipe)
public class RecipeItemView extends RelativeLayout {
    @ViewById
    ImageView image;
    @ViewById
    TextView title;
    @ViewById
    TextView introduction;
    @ViewById
    TextView date;
    @ViewById
    TextView author;

    public RecipeItemView(Context context) {
        super(context);
    }
    public void bind(Recipe recipe) {
        title.setText(recipe.title);
        introduction.setText(recipe.introduction);
        date.setText(recipe.getShortDate(recipe.getCreatedDate()));

        if(recipe.pictureBytes != null) recipe.decodeAndSetImage(image);
        else image.setImageResource(R.drawable.meal);
        if(recipe.ownerId != null) {
            if (recipe.author != null) author.setText("by " + recipe.author);
            else author.setText("by User " + Integer.toString(recipe.ownerId));
        } else {
            date.setText("");
            image.setImageDrawable(null);
            author.setText("");
        }
    }
}
