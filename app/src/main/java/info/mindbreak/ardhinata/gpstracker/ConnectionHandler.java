package info.mindbreak.ardhinata.gpstracker;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by ardhinata on 27/04/16.
 */
public class ConnectionHandler {
	public static final String SERVER_ADDRESS = "http://tracker.mindbreak.info";
	public static final String API_LOCATION = SERVER_ADDRESS + "/api-insert.php";
	public static final int TIMEOUT_SHORT = 10000;
	public static final int TIMEOUT_NORMAL = 20000;
	public static final int TIMEOUT_LONG = 60000;
	public String errorResponse = null;
	private boolean errorOccurred = false;
	private boolean timeoutError = false;
	private HttpURLConnection serverConn = null;
	private InputStream response = null;
	private ByteArrayOutputStream responseRaw;
	private int timeout = TIMEOUT_NORMAL;
	private Activity activity;

	public ConnectionHandler(Activity activity) {
		this.activity = activity;
	}

	public static boolean isOnline(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		return networkInfo != null && networkInfo.isConnectedOrConnecting();
	}

	public ConnectionHandler setUrlAddress(String address) {
		Log.i("Connection Handler", "Set address to: " + address);
		try {
//			Log.i("ConnectionHandler", "URL is " + address);
			URL serverHost = new URL(address);
			serverConn = (HttpURLConnection) serverHost.openConnection();
		} catch (IOException e) {
			Log.e("Connection Handler", e.getMessage(), e);
			errorOccurred = true;
		}
		return this;
	}

	public ConnectionHandler setPostData(String data) {
		Log.i("Connection Handler", "Set post: " + data);
		if (serverConn != null && !errorOccurred) {
			try {
				serverConn.setDoOutput(true);
				serverConn.setFixedLengthStreamingMode(data.length());
				OutputStream payload = new BufferedOutputStream(serverConn.getOutputStream());
				payload.write(data.getBytes());
				payload.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return this;
	}

	public ConnectionHandler setTimeout(int milliseconds) {
		timeout = milliseconds;
//            Log.i("ConnectionHandler", "Timeout: " + String.valueOf(milliseconds) + " and " + serverConn.getURL().getPath());
		return this;
	}

	public boolean connect() {
		errorOccurred = !isOnline(activity);
		Log.i("Is Connected", String.valueOf(isOnline(activity)));
		if (serverConn != null && !errorOccurred) {
			try {
				serverConn.setConnectTimeout(timeout);
				serverConn.setReadTimeout(timeout);
				serverConn.connect();
				ExecutorService executorService = Executors.newSingleThreadExecutor();
				response = new BufferedInputStream(serverConn.getInputStream());
//                    response = serverConn.getInputStream();
//                    Log.i("ConnectionHandler", "response available buffer, " + String.valueOf(response.available()));
				Callable<ByteArrayOutputStream> callable = new Callable<ByteArrayOutputStream>() {
					@Override
					public ByteArrayOutputStream call() throws Exception {
						ByteArrayOutputStream out = new ByteArrayOutputStream();
						byte[] buf = new byte[1024];
						int i = 1;
						while (i > 0) {
							i = response.read(buf, 0, 1024);
//                                Log.i("ConnectionHandler", "Begin read, " + String.valueOf(i));
							if (i < 0) break;
							out.write(buf, 0, i);
//                                Log.i("ConnectionHandler", "on write " + String.valueOf(i));

						}
						return out;
					}
				};
				Future<ByteArrayOutputStream> future = executorService.submit(callable);
				responseRaw = future.get(timeout, TimeUnit.MILLISECONDS);
			} catch (IOException | InterruptedException | ExecutionException e) {
				Log.e("Connection Handler", e.getMessage(), e);
				errorOccurred = true;
				byte[] errBuf = new byte[4096];
				try {
					serverConn.getErrorStream().read(errBuf);
					errorResponse = String.valueOf(errBuf);
				} catch (IOException e1) {
					Log.e("Connection Handler", e.getMessage(), e);
					errorResponse = "Error on error";
				}

			} catch (TimeoutException e) {
				Log.e("Connection Handler", e.getMessage(), e);
				errorOccurred = true;
				timeoutError = true;
			} finally {
				serverConn.disconnect();
			}
			return true;
		}
		return false;
	}

	public String getResponseString() {
		if (!errorOccurred) return responseRaw.toString();
		else return null;
	}

	public byte[] getResponseByteArray() {
		if (!errorOccurred) return responseRaw.toByteArray();
		else return null;
	}

	public boolean isNetworkTimeout() {
		return timeoutError;
	}
}
