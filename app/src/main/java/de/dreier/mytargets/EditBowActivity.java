package de.dreier.mytargets;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.ScrollView;

import com.wrapp.floatlabelededittext.FloatLabeledEditText;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EditBowActivity extends Activity implements View.OnClickListener {

    public static final String BOGEN_ID = "bogen_id";
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int SELECT_PICTURE = 2;
    private ImageView mImageView;
    private FloatLabeledEditText name, marke, size, height, tiller, desc;
    private RadioButton recurve, compound, longbow, blank;
    private Button cancel, newBow;
    private long mBowId = -1;
    private Uri fileUri;
    private Bitmap imageBitmap;
    private Drawable mActionBarBackgroundDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_bow);

        mActionBarBackgroundDrawable = getResources().getDrawable(R.drawable.ab_background);
        mActionBarBackgroundDrawable.setAlpha(0);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            mActionBarBackgroundDrawable.setCallback(mDrawableCallback);
        }
        getActionBar().setBackgroundDrawable(mActionBarBackgroundDrawable);

        ((NotifyingScrollView) findViewById(R.id.scrollView)).setOnScrollChangedListener(mOnScrollChangedListener);

        mImageView = (ImageView)findViewById(R.id.imageView);
        registerForContextMenu(mImageView);
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mImageView.showContextMenu();
            }
        });
        name = (FloatLabeledEditText)findViewById(R.id.bow_name);
        recurve = (RadioButton)findViewById(R.id.recurve);
        compound = (RadioButton)findViewById(R.id.compound);
        longbow = (RadioButton)findViewById(R.id.longbow);
        blank = (RadioButton)findViewById(R.id.blank);
        marke = (FloatLabeledEditText) findViewById(R.id.marke);
        size = (FloatLabeledEditText) findViewById(R.id.size);
        height = (FloatLabeledEditText) findViewById(R.id.height);
        tiller = (FloatLabeledEditText) findViewById(R.id.tiller);
        desc = (FloatLabeledEditText) findViewById(R.id.desc);
        cancel = (Button)findViewById(R.id.cancel_button);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        newBow = (Button)findViewById(R.id.new_bow_button);
        newBow.setOnClickListener(this);

        recurve.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                recurve.setChecked(true);
                compound.setChecked(false);
                longbow.setChecked(false);
                blank.setChecked(false);
            }
        });

        compound.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                recurve.setChecked(false);
                compound.setChecked(true);
                longbow.setChecked(false);
                blank.setChecked(false);
            }
        });

        longbow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                recurve.setChecked(false);
                compound.setChecked(false);
                longbow.setChecked(true);
                blank.setChecked(false);
            }
        });

        blank.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                recurve.setChecked(false);
                compound.setChecked(false);
                longbow.setChecked(false);
                blank.setChecked(true);
            }
        });
    }

    @Override
    public void onClick(View view) {
        TargetOpenHelper db = new TargetOpenHelper(this);

        int bowType=0;
        if(recurve.isChecked())
            bowType = 0;
        else if(compound.isChecked())
            bowType = 1;
        else if(longbow.isChecked())
            bowType = 2;
        else if(blank.isChecked())
            bowType = 3;

        Bitmap img = null;

        db.updateBow(mBowId, name.getTextString(), bowType,
                marke.getTextString(), size.getTextString(),
                height.getTextString(), tiller.getTextString(),
                desc.getTextString(), img);
    }

    private Drawable.Callback mDrawableCallback = new Drawable.Callback() {
        @Override
        public void invalidateDrawable(Drawable who) {
            getActionBar().setBackgroundDrawable(who);
        }

        @Override
        public void scheduleDrawable(Drawable who, Runnable what, long when) {
        }

        @Override
        public void unscheduleDrawable(Drawable who, Runnable what) {
        }
    };

    private NotifyingScrollView.OnScrollChangedListener mOnScrollChangedListener = new NotifyingScrollView.OnScrollChangedListener() {
        public void onScrollChanged(ScrollView who, int l, int t, int oldl, int oldt) {
            final int headerHeight = findViewById(R.id.imageView).getHeight() - getActionBar().getHeight();
            final float ratio = (float) Math.min(Math.max(t, 0), headerHeight) / headerHeight;
            final int newAlpha = (int) (ratio * 255);
            mActionBarBackgroundDrawable.setAlpha(newAlpha);
        }
    };

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu_image, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.action_from_gallery:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,
                        getString(R.string.select_picture)), SELECT_PICTURE);
                return true;
            case R.id.action_take_picture:
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    try {
                        fileUri = getOutputMediaFileUri();
                    } catch(NullPointerException e) {
                        return true;
                    }
                    takePictureIntent.putExtra( MediaStore.EXTRA_OUTPUT, fileUri );
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri selectedImageUri;
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            selectedImageUri = fileUri;
        } else if(requestCode==SELECT_PICTURE  && resultCode == RESULT_OK) {
            selectedImageUri = data.getData();
        } else {
            return;
        }

        try {
            imageBitmap = decodeSampledBitmapFromStream(selectedImageUri,mImageView.getWidth(),mImageView.getHeight());
            mImageView.setImageBitmap(imageBitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Uri getOutputMediaFileUri(){
        return Uri.fromFile(getOutputMediaFile());
    }

    private static File getOutputMediaFile(){
        if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Log.d("OutputMediaFile","SD card is not mounted!");
            return null;
        }

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyTargets");

        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyTargets", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "IMG_"+ timeStamp + ".jpg");

        return mediaFile;
    }

    public Bitmap decodeSampledBitmapFromStream(Uri uri, int reqWidth, int reqHeight) throws IOException {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        InputStream stream = getContentResolver().openInputStream(uri);
        BitmapFactory.decodeStream(stream, null, options);
        stream.close();

        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (width > reqWidth || height > reqHeight) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        options.inSampleSize = inSampleSize;
        options.inJustDecodeBounds = false;
        stream = getContentResolver().openInputStream(uri);
        return BitmapFactory.decodeStream(stream, null, options);
    }
}
