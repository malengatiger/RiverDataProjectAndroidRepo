package com.boha.rivermapper.toolbox;


import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;


public class Statics {

    /*
     * REMOTE URL - bohamaker back end - production
     * 68.169.60.130
     */

    public static final String WEBSOCKET_URL = "ws://http://69.89.15.29:8080/rdp/";
    public static final String URL = "http://69.89.15.29:8080/rdp/";

    //pecanwood
//    public static final String WEBSOCKET_URL = "ws://192.168.1.33:8080/rdp/";
//    public static final String URL = "http://192.168.1.33:8080/rdp/";

//codetribe

//    public static final String WEBSOCKET_URL = "ws://10.50.75.69:8080/mwp/";
//    public static final String URL = "http://10.50.75.69:8080/mwp/";

    public static final String UPLOAD_URL_REQUEST = "uploadUrl?";
    public static final String CRASH_REPORTS_URL = URL + "crash?";

    public static final String
            REQUEST_ENDPOINT = "wsrequest",
            COMPANY_ENDPOINT = "wscompany",
            RIVERS_SERVLET = "list?JSON=";


    public static void setDroidFontBold( Context ctx,  TextView txt) {
        Typeface font = Typeface.createFromAsset(ctx.getAssets(),
                "DroidSerif-Bold");
        txt.setTypeface(font);
    }

    public static void setRobotoFontBoldCondensed( Context ctx,  TextView txt) {
        Typeface font = Typeface.createFromAsset(ctx.getAssets(),
                "fonts/Roboto-BoldCondensed.ttf");
        txt.setTypeface(font);
    }

    public static void setRobotoFontRegular( Context ctx,  TextView txt) {
        Typeface font = Typeface.createFromAsset(ctx.getAssets(),
                "fonts/Roboto-Regular.ttf");
        txt.setTypeface(font);
    }

    public static void setRobotoFontLight( Context ctx,  TextView txt) {
        Typeface font = Typeface.createFromAsset(ctx.getAssets(),
                "fonts/Roboto-Light.ttf");
        txt.setTypeface(font);
    }

    public static void setRobotoFontBold( Context ctx,  TextView txt) {
        Typeface font = Typeface.createFromAsset(ctx.getAssets(),
                "fonts/Roboto-Bold.ttf");
        txt.setTypeface(font);
    }

    public static void setRobotoItalic( Context ctx,  TextView txt) {
        Typeface font = Typeface.createFromAsset(ctx.getAssets(),
                "fonts/Roboto-Italic.ttf");
        txt.setTypeface(font);
    }

    public static void setRobotoRegular( Context ctx,  TextView txt) {
        Typeface font = Typeface.createFromAsset(ctx.getAssets(),
                "fonts/Roboto-Regular.ttf");
        txt.setTypeface(font);
    }

}
