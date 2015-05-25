package com.boha.rivermapper.toolbox;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.boha.rivermapper.dto.ResponseDTO;
import com.boha.rivermapper.dto.RiverDTO;
import com.google.gson.Gson;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by aubreyM on 2014/06/30.
 */
public class CacheUtil {

    public interface CacheUtilListener {
        public void onFileDataDeserialized(ResponseDTO response);

        public void onDataCached(ResponseDTO response);

        public void onError();
    }

    static CacheUtilListener utilListener;
    public static final int CACHE_RIVER_DATA = 1;
    static int dataType;
    static Integer projectID;
    static ResponseDTO response;
    static Context ctx;
    static final String JSON_RIVER_DATA = "riverdata.json";


    public static void cacheRiverData(Context context, ResponseDTO r, CacheUtilListener cacheUtilListener) {
        dataType = CACHE_RIVER_DATA;
        response = r;
        utilListener = cacheUtilListener;
        ctx = context;
//        merge(response);
        new CacheTask().execute();

    }

    public static void getCachedRiverData(Context context, CacheUtilListener cacheUtilListener) {
        dataType = CACHE_RIVER_DATA;
        utilListener = cacheUtilListener;
        ctx = context;
        new CacheRetrieveTask().execute();
    }


    private static void merge(final ResponseDTO dto) {

        getCachedRiverData(ctx, new CacheUtilListener() {
            @Override
            public void onFileDataDeserialized(final ResponseDTO r) {
                HashMap<Integer, RiverDTO> map = new HashMap<Integer, RiverDTO>();
                List<RiverDTO> list = new ArrayList<RiverDTO>();
                for (RiverDTO d : r.getRiverList()) {
                    map.put(d.getRiverID(), d);
                    if (d.getDateAdded() == null) {
                        d.setDateAdded(new Date());
                    }
                    list.add(d);
                }

                for (RiverDTO x : dto.getRiverList()) {
                    if (!map.containsKey(x.getRiverID())) {
                        x.setDateAdded(new Date());
                        list.add(x);
                    }
                }
                Collections.sort(list);
                r.setRiverList(list);
                response = r;
                new CacheTask().execute();

            }


            @Override
            public void onDataCached(ResponseDTO response) {

            }

            @Override
            public void onError() {

            }
        });

    }

    static class CacheTask extends AsyncTask<Void, Void, Integer> {


        @Override
        protected Integer doInBackground(Void... voids) {
            String json = null;
            File file = null;
            FileOutputStream outputStream;
            try {
                switch (dataType) {


                    case CACHE_RIVER_DATA:
                        json = gson.toJson(response);
                        outputStream = ctx.openFileOutput(JSON_RIVER_DATA, Context.MODE_PRIVATE);
                        write(outputStream, json);
                        file = ctx.getFileStreamPath(JSON_RIVER_DATA);
                        if (file != null) {
                            Log.e(LOG, "River data cache written, path: " + file.getAbsolutePath() +
                                    " - length: " + file.length());
                        }
                        break;


                    default:
                        Log.e(LOG, "######### NOTHING done ...");
                        break;

                }

            } catch (IOException e) {
                Log.e(LOG, "Failed to cache data", e);
                return 9;
            }
            return 0;
        }

        private void write(FileOutputStream outputStream, String json) throws IOException {
            outputStream.write(json.getBytes());
            outputStream.close();
        }

        @Override
        protected void onPostExecute(Integer v) {
            if (utilListener != null) {
                if (v > 0) {
                    utilListener.onError();
                } else
                    utilListener.onDataCached(response);
            }

        }
    }

    static class CacheRetrieveTask extends AsyncTask<Void, Void, ResponseDTO> {

        private ResponseDTO getData(FileInputStream stream) throws IOException {
            String json = getStringFromInputStream(stream);
            ResponseDTO response = gson.fromJson(json, ResponseDTO.class);
            return response;
        }

        @Override
        protected ResponseDTO doInBackground(Void... voids) {
            ResponseDTO response = new ResponseDTO();
            response.setRiverList(new ArrayList<RiverDTO>());
            FileInputStream stream;
            try {
                switch (dataType) {
                    case CACHE_RIVER_DATA:
                        stream = ctx.openFileInput(JSON_RIVER_DATA);
                        response = getData(stream);
                        Log.i(LOG, "++ river data cache retrieved");
                        break;


                }
                response.setStatusCode(0);

            } catch (FileNotFoundException e) {
                Log.d(LOG, "#### cache file not found - returning a new response object, type = " + dataType);

            } catch (IOException e) {
                Log.v(LOG, "------------ Failed to retrieve cache", e);
            }

            return response;
        }

        @Override
        protected void onPostExecute(ResponseDTO v) {
            if (utilListener == null) {
                return;
            }

            if (v.getRiverList() == null) {
                v.setRiverList(new ArrayList<RiverDTO>());
            }

            utilListener.onFileDataDeserialized(v);


        }
    }


    private static String getStringFromInputStream(InputStream is) throws IOException {

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } finally {
            if (br != null) {
                br.close();
            }
        }
        String json = sb.toString();
        return json;

    }

    static final String LOG = CacheUtil.class.getSimpleName();
    static final Gson gson = new Gson();


}
