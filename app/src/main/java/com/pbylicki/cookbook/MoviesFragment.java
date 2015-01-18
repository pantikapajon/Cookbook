package com.pbylicki.cookbook;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pbylicki.cookbook.data.Recipe;

import java.util.List;

public class MoviesFragment extends Fragment {

    List<Recipe> recipeList;
    private TextView txt;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_movies, container, false);
        this.getArguments();


        return rootView;
    }

    @Override
    public void onStart(){
        super.onStart();
        //txt = (TextView)getActivity().findViewById(R.id.txt);
        //ProfileActivity activity = (ProfileActivity)getActivity();
        //recipeList.addAll(activity.getRecipeList());
        //txt.setText(recipeList.get(0).title);
    }
}
