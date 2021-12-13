package com.example.uas

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firebasedemo.adapterRV
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.ArrayList

var selected_date :String = "init"

class Statement : AppCompatActivity() {

    //data for rv

    val _date = mutableListOf<String>()
    val _category_name = mutableListOf<String>()
    val _transaction_type = mutableListOf<String>()
    val _cash = ArrayList<Int>()
    val _dcTransaction = ArrayList<dcTrasaction>()

    val _category_name_dropdown = mutableListOf<String>()
    val _category_id_dropdown = mutableListOf<String>()
    val _category_notes = mutableListOf<dcCategory>()
    private lateinit var selected_category: String

    private lateinit var _dropdown_category: Spinner

    private var db : FirebaseFirestore = FirebaseFirestore.getInstance()

    lateinit var _rvNotes : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statement)
        _rvNotes = findViewById(R.id.rv_statement)
        _dropdown_category = findViewById<Spinner>(R.id.spinner_category)

        val date_set_listener = DatePickerDialog.OnDateSetListener{view, year, monthOfYear, dayOfMonth ->
            Toast.makeText(this@Statement, "onsetdatelistener", year)
            ReadDataTransaction()
        }

        addDbListener()

        //dropdown category
        ReadDataCategory()
        ShowDataCategory()

        ReadDataTransaction()

        val date_picker = DatePickerFragment()
    }
    /*
    override fun onResume() {
        super.onResume()
        ReadDataTransaction()
    }

     */

    fun showDatePickerDialog(v: View) {
        val newFragment = DatePickerFragment()
        newFragment.show(supportFragmentManager, "datePicker")
        /*
        val date_set_listener = DatePickerDialog.OnDateSetListener{view, year, monthOfYear, dayOfMonth ->
            ReadDataTransaction()
        }

         */
    }

    private fun ReadDataTransaction() {
        db.collection("tb_transaction").get()
            .addOnSuccessListener { result ->
                _date.clear()
                _category_name.clear()
                _transaction_type.clear()
                _cash.clear()
                _dcTransaction.clear()
                for (doc in result) {
                    if(doc.data.get("category_name").toString() == selected_category &&
                        (doc.data.get("date").toString() == selected_date ||
                            selected_date == "init")) {
                        val date = doc.data.get("date").toString()
                        val category = doc.data.get("category_name").toString()
                        val transaction_type = doc.data.get("transaction_type").toString()
                        val cash = doc.data.get("cash").toString().toInt()
                        _date.add(date)
                        _category_name.add(category)
                        _transaction_type.add(transaction_type)
                        _cash.add(cash)
                    }
                }
                TransactionToDataClass()
                ShowData()
                Log.d("Firebase", "Read Data - size " + _dcTransaction.size)
            }
            .addOnFailureListener {
                Log.d("Firebase", it.message.toString())
            }
    }

    fun TransactionToDataClass() {
        for (position in _date.indices) {
            val data = dcTrasaction(_date[position], _category_name[position], _cash[position].toInt(), _transaction_type[position])
            _dcTransaction.add(data)
        }
    }

    fun ShowData() {
        _rvNotes.layoutManager = LinearLayoutManager(this)
        val notesAdapter = adapterRV(_dcTransaction)
        _rvNotes.adapter = notesAdapter
    }

    private fun ReadDataCategory() {
        db.collection("tb_category").get()
            .addOnSuccessListener { result ->
                _category_name_dropdown.clear()
                _category_notes.clear()
                for (doc in result) {
                    val id = doc.data.get("category_id").toString()
                    val name = doc.data.get("category_name").toString()
                    if (id !in _category_id_dropdown) _category_id_dropdown.add(id)
                    if (name !in _category_name_dropdown) _category_name_dropdown.add(name)
                }
                CategoryToDataClass()
                //reading data from dcCategory
                //not on onCreate because firebase is asynchronous
                if (_dropdown_category.adapter == null) {
                    Toast.makeText(this@Statement, "listening", Toast.LENGTH_SHORT).show()
                    val adapter = ArrayAdapter(
                        this,
                        android.R.layout.simple_spinner_dropdown_item, _category_name_dropdown
                    )
                    _dropdown_category.adapter = adapter
                }
                Log.d("Firebase", "Read Data - size " + _category_notes.size)
            }
            .addOnFailureListener {
                Log.d("Firebase", it.message.toString())
            }
    }

    fun CategoryToDataClass() {
        for (position in _category_id_dropdown.indices) {
            val data = dcCategory(_category_id_dropdown[position], _category_name_dropdown[position])
            _category_notes.add(data)
        }
    }

    private fun ShowDataCategory() {

        _dropdown_category.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                selected_category = parent?.getItemAtPosition(position).toString()
                ReadDataTransaction()
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

    private fun addDbListener() {
        db.collection("tb_category").addSnapshotListener { snapshot, e ->

            if (e != null) {
                return@addSnapshotListener
            }

            ReadDataCategory()
        }
    }
}

class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        return DatePickerDialog(requireActivity(),
            this, year, month, day)
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        Toast.makeText(
            requireActivity(),
            "$year $month $day", Toast.LENGTH_SHORT
        ).show()
        selected_date = "$day $month $year"
    }
}