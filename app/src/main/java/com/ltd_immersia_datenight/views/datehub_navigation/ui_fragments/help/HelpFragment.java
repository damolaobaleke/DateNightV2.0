package com.ltd_immersia_datenight.views.datehub_navigation.ui_fragments.help;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.browser.customtabs.CustomTabColorSchemeParams;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.ltd_immersia_datenight.R;

public class HelpFragment extends Fragment {
    CardView reportIssue, faq, improvement, tweet;
    Button termsAndCondition, eula, contactUs, privacyPolicy;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_help, container, false);

        reportIssue = view.findViewById(R.id.report_issue);
        faq = view.findViewById(R.id.faq);
        improvement = view.findViewById(R.id.suggest_improvement);
        tweet = view.findViewById(R.id.tweet);

        termsAndCondition = view.findViewById(R.id.terms_condition_btn);
        eula =  view.findViewById(R.id.eula_btn);
        contactUs = view.findViewById(R.id.contact_us_btn);
        privacyPolicy =view.findViewById(R.id.privacy_policy_btn);

        reportIssue.setOnClickListener(v->{reportAnIssue();});
        improvement.setOnClickListener(v->{suggestImprovement();});
        tweet.setOnClickListener(v->{tweet();});
        termsAndCondition.setOnClickListener(v->loadTermsAndConditions());
        eula.setOnClickListener(v->eula());
        privacyPolicy.setOnClickListener(v->privacyPolicy());
        contactUs.setOnClickListener(v->contactUs());

        return view;
    }

    public void reportAnIssue(){
        Intent intent = new Intent(requireContext(), ReportIssue.class);
        startActivity(intent);
    }

    public void suggestImprovement(){
        Intent intent = new Intent(requireContext(), SuggestImprovement.class);
        startActivity(intent);
    }

    public void tweet(){
        Intent intent = new Intent();
        intent.setType(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse("https://twitter.com/ImmersiaLimited"));
        startActivity(intent);
    }

    public void loadTermsAndConditions() {
        String url = "https://www.immersia.co.uk/files/Terms%20and%20conditions%20for%20supply%20of%20services%20to%20consumers%20via%20a%20website.pdf";
        Intent intent = new Intent();
        intent.setType(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse(url));
        startActivity(intent);

    }

    public void eula(){
        Intent intent = new Intent();
        intent.setType(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse("https://immersia.co.uk/files/EULA.pdf"));
        startActivity(intent);
    }

    public void contactUs(){
        Intent emailIntent = new Intent(Intent.ACTION_SEND,Uri.parse("mailto:" + "info@immersia.co.uk"));
        emailIntent.setType("text/plain");
        startActivity(emailIntent);
    }

    public void privacyPolicy(){
        Intent intent = new Intent();
        intent.setType(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse("https://immersia.co.uk/files/PrivacyPolicy.pdf"));
        startActivity(intent);
    }
}
