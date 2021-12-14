package com.example.uas

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle


class AddCategory : AppCompatActivity() {

    lateinit var last_visible: DocumentSnapshot

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_category)

        val db: FirebaseFirestore = FirebaseFirestore.getInstance()

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
                last_visible = documentSnapshots.documents[documentSnapshots.size() - 1]
                Log.d("after_lastVisible", last_visible.data?.get("category_name").toString())

            }
        btn_confirm.setOnClickListener {
            if (et_input.text.toString() != "") {
                Log.d("et_input value", et_input.text.toString())
                val data = dcCategory(
                    (last_visible.data?.get("category_id").toString().toInt() + 1).toString(),
                    et_input.text.toString())
                db.collection("tb_category").document()
                    .set(data)
                    .addOnSuccessListener {
                        Log.d("Firebase", "Category Acquired")
                        MotionToast.createColorToast(
                            this@AddCategory,
                            "Submission Success",
                            "Data Saved to Database",
                            MotionToastStyle.SUCCESS,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.SHORT_DURATION,
                            ResourcesCompat.getFont(this,R.font.helvetica_regular))
                    }
                    .addOnFailureListener {
                        Log.d("Firebase", it.message.toString())
                    }
                Log.d("test_input", et_input.text.toString())
                Log.d("test_input", last_visible.data?.get("category_id").toString())

                finish()
            } else {
                MotionToast.createColorToast(
                    this@AddCategory,
                    "No Input Detected",
                    "Please Input Something",
                    MotionToastStyle.WARNING,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.SHORT_DURATION,
                    ResourcesCompat.getFont(this,R.font.helvetica_regular))
            }
        }


    }
}