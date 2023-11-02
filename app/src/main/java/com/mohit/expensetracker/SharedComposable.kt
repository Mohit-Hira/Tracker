package com.mohit.expensetracker

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.github.skydoves.colorpicker.compose.AlphaTile
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.marosseleng.compose.material3.datetimepickers.date.ui.dialog.DatePickerDialog
import com.mohit.expensetracker.components.PickerTrigger
import com.mohit.expensetracker.components.ReportPage
import com.mohit.expensetracker.components.TableRow
import com.mohit.expensetracker.components.UnstyledTextField
import com.mohit.expensetracker.components.expensesList.ExpensesList
import com.mohit.expensetracker.components.expensesList.IncomesList
import com.mohit.expensetracker.models.Category
import com.mohit.expensetracker.models.Expense
import com.mohit.expensetracker.models.Recurrence
import com.mohit.expensetracker.ui.theme.BackgroundElevated
import com.mohit.expensetracker.ui.theme.Destructive
import com.mohit.expensetracker.ui.theme.DividerColor
import com.mohit.expensetracker.ui.theme.ExpenseTrackerTheme
import com.mohit.expensetracker.ui.theme.LabelSecondary
import com.mohit.expensetracker.ui.theme.Shapes
import com.mohit.expensetracker.ui.theme.TopAppBarBackground
import com.mohit.expensetracker.ui.theme.Typography
import com.mohit.expensetracker.viewmodels.AddViewModel
import com.mohit.expensetracker.viewmodels.CategoriesViewModel
import com.mohit.expensetracker.viewmodels.ExpensesViewModel
import com.mohit.expensetracker.viewmodels.IncomesViewModel
import com.mohit.expensetracker.viewmodels.ReportsViewModel
import io.realm.kotlin.ext.query
import kotlinx.coroutines.launch
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox
import java.text.DecimalFormat

//Add Function
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun Add(vm: AddViewModel = viewModel()) {
    val state by vm.uiState.collectAsState()

    val recurrences = listOf(
        Recurrence.None,
        Recurrence.Daily,
        Recurrence.Weekly,
        Recurrence.Monthly,
        Recurrence.Yearly
    )

    Scaffold(topBar = {
        MediumTopAppBar(
            title = { Text("Add") },
            colors = TopAppBarDefaults.mediumTopAppBarColors(
                containerColor = TopAppBarBackground
            )
        )
    }, content = { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .clip(Shapes.large)
                    .background(BackgroundElevated)
                    .fillMaxWidth()
            ) {
                TableRow(label = "Amount", detailContent = {
                    UnstyledTextField(
                        value = state.amount,
                        onValueChange = vm::setAmount,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("0") },
                        arrangement = Arrangement.End,
                        maxLines = 1,
                        textStyle = TextStyle(
                            textAlign = TextAlign.Right,
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                        )
                    )
                })
                Divider(
                    modifier = Modifier.padding(start = 16.dp),
                    thickness = 1.dp,
                    color = DividerColor
                )
                TableRow(label = "Recurrence", detailContent = {
                    var recurrenceMenuOpened by remember {
                        mutableStateOf(false)
                    }
                    TextButton(
                        onClick = { recurrenceMenuOpened = true }, shape = Shapes.large
                    ) {
                        Text(state.recurrence.name ?: Recurrence.None.name)
                        DropdownMenu(expanded = recurrenceMenuOpened,
                            onDismissRequest = { recurrenceMenuOpened = false }) {
                            recurrences.forEach { recurrence ->
                                DropdownMenuItem(text = { Text(recurrence.name) }, onClick = {
                                    vm.setRecurrence(recurrence)
                                    recurrenceMenuOpened = false
                                })
                            }
                        }
                    }
                })
                Divider(
                    modifier = Modifier.padding(start = 16.dp),
                    thickness = 1.dp,
                    color = DividerColor
                )
                var datePickerShowing by remember {
                    mutableStateOf(false)
                }
                TableRow(label = "Date", detailContent = {
                    TextButton(onClick = { datePickerShowing = true }) {
                        Text(state.date.toString())
                    }
                    if (datePickerShowing) {
                        DatePickerDialog(onDismissRequest = { datePickerShowing = false },
                            onDateChange = { it ->
                                vm.setDate(it)
                                datePickerShowing = false
                            },
                            initialDate = state.date,
                            title = { Text("Select date", style = Typography.titleLarge) })
                    }
                })
                Divider(
                    modifier = Modifier.padding(start = 16.dp),
                    thickness = 1.dp,
                    color = DividerColor
                )
                TableRow(label = "Note", detailContent = {
                    UnstyledTextField(
                        value = state.note,
                        placeholder = { Text("Leave some notes") },
                        arrangement = Arrangement.End,
                        onValueChange = vm::setNote,
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(
                            textAlign = TextAlign.Right,
                        ),
                    )
                })
                Divider(
                    modifier = Modifier.padding(start = 16.dp),
                    thickness = 1.dp,
                    color = DividerColor
                )
                TableRow(label = "Category", detailContent = {
                    var categoriesMenuOpened by remember {
                        mutableStateOf(false)
                    }
                    TextButton(
                        onClick = { categoriesMenuOpened = true }, shape = Shapes.large
                    ) {
                        Text(
                            state.category?.name ?: "Select a category first",
                            color = state.category?.color ?: Color.White
                        )
                        DropdownMenu(expanded = categoriesMenuOpened,
                            onDismissRequest = { categoriesMenuOpened = false }) {
                            state.categories?.forEach { category ->
                                DropdownMenuItem(text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            modifier = Modifier.size(10.dp),
                                            shape = CircleShape,
                                            color = category.color
                                        ) {}
                                        Text(
                                            category.name, modifier = Modifier.padding(start = 8.dp)
                                        )
                                    }
                                }, onClick = {
                                    vm.setCategory(category)
                                    categoriesMenuOpened = false
                                })
                            }
                        }
                    }
                })
            }
            Button(
                onClick = vm::submitExpense,
                modifier = Modifier.padding(16.dp),
                shape = Shapes.large,
                enabled = state.category != null
            ) {
                Text("Submit expense")
            }
        }
    })
}
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewAdd() {
    ExpenseTrackerTheme {
        val navController = rememberNavController()
        Add()
    }
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun Categories(
    navController: NavController, vm: CategoriesViewModel = viewModel()
) {
    val uiState by vm.uiState.collectAsState()

    val colorPickerController = rememberColorPickerController()

    Scaffold(topBar = {
        MediumTopAppBar(title = { Text("Categories") },
            colors = TopAppBarDefaults.mediumTopAppBarColors(
                containerColor = TopAppBarBackground
            ),
            navigationIcon = {
                Surface(
                    onClick = navController::popBackStack,
                    color = Color.Transparent,
                ) {
                    Row(modifier = Modifier.padding(vertical = 10.dp)) {
                        Icon(
                            Icons.Rounded.KeyboardArrowLeft, contentDescription = "Settings"
                        )
                        Text("Settings")
                    }
                }
            })
    }, content = { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                AnimatedVisibility(visible = true) {
                    LazyColumn(
                        modifier = Modifier
                            .padding(16.dp)
                            .clip(Shapes.large)
                            .fillMaxWidth()
                    ) {
                        itemsIndexed(
                            uiState.categories,
                            key = { _, category -> category.name }) { index, category ->
                            SwipeableActionsBox(
                                endActions = listOf(
                                    SwipeAction(
                                        icon = painterResource(R.drawable.delete),
                                        background = Destructive,
                                        onSwipe = { vm.deleteCategory(category) }
                                    ),
                                ),
                                modifier = Modifier.animateItemPlacement()
                            ) {
                                TableRow(modifier = Modifier.background(BackgroundElevated)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 16.dp)
                                    ) {
                                        Surface(
                                            color = category.color,
                                            shape = CircleShape,
                                            border = BorderStroke(
                                                width = 2.dp,
                                                color = Color.White
                                            ),
                                            modifier = Modifier.size(16.dp)
                                        ) {}
                                        Text(
                                            category.name,
                                            modifier = Modifier.padding(
                                                horizontal = 16.dp,
                                                vertical = 10.dp
                                            ),
                                            style = Typography.bodyMedium,
                                        )
                                    }
                                }
                            }
                            if (index < uiState.categories.size - 1) {
                                Row(modifier = Modifier
                                    .background(BackgroundElevated)
                                    .height(1.dp)) {
                                    Divider(
                                        modifier = Modifier.padding(start = 16.dp),
                                        thickness = 1.dp,
                                        color = DividerColor
                                    )
                                }
                            }
                        }
                    }
                }
            }
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                if (uiState.colorPickerShowing) {
                    Dialog(onDismissRequest = vm::hideColorPicker) {
                        Surface(color = BackgroundElevated, shape = Shapes.large) {
                            Column(
                                modifier = Modifier.padding(all = 30.dp)
                            ) {
                                Text("Select a color", style = Typography.titleLarge)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 24.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AlphaTile(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(60.dp)
                                            .clip(RoundedCornerShape(6.dp)),
                                        controller = colorPickerController
                                    )
                                }
                                HsvColorPicker(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(300.dp)
                                        .padding(10.dp),
                                    controller = colorPickerController,
                                    onColorChanged = { envelope ->
                                        vm.setNewCategoryColor(envelope.color)
                                    },
                                )
                                TextButton(
                                    onClick = vm::hideColorPicker,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 24.dp),
                                ) {
                                    Text("Done")
                                }
                            }
                        }
                    }
                }
                Surface(
                    onClick = vm::showColorPicker,
                    shape = CircleShape,
                    color = uiState.newCategoryColor,
                    border = BorderStroke(
                        width = 2.dp,
                        color = Color.White
                    ),
                    modifier = Modifier.size(width = 24.dp, height = 24.dp)
                ) {}
                Surface(
                    color = BackgroundElevated,
                    modifier = Modifier
                        .height(44.dp)
                        .weight(1f)
                        .padding(start = 16.dp),
                    shape = Shapes.large,
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        UnstyledTextField(
                            value = uiState.newCategoryName,
                            onValueChange = vm::setNewCategoryName,
                            placeholder = { Text("Category name") },
                            modifier = Modifier
                                .fillMaxWidth(),
                            maxLines = 1,
                        )
                    }
                }
                IconButton(
                    onClick = vm::createNewCategory,
                    modifier = Modifier
                        .padding(start = 16.dp)
                ) {
                    Icon(
                        Icons.Rounded.Send,
                        "Create category"
                    )
                }
            }
        }
    })
}
//Expense
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Expenses(
    navController: NavController,
    vm: ExpensesViewModel = viewModel()
) {
    val recurrences = listOf(
        Recurrence.Daily,
        Recurrence.Weekly,
        Recurrence.Monthly,
        Recurrence.Yearly
    )

    val state by vm.uiState.collectAsState()
    var recurrenceMenuOpened by remember {
        mutableStateOf(false)
    }

    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = { Text("Expenses") },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = TopAppBarBackground
                )
            )
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Total for:",
                        style = Typography.bodyMedium,
                    )
                    PickerTrigger(
                        state.recurrence.target ?: Recurrence.None.target,
                        onClick = { recurrenceMenuOpened = !recurrenceMenuOpened },
                        modifier = Modifier.padding(start = 16.dp)
                    )
                    DropdownMenu(expanded = recurrenceMenuOpened,
                        onDismissRequest = { recurrenceMenuOpened = false }) {
                        recurrences.forEach { recurrence ->
                            DropdownMenuItem(text = { Text(recurrence.target) }, onClick = {
                                vm.setRecurrence(recurrence)
                                recurrenceMenuOpened = false
                            })
                        }
                    }
                }
                Row(modifier = Modifier.padding(vertical = 32.dp)) {
                    Text(
                        "$",
                        style = Typography.bodyMedium,
                        color = LabelSecondary,
                        modifier = Modifier.padding(end = 4.dp, top = 4.dp)
                    )
                    Text(
                        DecimalFormat("0.#").format(state.sumTotal),
                        style = Typography.titleLarge
                    )
                }
                ExpensesList(
                    expenses = state.expenses,
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(
                            rememberScrollState()
                        )
                )
            }
        }
    )
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun ExpensesPreview() {
    ExpenseTrackerTheme {
        Expenses(navController = rememberNavController())
    }
}
//Income
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Income(
    navController: NavController,
    vm: IncomesViewModel = viewModel()
) {
    val recurrences = listOf(
        Recurrence.Daily,
        Recurrence.Weekly,
        Recurrence.Monthly,
        Recurrence.Yearly
    )

    val state by vm.uiState.collectAsState()
    var recurrenceMenuOpened by remember {
        mutableStateOf(false)
    }

    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = { Text("Income") },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = TopAppBarBackground
                )
            )
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Total for:",
                        style = Typography.bodyMedium,
                    )
                    PickerTrigger(
                        state.recurrence.target ?: Recurrence.None.target,
                        onClick = { recurrenceMenuOpened = !recurrenceMenuOpened },
                        modifier = Modifier.padding(start = 16.dp)
                    )
                    DropdownMenu(expanded = recurrenceMenuOpened,
                        onDismissRequest = { recurrenceMenuOpened = false }) {
                        recurrences.forEach { recurrence ->
                            DropdownMenuItem(text = { Text(recurrence.target) }, onClick = {
                                vm.setRecurrence(recurrence)
                                recurrenceMenuOpened = false
                            })
                        }
                    }
                }
                Row(modifier = Modifier.padding(vertical = 32.dp)) {
                    Text(
                        "$",
                        style = Typography.bodyMedium,
                        color = LabelSecondary,
                        modifier = Modifier.padding(end = 4.dp, top = 4.dp)
                    )
                    Text(
                        DecimalFormat("0.#").format(state.sumTotal),
                        style = Typography.titleLarge
                    )
                }
                IncomesList(
                    incomes = state.incomes,
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(
                            rememberScrollState()
                        )
                )
            }
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@Composable
fun Reports(vm: ReportsViewModel = viewModel()) {
    val uiState = vm.uiState.collectAsState().value

    val recurrences = listOf(
        Recurrence.Weekly,
        Recurrence.Monthly,
        Recurrence.Yearly
    )

    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = { Text("Reports") },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = TopAppBarBackground
                ),
                actions = {
                    IconButton(onClick = vm::openRecurrenceMenu) {
                        Icon(
                            painterResource(id = R.drawable.ic_today),
                            contentDescription = "Change recurrence"
                        )
                    }
                    DropdownMenu(
                        expanded = uiState.recurrenceMenuOpened,
                        onDismissRequest = vm::closeRecurrenceMenu
                    ) {
                        recurrences.forEach { recurrence ->
                            DropdownMenuItem(text = { Text(recurrence.name) }, onClick = {
                                vm.setRecurrence(recurrence)
                                vm.closeRecurrenceMenu()
                            })
                        }
                    }
                }
            )
        },
        content = { innerPadding ->
            val numOfPages = when (uiState.recurrence) {
                Recurrence.Weekly -> 53
                Recurrence.Monthly -> 12
                Recurrence.Yearly -> 1
                else -> 53
            }
            HorizontalPager(count = numOfPages, reverseLayout = true) { page ->
                ReportPage(innerPadding, page, uiState.recurrence)
            }
        }
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Settings(navController: NavController) {
    val coroutineScope = rememberCoroutineScope()
    var deleteConfirmationShowing by remember {
        mutableStateOf(false)
    }

    val eraseAllData: () -> Unit = {
        coroutineScope.launch {
            db.write {
                val expenses = this.query<Expense>().find()
                val categories = this.query<Category>().find()

                delete(expenses)
                delete(categories)

                deleteConfirmationShowing = false
            }
        }
    }

    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = { Text("Settings") },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = TopAppBarBackground
                )
            )
        },
        content = { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .clip(Shapes.large)
                        .background(BackgroundElevated)
                        .fillMaxWidth()
                ) {
                    TableRow(
                        label = "Categories",
                        hasArrow = true,
                        modifier = Modifier.clickable {
                            navController.navigate("settings/categories")
                        })
                    Divider(
                        modifier = Modifier
                            .padding(start = 16.dp), thickness = 1.dp, color = DividerColor
                    )
                    TableRow(
                        label = "Erase all data",
                        isDestructive = true,
                        modifier = Modifier.clickable {
                            deleteConfirmationShowing = true
                        })

                    if (deleteConfirmationShowing) {
                        AlertDialog(
                            onDismissRequest = { deleteConfirmationShowing = false },
                            title = { Text("Are you sure?") },
                            text = { Text("This action cannot be undone.") },
                            confirmButton = {
                                TextButton(onClick = eraseAllData) {
                                    Text("Delete everything")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { deleteConfirmationShowing = false }) {
                                    Text("Cancel")
                                }
                            }
                        )
                    }
                }
            }
        }
    )
}
