
package webservice;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;


/**
 * CommBaseRequest is abstract class of running requests. It define running and completing interface,
 * and completion notification list. Inheritance class must implement runRequest() method to start run action.
 *
 * @author Peter.Li
 */
public abstract class CommBaseRequest {

    /**
     * A notification list.
     */
    private List<IRequestComplete> _final_action_list = new ArrayList<>();
    /**
     * A notification for CommChainManger class.
     */
    private IRequestComplete _req_chain_notify;

    /**
     * A notification interface.
     */
    public interface IRequestComplete {

        /**
         * Recall method when request run completion.
         *
         * @param is_success A flag show this request is success or failure.
         */
        void onRequestComplete(boolean is_success);
    }

    /**
     *  Start running the request action.
     */
    public abstract void runRequest();

    /**
     * Add notification object to completed notification list.
     *
     * @param notify Notification object
     */
    public void addCompleteNotify(IRequestComplete notify) {
        if (null == notify) {
            return;
        }
        _final_action_list.add(notify);
    }

    /**
     * Clear all completed notifications.
     */
    public void resetCompleteNotify() {
        _final_action_list.clear();
    }

    /**
     * Set a completed notification for request chain.
     *
     * @param notify Completion notification.
     */
    void setRequestChainFinalNotify(IRequestComplete notify) {
        _req_chain_notify = notify;
    }

    /**
     * Call the observer notification when this request is completed.
     * This method must be called manually when running action completion
     *
     * @param is_success an checking flag if this request executes successfully.
     */
    protected void runCompleteAction(boolean is_success) {
        Log.i(getClass().getName(), "Run Complete Action");
        for (IRequestComplete notify : _final_action_list) {
            if (null != notify) {
                notify.onRequestComplete(is_success);
            }
        }

        if (null != (_req_chain_notify)) {
            _req_chain_notify.onRequestComplete(is_success);
        }
    }

}
