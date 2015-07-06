package webservice.task;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulation Result data for CommRequestBitmapTask
 */
class RequestBitmapData {
    /**
     * Result Bitmap object.
     */
    public Bitmap _bitmap;
    /**
     * Error message.
     */
    public String _msg;
}

public class CommRequestBitmapTask extends AsyncTask<String, Void, RequestBitmapData> {

    private boolean _is_success = false;
    private List<ActionComplete> _complete_notify = new ArrayList<>();

    /**
     * Callback interface.
     */
    public interface ActionComplete {
        /**
         * @param is_success Action if running successfully. (true: successful, false: exist error)
         * @param bmp Bitmap object.
         * @param msg Running message.
         */
        void onGetBitmapComplete(boolean is_success, Bitmap bmp, String msg);
    }

    /**
     * Constructor.
     */
    public CommRequestBitmapTask() {
        super();
    }

    /**
     * Register complete callback notify.
     *
     * @param notify Notify object.
     */
    public void addCompleteNotify(ActionComplete notify) {
        if (null == _complete_notify) {
            _complete_notify = new ArrayList<>();
        }
        _complete_notify.add(notify);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    /**
     * @param params URL string.
     * @return Encapsulation class included Bitmap and message string.
     */
    @Override
    protected RequestBitmapData doInBackground(String... params) {
        // Get Data stromg
        RequestBitmapData data = new RequestBitmapData();
        data._bitmap = null;
        data._msg = "";
        _is_success = false;

        try {
            URL url = new URL(params[0]);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.connect();
            data._bitmap = BitmapFactory.decodeStream(conn.getInputStream());
            conn.disconnect();
            _is_success = true;
        } catch (Exception e) {
            Log.w(this.getClass().getName(), e.toString());
            data._msg = e.getMessage();
        }

        return data;
    }

    /**
     * Call all registered notify.
     *
     * @param data Encapsulation class included Bitmap and message string.
     */
    @Override
    protected void onPostExecute(RequestBitmapData data) {
        super.onPostExecute(data);
        for (ActionComplete notify : _complete_notify) {
            notify.onGetBitmapComplete(_is_success, data._bitmap, data._msg);
        }
    }

}
