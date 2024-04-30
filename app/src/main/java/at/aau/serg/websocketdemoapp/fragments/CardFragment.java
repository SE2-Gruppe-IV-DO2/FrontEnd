package at.aau.serg.websocketdemoapp.fragments;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.util.Objects;

import at.aau.serg.websocketdemoapp.R;

public class CardFragment extends Fragment {
    private String cardName;
    int cardWidth;
    int leftMargin;
    float rotation;

    boolean clickable;


    public static CardFragment newInstance(String cardName, int cardWidth, int leftMargin, float rotation) {
        CardFragment fragment = new CardFragment();
        Bundle args = new Bundle();
        args.putString("cardName", cardName);
        args.putInt("cardWidth", cardWidth);
        args.putInt("leftMargin", leftMargin);
        args.putFloat("rotation", rotation);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            cardName = getArguments().getString("cardName");
            cardWidth = getArguments().getInt("cardWidth");
            leftMargin = getArguments().getInt("leftMargin");
            rotation = getArguments().getFloat("rotation");
        }
    }

    @SuppressLint("DiscouragedApi")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_card, container, false);
        ImageView iv = (ImageView) view.findViewById(R.id.cardImageView);

        Bundle bundle = getArguments();
        if (bundle != null){
            iv.setImageResource(getResources().getIdentifier(cardName, "drawable", requireContext().getPackageName()));
        }
        // Initialize UI elements and set click listener
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle card click
                Toast.makeText(getContext(), cardName, Toast.LENGTH_SHORT).show();
            }
        });

        FrameLayout.LayoutParams params =
                new FrameLayout.LayoutParams(cardWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(params);
        view.setRotation(rotation);
        view.setTranslationX(leftMargin);
        view.setTranslationY(300f);

        String color = "#00000000";
        if (Math.random() > 0.8f) {
            color = "#70FF0000";
        }
        View overlay = view.findViewById(R.id.redOverlay);
        params.setMargins(10,10,10,10);
        overlay.setLayoutParams(params);
        overlay.setBackgroundColor(Color.parseColor(color));

        return view;
    }
}
