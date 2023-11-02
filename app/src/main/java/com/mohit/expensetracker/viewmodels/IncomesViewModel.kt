package com.mohit.expensetracker.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohit.expensetracker.db
import com.mohit.expensetracker.models.Income
import com.mohit.expensetracker.models.Recurrence
import com.mohit.expensetracker.utils.calculateDateRange
import io.realm.kotlin.ext.query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class IncomesState(
    val recurrence: Recurrence = Recurrence.Daily,
    val sumTotal: Double = 1250.98,
    val incomes: List<Income> = listOf()
)

class IncomesViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(IncomesState())
    val uiState: StateFlow<IncomesState> = _uiState.asStateFlow()

    init {
        _uiState.update { currentState ->
            currentState.copy(
                incomes = db.query<Income>().find()
            )
        }
        viewModelScope.launch(Dispatchers.IO) {
            setRecurrence(Recurrence.Daily)
        }
    }

    fun setRecurrence(recurrence: Recurrence) {
        val (start, end) = calculateDateRange(recurrence, 0)

        val filteredIncomes = db.query<Income>().find().filter { income ->
            (income.date.toLocalDate().isAfter(start) && income.date.toLocalDate()
                .isBefore(end)) || income.date.toLocalDate()
                .isEqual(start) || income.date.toLocalDate().isEqual(end)
        }

        val sumTotal = filteredIncomes.sumOf { it.amount }

        _uiState.update { currentState ->
            currentState.copy(
                recurrence = recurrence,
                incomes = filteredIncomes,
                sumTotal = sumTotal
            )
        }
    }
}