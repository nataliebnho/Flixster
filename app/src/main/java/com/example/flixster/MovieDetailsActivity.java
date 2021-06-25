package com.example.flixster;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.flixster.databinding.ActivityMovieDetailsBinding;
import com.example.flixster.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import okhttp3.Headers;

import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;


public class MovieDetailsActivity extends AppCompatActivity {

    Movie movie;

    TextView tvTitle2;
    TextView tvOverview2;
    RatingBar rbVoteAverage;

    ImageView ivBackdrop;
    ImageView playButton;

    Context context;

    public static final String TAG = "MovieDetailsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_movie_details);
        ActivityMovieDetailsBinding binding = ActivityMovieDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        tvTitle2 = binding.tvTitle2;
        tvOverview2 = binding.tvOverview2;
        rbVoteAverage = binding.rbVoteAverage;
        ivBackdrop = binding.ivBackdrop;
        playButton = binding.playButton;

        //unwrap the movie passed in via intent, using its simple name as a key
        movie = (Movie) Parcels.unwrap(getIntent().getParcelableExtra(Movie.class.getSimpleName()));
        Log.d("MovieDetailsActivity", String.format("Showing details for %s", movie.getTitle()));

        String imageUrl;
        imageUrl = movie.getBackdropPath();

        int radius = 30; // corner radius, higher value = more rounded
        int margin = 10; // crop margin, set to 0 for corners with no crop
        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.flicks_movie_placeholder)
                //.transform(new RoundedCornersTransformation(radius, margin))
                .into(ivBackdrop);

        tvTitle2.setText(movie.getTitle());
        tvOverview2.setText(movie.getOverview());

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AsyncHttpClient client  = new AsyncHttpClient();
                client.get(createVideoURL(movie.getId()), new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int i, Headers headers, JSON json) {
                        JSONObject jsonObject = json.jsonObject;
                        try {
                            JSONArray results = jsonObject.getJSONArray("results");
                            String finalID = results.getJSONObject(0).getString("key");
                            Log.i(TAG, "Movies video: " + finalID);

                            Intent ytVid = new Intent(MovieDetailsActivity.this, MovieTrailerActivity.class);
                            ytVid.putExtra("id", finalID);
                            startActivity(ytVid);

                        } catch (JSONException e) {
                            Log.e(TAG, "Hit json exception");
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int i, Headers headers, String s, Throwable throwable) {
                        Log.d(TAG, "onFailure");
                    }
                });

            }
        });

        float voteAverage = movie.getVoteAverage().floatValue();
        rbVoteAverage.setRating(voteAverage / 2.0f);

    }

    public String createVideoURL(int id) {
        return String.format("https://api.themoviedb.org/3/movie/%d/videos?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed", id);
    }

}
//
//@GlideModule
//final class MyAppGlideModule extends AppGlideModule {
//
//        }