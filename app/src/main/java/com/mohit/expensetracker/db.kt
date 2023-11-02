package com.mohit.expensetracker

import com.mohit.expensetracker.models.Category
import com.mohit.expensetracker.models.Expense
import com.mohit.expensetracker.models.Income
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
//database
val config = RealmConfiguration.create(schema = setOf(Expense::class, Category::class, Income::class))

val db: Realm = Realm.open(config)