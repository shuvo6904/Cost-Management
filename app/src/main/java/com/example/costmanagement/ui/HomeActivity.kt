package com.example.costmanagement.ui

import android.content.Intent
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import com.example.costmanagement.databinding.ActivityHomeBinding
import com.example.costmanagement.network.CostModel
import com.github.ybq.android.spinkit.style.DoubleBounce
import com.google.firebase.database.FirebaseDatabase
import java.util.Calendar

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private val calender by lazy { Calendar.getInstance() }
    private val mDatabase by lazy { FirebaseDatabase.getInstance()}
    private var name = ""
    private var cost = 0.0
    private val doubleBounce by lazy { DoubleBounce() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        initListener()
    }

    private fun initView() {
        binding.spinKit.setIndeterminateDrawable(doubleBounce)
    }

    private fun initListener() {
        binding.submit.setOnClickListener {
            name = ""
            cost = 0.0
            if(validate()) saveData()
        }

        binding.report.setOnClickListener {
            startActivity(Intent(this, CostReportActivity::class.java))
        }
    }

    private fun validate(): Boolean {
        name = binding.name.text.toString()
        cost = binding.cost.text.toString().toDoubleOrNull() ?: 0.0
        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter name", Toast.LENGTH_SHORT).show()
            binding.name.requestFocus()
            return false
        }

        if (cost.equals(0.0)) {
            Toast.makeText(this, "Please enter cost", Toast.LENGTH_SHORT).show()
            binding.cost.requestFocus()
            return false
        }
        return true
    }

    private fun saveData() {
        val mRootRef = mDatabase.getReference("${calender.get(Calendar.YEAR)}")
        val key = mRootRef.push().key
        if (key != null) {
            val data = CostModel(
                id = key,
                name = name,
                cost = cost
            )
            binding.spinKit.visibility = View.VISIBLE
            mRootRef
                .child("${calender.get(Calendar.MONTH) + 1}")
                .child("${calender.get(Calendar.DATE)}")
                .child(key)
                .setValue(data)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        binding.name.setText("")
                        binding.cost.setText("")
                        binding.spinKit.visibility = View.GONE
                        Toast.makeText(this, "Data inserted successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        binding.spinKit.visibility = View.GONE
                        Toast.makeText(this, "Data insertion failed", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    binding.spinKit.visibility = View.GONE
                    Toast.makeText(this, it.localizedMessage, Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v: View? = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }
}