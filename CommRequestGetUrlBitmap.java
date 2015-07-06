package webservice;

import android.graphics.Bitmap;

import webservice.task.CommRequestBitmapTask;

/**
 * Get Image object from URL. We use CommRequestBitmapTask to get Bitmap object.
 */
public class CommRequestGetUrlBitmap extends CommBaseRequest {

    private String _url_str = "";
    private IRequestGetUrlBitmapAction _complete_action = null;

    /**
     * Callback interface.
     */
    public interface IRequestGetUrlBitmapAction {
        void onSuccess(Bitmap bitmap);
        void onFailure(String msg);
    }

    /**
     * Call back action when get Bitmap from URL completely.
     */
    class TaskActionComplete implements CommRequestBitmapTask.ActionComplete {

        @Override
        public void onGetBitmapComplete(boolean is_success, Bitmap bmp, String msg) {

            if (is_success) {
                _complete_action.onSuccess(bmp);
            } else {
                _complete_action.onFailure(msg);
            }

            runCompleteAction(is_success); // must be called finally
        }
    }

    /**
     * Constructor.
     *
     * @param url_str Destination URL string.
     * @param action Call back object.
     */
    public CommRequestGetUrlBitmap(String url_str, IRequestGetUrlBitmapAction action) {
        this._url_str = url_str;
        this._complete_action = action;
    }

    @Override
    public void runRequest() {
        CommRequestBitmapTask task = new CommRequestBitmapTask();
        task.addCompleteNotify(new TaskActionComplete());
        task.execute(_url_str);
    }
}
