package com.example.myapplication;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ApiUtil {
    protected static final String BASE_URL = "http://10.0.2.2/attendance_system2/";
    private static final String TAG = "ApiUtil";

    public static void markAttendance(Context context, String qrData,
                                      Response.Listener<JSONObject> success,
                                      Response.ErrorListener error) {

        String url = BASE_URL + "mark_attendance.php";

        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                response -> {
                    Log.d(TAG, "Attendance response: " + response);
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        success.onResponse(jsonResponse);
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON parsing error: " + e.getMessage());
                        error.onErrorResponse(new VolleyError("Invalid JSON response: " + response));
                    }
                },
                volleyError -> {
                    Log.e(TAG, "Attendance error: " + volleyError.getMessage());
                    if (volleyError.networkResponse != null) {
                        Log.e(TAG, "Status code: " + volleyError.networkResponse.statusCode);
                        Log.e(TAG, "Response data: " + new String(volleyError.networkResponse.data));
                    }
                    error.onErrorResponse(volleyError);
                }
        ) {
            @Override
            public byte[] getBody() {
                try {
                    JSONObject jsonBody = new JSONObject();
                    // Send the entire QR code JSON string under "qr_data" key
                    jsonBody.put("qr_data", qrData);

                    String body = jsonBody.toString();
                    Log.d(TAG, "Attendance request body: " + body);
                    return body.getBytes("utf-8");
                } catch (Exception e) {
                    Log.e(TAG, "Error creating attendance request body: " + e.getMessage());
                    return null;
                }
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
        };

        // Set retry policy: 15 seconds timeout, no retries, default backoff multiplier
        request.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        // Add the request to the Volley request queue
        Volley.newRequestQueue(context).add(request);
    }

    public static void uploadStudentData(Context context, String id, String name, String base64Image,
                                         Response.Listener<JSONObject> success,
                                         Response.ErrorListener error) {

        String url = BASE_URL+"upload_student.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d(TAG, "Registration Response: " + response);
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        success.onResponse(jsonResponse);
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON parsing error: " + e.getMessage());
                        error.onErrorResponse(new VolleyError("Invalid JSON response: " + response));
                    }
                },
                volleyError -> {
                    Log.e(TAG, "Upload error: " + volleyError.getMessage());
                    if (volleyError.networkResponse != null) {
                        Log.e(TAG, "Status code: " + volleyError.networkResponse.statusCode);
                        Log.e(TAG, "Response data: " + new String(volleyError.networkResponse.data));
                    }
                    error.onErrorResponse(volleyError);
                }) {
            @Override
            public byte[] getBody() {
                try {
                    JSONObject jsonBody = new JSONObject();
                    jsonBody.put("id", id);
                    jsonBody.put("name", name);
                    jsonBody.put("image", base64Image);

                    String body = jsonBody.toString();
                    Log.d(TAG, "Upload request body: " + body);
                    return body.getBytes("utf-8");
                } catch (Exception e) {
                    Log.e(TAG, "Error creating upload request body: " + e.getMessage());
                    return null;
                }
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
        };

        // Set retry policy (optional)
        request.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));


        Volley.newRequestQueue(context).add(request);
    }

    public static void getStudentInfo(Context context, String id, String name,
                                      Response.Listener<JSONObject> success,
                                      Response.ErrorListener error) {

        String url = BASE_URL + "check_student.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d(TAG, "GetStudentInfo Response: " + response);
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        success.onResponse(jsonResponse);
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON parsing error: " + e.getMessage());
                        error.onErrorResponse(new VolleyError("Invalid JSON response: " + response));
                    }
                },
                volleyError -> {
                    Log.e(TAG, "GetStudentInfo error: " + volleyError.getMessage());
                    if (volleyError.networkResponse != null) {
                        Log.e(TAG, "Status code: " + volleyError.networkResponse.statusCode);
                        Log.e(TAG, "Response data: " + new String(volleyError.networkResponse.data));
                    }
                    error.onErrorResponse(volleyError);
                }) {
            @Override
            public byte[] getBody() {
                try {
                    JSONObject jsonBody = new JSONObject();
                    jsonBody.put("studentId", id);
                    jsonBody.put("fullname", name);

                    String body = jsonBody.toString();
                    Log.d(TAG, "GetStudentInfo request body: " + body);
                    return body.getBytes("utf-8");
                } catch (Exception e) {
                    Log.e(TAG, "Error creating GetStudentInfo request body: " + e.getMessage());
                    return null;
                }
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        Volley.newRequestQueue(context).add(request);
    }


}