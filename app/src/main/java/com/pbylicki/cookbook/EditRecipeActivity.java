package com.pbylicki.cookbook;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pbylicki.cookbook.data.Recipe;
import com.pbylicki.cookbook.data.User;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.NonConfigurationInstance;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;

import java.io.ByteArrayOutputStream;
import java.io.File;

@EActivity(R.layout.activity_add_recipe)
@OptionsMenu(R.menu.menu_browse)
public class EditRecipeActivity extends Activity {

    public static final int REQUESTCODE = 42;

    @ViewById
    TextView header;
    @ViewById
    EditText title;
    @ViewById
    EditText introduction;
    @ViewById
    EditText servings;
    @ViewById
    EditText preparationMinutes;
    @ViewById
    EditText cookingMinutes;
    @ViewById
    EditText ingredients;
    @ViewById
    EditText steps;
    @ViewById
    ImageView image;

    @Extra
    Bundle bundle;
    Recipe recipe;
    @InstanceState
    User user;
    int recipeId;
    @InstanceState
    String oldPictureBytes;

    @Bean
    @NonConfigurationInstance
    RestEditRecipeBackgroundTask restBackgroundTask;
    ProgressDialog ringProgressDialog;

    @AfterViews
    void init() {
        //Gets User and Selected Recipe
        if(user == null) user = (User)bundle.getSerializable(BrowseActivity.USER);
        recipe = (Recipe) bundle.getSerializable(BrowseActivity.RECIPE);
        recipeId = recipe.id;
        //Populates View
        header.setText(getString(R.string.edit_recipe_header));
        title.setText(recipe.title);
        introduction.setText(recipe.introduction);
        if(recipe.servings != null) servings.setText(Integer.toString(recipe.servings));
        if(recipe.preparationMinutes != null) preparationMinutes.setText(Integer.toString(recipe.preparationMinutes));
        if(recipe.cookingMinutes != null) cookingMinutes.setText(Integer.toString(recipe.cookingMinutes));
        ingredients.setText(recipe.ingredients);
        steps.setText(recipe.steps);
        if(recipe.pictureBytes != null) recipe.decodeAndSetImage(image);
        oldPictureBytes = recipe.pictureBytes;

        ringProgressDialog = new ProgressDialog(this);
        ringProgressDialog.setMessage("Saving recipe...");
        ringProgressDialog.setIndeterminate(true);

    }

    @Click
    public void addbuttonClicked(){
        //Checks if required fields are filled
        if(isEmpty(title)) { showToastRequired(title); return; }
        if(isEmpty(servings)) { showToastRequired(servings); return; }
        if(isEmpty(ingredients)) { showToastRequired(ingredients); return; }
        if(isEmpty(steps)) { showToastRequired(steps); return; }

        //passes edited object to Rest client
        ringProgressDialog.show();
        //recipe = new Recipe();
        recipe.id = recipeId;
        recipe.title = title.getText().toString();
        recipe.introduction = introduction.getText().toString();
        recipe.servings = Integer.parseInt(servings.getText().toString());
        if(!isEmpty(preparationMinutes)) recipe.preparationMinutes = Integer.parseInt(preparationMinutes.getText().toString());
        else recipe.preparationMinutes = null;
        if(!isEmpty(cookingMinutes)) recipe.cookingMinutes = Integer.parseInt(cookingMinutes.getText().toString());
        else recipe.cookingMinutes = null;
        recipe.ingredients = ingredients.getText().toString();
        recipe.steps = steps.getText().toString();
        recipe.ownerId = user.id;
        restBackgroundTask.editRecipe(user, recipe, oldPictureBytes);

    }

    @Click
    void imagebuttonClicked(){
        openPictureSourceSelectionDialog();
    }

    @Click
    void deleteImagebuttonClicked(){
        recipe.pictureBytes = null;
        image.setImageResource(R.drawable.ic_action_picture);
        if(oldPictureBytes != null) restBackgroundTask.deletePicture(user, recipe);
    }

    public void deleteImageSuccess(){
        Toast.makeText(this, getString(R.string.edit_recipe_picture_deleted), Toast.LENGTH_LONG).show();
    }

    public void editError(Exception e) {
        ringProgressDialog.dismiss();
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        e.printStackTrace();
    }

    public void editSuccess() {
        ringProgressDialog.dismiss();
        Toast.makeText(this, getString(R.string.edit_recipe_toast_edited), Toast.LENGTH_LONG).show();
        BrowseActivity_.intent(this).user(user).start();
    }

    @OptionsItem(R.id.action_login)
    void actionLoginSelected(){
        if(user == null) LoginActivity_.intent(this).startForResult(BrowseActivity_.LOGIN_REQUESTCODE);
        else Toast.makeText(this, getString(R.string.user_already_logged_in), Toast.LENGTH_LONG).show();
    }

    @OptionsItem(R.id.action_add)
    void actionAddSelected() {

        if(user == null) LoginActivity_.intent(this).startForResult(REQUESTCODE);
        else AddRecipeActivity_.intent(this).user(user).start();

    }

    @OptionsItem(R.id.action_profile)
    void actionProfileSelected() {
        if(user == null) LoginActivity_.intent(this).startForResult(BrowseActivity_.PROFILE_REQUESTCODE);
        else ProfileActivity_.intent(this).user(user).start();
    }
    @OptionsItem(R.id.action_browse)
    void actionBrowseSelected() {
        BrowseActivity_.intent(this).user(user).start();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode == RESULT_OK){
            if (user == null) user =(User)data.getSerializableExtra(LoginActivity.LOGINRESULT);
            switch (requestCode) {
                case REQUESTCODE:   AddRecipeActivity_.intent(this).user(user).start();
                                    break;
                case BrowseActivity_.LOGIN_REQUESTCODE: init();
                                                        break;
                case BrowseActivity_.PROFILE_REQUESTCODE:   ProfileActivity_.intent(this).user(user).start();
                                                            break;
                case AddRecipeActivity_.INTENT_SELECT_FILE:    onPhotoFromGallerySelected(resultCode, data);
                    break;
                case AddRecipeActivity_.INTENT_SHOOT_WITH_CAMERA:  onPhotoFromCameraTaken(resultCode);
                    break;
                default:            break;
            }
        }
        if (resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "Login cancelled", Toast.LENGTH_LONG).show();
        }
    }

    private boolean isEmpty(EditText editText) {
        return editText.getText().toString().trim().length() == 0;
    }
    private void showToastRequired(EditText editText){
        Toast.makeText(this, getString(R.string.add_recipe_toast_required) + editText.getHint(), Toast.LENGTH_LONG).show();
    }

    private void openPictureSourceSelectionDialog() {
        final CharSequence[] items = { getString(R.string.add_recipe_add_photo_shoot), getString(R.string.add_recipe_add_photo_from_gallery),
                getString(R.string.add_recipe_cancel) };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.add_recipe_add_photo));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {
                    startCameraIntent();
                } else if (item == 1) {
                    startSelectFromGalleryIntent();
                } else {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }
    private void startSelectFromGalleryIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select File"), AddRecipeActivity_.INTENT_SELECT_FILE);
    }
    private void startCameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(Environment.getExternalStorageDirectory()+File.separator + "image.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        startActivityForResult(intent, AddRecipeActivity_.INTENT_SHOOT_WITH_CAMERA);
    }
    //@OnActivityResult(INTENT_SHOOT_WITH_CAMERA)
    void onPhotoFromCameraTaken(int resultCode) {
        if (resultCode == RESULT_OK) {
            File rawCameraImageFile = new File(Environment.getExternalStorageDirectory() + File.separator + "image.jpg");
            addCompressedImageFromPath(rawCameraImageFile.getAbsolutePath());
            rawCameraImageFile.delete();
        }
    }
    //@OnActivityResult(INTENT_SELECT_FILE)
    void onPhotoFromGallerySelected(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            String selectedImageFilePath = getPath(selectedImageUri, EditRecipeActivity.this);
            addCompressedImageFromPath(selectedImageFilePath);
        }
    }
    private void addCompressedImageFromPath(String selectedImageFilePath) {
        Bitmap bitmap = decodeSampledBitmapFromFile(selectedImageFilePath, 400, 400);
        image.setImageBitmap(bitmap);
        recipe.pictureBytes = compressAndEncodeToBase64(bitmap);
        //restBackgroundTask.addRecipe(user, recipe);
    }
    public Bitmap decodeSampledBitmapFromFile(String path, int reqWidth, int reqHeight)
    { // BEST QUALITY MATCH
//First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
// Calculate inSampleSize, Raw height and width of image
        final int sourceWidth = options.outWidth;
        final int sourceHeight = options.outHeight;
        options.inSampleSize = calculateInSampleSize(sourceWidth, sourceHeight, reqWidth, reqHeight);
        options.inPreferredConfig = Bitmap.Config.RGB_565;
// Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        return getAccuratelyResizedBitmap(bitmap, reqWidth, reqHeight);
    }
    public static int calculateInSampleSize(int sourceWidth, int sourceHeight, int reqWidth, int reqHeight) {
        int inSampleSize = 1;
        if (sourceHeight > reqHeight || sourceWidth > reqWidth) {
            final int halfHeight = sourceHeight / 2;
            final int halfWidth = sourceWidth / 2;
// Calculate the largest inSampleSize value that is a power of 2 and keeps both
// height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }
    public Bitmap getAccuratelyResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        if (scaleWidth < scaleHeight) {
            newWidth = Math.round(width*scaleWidth);
            newHeight = Math.round(height*scaleWidth);
        } else {
            newWidth = Math.round(width*scaleHeight);
            newHeight = Math.round(height*scaleHeight);
        }
        return Bitmap.createScaledBitmap(bm, newWidth, newHeight, true);
    }
    public String getPath(Uri uri, Activity activity) {
        String[] projection = { MediaStore.MediaColumns.DATA };
        Cursor cursor = activity.managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
    public String compressAndEncodeToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        byte[] b = byteArrayOutputStream.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.NO_WRAP | Base64.NO_PADDING);
        //Log.d(getClass().getSimpleName(), imageEncoded);
        //Toast.makeText(this, "" + imageEncoded.length(), Toast.LENGTH_SHORT).show();
        return imageEncoded;
    }
}
