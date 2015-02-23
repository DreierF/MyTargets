package de.dreier.mytargets.activities;

import android.content.Intent;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.iangclifton.android.floatlabel.FloatLabel;

import de.dreier.mytargets.R;
import de.dreier.mytargets.managers.DatabaseManager;
import de.dreier.mytargets.models.Arrow;

public class EditArrowActivity extends EditWithImageActivity {

    public static final String ARROW_ID = "arrow_id";

    private FloatLabel name, length, material, spine, weight, vanes, nock, comment;
    private long mArrowId = -1;

    public EditArrowActivity() {
        super(R.layout.activity_edit_arrow, R.drawable.arrows);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(ARROW_ID)) {
            mArrowId = intent.getLongExtra(ARROW_ID, -1);
        }

        name = (FloatLabel) findViewById(R.id.arrow_name);
        length = (FloatLabel) findViewById(R.id.arrow_length);
        material = (FloatLabel) findViewById(R.id.arrow_material);
        spine = (FloatLabel) findViewById(R.id.arrow_spine);
        weight = (FloatLabel) findViewById(R.id.arrow_weight);
        vanes = (FloatLabel) findViewById(R.id.arrow_vanes);
        nock = (FloatLabel) findViewById(R.id.arrow_nock);
        comment = (FloatLabel) findViewById(R.id.arrow_comment);

        if (savedInstanceState == null && mArrowId != -1) {
            DatabaseManager db = new DatabaseManager(this);
            Arrow arrow = db.getArrow(mArrowId, false);
            name.setText(arrow.name);
            length.setText(arrow.length);
            material.setText(arrow.material);
            spine.setText(arrow.spine);
            weight.setText(arrow.weight);
            vanes.setText(arrow.vanes);
            nock.setText(arrow.nock);
            comment.setText(arrow.comment);
            imageBitmap = arrow.image;
            mImageView.setImageBitmap(imageBitmap);
            mImageFile = arrow.imageFile;
        } else if (savedInstanceState != null) {
            name.setText(savedInstanceState.getString("name"));
            length.setText(savedInstanceState.getString("length"));
            material.setText(savedInstanceState.getString("material"));
            spine.setText(savedInstanceState.getString("spine"));
            weight.setText(savedInstanceState.getString("weight"));
            vanes.setText(savedInstanceState.getString("vanes"));
            nock.setText(savedInstanceState.getString("nock"));
            comment.setText(savedInstanceState.getString("comment"));
        }
    }

    @Override
    public void onSave() {
        DatabaseManager db = new DatabaseManager(this);

        Arrow arrow = new Arrow();
        arrow.id = mArrowId;
        arrow.name = name.getTextString();
        arrow.length = length.getTextString();
        arrow.material = material.getTextString();
        arrow.spine = spine.getTextString();
        arrow.weight = weight.getTextString();
        arrow.vanes = vanes.getTextString();
        arrow.nock = nock.getTextString();
        arrow.comment = comment.getTextString();

        arrow.image = ThumbnailUtils.extractThumbnail(imageBitmap, 100, 100);
        arrow.imageFile = mImageFile;

        mArrowId = db.updateArrow(arrow);
        finish();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("name", name.getTextString());
        outState.putString("length", length.getTextString());
        outState.putString("material", material.getTextString());
        outState.putString("spine", spine.getTextString());
        outState.putString("weight", weight.getTextString());
        outState.putString("vanes", vanes.getTextString());
        outState.putString("nock", nock.getTextString());
        outState.putString("comment", comment.getTextString());
        outState.putParcelable("img", imageBitmap);
        outState.putString("image_file", mImageFile);
    }
}