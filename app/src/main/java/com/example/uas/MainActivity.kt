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
import android.text.TextUtils
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    //category data
    val _category_name = mutableListOf<String>()
    val _category_id = mutableListOf<String>()
    val _category_notes = mutableListOf<dcCategory>()
    private lateinit var _dropdown_category : Spinner
    private lateinit var et_input_money : EditText
    private lateinit var selected_category : String
    private lateinit var selected_transaction_type : String


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
                selected_transaction_type = parent?.getItemAtPosition(position).toString()
                /*
                Toast.makeText(this@MainActivity,
                    _dropdown_items[position], Toast.LENGTH_SHORT).show()
                 */
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }


        }

        addDbListener()

        //dropdown category
        ReadDataCategory()
        ShowDataCategory()

        //another declaration new category
        val btn_add_category = findViewById<Button>(R.id.btn_add_category)
        btn_add_category.setOnClickListener{
            val intent = Intent(this@MainActivity, AddCategory::class.java)
            startActivity(intent)
        }

        val btn_confirm = findViewById<Button>(R.id.btn_confirm)
        et_input_money = findViewById<EditText>(R.id.et_input_money)
        btn_confirm.setOnClickListener{
            if(et_input_money.text != null){
                val digitsOnly = TextUtils.isDigitsOnly(et_input_money.getText())
                if(digitsOnly == true){
                    AddTransaction2Firebase()
                }
            }
        }

    }


    private fun AddTransaction2Firebase(){
        val ISO_8601_FORMAT = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sss'Z'")
        val now: String = ISO_8601_FORMAT.format(Date())
        val data = dcTrasaction(now, selected_category, et_input_money.text.toString().toInt(),selected_transaction_type)
        db.collection("tb_transaction").document()
            .set(data)
            .addOnSuccessListener {
                Toast.makeText(this, "Transaction Acquired", Toast.LENGTH_SHORT).show()
                Log.d("Firebase", "Transaction Acquired")
            }
            .addOnFailureListener {
                Log.d("Transaction", it.message.toString())
            }
        Log.d("Transaction", et_input_money.text.toString())
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
                    if (id !in _category_id) _category_id.add(id)
                    if (name !in _category_name) _category_name.add(name)
                }
                CategoryToDataClass()
                //reading data from dcCategory
                //not on onCreate because firebase is asynchronous
                if (_dropdown_category.adapter == null) {
                    Toast.makeText(this@MainActivity, "listening", Toast.LENGTH_SHORT).show()
                    val adapter = ArrayAdapter(this,
                        android.R.layout.simple_spinner_dropdown_item, _category_name)
                    _dropdown_category.adapter = adapter
                }
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
                selected_category = parent?.getItemAtPosition(position).toString()
                /*
                Log.d("cat_test", _category_name[position])
                Toast.makeText(this@MainActivity,
                    _category_name[position], Toast.LENGTH_SHORT).show()

                 */

            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
                _dropdown_category.setSelection(1)
            }
        }
    }

    private fun addDbListener(){
        db.collection("tb_category").addSnapshotListener{
            snapshot, e ->

            if (e != null) {
                return@addSnapshotListener
            }

            ReadDataCategory()
        }
    }



}