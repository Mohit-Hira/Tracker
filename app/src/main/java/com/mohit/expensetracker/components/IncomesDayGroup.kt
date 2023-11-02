package com.mohit.expensetracker.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mohit.expensetracker.models.DayIncomes
import com.mohit.expensetracker.ui.theme.LabelSecondary
import com.mohit.expensetracker.ui.theme.Typography
import com.mohit.expensetracker.utils.formatDay
import java.text.DecimalFormat
import java.time.LocalDate

@Composable
fun IncomesDayGroup(
    date: LocalDate,
    dayIncomes: DayIncomes,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            date.formatDay(),
            style = Typography.headlineMedium,
            color = LabelSecondary
        )
        Divider(modifier = Modifier.padding(top = 10.dp, bottom = 4.dp))
        dayIncomes.incomes.forEach { income ->
            IncomeRow(
                income = income,
                modifier = Modifier.padding(top = 12.dp)
            )
        }
        Divider(modifier = Modifier.padding(top = 16.dp, bottom = 4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Total:", style = Typography.bodyMedium, color = LabelSecondary)
            Text(
                DecimalFormat("USD 0.#").format(dayIncomes.total),
                style = Typography.headlineMedium,
                color = LabelSecondary
            )
        }
    }
}