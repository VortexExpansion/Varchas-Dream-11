package com.example.varchasdream11.Matches;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Calendar;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.varchasdream11.R;
import com.example.varchasdream11.TeamSelection.TeamSelectionActivity;
import com.example.varchasdream11.databinding.ActivitySportsBinding;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;


public class Sports extends AppCompatActivity {

    private RecyclerView SportsRecyclerList;
    private DatabaseReference MatchRef;
    private String sports_name;
    ActivitySportsBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySportsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sports_name = getIntent().getExtras().get("sports_name").toString();

        MatchRef = FirebaseDatabase.getInstance().getReference().child("Games").child(sports_name);

        SportsRecyclerList = (RecyclerView) findViewById(R.id.sportsRecyclerList);
        SportsRecyclerList.setLayoutManager(new LinearLayoutManager(this));

//        Date today = new Date();
//        final long currentTime = today.getTime();
//        Toast.makeText(this, currentTime+"", Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Match> options = new FirebaseRecyclerOptions.Builder<Match>()
                .setQuery(MatchRef, Match.class)
                .build();
        

        FirebaseRecyclerAdapter<Match, MatchViewHolder> adapter = new FirebaseRecyclerAdapter<Match, MatchViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final MatchViewHolder holder, final int position, @NonNull Match model) {
                holder.team1Text.setText(model.getTeam1Name());
                holder.team2Text.setText(model.getTeam2Name());
                //holder.matchTime.setText(model.getMatchTime());

                Picasso.get().load(model.getTeam1Image()).placeholder(R.drawable.cricket_logo_remastered).into(holder.team1Image);
                Picasso.get().load(model.getTeam2Image()).placeholder(R.drawable.cricket_logo_remastered).into(holder.team2Image);

                //String input = "20:00 07:03";
                String input = model.getMatchTime();
                if(input != null) {


                    int month_ref, date_ref, hrs_ref, min_ref;

                    hrs_ref = Integer.parseInt(input.substring(0, 2));
                    min_ref = Integer.parseInt(input.substring(3, 5));
                    date_ref = Integer.parseInt(input.substring(6, 8));
                    month_ref = Integer.parseInt(input.substring(9));
                    Calendar c1 = Calendar.getInstance();

                    // Set Month
                    // MONTH starts with 0 i.e. ( 0 - Jan)
                    c1.set(Calendar.MONTH, month_ref - 1);

                    // Set Date
                    c1.set(Calendar.DATE, date_ref);

                    // Set Year
                    c1.set(Calendar.YEAR, 2021);
                    c1.set(Calendar.HOUR_OF_DAY, hrs_ref);
                    c1.set(Calendar.MINUTE, min_ref);


                    Date dateOne = c1.getTime();
                    //Toast.makeText(Sports.this, dateOne+"", Toast.LENGTH_SHORT).show();
                    long timer = dateOne.getTime();
                    Date today = new Date();
                    final long currentTime = today.getTime();
                    long expiryTime = timer - currentTime;

                    new CountDownTimer(expiryTime, 500) {
                        public void onTick(long millisUntilFinished) {
                            long seconds = millisUntilFinished / 1000;
                            long minutes = seconds / 60;
                            long hours = minutes / 60;
                            long days = hours / 24;

                            // reference time = time = days + " " + "d" + " : " + hours % 24 + " h" + " : " + minutes % 60 + " m" + " : " + seconds % 60 + " s";

                            // Days format
                            String time = "";
                            time = days + " days to go";
                            if (days==1){
                                time = days + " day to go";
                            }
                            else if(days==0 && hours==0 && minutes==0){
                                time = seconds % 60 + " s";
                            }
                            else if (days==0 && hours==0 ){
                                time = minutes % 60 + " m" + " : " + seconds % 60 + " s";
                            }
                            else if (days==0 ){
                                time = hours + "h" + " : " + minutes % 60 + " m" + " : " + seconds % 60 + " s";
                            }

                            //Hours format
                            //String time = hours + "h" + " : " + minutes % 60 + " m" + " : " + seconds % 60 + " s";
                            holder.matchTime.setText(time);
                        }

                        public void onFinish() {
                            holder.matchTime.setText("Time up!");
                        }
                    }.start();
                }
                else{
                    holder.matchTime.setText(model.getMatchTime());
                }
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String match_id = getRef(position).getKey();

                        Intent matchIntent = new Intent(Sports.this, TeamSelectionActivity.class);
                        matchIntent.putExtra("match_id", match_id);
                        startActivity(matchIntent);


                    }
                });
            }

            @NonNull
            @Override
            public MatchViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.matches_sample, viewGroup, false);
                MatchViewHolder viewHolder = new MatchViewHolder(view);
                return viewHolder;
            }
        };

        SportsRecyclerList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class MatchViewHolder extends RecyclerView.ViewHolder{

        TextView team1Text, team2Text, matchTime;
        ImageView team1Image, team2Image;

        public MatchViewHolder(@NonNull View itemView) {
            super(itemView);

            matchTime = itemView.findViewById(R.id.matchTime);
            team1Text = itemView.findViewById(R.id.team1Text);
            team2Text = itemView.findViewById(R.id.team2Text);
            team1Image = itemView.findViewById(R.id.team1Image);
            team2Image = itemView.findViewById(R.id.team2Image);
        }
    }
}