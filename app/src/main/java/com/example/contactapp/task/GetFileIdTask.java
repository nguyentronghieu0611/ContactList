package com.example.contactapp.task;

import android.content.Context;
import android.os.AsyncTask;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;

import org.json.JSONException;
import org.json.JSONObject;



public class GetFileIdTask extends AsyncTask<Object,Void,JSONObject> {
    private final Context mContext;
    private final DbxClientV2 mDbxClient;
    private final GetFileIdTask.Callback mCallback;
    private Exception mException;

    public interface Callback {
        void onGetIdComplete(JSONObject result);
        void onError(Exception e);
    }

    public GetFileIdTask(Context context, DbxClientV2 dbxClient, GetFileIdTask.Callback callback) {
        mContext = context;
        mDbxClient = dbxClient;
        mCallback = callback;
    }

    @Override
    protected JSONObject doInBackground(Object... objects) {
        JSONObject id= null;
        ListFolderResult result = null;
        try {
            result = mDbxClient.files().listFolder("");
            while (true) {
                for (Metadata metadata : result.getEntries()) {
                    System.out.println(metadata.getPathLower());
                }
                if (!result.getHasMore()) {
                    break;
                }
                result = mDbxClient.files().listFolderContinue(result.getCursor());
            }
            if(result.getEntries().size()>0){
                for(int i=0;i<result.getEntries().size();i++){
                    if(result.getEntries().get(i).getName().equalsIgnoreCase("contact.txt")){
                        JSONObject obj = new JSONObject(result.getEntries().get(i).toString());
                        id = obj;
                    }
                }
            }
        } catch (DbxException | JSONException e) {
            e.printStackTrace();
        }
        return id;
    }

    @Override
    protected void onPostExecute(JSONObject result) {
        super.onPostExecute(result);
        if (mException != null) {
            mCallback.onError(mException);
        } else {
            mCallback.onGetIdComplete(result);
        }
    }
}
