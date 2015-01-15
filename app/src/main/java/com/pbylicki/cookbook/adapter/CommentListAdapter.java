package com.pbylicki.cookbook.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.pbylicki.cookbook.data.Comment;
import com.pbylicki.cookbook.data.CommentList;
import com.pbylicki.cookbook.data.Recipe;
import com.pbylicki.cookbook.data.RecipeList;
import com.pbylicki.cookbook.itemView.CommentItemView;
import com.pbylicki.cookbook.itemView.CommentItemView_;
import com.pbylicki.cookbook.itemView.RecipeItemView;
import com.pbylicki.cookbook.itemView.RecipeItemView_;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@EBean
public class CommentListAdapter extends BaseAdapter {
    @RootContext
    Context context;
    List<Comment> comments = new ArrayList<Comment>();
    public CommentListAdapter() {
    }
    public void update(CommentList commentList) {
        comments.clear();
        comments.addAll(commentList.records);
        Collections.sort(comments);
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return comments.size();
    }
    @Override
    public Comment getItem(int i) {
        return comments.get(i);
    }
    @Override
    public long getItemId(int i) {
        return i;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CommentItemView commentItemView;
        if (convertView == null) {
            commentItemView = CommentItemView_.build(context);
        } else {
            commentItemView = (CommentItemView) convertView;
        }
        commentItemView.bind(getItem(position));
        return commentItemView;
    }
}
