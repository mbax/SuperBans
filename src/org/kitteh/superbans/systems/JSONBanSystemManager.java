package org.kitteh.superbans.systems;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.kitteh.superbans.SuperBans;

public abstract class JSONBanSystemManager extends BanSystemManager {

    protected class asyncCall implements Runnable {
        private final String url;
        private final HashMap<String, String> POSTData;
        private final ResponseProcessor processor;
        private final String processorID;

        public asyncCall(String url) {
            this(url, new HashMap<String, String>(), null, null);
        }

        public asyncCall(String url, HashMap<String, String> POSTData) {
            this(url, POSTData, null, null);
        }

        public asyncCall(String url, HashMap<String, String> POSTData, ResponseProcessor processor, String processorID) {
            this.url = url;
            this.POSTData = POSTData;
            this.processor = processor;
            this.processorID = processorID;
        }

        @Override
        public void run() {
            if (this.processor != null) {
                synchronized (this.processor) {
                    this.processor.processed(this.processorID, JSONBanSystemManager.APICall(this.url, this.POSTData));
                }
            } else {
                JSONBanSystemManager.APICall(this.url, this.POSTData);
            }
        }
    }

    protected abstract class ResponseProcessor {
        public abstract void processed(String ID, JSONObject object);
    }

    private static String makeUTF8(String toConvert) throws UnsupportedEncodingException {
        return URLEncoder.encode(toConvert, "UTF-8");
    }

    private static String postVariable(HashMap<String, String> variables) {
        if(variables.isEmpty()){
            return "";
        }
        try {
            final StringBuilder stringBuilder = new StringBuilder();
            for (final Map.Entry<String, String> entry : variables.entrySet()) {
                if (stringBuilder.length() == 0) {
                    stringBuilder.append(JSONBanSystemManager.makeUTF8(entry.getKey()) + "=" + JSONBanSystemManager.makeUTF8(entry.getValue()));
                } else {
                    stringBuilder.append("&" + JSONBanSystemManager.makeUTF8(entry.getKey()) + "=" + JSONBanSystemManager.makeUTF8(entry.getValue()));
                }
            }
            return stringBuilder.toString();
        } catch (final UnsupportedEncodingException e) {
            return "";
        }
    }

    protected static JSONObject APICall(String url, HashMap<String, String> POSTData) {
        String preprocessed;
        try {
            final String POSTstring = JSONBanSystemManager.postVariable(POSTData);
            final URL urlTarget = new URL(url.replace(" ", "%20"));
            final URLConnection connection = urlTarget.openConnection();
            connection.setDoOutput(true);
            connection.setConnectTimeout(8000);
            connection.setReadTimeout(15000);
            connection.setRequestProperty("User-agent", "SuperBans");
            final OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(POSTstring);
            writer.flush();
            final StringBuilder stringBuilder = new StringBuilder();
            final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            writer.close();
            reader.close();
            preprocessed = stringBuilder.toString();
            SuperBans.Debug("Received from API: " + preprocessed);
        } catch (final Exception e) {
            SuperBans.Debug("Error communicating to " + BanSystemManager.getName(), e);
            preprocessed = "";
        }
        JSONObject result = null;
        try {
            result = new JSONObject(preprocessed);
        } catch (final JSONException e) {
        }
        return result;
    }

    public JSONBanSystemManager(SuperBans plugin, String name) {
        super(plugin, name);
    }

    @SuppressWarnings("rawtypes")
    protected HashMap<String, String> JSONToHashMap(JSONObject result) {
        final HashMap<String, String> output = new HashMap<String, String>();
        if (result != null) {
            final Iterator i = result.keys();
            if (i != null) {
                while (i.hasNext()) {
                    final String next = (String) i.next();
                    output.put(next, result.optString(next, ""));
                }
            }
        }
        return output;
    }

}
