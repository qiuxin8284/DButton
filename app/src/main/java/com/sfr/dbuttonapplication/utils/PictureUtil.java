package com.sfr.dbuttonapplication.utils;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Base64;
import android.view.View;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * ??????
 *
 * @author michael.cui
 */
public class PictureUtil {

    public static Bitmap tempBitmap;
    protected static boolean NetGetingFlag;

    //bitmap????string
    public static String BitMapToJpegString(Bitmap picture) {
        if (picture == null)
            return "";
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        picture.compress(CompressFormat.JPEG, 100, stream);
        byte[] picByte = stream.toByteArray();
        return Base64.encodeToString(picByte, Base64.DEFAULT);
    }

    public static Bitmap scaleRate(Bitmap bitmap) {
        Bitmap icon = bitmap;
        int width = icon.getWidth(), height = icon.getHeight();
        float scaleRate = 0.8f;
        while (width >= 300 || width >= 300) {
            Matrix matrix = new Matrix();
            matrix.postScale(width * scaleRate, height * scaleRate);
            Bitmap newIcon = Bitmap.createBitmap(icon, 0, 0, width, height, matrix, true);
            icon.recycle();
            icon = newIcon;
            width = icon.getWidth();
            height = icon.getHeight();
        }
        return icon;
    }

    public static Bitmap resizeImage(Bitmap bitmap, int w, int h) {
        Bitmap BitmapOrg = bitmap;
        int width = BitmapOrg.getWidth();
        int height = BitmapOrg.getHeight();
        int newWidth = w;
        int newHeight = h;

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // if you want to rotate the Bitmap
        // matrix.postRotate(45);
        Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width,
                height, matrix, true);
        return resizedBitmap;
    }

    public static Bitmap convertStringToBitmap(String st) {
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(st, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0,
                    bitmapArray.length);
            return bitmap;
        } catch (Exception e) {
            return null;
        }
    }


    public static byte[] BitmapToBytes(Bitmap picture) {
        Bitmap bitmap = null;
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            picture.compress(CompressFormat.JPEG, 100, stream);
            byte[] picByte = stream.toByteArray();
            return picByte;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Bitmap BytesToBitmap(byte[] bitmapArray) {
        // OutputStream out;
        //st
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
            return bitmap;
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public static byte[] readStream(InputStream inStream) throws Exception {
        byte[] buffer = new byte[1024];
        int len = -1;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        byte[] data = outStream.toByteArray();
        outStream.close();
        inStream.close();
        return data;

    }


    public static Bitmap getBitmapFromUrl(String Url) {
        tempBitmap = null;
        try {
            final URL url = new URL(Url);
            int i = 0;
            NetGetingFlag = true;
            new Thread() {
                public void run() {
                    try {
                        tempBitmap = BitmapFactory.decodeStream(url.openStream());
                        NetGetingFlag = false;

                    } catch (Exception e) {
                        e.printStackTrace();
                        NetGetingFlag = false;
                    }
                }

                ;

            }.start();


            while (i < 10) {
                if (!NetGetingFlag)
                    break;
                i++;
                Thread.sleep(1000);
            }
            return tempBitmap;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }


    public static Bitmap ScaleToStandard(Bitmap bitmap) {
        try {
            Matrix matrix = new Matrix();
            float scaleRate = (float) (800.0 / bitmap.getHeight());
            matrix.postScale(scaleRate, scaleRate);
            Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            return resizeBmp;
        } catch (Exception e) {
            return null;
        }
    }

    //???????????
    public static Bitmap ScaleTo(Bitmap bitmap, int width) {
        try {
            Matrix matrix = new Matrix();
            float scaleRate = (float) (((float) width) / bitmap.getWidth());
            matrix.postScale(scaleRate, scaleRate);
            Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            return resizeBmp;
        } catch (Exception e) {
            return null;
        }

    }


    public static Bitmap getPicFromUri(Uri uri, ContentResolver resolver) {
        byte[] mContent;
        try {
            mContent = readStream(resolver.openInputStream(uri));
            Bitmap myBitmap = getPicFromBytes(mContent, null);
            return myBitmap;
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap getPicFromBytes(byte[] bytes, BitmapFactory.Options opts) {
        if (bytes != null)
            if (opts != null)
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
            else
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return null;
    }

    public static void saveMyBitmap(String bitName, Bitmap mBitmap) {
        File f = new File("/sdcard/" + bitName + ".png");
        try {
            f.createNewFile();
        } catch (IOException e) {
            // TODO Auto-generated catch block
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {

        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int heightRatio = Math.round((float) height
                    / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

    public static Bitmap getSmallBitmap(String filePath) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        options.inSampleSize = calculateInSampleSize(options, 480, 800);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    public static Bitmap SizeBitmap(String filePath, int width, int height) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        options.inSampleSize = calculateInSampleSize(options, width, height);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);

    }

    public static File getAlbumDir() {
        File dir = new File("/mnt/sdcard/zhujj/");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    public static int dp2px(Context context, int dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;

    }

    static public Bitmap drawableToBitmap(Drawable drawable) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(width, height, config);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;
    }

    static public Bitmap ResourceToBitmap(Context c, int rid) {
        try {
            Drawable draw1 = c.getResources().getDrawable(rid);
            return drawableToBitmap(draw1);
        } catch (Exception e) {
            return null;
        }
    }

    public static Bitmap getRoundCornerImage(Bitmap bitmap, int roundPixels) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        Bitmap roundConcerImage = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(roundConcerImage);
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);


        canvas.drawRoundRect(rectF, roundPixels, roundPixels, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, null, rect, paint);

        return roundConcerImage;

    }

    public static BitmapDrawable BitmapToDrawAble(Bitmap b) {
        return new BitmapDrawable(b);
    }

    //	public static boolean savePreviewBitmap(Context context,Bitmap bm,String picId){
    //		File previewTempFile=new File(Environment.getExternalStorageDirectory()+
    //				"/HuaXin/"+picId+".jpg");
    //		File file=new File(Environment.getExternalStorageDirectory()+"/CarTalkie");
    //		if (!file.exists()) {
    //			file.mkdirs();
    //		}
    //		if(previewTempFile.exists()){
    //			previewTempFile.delete();
    //		}
    //		try{
    //			previewTempFile.createNewFile();
    //		}catch (IOException e) {
    //			e.printStackTrace();
    //			return false;
    //		}
    //		FileOutputStream fileOutputStream=null;
    //		try {
    //			fileOutputStream=new FileOutputStream(previewTempFile);
    //		} catch (IOException e) {
    //			e.printStackTrace();
    //			return false;
    //		}
    //		bm.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
    //		try {
    //			fileOutputStream.flush();
    //			fileOutputStream.close();
    //		} catch (IOException e) {
    //			e.printStackTrace();
    //			return false;
    //		}
    //		ScanUtil.folderScan(Environment.getExternalStorageDirectory()+"/CarTalkie");
    //		//CRFApplication.instance.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://"+ Environment.getExternalStorageDirectory()+"/HuaXin"+picId+".jpg")));
    //		//CRFApplication.instance.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"+ Environment.getExternalStorageDirectory())));
    //		//MediaScannerConnection.scanFile(CRFApplication.instance, new String[]{"file://"+ Environment.getExternalStorageDirectory()+"/HuaXin"}, null, null);
    //		/*MediaScannerConnection.scanFile(context, new String[] { Environment.getExternalStorageDirectory().toString()+"/HuaXin"}, null, new MediaScannerConnection.OnScanCompletedListener() {
    //	            public void onScanCompleted(String path, Uri uri)
    //	              {
    //	                  LogUtil.i("ExternalStorage", "Scanned " + path + ":");
    //	                  LogUtil.i("ExternalStorage", "-> uri=" + uri);
    //	              }
    //	            });*/
    //		return true;
    //	}
    public static Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) {
            baos.reset();
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);
            options -= 10;
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
        return bitmap;
    }

    public static class DrawAbleUtil {
        public static void DrawText(Drawable D, String Text, int x, int y) {

            Canvas canvas = new Canvas();
            Paint paint = new Paint();
            paint.setColor(Color.parseColor("#424242"));
            paint.setTextSize(20);
            canvas.drawText(Text, x, y, paint);
            D.draw(canvas);
        }
    }

    public static byte[] DrawabletoByte(Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        BitmapDrawable bd = (BitmapDrawable) drawable;
        Bitmap bitmap = bd.getBitmap();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.PNG, 100, os);
        return os.toByteArray();
    }

    public static byte[] BitmaptoByte(Bitmap bitmap) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.PNG, 100, os);
        return os.toByteArray();
    }

    public static Bitmap BytetoBitmap(byte[] b) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length, null);
        return bitmap;
    }

    public static Drawable BytetoDrawable(byte[] b) {
        Drawable drawable = null;
        try {
            Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length, null);
            BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmap);
            drawable = bitmapDrawable;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return drawable;
    }

    public static Bitmap returnBitmap(String url) {
        URL fileUrl = null;
        Bitmap bitmap = null;

        try {
            fileUrl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        try {
            HttpURLConnection conn = (HttpURLConnection) fileUrl
                    .openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;

    }

    public static String encodeToBase64(String file_path) {
//		StringBuilder file_builder = new StringBuilder();
//		if(!TextUtils.isEmpty(file_path)) {
//			try {
//				InputStream file_stream = new FileInputStream(new File(file_path));
//				byte[] temp_buffer = new byte[1024];
//				boolean var4 = true;
//
//				while(-1 != file_stream.read(temp_buffer)) {
//					file_builder.append(Base64.encode(temp_buffer));
//				}
//
//				file_stream.close();
//			} catch (Exception var5) {
//				var5.printStackTrace();
//			}
//		}
//
//		return null != file_builder && file_builder.length() > 0?file_builder.toString():null;
        return null;
    }

    public static Bitmap getViewBitmap(View addViewContent) {

        addViewContent.setDrawingCacheEnabled(true);

        addViewContent.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        addViewContent.layout(0, 0,
                addViewContent.getMeasuredWidth(),
                addViewContent.getMeasuredHeight());

        addViewContent.buildDrawingCache();
        Bitmap cacheBitmap = addViewContent.getDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);

        return bitmap;
    }

    public static void gcBitmap(Bitmap bitmap) {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle(); //
            bitmap = null;
            System.gc(); //
        }
    }

    @SuppressLint("NewApi")
    public static String bitmapToBase64(Bitmap bitmap) {

        //
        String reslut = null;

        ByteArrayOutputStream baos = null;

        try {

            if (bitmap != null) {

                baos = new ByteArrayOutputStream();

                bitmap.compress(CompressFormat.JPEG, 30, baos);

                baos.flush();
                baos.close();
                byte[] byteArray = baos.toByteArray();

                reslut = Base64.encodeToString(byteArray, Base64.DEFAULT);
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            try {
                if (baos != null) {
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return reslut;

    }


    public static Bitmap base64ToBitmap(String base64String) {

        byte[] decode = Base64.decode(base64String, Base64.DEFAULT);

        Bitmap bitmap = BitmapFactory.decodeByteArray(decode, 0, decode.length);

        return bitmap;
    }

}
