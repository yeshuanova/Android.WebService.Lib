package webservice;


import java.util.ArrayList;
import java.util.List;

/**
 * A management class for request objects. We can add request object into class
 * and set three different running modes (Sequence, Sequence_Continue, and Overall)
 * to control the request running behavior. Finally, the completion notify will be
 * executed when running task is completed or stopped.
 */
public class CommChainManager {

	private List<CommBaseRequest> _request_list = new ArrayList<>();
	private List<OnRequestChainComplete> _chain_complete_notify_list = new ArrayList<>();
	private OnRequestChainActionState _action_state = new SequenceState();

	/**
	 * Run request mode. (Default is OVERALL)
	 */
	public enum MODE {
		/**
		 * Run requests in list one by one. (Stop when running request failure)
		 */
		SEQUENCE,
		/**
		 * Run requests in list one by one. (Non-Stop when running request failure)
		 */
		SEQUENCE_CONTINUE,
		/**
		 * Run all requests in list at the same time.
		 */
		OVERALL
	}

	/**
	 * A interface to execute requests with different running mode.
	 */
	private interface OnRequestChainActionState {
		/**
		 * Start to run the request list.
		 * @param list Request list will be run.
		 */
		void onStartRunRequestChain(List<CommBaseRequest> list);

		/**
		 * When one request completes, this method will be called to
		 * decide next action.
		 *
		 * @param next_index Index that will be run in next time.
		 * @param is_success Flag if running previous request successfully.
		 */
		void onRunSingleRequestComplete(int next_index, boolean is_success);
	}

	/**
	 * A interface called
	 */
	public interface OnRequestChainComplete {
		/**
		 * Method that will be called when running list completion.
		 *
		 * @param is_success Flag if executing request list successfully.
		 */
		void onRequestChainComplete(boolean is_success);
	}

	/**
	 * Running Mode - Sequence State.
	 */
	private class SequenceState implements OnRequestChainActionState {

		@Override
		public void onStartRunRequestChain(List<CommBaseRequest> list) {
			list.get(0).runRequest();
		}

		@Override
		public void onRunSingleRequestComplete(int next_index, boolean is_success) {
			if (!is_success) {
				runRequestChainCompleteNotify(is_success);
				return;
			}
			
			if (next_index < _request_list.size()) {
				_request_list.get(next_index).runRequest();
			} 
		}
	}

	/**
	 * Running Mode - Sequence Continue State.
	 */
	private class SequenceContinueState implements OnRequestChainActionState {
		
		@Override
		public void onStartRunRequestChain(List<CommBaseRequest> list) {
			list.get(0).runRequest();
		}
		
		@Override
		public void onRunSingleRequestComplete(int next_index, boolean is_success) {
			if (next_index < _request_list.size()) {
				_request_list.get(next_index).runRequest();
			}
		}
	}

	/**
	 * Running Mode - Overall State.
	 */
	private class OverallState implements OnRequestChainActionState {

		@Override
		public void onStartRunRequestChain(List<CommBaseRequest> list) {
			for (CommBaseRequest request : _request_list) request.runRequest();
		}
		
		@Override
		public void onRunSingleRequestComplete(int next_index, boolean is_success) {
		}
	}

	/**
	 * Callback to decide action when a request complete.
	 */
	private class IRequestComplete implements CommBaseRequest.IRequestComplete {

		/**
		 * Flag for all request run is success.
		 */
		private boolean _is_all_success = true;

		/**
		 * The index of request will be run.
		 */
		private int _run_index = 0;

		@Override
		public void onRequestComplete(boolean is_success) {

			_is_all_success = _is_all_success && is_success;
			++_run_index;

			if (_run_index >= _request_list.size()) {
				runRequestChainCompleteNotify(_is_all_success);
				return;
			}
			
			_action_state.onRunSingleRequestComplete(_run_index, is_success);
		}
	}

	/**
	 * Constructor
	 */
	public CommChainManager() {}

	/**
	 * Set running mode.
	 * @param mode Running Mode.
	 */
	public void setMode(MODE mode) {
		switch (mode) {
		case SEQUENCE:
			_action_state = new SequenceState();
			break;
		case SEQUENCE_CONTINUE:
			_action_state = new SequenceContinueState();
			break;
		case OVERALL:
			_action_state = new OverallState();
			break;
		}
	}

	/**
	 * Add notification object will be called when request completion.
	 * @param notify Notification object.
	 */
	public void addRequestChainCompleteNotify(OnRequestChainComplete notify) {
		if (null == notify) {
			return;
		}
		_chain_complete_notify_list.add(notify);
	}

	/**
	 * Run completed notification.
	 * @param is_success True if all requests are successful, or false if one of requests is failure.
	 */
	private void runRequestChainCompleteNotify(boolean is_success) {
		for (OnRequestChainComplete action : _chain_complete_notify_list) {
			action.onRequestChainComplete(is_success);
		}
	}

	/**
	 * Add a request object.
	 * @param request A request object extends form CommBaseRequest class
	 */
	public void addRequest(CommBaseRequest request) {
		if (null == request) {
			return;
		}
		_request_list.add(request);
	}

	/**
	 * Start to run request chain.
	 */
	public void runRequestChain() {

		IRequestComplete final_notify = new IRequestComplete();
		for (CommBaseRequest action : _request_list) {
			action.setRequestChainFinalNotify(final_notify);
		}
		_action_state.onStartRunRequestChain(_request_list);
	}

}
