/*
 * Copyright 2020 Damola Obaleke. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.datenight_immersia_ltd.views.readyplayerweb;

import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabColorSchemeParams;
import androidx.browser.customtabs.CustomTabsIntent;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import com.datenight_immersia_ltd.R;
import com.datenight_immersia_ltd.databinding.ActivityCreatAvatarBinding;
import com.datenight_immersia_ltd.modelfirestore.User.UserModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class CreatAvatarActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityCreatAvatarBinding binding;
    DocumentReference userDocRef;
    FirebaseFirestore db;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreatAvatarBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        userDocRef = db.collection("userData").document(mAuth.getCurrentUser().getUid());

        //webViewImplmentTwo();
        //webView();
        //webViewBottomSheet();
        //webViewDialogue();
        //openReadyPlayerWebPage();
        customTabLoadAvatar();

        binding.submitAvatarBtn.setOnClickListener(v -> storeAvatarLink());

        binding.forgotLinkText.setOnClickListener(this);


    }

    public void storeAvatarLink() {
        Log.i("Avatar Link", binding.avatarLinkInput.getText().toString());
        Toast.makeText(this, binding.avatarLinkInput.getText().toString(), Toast.LENGTH_LONG).show();

        HashMap<String, Object> avatar = new HashMap<>();
        avatar.put("avatar", binding.avatarLinkInput.getText().toString());

        userDocRef.update("avatar",avatar).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(CreatAvatarActivity.this, "Avatar stored", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void webView() {
        //don't cache
        binding.webView.clearCache(true);
        binding.webView.getSettings().setAppCacheEnabled(false);
        binding.webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        //don't cache

        binding.webView.getSettings().setJavaScriptEnabled(true);
        binding.webView.getSettings().setDomStorageEnabled(true);
        binding.webView.loadUrl("https://datenight.readyplayer.me/avatar"); //youtube.com ==opens youtube android app
    }

    public void webViewDialogue() {
        View view = getLayoutInflater().inflate(R.layout.readyplayer_webview, null);

        WebView readyPlayerWeb = view.findViewById(R.id.ready_player_webview);
        readyPlayerWeb.clearCache(true);
        Button closeWebView = view.findViewById(R.id.close_webview);

        readyPlayerWeb.getSettings().setJavaScriptEnabled(true);
        readyPlayerWeb.getSettings().setDomStorageEnabled(true);
        //don't cache
        readyPlayerWeb.getSettings().setAppCacheEnabled(false);
        readyPlayerWeb.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        //don't cache

        readyPlayerWeb.loadUrl("https://datenight.readyplayer.me/avatar"); //datenight.readyplayer.me/avatar

        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        closeWebView.setOnClickListener(v -> {
            alertDialog.dismiss();
        });

        alertDialog.setView(view);
        alertDialog.show();
    }

    public void webViewBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.readyplayer_webview, null);

        WebView readyPlayerWeb = view.findViewById(R.id.ready_player_webview);
        readyPlayerWeb.clearCache(true);
        Button closeWebView = view.findViewById(R.id.close_webview);

        readyPlayerWeb.getSettings().setJavaScriptEnabled(true);
        readyPlayerWeb.getSettings().setDomStorageEnabled(true);

        //don't cache
        readyPlayerWeb.getSettings().setAppCacheEnabled(false);
        readyPlayerWeb.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        //don't cache

        readyPlayerWeb.loadUrl("https://datenight.readyplayer.me/avatar");

        closeWebView.setOnClickListener(v -> bottomSheetDialog.dismiss());

        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.create();
        bottomSheetDialog.show();
    }

    public void webViewImplmentTwo() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        WebView webView = new WebView(this);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);

        webView.loadUrl("https://datenight.readyplayer.me/avatar");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Log.i("WEB", request.getUrl().toString());

                view.loadUrl(String.valueOf(request.getUrl()));
                return true;
            }
        });

        alertDialog.setView(webView);
        alertDialog.show();
    }

    public void customTabLoadAvatar() {
        String url = "https://datenight.readyplayer.me/avatar";
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent intent = builder.build();
        intent.launchUrl(this, Uri.parse(url));

        //color of addressbar
        int pink = Color.parseColor("#Fc3e7a"); //datenight pink
        int white = Color.parseColor("#Fc3e7a"); //datenight white

        CustomTabColorSchemeParams.Builder colorBuilder = new CustomTabColorSchemeParams.Builder();
        colorBuilder.setToolbarColor(pink);
        colorBuilder.setNavigationBarDividerColor(white);
        colorBuilder.build();

        //builder.setDefaultColorSchemeParams()


    }

    public void openReadyPlayerWebPage() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse("https://datenight.readyplayer.me/avatar"));
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        //webViewDialogue(); openReadyPlayerWebPage()
        binding.forgotLinkText.setOnClickListener(j -> customTabLoadAvatar());
    }
}