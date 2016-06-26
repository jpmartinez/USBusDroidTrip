package tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Helpers;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;


/**
 * Created by Kavesa on 26/4/2016.
 */
public class RestCall {

    private String restURL;
    private String method;
    private JSONObject dataOut;
    private String token;

    public RestCall() {
    }

    public RestCall(String url, String callMethod, JSONObject data, String auth_token) {
        restURL = url;
        method = callMethod;
        dataOut = data;
        token = auth_token;
    }

    public String getRestURL() {
        return restURL;
    }

    public void setRestURL(String restURL) {
        this.restURL = restURL;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public JSONObject getDataOut() {
        return dataOut;
    }

    public void setDataOut(JSONObject dataOut) {
        this.dataOut = dataOut;
    }

    public JSONObject getData() throws JSONException {
        JSONObject toReturn = null;
        System.out.println("==================");

        if(this.restURL == null || this.restURL.isEmpty()) {
            toReturn = new JSONObject("{\"error\":\"USBus - URL not initialized\"");
        }
        else {

            HttpURLConnection connection = null;
            BufferedReader reader = null;
            StringBuilder sb = new StringBuilder();
            try {
                URL restURL = new URL(this.restURL);
                connection = (HttpURLConnection) restURL.openConnection();
                connection.setRequestMethod(this.method);
                connection.setRequestProperty("Content-Type", "application/json");
                if (token != null && !token.isEmpty()) {
                    connection.setRequestProperty("Authorization", "Bearer " + token);
                }
                connection.connect();

                if(dataOut != null) {
                    OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
                    out.write(this.dataOut.toString());
                    out.close();
                }

                int HttpResult = connection.getResponseCode();

                if (HttpResult == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(
                            connection.getInputStream(), "utf-8"));
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }
                    br.close();

                    toReturn = new JSONObject();
                    toReturn.put("result", "OK");
                    toReturn.put("data", new JSONObject(sb.toString()));

                } else {
                    System.out.println(connection.getResponseMessage());
                    toReturn = new JSONObject("{\"result\":\"ERROR\", \"data\": \"" + connection.getResponseMessage() + "\"}");
                }

            } catch (ProtocolException e1) {
                e1.printStackTrace();
                toReturn = new JSONObject("{\"result\":\"ERROR\", \"data\": \"ProtocolException - " + e1.getMessage().replace(":", "-") + "\"}");
            } catch (MalformedURLException e1) {
                e1.printStackTrace();
                toReturn = new JSONObject("{\"result\":\"ERROR\", \"data\": \"MalformedURLException - " + e1.getMessage().replace(":", "-") + "\"}");
            } catch (IOException e1) {
                e1.printStackTrace();
                toReturn = new JSONObject("{\"result\":\"ERROR\", \"data\": \"IOException - " + e1.getMessage().replace(":", "-") + "\"}");
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                    return toReturn;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return toReturn;
    }
}

