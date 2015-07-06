package webservice;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Window;

/**
 * CommProgressDialog show progressing dialog when sending data.
 */
public class CommProgressDialog {
	
	private ProgressDialog _progress_dlg = null;
	private CommChainManager _request_manager;
	private int _delay_time = 2000;
	private boolean _is_send_finish = false;
	private boolean _is_delay_finish = false;

	/**
	 * RequestChainCompleteAction is called when request chain is completed.
	 */
	class RequestChainCompleteAction implements CommChainManager.OnRequestChainComplete {

		@Override
		public void onRequestChainComplete(boolean is_success) {
			Log.d(this.getClass().getName(), "Run ProgressDialog request completion");
			_is_send_finish = true;
			checkFinish();
		}
	}

	/**
	 * TimeDelayAsyncTask run delay action using setting delay time.
	 */
	class TimeDelayAsyncTask extends AsyncTask<Integer, Void, Void> {

		@Override
		protected Void doInBackground(Integer... params) {
			try {
				Thread.sleep(params[0]);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			_is_delay_finish = true;
			checkFinish();
		}
	}

	/**
	 * Constructor.
	 * @param context Context object.
	 * @param msg Message string that show in dialog box.
	 * @param manager Running chain object.
	 */
	public CommProgressDialog(Context context, String msg, CommChainManager manager) {
		_progress_dlg = new ProgressDialog(context);
		_progress_dlg.setCancelable(false);
		_progress_dlg.setCanceledOnTouchOutside(false);
		_progress_dlg.setMessage(msg);
		_progress_dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
		_request_manager = manager;
	}

	/**
	 * Get minimum delay time.
	 * @return Minimum delay time (millisecond).
	 */
	public int get_delay_time() {
		return _delay_time;
	}

	/**
	 * Set minimum delay time of showing progress dialog.
	 * @param _delay_time Minimum delay time (millisecond).
	 */
	public void set_delay_time(int _delay_time) {
		this._delay_time = _delay_time;
	}

	/**
	 * Start to show progress dialog and run CommChainManager object.
	 */
	public void runProgressTask() {
		_progress_dlg.show();
		_is_delay_finish = false;
		_is_send_finish = false;
		_request_manager.addRequestChainCompleteNotify(new RequestChainCompleteAction());
		_request_manager.runRequestChain();
		(new TimeDelayAsyncTask()).execute(this._delay_time);
	}

	/**
	 * Check if the the progress completion. If all conditions are satisfied, progress dialog will be closed.
	 */
	protected void checkFinish() {
		if (_is_send_finish && _is_delay_finish) {
			_progress_dlg.dismiss();
		}
	}

}
