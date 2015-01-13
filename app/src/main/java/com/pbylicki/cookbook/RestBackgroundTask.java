package com.pbylicki.cookbook;

import com.pbylicki.cookbook.data.PhoneBook;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.rest.RestService;


@EBean
public class RestBackgroundTask {
    @RootContext
    MyActivity activity;
    @RestService
    CookbookRestClient restClient;
    /*@Background
    void getPhoneBook() {
        try {
            restClient.setHeader("X-Dreamfactory-Application-Name", "phonebook");
            PhoneBook phoneBook = restClient.getPhoneBook();
            publishResult(phoneBook);
        } catch (Exception e) {
            publishError(e);
        }
    }*/
    @UiThread
    void publishResult(PhoneBook phoneBook) {
        //activity.updatePhonebook(phoneBook);
    }
    @UiThread
    void publishError(Exception e) {
        //activity.showError(e);
    }
}
