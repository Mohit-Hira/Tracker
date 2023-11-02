package com.mohit.expensetracker.mock

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color
import com.mohit.expensetracker.models.Category
import com.mohit.expensetracker.models.Expense
import com.mohit.expensetracker.models.Recurrence
import io.github.serpro69.kfaker.Faker
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

val faker = Faker()

val mockCategories = listOf(
  Category(
    "Bills",
    Color(
      faker.random.nextInt(0, 255),
      faker.random.nextInt(0, 255),
      faker.random.nextInt(0, 255)
    )
  ),
  Category(
    "Subscriptions", Color(
      faker.random.nextInt(0, 255),
      faker.random.nextInt(0, 255),
      faker.random.nextInt(0, 255)
    )
  ),
  Category(
    "Take out", Color(
      faker.random.nextInt(0, 255),
      faker.random.nextInt(0, 255),
      faker.random.nextInt(0, 255)
    )
  ),
  Category(
    "Hobbies", Color(
      faker.random.nextInt(0, 255),
      faker.random.nextInt(0, 255),
      faker.random.nextInt(0, 255)
    )
  ),
)

@RequiresApi(Build.VERSION_CODES.O)
val mockExpenses: List<Expense> = List(30) {
  Expense(
    amount = faker.random.nextInt(min = 1, max = 999)
      .toDouble() + faker.random.nextDouble(),

    ),
    recurrence = faker.random.randomValue(
      listOf(
        Recurrence.None,
        Recurrence.Daily,
        Recurrence.Monthly,
        Recurrence.Weekly,
        Recurrence.Yearly
      )
    ),
    note = faker.australia.animals(),
    category = faker.random.randomValue(mockCategories)
  )
}