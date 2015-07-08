package webservice;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import webservice.task.CommRequestMessageTask;

/**
 * A Network Request that sends json format data to server and gets .
 * This class uses CommRequestMessageTask to send and receiver json format string, and
 * uses Gson library to convert string from data object to JSON string, and vice versa.
 *
 * Step:
 * 1. Create class object using necessary information like sending data object, Gson TypeToken<> object,
 * and call back object created by users.
 *
 * 2. When call runRequest() method, this method converts data object to string using Gson,
 * creates CommRequestMessageTask object to execute action.
 *
 * 3. When get request complete, CommRequestMessageTask call TaskCompleteAction::onTaskComplete(...) method
 * and send necessary data.
 *
 * 4. Call runCompleteAction(boolean) when process completes.
 *
 * @param <SendType> The requesting type.
 * @param <ReturnType> The responding type.
 */
public class CommRequestJsonMsg<ReturnType> extends CommBaseRequest {

	private RequestJsonMsgCallback<ReturnType> _callback;
	private TypeToken<ReturnType> _return_type_token;
	private CommBaseStatus _comm_obj;

	/**
	 * Callback object. Convert string to return type object using Gson library.
	 */
    class TaskCompleteAction implements CommRequestMessageTask.ITaskCompleteAction {

        @Override
        public void onTaskComplete(boolean isSuccess, String result) {
            boolean is_convert_success = false;
            if (isSuccess) {
                try {
                    Log.i(this.getClass().getName(), "Return Str : \n" + result);
                    ReturnType return_data = new Gson().getAdapter(_return_type_token).fromJson(result);
                    _callback.onRequestDataSuccess(return_data);
                    is_convert_success = true;
                } catch (JsonSyntaxException e) {
                    Log.w(this.getClass().getName(), "JsonSyntaxException: " + e.toString());
                    e.printStackTrace();
                    _callback.onRequestDataFailed(e.toString());
                } catch (Exception e) {
                    Log.w(this.getClass().getName(), "Exception: " + e.toString());
                    e.printStackTrace();
                    _callback.onRequestDataFailed(e.toString());
                }
            } else {
                try {
                    _callback.onRequestDataFailed(result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            runCompleteAction(is_convert_success);	// must be run finally!
        }
    }

	/**
	 * Callback interface.
	 * @param <ReturnType> Return data object.
	 */
	public interface RequestJsonMsgCallback<ReturnType> {
		/**
		 * Call this method if process run successfully.
		 * @param return_data Return data object.
		 */
		void onRequestDataSuccess(ReturnType return_data);

		/**
		 * Call this method if process exists error.
		 * @param fail_msg Error message.
		 */
		void onRequestDataFailed(String fail_msg);
	}

	/**
	 * Constructor.
	 *
	 * @param callback Callback object implemented RequestJsonMsgCallback<> interface
	 * @param return_type_token Gson TypeToken object of responding data type.
	 */
	public CommRequestJsonMsg(
			RequestJsonMsgCallback<ReturnType> callback,
			TypeToken<ReturnType> return_type_token,
			CommBaseStatus comm_obj) {

		this._callback = callback;
		this._return_type_token = return_type_token;
		this._comm_obj = comm_obj;
	}

	@Override
	public void runRequest() {
		CommRequestMessageTask send_data_http = new CommRequestMessageTask();
		send_data_http.addCompleteNotify(new TaskCompleteAction());
		send_data_http.execute(_comm_obj);
	}

}
