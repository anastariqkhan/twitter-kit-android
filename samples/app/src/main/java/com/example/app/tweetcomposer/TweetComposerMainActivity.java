/*
 * Copyright (C) 2015 Twitter, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.example.app.tweetcomposer;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.widget.Button;

import com.example.app.BaseActivity;
import com.example.app.R;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetcomposer.ComposerActivity;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;
import com.twitter.sdk.android.tweetcomposer.TweetUploadService;

import java.net.MalformedURLException;
import java.net.URL;

public class TweetComposerMainActivity extends BaseActivity {
    private static final String TAG = "TweetComposer";
    private static final String IMAGE_TYPES = "image/*";
    private static final int IMAGE_PICKER_CODE = 141;
    private TwitterLoginButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tweetcomposer_activity_main);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.kit_tweetcomposer);
        }


        loginButton = findViewById(R.id.login_button);
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // Do something with result, which provides a TwitterSession for making API calls
                Log.e(TAG, "success: ");
                launchComposer();
            }

            @Override
            public void failure(TwitterException exception) {
                // Do something on failure
                Log.e(TAG, "failure: ");
            }
        });

        TweetUploadService.setTweetCallback(new TweetUploadService.TweetCallback() {
            @Override
            public void onSuccess(Tweet tweet) {
                Log.e(TAG, "onSuccess: " + tweet.text);
            }

            @Override
            public void onFailure(Intent failureIntent) {
                Log.e(TAG, "onFailure: ");

            }

            @Override
            public void onCancel(Intent cancelIntent) {
                Log.e(TAG, "onCancel: ");
            }
        });

        final Button tweetComposer = findViewById(R.id.tweet_composer);
        tweetComposer.setOnClickListener(view -> {
            try {


                new TweetComposer.Builder(TweetComposerMainActivity.this)
                        .text("Tweet from TwitterKit!")
                        .url(new URL("http://www.twitter.com")).show();

            } catch (MalformedURLException e) {
                Log.e(TAG, "error creating tweet intent", e);
            }
        });

        final Button organicComposer = findViewById(R.id.organic_composer);
        organicComposer.setOnClickListener(view ->
                {
                    TwitterAuthClient authClient = new TwitterAuthClient();
                    authClient.authorize(this, new Callback<TwitterSession>() {
                        @Override
                        public void success(Result<TwitterSession> result) {
                            launchComposer();
                        }

                        @Override
                        public void failure(TwitterException exception) {

                        }
                    });
                }

        );
    }

    void launchPicker() {
        final Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(IMAGE_TYPES);
        startActivityForResult(Intent.createChooser(intent, "Pick an Image"), IMAGE_PICKER_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_PICKER_CODE && resultCode == Activity.RESULT_OK) {
            launchComposer(data.getData());
        }

        // Pass the activity result to the login button.
        loginButton.onActivityResult(requestCode, resultCode, data);
    }

    void launchComposer(Uri uri) {
        final TwitterSession session = TwitterCore.getInstance().getSessionManager()
                .getActiveSession();
        final Intent intent = new ComposerActivity.Builder(TweetComposerMainActivity.this)
                .session(session)
                .image(uri)
                .text("Tweet from TwitterKit!")
                .hashtags("#twitter")
                .createIntent();
        startActivity(intent);
    }

    void launchComposer() {
        final TwitterSession session = TwitterCore.getInstance().getSessionManager()
                .getActiveSession();
        final Intent intent = new ComposerActivity.Builder(TweetComposerMainActivity.this)
                .session(session)
                .text("Tweet from TwitterKit!")
                .hashtags("#twitter")
                .createIntent();
        startActivity(intent);
    }
}
