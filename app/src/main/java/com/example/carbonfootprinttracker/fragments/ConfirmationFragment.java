package com.example.carbonfootprinttracker.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.carbonfootprinttracker.R;
import com.example.carbonfootprinttracker.models.Carbie;
import com.parse.ParseException;
import com.parse.SaveCallback;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ConfirmationFragment extends Fragment {
    @BindView(R.id.tvStartPoint2)
    TextView tvStartPoint2;
    @BindView(R.id.tvEndPoint2)
    TextView tvEndPoint2;
    @BindView(R.id.tvMode2)
    TextView tvMode2;
    @BindView(R.id.etCarbieName)
    EditText etCarbieName;
    @BindView(R.id.btnConfirmNo)
    Button btnConfirmNo;
    @BindView(R.id.btnConfirmYes)
    Button btnConfirmYes;
    @BindView(R.id.btnYesAndGo)
    Button btnYesAndGo;
    @BindView(R.id.btnDetailTest)
    Button btnDetailTest;
    private final String TAG = "ConfirmationFragment";
    private Carbie carbie;
    private FragmentManager fragmentManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_confirmation_screen, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        fragmentManager = getFragmentManager();
        carbie = getArguments().getParcelable("carbie");
        carbie.setScore();
        carbie.setUser();
        tvStartPoint2.setText(carbie.getStartLocation());
        tvEndPoint2.setText(carbie.getEndLocation());
        tvMode2.setText(carbie.getTransportation());
        btnConfirmNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMainFragment();
            }
        });
        btnConfirmYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etCarbieName.getText().toString().equals("")) {
                    Toast.makeText(getContext(), "Please enter a title!", Toast.LENGTH_LONG).show();
                } else {
                    carbie.setTitle(etCarbieName.getText().toString());
                    carbie.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null) {
                                Log.d(TAG, "Error while saving");
                                e.printStackTrace();
                                return;
                            }
                            Log.d(TAG, "Success!");
                        }
                    });
                    goToMainFragment();
                }
            }
        });

        btnDetailTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                carbie.setTitle(etCarbieName.getText().toString());
                carbie.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.d(TAG, "Error while saving");
                            e.printStackTrace();
                            return;
                        }
                        Log.d(TAG, "Success!");
                    }
                });
                Fragment detialsFragment = new DetailsFragment();
                Bundle args = new Bundle();
                args.putParcelable("carbie", carbie);
                detialsFragment.setArguments(args);

                fragmentManager.beginTransaction().replace(R.id.fragmentPlaceholder, detialsFragment).commit();
            }
        });


//        btnYesAndGo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                carbie.setTitle(etCarbieName.getText().toString());
//                carbie.saveInBackground(new SaveCallback() {
//                    @Override
//                    public void done(ParseException e) {
//                        if (e != null) {
//                            Log.d(TAG, "Error while saving");
//                            e.printStackTrace();
//                            return;
//                        }
//                        Log.d(TAG, "Success!");
//                    }
//                });
//                //TODO wire to google maps
//                // Map point based on address
//                Uri gmmIntentUri = Uri.parse("google.navigation:q=Taronga+Zoo,+Sydney+Australia");
//                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
//                mapIntent.setPackage("com.google.android.apps.maps");
//                startActivity(mapIntent);
//                goToMainFragment();
//            }
//        });
    }

    private void goToMainFragment() {
        getActivity().findViewById(R.id.currentScoreTab).performClick();
    }
}
