package medisys.android.com.medisys.client;

import android.os.AsyncTask;
import android.util.JsonWriter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;

import medisys.android.com.medisys.entities.LoginCredentials;

/**
 * Created by eselman on 24/03/2016.
 */
public class LoginClientTask extends AsyncTask {
    public static interface Callback {
        void loginCallback(Object sessionKey);
    }
    private Callback callback;

    public LoginClientTask (Callback callback) {
        this.callback = callback;
    }

    @Override
    protected Object doInBackground(Object[] params) {
        String sessionKey = null;

        try {

            // Create connection to login.
            HttpURLConnection loginConnection = (HttpURLConnection) new URL("http://10.0.2.2:8080/medisys/auth/login")
                    .openConnection();

            loginConnection.setDoInput(true);
            loginConnection.setDoOutput(true);
            loginConnection.setRequestMethod("POST");
            loginConnection.setRequestProperty("Content-Type", "application/json;charset=utf-8");

            // Write JSON Object
            JSONObject user = new JSONObject();
            user.put("username", params[0]);
            user.put("password", params[1]);
            OutputStreamWriter wr= new OutputStreamWriter(loginConnection.getOutputStream());
            wr.write(user.toString());
            wr.flush();

            // Parse response.
            Gson loginGson = new Gson();
            Type objType = new TypeToken<LoginCredentials>(){}.getType();
            InputStream loginInputStream = loginConnection.getInputStream();
            String loginStr = IOUtils.toString(loginInputStream);
            LoginCredentials loginCredentials  = loginGson.fromJson(loginStr, objType);
            sessionKey = loginCredentials.getSessionKey();
            loginConnection.disconnect();
        } catch (Exception e){
            e.printStackTrace();
        }
        return sessionKey;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        callback.loginCallback(o);
    }
}
