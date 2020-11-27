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

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import com.datenight_immersia_ltd.R;
import com.datenight_immersia_ltd.databinding.ActivityCreatAvatarBinding;

public class CreatAvatarActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityCreatAvatarBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreatAvatarBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        webViewDialogue();
        //openReadyPlayerWebPage();
        binding.submitAvatarBtn.setOnClickListener(v -> storeAvatarLink());

        binding.forgotLinkText.setOnClickListener(this);


    }

    public void storeAvatarLink() {
        Log.i("Avatar Link", binding.avatarLinkInput.getText().toString());
        Toast.makeText(this, binding.avatarLinkInput.getText().toString(), Toast.LENGTH_LONG).show();
    }

    public void webViewDialogue() {
        View view = getLayoutInflater().inflate(R.layout.readyplayer_webview, null);

        WebView readyPlayerWeb = view.findViewById(R.id.ready_player_webview);
        Button closeWebView = view.findViewById(R.id.close_webview);

        readyPlayerWeb.getSettings().setJavaScriptEnabled(true);
        readyPlayerWeb.getSettings().setDomStorageEnabled(true);
        readyPlayerWeb.loadUrl("https://datenight.readyplayer.me/avatar");

        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        closeWebView.setOnClickListener(v -> {
            alertDialog.dismiss();
        });

        alertDialog.setView(view);
        alertDialog.show();
    }

    public void openReadyPlayerWebPage(){
        Intent intent  = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse("https://datenight.readyplayer.me/avatar"));
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        //webViewDialogue(); openReadyPlayerWebPage()
        binding.forgotLinkText.setOnClickListener(j -> openReadyPlayerWebPage());
    }
}