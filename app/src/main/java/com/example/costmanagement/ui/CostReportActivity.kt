package com.example.costmanagement.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.costmanagement.R
import com.example.costmanagement.databinding.ActivityCostReportBinding
import com.example.costmanagement.network.CostModel
import com.example.costmanagement.ui.adapter.ReportAdapter
import com.github.ybq.android.spinkit.style.DoubleBounce
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Calendar

class CostReportActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCostReportBinding
    private val doubleBounce by lazy { DoubleBounce() }
    private val reportAdapter: ReportAdapter by lazy { ReportAdapter() }
    private val calender by lazy { Calendar.getInstance() }
    private val mDatabase by lazy { FirebaseDatabase.getInstance()}
    private var data: ArrayList<CostModel> = arrayListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCostReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        fetchData()
        initListener()
    }

    private fun fetchData() {
        binding.spinKit.visibility = View.VISIBLE
        val mRootRef = mDatabase.getReference("${calender.get(Calendar.YEAR)}").child("${calender.get(Calendar.MONTH) + 1}")
        mRootRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot1: DataSnapshot) {
                binding.spinKit.visibility = View.VISIBLE
                data.clear()
                for (dataSnapshot2 in dataSnapshot1.children) {
                    for (dataSnapshot3 in dataSnapshot2.children) {
                        val report = dataSnapshot3.getValue(CostModel::class.java)
                        if (report != null) {
                            data.add(report)
                            Log.d("cost_data", data.toString())
                        }
                    }
                }
                reportAdapter.submitList(data)
                binding.spinKit.visibility = View.GONE
            }

            override fun onCancelled(databaseError: DatabaseError) {
                binding.spinKit.visibility = View.GONE
                Toast.makeText(this@CostReportActivity, databaseError.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun initListener() {

    }

    private fun initView() {
        binding.spinKit.setIndeterminateDrawable(doubleBounce)
        binding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@CostReportActivity)
            adapter = reportAdapter
        }

    }
}