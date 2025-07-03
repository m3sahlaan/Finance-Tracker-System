package com.example.personalfinancetrackerapp

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.personalfinancetrackerapp.model.Transaction
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CategoryChartActivity : AppCompatActivity() {

    private lateinit var barChart: BarChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_chart)

        barChart = findViewById(R.id.barChart)

        val transactions = loadTransactions()
        val categoryTotals = calculateTotals(transactions)

        setupBarChart(categoryTotals)
    }

    private fun loadTransactions(): List<Transaction> {
        val prefs = getSharedPreferences("FinancePrefs", Context.MODE_PRIVATE)
        val json = prefs.getString("transactions", null)
        val type = object : TypeToken<List<Transaction>>() {}.type
        return if (json != null) Gson().fromJson(json, type) else emptyList()
    }

    private fun calculateTotals(transactions: List<Transaction>): Map<String, Float> {
        val categoryMap = mutableMapOf<String, Float>()

        for (t in transactions) {
            val current = categoryMap[t.category] ?: 0f
            categoryMap[t.category] = current + t.amount.toFloat()
        }

        return categoryMap
    }

    private fun setupBarChart(categoryTotals: Map<String, Float>) {
        val entries = ArrayList<BarEntry>()
        val labels = ArrayList<String>()
        
        // Convert map entries to BarEntries and collect labels
        categoryTotals.entries.forEachIndexed { index, entry ->
            entries.add(BarEntry(index.toFloat(), entry.value))
            labels.add(entry.key)
        }

        // Create dataset
        val dataSet = BarDataSet(entries, "Category-wise Spending")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
        dataSet.valueTextColor = Color.BLACK
        dataSet.valueTextSize = 14f

        // Create bar data
        val data = BarData(dataSet)
        data.barWidth = 0.9f // Width of bars

        // Configure the chart
        barChart.data = data
        barChart.description.isEnabled = false
        barChart.setFitBars(true)
        
        // Configure X axis to show category names
        val xAxis = barChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.setCenterAxisLabels(false)
        xAxis.labelRotationAngle = 45f // Rotate labels for better readability
        
        // Limit the number of visible labels to avoid crowding
        if (labels.size > 10) {
            xAxis.labelCount = 10
        }
        
        // Configure Y axis
        barChart.axisLeft.axisMinimum = 0f
        barChart.axisRight.isEnabled = false

        // Set chart title
        barChart.setDrawValueAboveBar(true)
        barChart.animateY(1000)
        barChart.invalidate()
    }
}
