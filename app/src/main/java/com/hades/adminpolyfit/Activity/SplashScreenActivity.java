package com.hades.adminpolyfit.Activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.KeyguardManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hades.adminpolyfit.Utils.Constants;
import com.hades.adminpolyfit.R;
import com.hades.adminpolyfit.Services.AdminPolyfitServices;
import com.hades.adminpolyfit.Services.RetrofitClient;
import com.ornach.nobobutton.NoboButton;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Objects;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import retrofit2.Retrofit;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class SplashScreenActivity extends AppCompatActivity implements View.OnClickListener {
    private final int SPLASH_DISPLAY_LENGTH = 5000;
    LinearLayout layoutLogin, layoutLogo;
    Animation animation;
    NoboButton btnLogin;
    private KeyStore keyStore;
    private static final String KEY_NAME = "PHAYTRAN";
    private Cipher cipher;
    ImageView fingerPrint;
    private AdminPolyfitServices adminPolyfitServices;
    private CompositeSubscription mSubscriptions = new CompositeSubscription();
    EditText edtUserName, edtPassword;
    TextView tvOr,tvAuthentication;
    String password;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_splash_screen);
        connectView();
        Retrofit retrofit = RetrofitClient.getInstance();
        adminPolyfitServices = retrofit.create(AdminPolyfitServices.class);
        OnStartApplication();
        showLogo();
//        setupFingerPrint();
        checkIsLogin();

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogin:
                loginAdmin(edtUserName.getText().toString(), edtPassword.getText().toString());
                break;

        }
    }

//    private void login() {
////        startActivity(new Intent(this, MainActivity.class));
//        loginUser();
//    }

    private void connectView() {
        layoutLogin = findViewById(R.id.layoutLogin);
        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);
        layoutLogo = findViewById(R.id.layoutLogo);
        fingerPrint = findViewById(R.id.imvFinger);
        edtUserName = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        tvOr=findViewById(R.id.tvOr);
        tvAuthentication=findViewById(R.id.tvAuthentication);
    }

    private void OnStartApplication() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPreferences = getSharedPreferences(Constants.LOGIN, MODE_PRIVATE);
                String username = sharedPreferences.getString(Constants.USERNAME, "");
                if (!username.isEmpty()) {
                    Intent intentToMain = new Intent(SplashScreenActivity.this, MainActivity.class);
                    startActivity(intentToMain);
                    finish();
                } else {
                    layoutLogo.setVisibility(View.GONE);
                    layoutLogin.setVisibility(View.VISIBLE);
                    animation = AnimationUtils.loadAnimation(getApplicationContext(),
                            R.anim.fade_in);
                    layoutLogin.startAnimation(animation);
                }
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    private void showLogo() {
        layoutLogo.setVisibility(View.VISIBLE);
        animation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fade_in);
        layoutLogo.startAnimation(animation);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setupFingerPrint() {
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        FingerprintManager fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        if (!fingerprintManager.isHardwareDetected())
            Toast.makeText(this, "Fingerprint authentication permission not enable", Toast.LENGTH_SHORT).show();
        else {
            if (!fingerprintManager.hasEnrolledFingerprints())
                Toast.makeText(this, "Register at least one fingerprint in Settings", Toast.LENGTH_SHORT).show();
            else {
                if (!keyguardManager.isKeyguardSecure())
                    Toast.makeText(this, "Lock screen security not enabled in Settings", Toast.LENGTH_SHORT).show();
                else
                    genKey();

                if (cipherInit()) {
                    FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(cipher);
                    FingerPrintHandle helper = new FingerPrintHandle(this, fingerPrint,edtPassword,password);
                    helper.startAuthentication(fingerprintManager, cryptoObject);

                }
            }
        }
    }

    private boolean cipherInit() {

        try {
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }

        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME, null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (IOException e1) {

            e1.printStackTrace();
            return false;
        } catch (NoSuchAlgorithmException e1) {

            e1.printStackTrace();
            return false;
        } catch (CertificateException e1) {

            e1.printStackTrace();
            return false;
        } catch (UnrecoverableKeyException e1) {

            e1.printStackTrace();
            return false;
        } catch (KeyStoreException e1) {

            e1.printStackTrace();
            return false;
        } catch (InvalidKeyException e1) {

            e1.printStackTrace();
            return false;
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void genKey() {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }

        KeyGenerator keyGenerator = null;

        try {
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }

        try {
            keyStore.load(null);
            keyGenerator.init(new KeyGenParameterSpec.Builder(KEY_NAME, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT).setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7).build()
            );
            keyGenerator.generateKey();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }


    }

    private void loginAdmin(final String userName, final String password) {
        final ProgressDialog progressDialog = new ProgressDialog(SplashScreenActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Processing...");
        progressDialog.setIndeterminate(false);
        progressDialog.show();

        mSubscriptions.add(adminPolyfitServices.loginAdmin(userName, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        if (s.contains("User does not exist")) {
                            Toast.makeText(SplashScreenActivity.this, "" + s, Toast.LENGTH_SHORT).show();
                            Log.e("PhayTV::", s);
                            progressDialog.dismiss();
                        } else {
                            Toast.makeText(SplashScreenActivity.this, "Login success!", Toast.LENGTH_SHORT).show();
                            SharedPreferences.Editor editor = getSharedPreferences(Constants.LOGIN, MODE_PRIVATE).edit();
                            editor.putString("username", userName);
                            editor.putString("password", password);
                            editor.putString("token", s);
                            editor.apply();
                            progressDialog.dismiss();
                            Log.e("PhayTran", "username:" + userName + "\n" + "password" + password);
                            startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                            finish();
                        }

                    }
                }));
    }
    private void checkIsLogin(){
        SharedPreferences sharedPreferences=getSharedPreferences(Constants.LOGIN,MODE_PRIVATE);
        String username = sharedPreferences.getString("username","");
        password=sharedPreferences.getString("password","");
        if(username.length()>0){
            edtUserName.setText(username);
            edtUserName.setFocusable(false);
            fingerPrint.setVisibility(View.VISIBLE);
            tvAuthentication.setVisibility(View.VISIBLE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                setupFingerPrint();
            }
        }else {
            fingerPrint.setVisibility(View.GONE);
            tvOr.setVisibility(View.GONE);
            tvAuthentication.setVisibility(View.GONE);
        }
    }

}
