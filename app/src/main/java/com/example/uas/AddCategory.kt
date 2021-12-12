package com.example.uas

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


class AddCategory : AppCompatActivity() {

    lateinit var last_visible : DocumentSnapshot

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_category)

        val db:FirebaseFirestore = FirebaseFirestore.getInstance()

        //
        val et_input = findViewById<EditText>(R.id.et_new_category)
        val btn_confirm = findViewById<Button>(R.id.btn_confirm)

        // get the last id of the category id to increment the id
        val last = db.collection("tb_category")
            .orderBy("category_id", Query.Direction.DESCENDING)
            .limit(1)

        last.get()
            .addOnSuccessListener { documentSnapshots ->
                // ...

                // Get the last visible document
                last_visible = documentSnapshots.documents[documentSnapshots.size()-1]
                Log.d("after_lastVisible", last_visible.data?.get("category_name").toString())
                Toast.makeText(this@AddCategory,
                    "Please Input SumTin", Toast.LENGTH_SHORT).show()

            }
        btn_confirm.setOnClickListener{
            val data = dcCategory((last_visible.data?.get("category_id").toString().toInt() + 1).toString() , et_input.text.toString())
            if(et_input.text != null) {
                db.collection("tb_category").document()
                    .set(data)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Data Acquired", Toast.LENGTH_SHORT).show()
                        Log.d("Firebase", "Data Acquired")
                    }
                    .addOnFailureListener {
                        Log.d("Firebase", it.message.toString())
                    }
                Log.d("test_input", et_input.text.toString())
                Log.d("test_input", last_visible.data?.get("category_id").toString())
            }else{

            }
        }


    }
}