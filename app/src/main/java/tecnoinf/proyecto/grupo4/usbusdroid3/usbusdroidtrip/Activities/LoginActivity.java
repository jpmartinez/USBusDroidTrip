package tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Helpers.RestCall;
import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.R;

public class LoginActivity extends AppCompatActivity {

    private static String loginURL;
    private SharedPreferences sharedPreferences;
    private String saved_username;
    private String saved_password;
    private UserLoginTask mAuthTask = null;
    private View mProgressView;
    private View mLoginFormView;
    private EditText mPasswordView;
    private AutoCompleteTextView mEmailView;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginURL = getString(R.string.URLlogin, getString(R.string.URL_REST_API));
        sharedPreferences = getSharedPreferences("USBusData", Context.MODE_PRIVATE);
//        saved_password = sharedPreferences.getString("password", "first_use");
//        if(!saved_password.equalsIgnoreCase("first_use")) {
//            saved_username = sharedPreferences.getString("username", "");
//
//            mAuthTask = new UserLoginTask(saved_username, saved_password, getApplicationContext(), "twitter");
//            mAuthTask.execute((Void) null);
//        }

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        mEmailView = (AutoCompleteTextView) findViewById(R.id.username);
        mPasswordView = (EditText) findViewById(R.id.password);

        loginButton = (Button) findViewById(R.id.email_sign_in_button);
        assert loginButton != null;
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });
    }

    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) || password.length() < 4) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password, getApplicationContext(), "usbus");
            mAuthTask.execute((Void) null);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        mProgressView.setVisibility(show? View.VISIBLE : View.GONE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String username;
        private final String mPassword;
        private final String mType;
        private String token;
        private Context mCtx;

        UserLoginTask(String user, String password, Context ctx, String type) {
            username = user;
            mPassword = password;
            mCtx = ctx;
            mType = type;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            JSONObject result;
            JSONObject registerResult;
            try {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                JSONObject credentials = new JSONObject();
                credentials.put("username", username);
                credentials.put("tenantId", mCtx.getString(R.string.tenantId));
                credentials.put("password", mPassword);

                RestCall call = new RestCall(loginURL, "POST", credentials, null);
                result = call.getData();
                //String dummy = result.toString();
                System.out.println(result);
                if(result.get("result").toString().equalsIgnoreCase("OK")) {
                    //login OK
                    System.out.println("LOGIN OK...");
                    JSONObject data = new JSONObject(result.get("data").toString());
                    token = data.getString("token");

                    editor.putString("token", token);
                    editor.putString("username", username);
                    editor.putString("password", mPassword);
                    editor.putString("tenantId", getString(R.string.tenantId));
                    editor.putString("loginURL", loginURL);
                    editor.apply();
                } else {
                    //algun error
                    System.out.println("DANGER WILL ROBINSON..." + result.get("result").toString());
                    return false;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                showProgress(true);
                Intent mainIntent = new Intent(getBaseContext(), MainActivity.class);
//                mainIntent.putExtra("token", token);
//                mainIntent.putExtra("username", username);
                startActivity(mainIntent);

                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}
