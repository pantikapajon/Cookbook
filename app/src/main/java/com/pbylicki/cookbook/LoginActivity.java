package com.pbylicki.cookbook;

import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.pbylicki.cookbook.data.EmailAndPassword;
import com.pbylicki.cookbook.data.User;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.NonConfigurationInstance;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;


@EActivity(R.layout.activity_login)
public class LoginActivity extends Activity {
    @ViewById
    EditText email;
    @ViewById
    EditText password;
    ProgressDialog ringProgressDialog;
    @Bean
    @NonConfigurationInstance
    RestLoginBackgroundTask restBackgroundTask;
    @AfterViews
    void init() {
        ringProgressDialog = new ProgressDialog(this);
        ringProgressDialog.setMessage("Logging in...");
        ringProgressDialog.setIndeterminate(true);
    }
    @Click
    void loginClicked() {
        ringProgressDialog.show();
        EmailAndPassword emailAndPassword = new EmailAndPassword();
        emailAndPassword.email = email.getText().toString();
        emailAndPassword.password = password.getText().toString();
        restBackgroundTask.login(emailAndPassword);
    }
    public void loginSuccess(User user) {
        ringProgressDialog.dismiss();
        Log.d(this.getClass().getSimpleName(), "Login succeeded");
        //MyActivity_.intent(this).user(user).start();
    }
    public void showError(Exception e) {
        ringProgressDialog.dismiss();
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        e.printStackTrace();
    }
}
