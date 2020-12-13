package app.my.firebase_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

public class MyNotes extends AppCompatActivity {
    FirebaseFirestore db;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    private FirestoreRecyclerAdapter fireadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_notes);

        db = FirebaseFirestore.getInstance();


        recyclerView = findViewById(R.id.recycle);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        FetchData();
    }

    private void FetchData() {

        Query query = db.collection("notes").whereEqualTo("userid", FirebaseAuth.getInstance().getCurrentUser().getUid());
        final FirestoreRecyclerOptions<Note> response = new FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(query, Note.class)
                .build();


        fireadapter = new FirestoreRecyclerAdapter<Note, NoteHolder>(response) {
            @Override
            public void onBindViewHolder(NoteHolder holder, int position, final Note model) {
                // progressBar.setVisibility(View.GONE);
                holder.textName.setText(model.getTitle());
                holder.date.setText(model.getDate());
                holder.desc.setText(model.getDescription());

                model.setId(response.getSnapshots().getSnapshot(position).getId());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getBaseContext(), AddNote.class);
                        i.putExtra("note", model);

                        startActivity(i);
                    }
                });

                holder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View e) {

                        db.collection("notes").document(model.getId())
                                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                Toast.makeText(getApplicationContext(), "Note deleted !",
                                        Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                });
            }

            @Override
            public NoteHolder onCreateViewHolder(ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.recycler_item, group, false);

                return new NoteHolder(view);
            }

            @Override
            public void onError(FirebaseFirestoreException e) {
                Log.e("error", e.getMessage());
            }
        };

        fireadapter.notifyDataSetChanged();
        recyclerView.setAdapter(fireadapter);
    }

    public class NoteHolder extends RecyclerView.ViewHolder {
        TextView textName;
        TextView date;
        TextView desc;
        ImageView delete;

        public NoteHolder(View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.title);
            date = itemView.findViewById(R.id.date);
            desc = itemView.findViewById(R.id.desc);
            delete = itemView.findViewById(R.id.delete);

        }
    }
    @Override
    public void onStop() {
        super.onStop();
        fireadapter.stopListening();
    }
    @Override
    public void onStart() {
        super.onStart();
       // empty.setVisibility(View.VISIBLE);
        fireadapter.startListening();


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Toast.makeText(getApplicationContext(), ""+item.getTitle().toString(), Toast.LENGTH_SHORT).show();
        if(item.getItemId()==R.id.menu_add){
            Intent i = new Intent(this,AddNote.class);
            startActivity(i);

        }
        return super.onOptionsItemSelected(item);
    }


}