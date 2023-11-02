package com.mohit.expensetracker.components.expensesList

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mohit.expensetracker.components.IncomesDayGroup
import com.mohit.expensetracker.models.Income
import com.mohit.expensetracker.models.groupedByDay


@Composable
fun IncomesList(incomes: List<Income>, modifier: Modifier = Modifier) {
    val groupedIncomes = incomes.groupedByDay()

    Column(modifier = modifier) {
        if (groupedIncomes.isEmpty()) {
            Text("No data for selected date range.", modifier = Modifier.padding(top = 32.dp))
        } else {
            groupedIncomes.keys.forEach { date ->
                if (groupedIncomes[date] != null) {
                    IncomesDayGroup(
                        date = date,
                        dayIncomes = groupedIncomes[date]!!,
                        modifier = Modifier.padding(top = 24.dp)
                    )
                }
            }
        }
    }
}
