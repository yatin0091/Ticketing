package io.webguru.ticketing.Contractor;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.webguru.ticketing.Agent.AgentTicketHolder;
import io.webguru.ticketing.Agent.AgentTicketView;
import io.webguru.ticketing.Global.GlobalFunctions;
import io.webguru.ticketing.Global.RecyclerItemClickListener;
import io.webguru.ticketing.Global.SignOut;
import io.webguru.ticketing.Global.UserProfile;
import io.webguru.ticketing.POJO.Ticket;
import io.webguru.ticketing.POJO.UserInfo;
import io.webguru.ticketing.R;
import io.webguru.ticketing.Requester.RequesterMainActivity;
import io.webguru.ticketing.Requester.RequesterTicketHolder;

public class ContractorMainActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.requested_ticket_list)
    RecyclerView mRecyclerView;
    @Bind(R.id.progressBar)
    ProgressBar mProgressBar;

    private UserInfo userInfo;
    private Ticket[] ticketsArray = new Ticket[100];
    //RecyclerView objects
    private LinearLayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;
    //Firebase Database refernce
    private DatabaseReference mDatabase;

    private String TAG = "ContractorMAINACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contractor_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            userInfo = (UserInfo) bundle.get("UserInfo");
        }
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(false);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        // Reversing the order of view Latest at top
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("ticketing");
        Query query = mDatabase.orderByChild("isVisibleContractor").equalTo("Yes");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot == null || dataSnapshot.getValue() == null) {
                    mProgressBar.setVisibility(View.GONE);
                    GlobalFunctions.showToast(ContractorMainActivity.this, "No tickets available for this status", Toast.LENGTH_LONG);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mAdapter = new FirebaseRecyclerAdapter<Ticket, AgentTicketHolder>(Ticket.class, R.layout.agent_ticket_cardview, AgentTicketHolder.class, query) {
            @Override
            protected void populateViewHolder(AgentTicketHolder viewHolder, Ticket ticket, int position) {
                mProgressBar.setVisibility(View.GONE);
                    ticketsArray[position] = ticket;
                    viewHolder.setViewElements(ticket, position, true);
            }

        };
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(ContractorMainActivity.this, AgentTicketView.class);
                intent.putExtra("UserInfo", userInfo);
                intent.putExtra("Ticket", ticketsArray[position]);
                startActivity(intent);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_contractor_requester, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_signout) {
            Intent intent = new Intent(ContractorMainActivity.this, SignOut.class);
            intent.putExtra("UserInfo", userInfo);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
