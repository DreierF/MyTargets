package de.dreier.mytargets.shared.utils;

import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;

public class RegionUtils {
    public static Region getScaledRegion(Path path, float scale) {
        // Scale the path
        Path scaledPath = new Path(path);
        Matrix matrix = new Matrix();
        matrix.setScale(scale, scale);
        scaledPath.transform(matrix);

        // Get path bounds
        RectF bounds = new RectF();
        scaledPath.computeBounds(bounds, true);
        Rect rectBounds = new Rect();
        bounds.roundOut(rectBounds);

        // Create region from path
        Region region = new Region();
        region.setPath(scaledPath, new Region(rectBounds));
        return region;
    }
}
