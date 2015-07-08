package webservice.task;

import android.os.AsyncTask;
import android.util.Log;

import webservice.CommBaseStatus;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * CommRequestMessageTask extended from Android AsyncTask<> requests a message string to server
 * and responds string message. We use abstract class CommBaseStatus to implement connection setting
 * like url, http get/post, and sending string. When sending message completion, we call all register
 * interface ITaskCompleteAction to return data.
 */
public class CommRequestMessageTask extends AsyncTask<CommBaseStatus, Void, String> {

	private boolean _comm_success = false;
	private List<ITaskCompleteAction> _complete_notify = new ArrayList<>();
	private static final int TIME_OUT_CONN = 5000;
	private static final int TIME_OUT_READ = 10000;

	/**
	 * Completion callback interface.
	 */
	public interface ITaskCompleteAction {
		/**
		 * @param isSuccess Running status. (true: success, false: failure)
		 * @param result Result data string or error message string.
		 */
		void onTaskComplete(boolean isSuccess, String result);
	}

	/**
	 * Constructor.
	 */
	public CommRequestMessageTask() {
		super();
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	/**
	 * @param data Connection information object.
	 * @return Data string (successful) or error message (failure).
	 */
	@Override
	protected String doInBackground(CommBaseStatus... data) {
		String res_str = "";
		_comm_success = false;
		try {
			
			CommBaseStatus comm_data = data[0];
			
			Log.i(this.getClass().getName(), "URL : " + comm_data.getRequestURL());
			URL url = new URL(comm_data.getRequestURL());
			HttpURLConnection conn_url = (HttpURLConnection)url.openConnection();
			conn_url.setConnectTimeout(TIME_OUT_CONN);
			conn_url.setReadTimeout(TIME_OUT_READ);
			
			if (comm_data.isHttpPost()) {
				conn_url.setDoInput(true);
				conn_url.setDoOutput(true);
				OutputStream os = conn_url.getOutputStream();
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
				Log.i(this.getClass().getName(), "POST DATA : " + comm_data.getPostString());
				writer.write(comm_data.getPostString());
				writer.flush();
				writer.close();
				os.close();
			} else {
				conn_url.setDoOutput(false);
			}

			// Get input stream and convert to data string
			InputStream input_stream = conn_url.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(input_stream, "UTF-8"));
			StringBuilder builder = new StringBuilder();
			String line = "";
			while ((line = reader.readLine()) != null) {
				builder.append(line);
			}
			res_str = builder.toString();
			reader.close();
			
			conn_url.disconnect();
			_comm_success = true;
		} catch (Exception e) {
			e.printStackTrace();
			res_str = e.toString();
			Log.w(this.getClass().getName(), "Exception: Exception:\n" + e.toString());
		}
		
		return res_str;
	}

	/**
	 * Add notify object to call back list.
	 * @param notify Notify object implemented with ITaskCompleteAction interface.
	 */
	public void addCompleteNotify(ITaskCompleteAction notify) {
		if (null == _complete_notify) {
			_complete_notify = new ArrayList<>();
		}
		_complete_notify.add(notify);
	}
	
	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		// Call all notify list.
		for (ITaskCompleteAction notify : _complete_notify) {
			notify.onTaskComplete(_comm_success, result);
		}
	}

}
