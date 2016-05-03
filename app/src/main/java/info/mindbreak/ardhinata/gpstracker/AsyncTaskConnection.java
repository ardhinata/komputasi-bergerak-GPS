package info.mindbreak.ardhinata.gpstracker;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by ardhinata on 27/04/16.
 */
public class AsyncTaskConnection extends AsyncTask {
	String postData = null;
	Activity activity;
	private Bundle output = new Bundle();
	private Bundle args;
	private ConnectionCallback callback;
	private ConnectionHandler handler = null;
	private ArrayList<InfoContainer> loc = new ArrayList<>();

	public AsyncTaskConnection(@NonNull Activity currentActivity, @NonNull Bundle bundledArgs, @NonNull ConnectionCallback connectionCallback, ArrayList<InfoContainer> arrayList) {
		activity = currentActivity;
		args = bundledArgs;
		callback = connectionCallback;
		loc = arrayList;
		handler = new ConnectionHandler(activity);
		Log.i("AsyncTask", "Async Task for " + currentActivity.getLocalClassName() + " created");
	}

	@Override
	protected Object doInBackground(Object[] params) {
		backgroundTask();
		return null;
	}

	@Override
	protected void onPostExecute(Object o) {
		callback.connectionFinished(output);

	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		callback.preExecuteWrap();
	}

	private void backgroundTask() {
		Log.i("AsyncTask", "Async Task for " + activity.getLocalClassName() + " being executed with name " + args.getString("task"));
		output = new Bundle();
		switch (args.getString("task")) {
			case "check_server":
				output.putString("task", args.getString("task"));
				handler.setUrlAddress(ConnectionHandler.SERVER_ADDRESS + "/up.txt").setTimeout(ConnectionHandler.TIMEOUT_SHORT).connect();
//				Log.i("Check Connect", handler.getResponseString());
				if (handler.getResponseString() != null && handler.getResponseString().contains("Ok")) {
					output.putBoolean("check_boolean_connected", true);
				} else {
					output.putBoolean("check_boolean_connected", false);
				}
				break;

			case "upload":
				Log.i("AsyncTask", "Location size is " + String.valueOf(loc.size()));
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				output.putString("task", args.getString("task"));
				String username = args.getString("string_username").replace(" ", "");
				for (int i = 0; i < loc.size(); i++) {
					InfoContainer info = loc.get(i);
					postData = "latitude=" + info.getLat() + "&longitude=" + info.getLon() + "&altitude=" + info.getAlt() + "&accuracy=" + info.getAcc() + "&username=" + username + "&time=" + Uri.encode(df.format(info.getCalendar().getTime()));
					handler = new ConnectionHandler(activity);
					handler.setUrlAddress(ConnectionHandler.SERVER_ADDRESS + "/api-insert.php").setPostData(postData).setTimeout(ConnectionHandler.TIMEOUT_SHORT).connect();
					Log.i("Send Location", "Message #" + String.valueOf(i) + ": " + handler.getResponseString());
				}
				break;

			default:
				break;
		}
	}

	interface ConnectionCallback {
		void connectionFinished(Bundle output);

		void connectError(Bundle output);

		void preExecuteWrap();

		void postExecuteWrap();
	}
}
