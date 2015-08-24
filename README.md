# Android WebService Library

## Introduction
This project is a simple web request service library for Android platform. You can use `git subtree` command to pull this library to your Android project and create custom **Request** or **Task** class yourself.

## Basic Concept
There are three parts - `Task`, `Request` and `RequestManager`, in this library.

1. **Task**

	**Task** is basic part that translates data to web server. Usually, we use `AsyncTask<>` class to send data in another thread and `Task` calls delegate class method when action completes. For example, the `CommRequestBitmapTask` try to get data stream by URL and convert to Bitmap structure. If process completes, it calls `onGetBitmapComplete(...)` method implemented by `ActionComplete` interface. We can add custom delegated class implemented by `ActionComplete` interface to run our method.
	
2. **Request**

	**Request** part is core process unit to prepare data, create custom **Task** delegate to prepare necessary information, start **Task** action, receive responding data, process returning data and execute callback method set by user.
	
	For instance, `CommRequestJsonMsg` class is a **Request** component that send data to server using `CommRequestMessageTask` and `CommBaseStatus` class.  When data returns, it convert string to specified class structure by [Gson](https://github.com/google/gson) library. If process completes, it return converted class to user. 

3. **RequestManager**
 
  **RequestManager** is a special class to manage running process of multiple **Request** with different mode. For example, `CommChainManager` can add object that inherits `CommBaseRequest' class and executes requests with **SEQUENCE**, **SEQUENCE_CONTINUE**, or **OVERALL** mode.

## Example

A simple example is pushed in [here](https://github.com/yeshuanova/Android.WebService).
