package com.pbylicki.cookbook;


import android.os.Bundle;
import android.support.v4.app.ListFragment;

import com.pbylicki.cookbook.adapter.RecipeListAdapter;
import com.pbylicki.cookbook.data.RecipeList;

public class TopRatedFragment extends ListFragment {

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MyActivity activity = (MyActivity)getActivity();
        RecipeList recipeList = activity.getRecipeList();
        /*List<String> values = new ArrayList<String>();
        for(Recipe recipe : recipeList.records) values.add(recipe.title);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, values);
        setListAdapter(adapter);*/
        RecipeListAdapter recipeListAdapter = new RecipeListAdapter();
        setListAdapter(recipeListAdapter);

    }
}
