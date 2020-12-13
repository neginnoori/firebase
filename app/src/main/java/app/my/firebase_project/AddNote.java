package app.my.firebase_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.chrono.HijrahDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddNote extends AppCompatActivity {

    FloatingActionButton save_note;
    EditText title,text;
    Note note;
    ProgressBar progress;
    FirebaseFirestore db;
    Calendar calendar;
    SimpleDateFormat format;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        save_note = findViewById(R.id.save);



        db = FirebaseFirestore.getInstance();
        calendar = Calendar.getInstance();
        format = new SimpleDateFormat("dd/MMM/yy - hh:mm");
        String date = format.format(calendar.getTime());

        progress = findViewById(R.id.progress);
        title = findViewById(R.id.title);
        title.setText(date);
        text = findViewById(R.id.text);
        progress.setVisibility(View.INVISIBLE);
        if(getIntent().hasExtra("note")){
            note = new Note();
            Bundle data = getIntent().getExtras();
            note = (Note) data.getParcelable("note");
            title.setText(note.getTitle());
            text.setText(note.getDescription());

        }

        save_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View e) {

                String title_text = title.getText().toString().trim();
                String text_edit = text.getText().toString().trim();
                if (title_text.matches("") || text_edit.matches("")) {
                    Toast.makeText(getApplicationContext(), "It's empty", Toast.LENGTH_SHORT).show();
                } else {
                    if (getIntent().hasExtra("note")==false) {
                        progress.setVisibility(View.VISIBLE);
                        CreateNote();
                    } else {
                        if (note.getTitle().equals(title_text) && note.getDescription().equals(text_edit)) {

                            finish();
                        } else {
                            progress.setVisibility(View.VISIBLE);

                            UpdateNote();
                        }
                    }
                }

            }
        });
}
    private  void UpdateNote(){
        String date = format.format(calendar.getTime());
        DocumentReference doc = db.collection("notes").document(note.getId());
        note.setTitle(title.getText().toString());
        note.setDescription(text.getText().toString());
        doc.update("title",note.getTitle());
        doc.update("description",note.getDescription());
        doc.update("date",date).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progress.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), " done", Toast.LENGTH_LONG).show();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progress.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), "error: " + e.getMessage(), Toast.LENGTH_LONG).show();

            }
        });

    }
    private void CreateNote(){
        String date = format.format(calendar.getTime());

        Map<String, Object> newContact = new HashMap<>();
        newContact.put("title", title.getText().toString());
        newContact.put("description", text.getText().toString());
        newContact.put("userid", FirebaseAuth.getInstance().getCurrentUser().getUid());
        newContact.put("date", date);
        db.collection("notes").add(newContact).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                Toast.makeText(getApplicationContext(), "Note saved",
                        Toast.LENGTH_SHORT).show();
                progress.setVisibility(View.INVISIBLE);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "ERROR" +e.toString(),
                        Toast.LENGTH_SHORT).show();
                progress.setVisibility(View.INVISIBLE);
            }
        });
    }
}