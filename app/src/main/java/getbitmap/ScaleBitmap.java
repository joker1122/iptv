package getbitmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

/**
 * Created by PC-001 on 2018/4/3.
 */

public class ScaleBitmap {
    public static Bitmap get(Context context, float height, float width, int id) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), id);
        int mheight = bitmap.getHeight();
        int mwidth = bitmap.getWidth();
        Matrix matrix = new Matrix();
        matrix.postScale(height, width);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, mwidth, mheight, matrix, true);
        return bitmap;
    }
}
