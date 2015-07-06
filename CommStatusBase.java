package webservice;

/**
 * CommStatusBase is abstract base class that defined some method that will be called on CommRequestMessageTask.
 * we can override some methods and custom sending format for different
 */
public abstract class CommStatusBase {

	private String _url = "";
	private String _data_str = "";
	private CommType _comm_type = CommType.HttpPost;

	/**
	 * Http sending mode.
	 */
	public enum CommType {
		/**
		 * Http/Post mode.
		 */
		HttpPost,
		/**
		 * Http/Get mode.
		 */
		HttpGet
	}

	/**
	 * Get URL string. If connection type is Http/Get, this string includes data string.
	 * @return Sending URL string.
	 */
	public String getRequestURL() {
		return getOriginalURL();
	}

	/**
	 * Get Http/Post data string. If using Http/Get, this method return empty string.
	 * @return Http/Post data string.
	 */
	public String getPostString() {
		return "";
	}

	/**
	 * Set Http/Post data string.
	 * @param str Http/Post data string.
	 */
	public void setDataString(String str) {
		this._data_str = str;
	}

	/**
	 * Get data string that will be sent.
	 * @return Data string.
	 */
	protected String getDataString() {
		return _data_str;
	}

	/**
	 * Set connection mode as Http/Post or Http/Get.
	 * @param type Connection mode.
	 */
	public void setHttpType(CommType type) {
		_comm_type = type;
	}

	/**
	 * Check if using Http/Post.
	 * @return True if using Http/Post. False if using Http/Get.
	 */
	public boolean isHttpPost() {
		return CommType.HttpPost == this._comm_type;
	}

	/**
	 * Check if using Http/Get.
	 * @return True if using Http/Get. False if using Http/Post.
	 */
	public boolean isHttpGet() {
		return CommType.HttpGet == this._comm_type;
	}

	/**
	 * Set original URL string.
	 * @param url URL string.
	 */
	public void setOriginalURL(String url) {
		_url = url;
	}

	/**
	 * Get original URL string.(not include Http/Get data string)
	 * @return URL string.
	 */
	protected String getOriginalURL() {
		return _url;
	}

}
