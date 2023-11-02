package com.mohit.expensetracker.models

import io.realm.kotlin.types.ObjectId
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime

class Income(): RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId.create()
    var amount: Double = 0.0

    private var _recurrenceName: String = "None"
    val recurrence: Recurrence get() { return _recurrenceName.toRecurrence() }

    private var _dateValue: String = LocalDateTime.now().toString()
    val date: LocalDateTime get() { return LocalDateTime.parse(_dateValue) }

    var note: String = ""
    var category: Category? = null

    constructor(
        amount: Double,
        recurrence: Recurrence,
        date: LocalDateTime,
        note: String,
        category: Category,
    ) : this() {
        this.amount = amount
        this._recurrenceName = recurrence.name
        this._dateValue = date.toString()
        this.note = note
        this.category = category
    }
}

data class DayIncomes(
    val incomes: MutableList<Income>,
    var total: Double,
)

fun List<Income>.groupedByDay(): Map<LocalDate, DayIncomes> {
    val dataMap: MutableMap<LocalDate, DayIncomes> = mutableMapOf()

    this.forEach { income ->
        val date = income.date.toLocalDate()

        if (dataMap[date] == null) {
            dataMap[date] = DayIncomes(
                incomes = mutableListOf(),
                total = 0.0
            )
        }

        dataMap[date]!!.incomes.add(income)
        dataMap[date]!!.total = dataMap[date]!!.total.plus(income.amount)
    }

    dataMap.values.forEach { dayIncomes ->
        dayIncomes.incomes.sortBy { income -> income.date }
    }

    return dataMap.toSortedMap(compareByDescending { it })
}

fun List<Income>.groupedByDayOfWeek(): Map<String, DayIncomes> {
    val dataMap: MutableMap<String, DayIncomes> = mutableMapOf()

    this.forEach { income ->
        val dayOfWeek = income.date.toLocalDate().dayOfWeek

        if (dataMap[dayOfWeek.name] == null) {
            dataMap[dayOfWeek.name] = DayIncomes(
                incomes = mutableListOf(),
                total = 0.0
            )
        }

        dataMap[dayOfWeek.name]!!.incomes.add(income)
        dataMap[dayOfWeek.name]!!.total = dataMap[dayOfWeek.name]!!.total.plus(income.amount)
    }

    return dataMap.toSortedMap(compareByDescending { it })
}

fun List<Income>.groupedByDayOfMonth(): Map<Int, DayIncomes> {
    val dataMap: MutableMap<Int, DayIncomes> = mutableMapOf()

    this.forEach { income ->
        val dayOfMonth = income.date.toLocalDate().dayOfMonth

        if (dataMap[dayOfMonth] == null) {
            dataMap[dayOfMonth] = DayIncomes(
                incomes = mutableListOf(),
                total = 0.0
            )
        }

        dataMap[dayOfMonth]!!.incomes.add(income)
        dataMap[dayOfMonth]!!.total = dataMap[dayOfMonth]!!.total.plus(income.amount)
    }

    return dataMap.toSortedMap(compareByDescending { it })
}

fun List<Income>.groupedByMonth(): Map<String, DayIncomes> {
    val dataMap: MutableMap<String, DayIncomes> = mutableMapOf()

    this.forEach { income ->
        val month = income.date.toLocalDate().month

        if (dataMap[month.name] == null) {
            dataMap[month.name] = DayIncomes(
               incomes = mutableListOf(),
                total = 0.0
            )
        }

        dataMap[month.name]!!.incomes.add(income)
        dataMap[month.name]!!.total = dataMap[month.name]!!.total.plus(income.amount)
    }

    return dataMap.toSortedMap(compareByDescending { it })
}