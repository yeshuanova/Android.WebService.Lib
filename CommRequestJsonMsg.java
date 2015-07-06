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
public class CommRequestJsonMsg<SendType, ReturnType> extends CommBaseRequest {

	private RequestJsonMsgCallback<ReturnType> _callback;
	private TypeToken<SendType> _send_type_token;
	private TypeToken<ReturnType> _return_type_token;
	private SendType _send_data;
	private CommStatusBase _comm_obj = new DefaultStatus();

	/**
	 * Default status object.
	 */
	private class DefaultStatus extends CommStatusBase {

	}

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
	 * @param send_data Sending data object.
	 * @param callback Callback object implemented RequestJsonMsgCallback<> interface
	 * @param send_type_token Gson TypeToken object of requesting data type.
	 * @param return_type_token Gson TypeToken object of responding data type.
	 */
	public CommRequestJsonMsg(
			SendType send_data,
			RequestJsonMsgCallback<ReturnType> callback,
			TypeToken<SendType> send_type_token,
			TypeToken<ReturnType> return_type_token) {

		setSendData(send_data);
		setRequestCallback(callback);
		setSendDataTypeToken(send_type_token);
		setReturnDataTypeToken(return_type_token);
	}

	private void setSendData(SendType send_data) {
		_send_data = send_data;
	}

	private void setRequestCallback(RequestJsonMsgCallback<ReturnType> call_back) {
		_callback = call_back;
	}

	private void setSendDataTypeToken(TypeToken<SendType> type_token) {
		_send_type_token = type_token;
	}

	private void setReturnDataTypeToken(TypeToken<ReturnType> type_token) {
		_return_type_token = type_token;
	}

	public void setCommObj(CommStatusBase obj) {
		_comm_obj = obj;
	}

	@Override
	public void runRequest() {
		_comm_obj.setDataString(new Gson().toJson(_send_data, _send_type_token.getType()));

		CommRequestMessageTask send_data_http = new CommRequestMessageTask();
		send_data_http.addCompleteNotify(new TaskCompleteAction());
		send_data_http.execute(_comm_obj);
	}

}
