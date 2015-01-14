package com.pbylicki.cookbook.itemView;

import android.content.Context;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pbylicki.cookbook.R;
import com.pbylicki.cookbook.data.Recipe;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

@EViewGroup(R.layout.list_item_recipe)
public class RecipeItemView extends RelativeLayout {
    @ViewById
    TextView title;
    @ViewById
    TextView introduction;
    @ViewById
    TextView date;

    public RecipeItemView(Context context) {
        super(context);
    }
    public void bind(Recipe recipe) {
        title.setText(recipe.title);
        introduction.setText(recipe.introduction);
        date.setText(recipe.getCreatedDate().toString());
    }
}
