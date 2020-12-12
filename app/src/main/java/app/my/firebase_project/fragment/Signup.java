package app.my.firebase_project.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import androidx.navigation.Navigation;
import app.my.firebase_project.MyNotes;
import app.my.firebase_project.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Signup#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Signup extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Signup() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Signup.
     */
    // TODO: Rename and change types and number of parameters
    public static Signup newInstance(String param1, String param2) {
        Signup fragment = new Signup();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    EditText passwor,email;
    TextView signin_txt;

    Button singup_btn;
    ProgressBar progress;
    Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_signup, container, false);

        context = getContext();
        signin_txt = v.findViewById(R.id.signin);
        progress = v.findViewById(R.id.progress);
        singup_btn = v.findViewById(R.id.login);
        passwor = v.findViewById(R.id.password);
        email = v.findViewById(R.id.email);
        progress.setVisibility(View.GONE);

        singup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progress.setVisibility(View.VISIBLE);

                if(passwor.getText().length()<6){
                    Toast.makeText(context, "min length for password is: 6", Toast.LENGTH_SHORT).show();
                }else{
                    CreateUser(email.getText().toString(),passwor.getText().toString());

                }
            }
                    });

        signin_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).popBackStack();

            }
        });
        return v;
    }

    public void CreateUser(String email,String password) {


        FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Toast.makeText(context, "Succesful", Toast.LENGTH_SHORT).show();
                progress.setVisibility(View.INVISIBLE);

                Intent i = new Intent(context, MyNotes.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                startActivity(i);

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progress.setVisibility(View.INVISIBLE);

                        Toast.makeText(context, "fail"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}