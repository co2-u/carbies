package com.example.carbonfootprinttracker.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carbonfootprinttracker.ItemClickSupport;
import com.example.carbonfootprinttracker.MainActivity;
import com.example.carbonfootprinttracker.R;
import com.example.carbonfootprinttracker.adapters.UserAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FollowFragment extends Fragment {
    private static final String TAG = "FollowFragment";

    @BindView(R.id.searchView) SearchView searchView;
    @BindView(R.id.rvUsers) RecyclerView rvUsers;

    private List<ParseUser> mUsers;
    private UserAdapter userAdapter;
    private Context context;
    private FragmentManager fragmentManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_follow, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        context = getActivity().getApplicationContext();
        fragmentManager = getFragmentManager();

        rvUsers.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        mUsers = new ArrayList<>();
        userAdapter = new UserAdapter(context, mUsers);
        rvUsers.setAdapter(userAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        rvUsers.setLayoutManager(linearLayoutManager);

        queryUsersStartWith("");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                queryUsersStartWith(newText);
                return false;
            }
        });

        ItemClickSupport.addTo(rvUsers).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
//                AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
//                alertDialog.setTitle("Follow User?");
//                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NO",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                            }
//                        });
//                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                ParseUser clickedUser = mUsers.get(position);
//                                ParseUser.getCurrentUser().add("following", clickedUser.getObjectId());
//                                ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
//                                    @Override
//                                    public void done(ParseException e) {
//                                        if (e != null) {
//                                            e.printStackTrace();
//                                        } else {
//                                            Log.d(TAG, "" + ParseUser.getCurrentUser().getUsername() + " followed " + clickedUser.getUsername());
//                                        }
//                                        dialog.dismiss();
//                                    }
//                                });
//                            }
//                        });
//                alertDialog.show();
                goToProfile(mUsers.get(position));
            }
        });
    }

    private void queryUsersStartWith(String prefix) {
        ParseQuery<ParseUser> userParseQuery = ParseUser.getQuery();
        userParseQuery.whereStartsWith("username", prefix);
        userParseQuery.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
        userParseQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e != null) {
                    e.printStackTrace();
                } else {
                    mUsers.clear();
                    mUsers.addAll(objects);
                    userAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void goToProfile(ParseUser user) {
        Fragment profileFragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putParcelable("user", user);
        profileFragment.setArguments(args);
        fragmentManager.beginTransaction().replace(R.id.fragmentPlaceholder, profileFragment).addToBackStack("CommunityFragment").commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.findViewById(R.id.tvName).setVisibility(TextView.GONE);
        mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mainActivity.getSupportActionBar().setDisplayShowTitleEnabled(true);
        mainActivity.getSupportActionBar().setTitle("Follow User");
        mainActivity.setCalendarTabVisibility(false);
    }

    @Override
    public void onStop() {
        super.onStop();
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.findViewById(R.id.tvName).setVisibility(TextView.VISIBLE);
        mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        mainActivity.getSupportActionBar().setDisplayShowTitleEnabled(false);
        mainActivity.setCalendarTabVisibility(false);
    }
}
