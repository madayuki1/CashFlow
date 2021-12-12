package com.example.uas

import android.app.DatePickerDialog
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.DatePicker
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firebasedemo.adapterRV
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.ArrayList

class Statement : AppCompatActivity() {

    //data for rv

    val _date = mutableListOf<String>()
    val _category_name = mutableListOf<String>()
    val _transaction_type = mutableListOf<String>()
    val _cash = ArrayList<Int>()
    val _dcTransaction = ArrayList<dcTrasaction>()

    private var db : FirebaseFirestore = FirebaseFirestore.getInstance()

    lateinit var _rvNotes : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statement)
        _rvNotes = findViewById(R.id.rv_statement)
        ReadDataTransaction()

        val date_picker = DatePickerFragment()
    }

    fun showDatePickerDialog(v: View) {
        val newFragment = DatePickerFragment()
        newFragment.show(supportFragmentManager, "datePicker")
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
                    val date = doc.data.get("date").toString()
                    val category = doc.data.get("category_name").toString()
                    val transaction_type = doc.data.get("transaction_type").toString()
                    val cash = doc.data.get("cash").toString().toInt()
                    _date.add(date)
                    _category_name.add(category)
                    _transaction_type.add(transaction_type)
                    _cash.add(cash)

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
}

class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        return DatePickerDialog(requireActivity(), this, year, month, day)
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        Toast.makeText(
            requireActivity(),
            "$year $month $day", Toast.LENGTH_SHORT
        ).show()
    }
}