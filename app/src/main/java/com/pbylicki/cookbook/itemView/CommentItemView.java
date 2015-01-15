package com.pbylicki.cookbook.itemView;

import android.content.Context;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pbylicki.cookbook.R;
import com.pbylicki.cookbook.data.Comment;
import com.pbylicki.cookbook.data.Recipe;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

@EViewGroup(R.layout.list_item_comment)
public class CommentItemView extends RelativeLayout {
    @ViewById
    TextView author;
    @ViewById
    TextView text;
    @ViewById
    TextView date;

    public CommentItemView(Context context) {
        super(context);
    }
    public void bind(Comment comment) {
        author.setText("User " + Integer.toString(comment.ownerId));
        text.setText(comment.text);
        date.setText(comment.getCreatedDate().toString());
    }
}
