package com.memy.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Base64;
import android.view.Display;
import android.webkit.MimeTypeMap;

import java.io.ByteArrayOutputStream;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Common util class
 */
public class Utils {


    /**
     * Method Handling to get current country code
     *
     * @param cID country code id
     * @return country code
     */
    public static String getCountryName(String cID) {
        Locale loc = new Locale("", cID);
        return loc.getDisplayCountry().trim();
    }

    /**
     * Method used to get the device width and height
     *
     * @param context activity context
     * @return point value
     */
    public static Point getMeasurementDetail(Context context) {
        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    public static boolean isNetworkConnected(Context ctx) {
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    public static String encodeImage(Bitmap bm)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);
        return encImage;
    }

    public static String getMimeType(Context context,Uri uri) {
        String mimeType = null;
        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
            ContentResolver cr = context.getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                    .toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.toLowerCase());
        }
        return mimeType;
    }

    public static String getRealPath(Context context,Uri uri) {

        String docId = "";

        try{
            docId = DocumentsContract.getDocumentId(uri);
        }catch (Exception e)
        {
            e.printStackTrace();
            try{
                docId = DocumentsContract.getTreeDocumentId(uri);
            }catch (Exception e1)
            {
                e.printStackTrace();
            }
        }

        String[] split = docId.split(":");
        String type = split[0];
        Uri contentUri;
        switch (type) {
            case "image":
                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                break;
            case "video":
                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                break;
            case "audio":
                contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                break;
            default:
                contentUri = MediaStore.Files.getContentUri("external");
        }
        String selection = "_id=?";
        String[] selectionArgs = new String[]{
                split[1]
        };

        return getDataColumn(context, contentUri, selection, selectionArgs);
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String column = "_data";
        String[] projection = {
                column
        };
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(column);
                String value = cursor.getString(column_index);
                if (value.startsWith("content://") || !value.startsWith("/") && !value.startsWith("file://")) {
                    return null;
                }
                return value;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    public static String getFilePathFromContentUri(Uri selectedVideoUri,
                                             ContentResolver contentResolver) {
        String filePath;
        String[] filePathColumn = {MediaStore.MediaColumns.DATA};
        Cursor cursor = contentResolver.query(selectedVideoUri, filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        filePath = cursor.getString(columnIndex);
        cursor.close();
        return filePath;
    }

    public static String getImageRealPath(ContentResolver contentResolver, Uri uri, String whereClause)
    {
        String ret = "";
        // Query the URI with the condition.
        Cursor cursor = contentResolver.query(uri, null, whereClause, null, null);
        if(cursor!=null)
        {
            boolean moveToFirst = cursor.moveToFirst();
            if(moveToFirst)
            {
                // Get columns name by URI type.
                String columnName = MediaStore.Images.Media.DATA;
                if( uri==MediaStore.Images.Media.EXTERNAL_CONTENT_URI )
                {
                    columnName = MediaStore.Images.Media.DATA;
                }else if( uri==MediaStore.Audio.Media.EXTERNAL_CONTENT_URI )
                {
                    columnName = MediaStore.Audio.Media.DATA;
                }else if( uri==MediaStore.Video.Media.EXTERNAL_CONTENT_URI )
                {
                    columnName = MediaStore.Video.Media.DATA;
                }
                // Get column index.
                int imageColumnIndex = cursor.getColumnIndex(columnName);
                // Get column value which is the uri related file local path.
                ret = cursor.getString(imageColumnIndex);
            }
        }
        return ret;
    }

    public static boolean isImageFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && ((mimeType.toLowerCase()).startsWith("image"));
    }

    public static boolean isVideoFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && ((mimeType.toLowerCase()).startsWith("video"));
    }

    public static boolean isAudioFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && ((mimeType.toLowerCase()).startsWith("audio"));
    }

    public static String getFormattedDate(String dateStr) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = simpleDateFormat.parse(dateStr);
            Calendar smsTime = Calendar.getInstance();
            smsTime.setTimeInMillis(date.getTime());

            Calendar now = Calendar.getInstance();

            final String timeFormatString = "hh:mm aa";
            final String dateTimeFormatString = "MMMM d, hh:mm aa";
            final long HOURS = 60 * 60 * 60;
            if (now.get(Calendar.DATE) == smsTime.get(Calendar.DATE)) {
                return "Today " + DateFormat.format(timeFormatString, smsTime);
            } else if (now.get(Calendar.DATE) - smsTime.get(Calendar.DATE) == 1) {
                return "Yesterday " + DateFormat.format(timeFormatString, smsTime);
            } else if (now.get(Calendar.YEAR) == smsTime.get(Calendar.YEAR)) {
                return DateFormat.format(dateTimeFormatString, smsTime).toString();
            } else {
                return DateFormat.format("MMMM dd yyyy, hh:mm aa", smsTime).toString();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }
}