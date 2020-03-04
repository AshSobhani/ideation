package com.example.ideation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ViewProjectActivity extends AppCompatActivity {
	private static final String TAG = "ViewProjectActivity";

	//Make variables
	String projectUID;
	private TextView titleField, projectOwnerField, categoryField, descriptionField;
	private FirebaseUser firebaseUser;
	private FirebaseAuth firebaseAuth;

	//Get Fire Store instance and store in db variable
	private FirebaseFirestore db = FirebaseFirestore.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_project);

		//Assign instance and user to variables
		firebaseAuth = FirebaseAuth.getInstance();
		firebaseUser = firebaseAuth.getCurrentUser();

		//Retrieve project UID
		Bundle bundle = getIntent().getExtras();
		projectUID = bundle.getString("projectUID");

		//Assign views to variables
		titleField = findViewById(R.id.projectTitleText);
		projectOwnerField = findViewById(R.id.projectOwnerText);
		categoryField = findViewById(R.id.categoryText);
		descriptionField = findViewById(R.id.descriptionText);

		//Retrieve and assign user data to text fields
		retrieveProjectData();
	}

	private void retrieveProjectData() {
		db.collection(IdeationContract.COLLECTION_PROJECTS).document(projectUID).get()
				.addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
					@Override
					public void onSuccess(DocumentSnapshot documentSnapshot) {
						if (documentSnapshot.exists()) {
							//Set the text fields
							titleField.setText(documentSnapshot.getString(IdeationContract.PROJECT_TITLE));
							categoryField.setText(documentSnapshot.getString(IdeationContract.PROJECT_CATEGORY));
							descriptionField.setText(documentSnapshot.getString(IdeationContract.PROJECT_DESCRIPTION));

							//Get the ownerUID
							String ownerUID = documentSnapshot.getString(IdeationContract.PROJECT_OWNERUID);

							//Use the ownerUID to retrieve their name
							db.collection(IdeationContract.COLLECTION_USERS).document(ownerUID).get()
									.addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
										@Override
										public void onSuccess(DocumentSnapshot documentSnapshot) {
											if (documentSnapshot.exists()) {
												projectOwnerField.setText(documentSnapshot.getString(IdeationContract.USER_USERNAME));
											}
										}
									});
						} else {
							Toast.makeText(ViewProjectActivity.this, "Document does not exist", Toast.LENGTH_SHORT).show();
						}
					}
				})
				.addOnFailureListener(new OnFailureListener() {
					@Override
					public void onFailure(@NonNull Exception e) {
						Toast.makeText(ViewProjectActivity.this, "Error!", Toast.LENGTH_SHORT).show();
						Log.d(TAG, e.toString());
					}
				});
	}
}
