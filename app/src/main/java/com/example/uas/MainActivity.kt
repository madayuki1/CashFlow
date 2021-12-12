package com.example.uas

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    //category data
    val _category_name = mutableListOf<String>()
    val _category_id = mutableListOf<String>()
    val _category_notes = mutableListOf<dcCategory>()
    private lateinit var _dropdown_category : Spinner


    val db : FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //declaration for dropdowns
        val _dropdown_transaction_type = findViewById<Spinner>(R.id.spinner_transaction)
        val _dropdown_items = resources.getStringArray(R.array.transaction)
        _dropdown_category = findViewById<Spinner>(R.id.spinner_category)



        //dropdown transaction
        if (_dropdown_transaction_type != null){
            val adapter = ArrayAdapter(this,
            android.R.layout.simple_spinner_dropdown_item, _dropdown_items)
            _dropdown_transaction_type.adapter = adapter
        }

        _dropdown_transaction_type.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>,
                                        view: View, position: Int, id: Long) {
                Toast.makeText(this@MainActivity,
                    _dropdown_items[position], Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }

            //value event listener for changing data
            val postListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    ReadDataCategory()
                    ShowDataCategory()
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Getting Post failed, log a message
                    Log.w("event_listener", "loadPost:onCancelled", databaseError.toException())
                }
            }

        }

        //dropdown category
        ReadDataCategory()
        ShowDataCategory()

        //another declaration new category
        val btn_add_category = findViewById<Button>(R.id.btn_add_category)
        btn_add_category.setOnClickListener{
            val intent = Intent(this@MainActivity, AddCategory::class.java)
            startActivity(intent)
        }

    }



    private fun ReadDataCategory(){
        db.collection("tb_category").get()
            .addOnSuccessListener { result->
                _category_id.clear()
                _category_name.clear()
                _category_notes.clear()
                for(doc in result){
                    val id = doc.data.get("category_id").toString()
                    val name = doc.data.get("category_name").toString()
                    _category_id.add(id)
                    _category_name.add(name)
                }
                CategoryToDataClass()
                //reading data from dcCategory
                //not on onCreate because firebase is asynchronous
                    val adapter = ArrayAdapter(this,
                        android.R.layout.simple_spinner_dropdown_item, _category_name)
                    _dropdown_category.adapter = adapter
                Log.d("Firebase", "Read Data - size " + _category_notes.size)
            }
            .addOnFailureListener{
                Log.d("Firebase", it.message.toString())
            }
    }

    fun CategoryToDataClass() {
        for (position in _category_id.indices) {
            val data = dcCategory(_category_id[position], _category_name[position])
            _category_notes.add(data)
        }
    }

    private fun ShowDataCategory(){

        _dropdown_category.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>,
                                        view: View, position: Int, id: Long) {
                Log.d("cat_test", _category_name[position])
                Toast.makeText(this@MainActivity,
                    _category_name[position], Toast.LENGTH_SHORT).show()

            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
                _dropdown_category.setSelection(1)
            }
        }
    }

}