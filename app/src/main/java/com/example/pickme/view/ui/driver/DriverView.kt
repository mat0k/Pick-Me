package com.example.pickme.view.ui.driver

import com.example.pickme.PickUpService
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.pickme.MainActivity
import com.example.pickme.PickUpAcceptedService
import com.example.pickme.R
import com.example.pickme.TripJoinedService
import com.example.pickme.data.model.LocalPickUp
import com.example.pickme.data.model.LocalTripDbHelper
import com.example.pickme.data.model.Passenger
import com.example.pickme.data.model.PickUp
import com.example.pickme.ui.passenger.ui.theme.PickMeUpTheme
import com.example.pickme.view.ui.passenger.SearchLocationDialog
import com.example.pickme.view.ui.login.LoginViewModel
import com.example.pickme.view.ui.login.LoginViewModelFactory
import com.example.pickme.view.ui.passenger.LoginDialog
import com.example.pickme.view.ui.passenger.UserProfileRow
import com.example.pickme.viewModel.PassengerViewModel
import com.example.pickme.viewModel.TripViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import com.example.pickme.data.model.Place
import com.example.pickme.data.model.LocalTrip
import java.util.Locale
import com.google.firebase.database.GenericTypeIndicator
import com.example.pickme.data.repository.OneSignalNotificationSender
import com.example.pickme.view.ui.passenger.PickUps
import com.example.pickme.viewModel.PickUpViewModel


data class BottomNavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

class DriverView : ComponentActivity() {
    private val tripViewModel: TripViewModel by viewModels()
    private val pickUpViewModel: PickUpViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startService(Intent(this, PickUpService::class.java))
        startService(Intent(this, TripJoinedService::class.java))
        stopService(Intent(this, PickUpAcceptedService::class.java))
        setContent {
            PickMeUpTheme {
                val items = listOf(
                    BottomNavigationItem("trips", Icons.Filled.Create, Icons.Outlined.Create),
                    BottomNavigationItem("home", Icons.Filled.Home, Icons.Outlined.Home),
                    BottomNavigationItem("profile", Icons.Filled.Person, Icons.Outlined.Person)
                )
                var selectedItemIndex by remember { mutableIntStateOf(items.indexOfFirst { it.title == "home" }) }
                val navController = rememberNavController()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
                        bottomBar = {
                            NavigationBar {
                                items.forEachIndexed { index, item ->
                                    NavigationBarItem(
                                        selected = (selectedItemIndex == index),
                                        onClick = {
                                            selectedItemIndex = index
                                            navController.navigate(item.title)
                                        },
                                        label = { Text(item.title) },
                                        alwaysShowLabel = false,
                                        icon = {
                                            Box {
                                                Icon(
                                                    imageVector = if (index == selectedItemIndex) {
                                                        item.selectedIcon
                                                    } else item.unselectedIcon,
                                                    contentDescription = item.title
                                                )
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    ) { innerPadding ->
                        Box(modifier = Modifier.padding(innerPadding)) {
                            NavHost(navController = navController, startDestination = "home") {
                                composable("trips") {
                                    TripsScreen(navController, tripViewModel, pickUpViewModel)
                                }
                                composable("home") {
                                    Startup(navController, pickUpViewModel)
                                }
                                composable("profile") {
                                    ProfileScreen(navController, this@DriverView)
                                }
                            }
                        }
                    }
                }
            }

        }
    }
}


@Composable
fun Startup(navController: NavHostController, pickUpViewModel: PickUpViewModel
){
    val context= LocalContext.current
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(navController, pickUpViewModel)
        }
        composable("pickUpPreview") {
            PickUpPreview(
                context,
                navController,
                pickUpViewModel
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController, pickUpViewModel: PickUpViewModel) {
    val context = LocalContext.current

    val showBottomSheet = remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val viewModelFactory = remember { HomeScreenVMFactory(context) }
    val viewModel = viewModel<HomeScreenVM>(factory = viewModelFactory)
    val pickUps: List<PickUp> by viewModel.pickUps.observeAsState(initial = emptyList())
    val formatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = {
                Text("Home")
            },
                actions = {
                    IconButton(onClick = {
                        showBottomSheet.value = true
                    }) {
                        Icon(Icons.Filled.FilterAlt, contentDescription = "Filter")
                    }
                }
            )
        }
    ) { it ->
        LazyColumn(
            modifier = Modifier.padding(it),
        )
        {
            items(pickUps) { pickUp ->
                val passenger =
                    viewModel.getPassengerData(pickUp.passengerId).observeAsState().value
                PickUpCard(navController, pickUp, passenger, pickUpViewModel)
            }
        }
        if (showBottomSheet.value) {
            ModalBottomSheet(
                sheetState = sheetState,
                onDismissRequest = { showBottomSheet.value = false }
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "Working Hours: ${
                            LocalTime.of(
                                viewModel.workingHoursRange.value.start.toInt(),
                                0
                            ).format(formatter)
                        } - ${
                            LocalTime.of(viewModel.workingHoursRange.value.endInclusive.toInt(), 0)
                                .format(formatter)
                        }"
                    )
                    RangeSlider(
                        value = viewModel.workingHoursRange.value,
                        onValueChange = { range -> viewModel.workingHoursRange.value = range },
                        valueRange = 0f..23f,
                        steps = 24,
                        onValueChangeFinished = {
                            viewModel.saveFilters(context)
                        }
                    )
                    Text(
                        String.format(
                            Locale.ENGLISH,
                            "Radius: %.1f km",
                            viewModel.radius.floatValue
                        )
                    )
                    Slider(
                        value = viewModel.radius.floatValue,
                        onValueChange = {
                            viewModel.radius.floatValue = it
                            viewModel.saveFilters(context)
                        },
                        valueRange = 1f..10f,
                        steps = 8
                    )
                }
            }
        }

    }
}

@Composable
fun PickUpCard(navController: NavHostController, pickUp: PickUp, passenger: Passenger?, pickUpViewModel:PickUpViewModel) {
    val passengerViewModel= PassengerViewModel()
    val context= LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.weight(4f)
            ) {
                Text(
                    text = pickUp.dateAndTime,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Start: ${pickUp.pickUpTitle}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Destination: ${pickUp.targetTitle}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Distance: ${pickUp.distance} km",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text= "Price: ${pickUp.price} $"
                )
                Text(
                    text = "Passenger: ${passenger?.name} ${passenger?.surname}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(
                    onClick = {
                        pickUpViewModel.setPrevPickUpTitle(pickUp.pickUpTitle)
                        pickUpViewModel.setPrevTargetTitle(pickUp.targetTitle)
                        pickUpViewModel.setPrevPickUPLatLng(pickUp.pickUpLatLng)
                        pickUpViewModel.setPrevTargetLatLng(pickUp.targetLatLng)
                        pickUpViewModel.setPrevDistance(pickUp.distance)

                        if (passengerViewModel.isNetworkAvailable(context)) {
                            navController.navigate("pickUpPreview") //here
                        } else {
                            passengerViewModel.ShowWifiProblemDialog(context)
                        }
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.search_location1),
                        contentDescription = "Preview"
                    )
                }
            }
        }
    }
}


@Composable
fun TripsScreen(
    navController: NavHostController,
    tripViewModel: TripViewModel,
    pickUpViewModel: PickUpViewModel
) {

    val navController = rememberNavController()
    val context = LocalContext.current
    NavHost(navController = navController, startDestination = "setTrips") {
        composable("setTrips") {
            SetTrips(navController, tripViewModel, pickUpViewModel)
        }
        composable("mapView") {
            MapView(navController, tripViewModel)
        }
        composable("pickUpPreview") {
            PickUpPreview(context, navController, pickUpViewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetTrips(
    navController: NavHostController,
    tripViewModel: TripViewModel,
    pickUpViewModel: PickUpViewModel
) {

    val context = LocalContext.current
    var tripTitle by remember { mutableStateOf(tripViewModel.tripTitle.value) }

    var startingTitle = tripViewModel.tripStartTitle.value

    var destinationTitle = tripViewModel.tripDestTitle.value

    var seats by remember { mutableIntStateOf(tripViewModel.seats.value) }

    var isButtonClicked1 by remember {
        mutableStateOf(
            startingTitle != "Starting point"
        )
    }
    var isButtonClicked2 by remember { mutableStateOf(false) }

    var pickedDate by remember {
        mutableStateOf(LocalDate.now())
    }
    var pickedTime by remember {
        mutableStateOf(LocalTime.now())
    }
    val formattedDate by remember {
        derivedStateOf {
            DateTimeFormatter
                .ofPattern("MMM dd yyyy")
                .format(pickedDate)
        }
    }
    val formattedTime by remember {
        derivedStateOf {
            DateTimeFormatter
                .ofPattern("hh:mm a")
                .format(pickedTime)
        }
    }

    val dateDialogState = rememberMaterialDialogState()
    val timeDialogState = rememberMaterialDialogState()

    var enableConfirmation1 by remember {
        mutableStateOf(false)
    }

    var enableConfirmation2 by remember {
        mutableStateOf(false)
    }

    if (startingTitle != "Starting point") {
        enableConfirmation1 = true
    }
    val passengerViewModel = PassengerViewModel()

    val database = Firebase.database
    val myRef = database.getReference("Trips")
    val sharedPref = LocalContext.current.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
    val driverId = sharedPref.getString("lastUserId", null)
    var isVerified = false
    var enableConfirm = false

    val dbHelper = LocalTripDbHelper(context)

    val showDialog = remember { mutableStateOf(false) }
    val tripToDelete = remember { mutableStateOf<LocalTrip?>(null) }

    var trips by remember { mutableStateOf(listOf<LocalTrip>()) }

    var showBottomSheet by remember {
        mutableStateOf(false)
    }
    val sheetState = rememberModalBottomSheetState()

    var isLoading by remember {
        mutableStateOf(true)
    }
    if (passengerViewModel.isNetworkAvailable(context)) {
        enableConfirm = true
        val driverRef = database.getReference("Drivers").child(driverId ?: "")
        driverRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                isVerified = dataSnapshot.child("verified").getValue(Boolean::class.java) ?: false
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("Error reading driver verification status: ${databaseError.message}")
            }
        })
    } else {
        passengerViewModel.ShowWifiProblemDialog(context)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 5.dp, end = 16.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Add new Trip",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
                modifier = Modifier.padding(start = 10.dp, top = 6.dp)
            )
        }

        TextField(
            value = tripTitle,
            onValueChange = { tripTitle = it },
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(fontSize = 22.sp),
            label = {
                Text(
                    "Trip Title",
                    style = TextStyle(fontSize = 20.sp) // Adjust the value (20) to make the text bigger
                )
            }
        )

        // Seats
///////////////////////////////////////////////////////////////////////////////////////////////
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Seats")
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                IconButton(
                    onClick = { if (seats > 0) seats-- }
                ) {
                    Icon(
                        painterResource(id = R.drawable.remove_icon),
                        contentDescription = "Decrease seats"
                    )
                }
                Text("$seats")
                IconButton(
                    onClick = { seats++ }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Increase seats"
                    )
                }
            }
        }
        // Location
        ///////////////////////////////////////////////////////////////////////////////////////////////
        //Spacer(modifier = Modifier.padding(top = 5.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 50.dp, max = 110.dp), // row of two field and location button
            horizontalArrangement = Arrangement.Center,

            ) {

            if (isButtonClicked1) {
                Box(
                    modifier = Modifier.weight(0.9f)
                ) {

                    Column {
                        Box(                        // pick up location box
                            modifier = Modifier
                                .padding(start = 4.dp, end = 4.dp, top = 0.dp)
                                .background(
                                    color = Color.White,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    color = Color.Black,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(2.dp)
                        ) {
                            // row for pick up location and cancel button
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = startingTitle,
                                    fontSize = 18.sp,
                                    color = Color.Black
                                )
                            }
                        }


                        Box(                    //target location box
                            modifier = Modifier
                                .padding(start = 4.dp, end = 4.dp, top = 4.dp)
                                .background(
                                    color = Color.White,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    color = Color.Black,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(2.dp)
                        ) {
                            // row for pick up location and cancel button
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = destinationTitle,
                                    fontSize = 18.sp,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }
                Box(                    //cancel button box
                    modifier = Modifier
                        .weight(0.1f)
                        .fillMaxSize()
                        .heightIn(max = 110.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = {
                            startingTitle = "Starting point"
                            destinationTitle = "Destination"
                            isButtonClicked1 = false
                            enableConfirmation1 = false
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(4.dp)), // Adjust the corner radius here
                        contentPadding = PaddingValues(0.dp), // Remove the default padding
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "edit icon",
                                modifier = Modifier.size(20.dp) // Adjust the icon size
                            )
                        }
                    }
                }

            } else {

                Button(
                    modifier = Modifier
                        .size(width = 210.dp, height = 45.dp),
                    shape = RoundedCornerShape(15.dp),
                    onClick = {
                        if (tripTitle.isNotEmpty()) {
                            tripViewModel.setTripTitle(tripTitle)
                        }
                        if (seats != 0) {
                            tripViewModel.setTripTitle(tripTitle)
                        }
                        if (passengerViewModel.isNetworkAvailable(context)) {
                            navController.navigate("mapView")
                        } else {
                            passengerViewModel.ShowWifiProblemDialog(context)
                        }
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.location),
                        contentDescription = "Calendar Icon",
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        modifier = Modifier
                            .padding(start = 6.dp),
                        text = "Set location",
                        fontSize = 20.sp
                    )
                }
            }
        }
        // Date
///////////////////////////////////////////////////////////////////////////////////////////////

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 50.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            if (isButtonClicked2) { // calender
                Box(
                    modifier = Modifier.weight(0.9f)
                ) {
                    Box(                        // pick up location box
                        modifier = Modifier
                            .padding(start = 4.dp, end = 12.dp, top = 2.dp)
                            .background(color = Color.White, shape = RoundedCornerShape(8.dp))
                            .border(
                                width = 1.dp,
                                color = Color.Black,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(8.dp)
                    ) {
                        // row for pick up location and cancel button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val dateAndTimeField = "$formattedDate, $formattedTime"
                            Text(
                                text = dateAndTimeField,                  // date & time text
                                fontSize = 18.sp,
                                color = Color.Black
                            )
                        }
                    }
                }
                Box(                    //cancel button box
                    modifier = Modifier
                        .weight(0.1f)
                        .fillMaxSize()
                        .heightIn(max = 80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = {
                            pickedDate = LocalDate.now()
                            pickedTime = LocalTime.now()
                            isButtonClicked2 = false
                            enableConfirmation2 = false
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(4.dp)), // Adjust the corner radius here
                        contentPadding = PaddingValues(0.dp), // Remove the default padding
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "edit icon",
                                modifier = Modifier.size(20.dp) // Adjust the icon size
                            )
                        }
                    }
                }

            } else {

                Button(
                    modifier = Modifier
                        .size(width = 210.dp, height = 45.dp),
                    shape = RoundedCornerShape(15.dp),
                    onClick = {
                        dateDialogState.show()
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.calendar),
                        contentDescription = "Calendar Icon",
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        modifier = Modifier
                            .padding(start = 10.dp),
                        text = "Set Date",
                        fontSize = 20.sp
                    )
                }
            }
        }

        // Confirmation
///////////////////////////////////////////////////////////////////////////////////////////////
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                modifier = Modifier
                    .size(width = 200.dp, height = 40.dp),
                shape = RoundedCornerShape(15.dp),
                enabled = enableConfirmation1 && enableConfirmation2 && tripTitle != "" && seats != 0,
                onClick = {

                    if (passengerViewModel.isNetworkAvailable(context) && enableConfirm) {
                        val title = tripTitle
                        val tripSeats = seats
                        val starting = startingTitle
                        val end = destinationTitle
                        val startingLatLng = tripViewModel.tripStartLatLng.value
                        val destinationLatLng = tripViewModel.tripDestLatLng.value
                        val date = formattedDate
                        val time = formattedTime
                        val tripDistance = tripViewModel.distance.value


                        // val driverId = sharedPref.getString("lastUserId", null

                        // Generate a unique key for the new trip
                        val tripKey = myRef.push().key

                        // Check if the key is not null
                        if (tripKey != null) {
                            // Create a new trip object
                            val trip = mapOf(
                                "title" to title,
                                "seats" to tripSeats,
                                "starting" to starting,
                                "end" to end,
                                "startingLatLng" to startingLatLng,
                                "destinationLatLng" to destinationLatLng,
                                "date" to date,
                                "time" to time,
                                "tripDistance" to tripDistance,
                                "verified" to isVerified,
                                "id" to driverId,
                                "availableSeats" to tripSeats
                            )

                            // Add trip to the Firebase database using the unique key
                            myRef.child(tripKey).setValue(trip)
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        context,
                                        "Trip added",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    isButtonClicked2 = false
                                    enableConfirmation2 = false
                                }
                                .addOnFailureListener {
                                    Toast.makeText(
                                        context,
                                        "Failed to add trip",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            myRef.child(tripKey).child("passengersIds").setValue(listOf<String>())
                            // Save the trip locally
                            val localTrip = driverId?.let {
                                LocalTrip(
                                    id = tripKey, // Use the unique key as the trip ID
                                    driverId = it,
                                    title = title,
                                    seats = seats,
                                    starting = starting,
                                    end = end,
                                    startingLatLng = startingLatLng,
                                    destinationLatLng = destinationLatLng,
                                    date = date,
                                    time = time,
                                    tripDistance = tripDistance,
                                    verified = isVerified,
                                )
                            }
                            if (localTrip != null) {
                                dbHelper.insertTrip(localTrip)
                            }
                        }
                    } else {
                        passengerViewModel.ShowWifiProblemDialog(context)
                    }
                }) {
                Text(
                    text = "Confirmation",
                    fontSize = 20.sp
                )
            }
        }

        // History
///////////////////////////////////////////////////////////////////////////////////////////////

        LaunchedEffect(Unit) {
            trips = dbHelper.getAllTrips()
        }
        LazyColumn {
            items(trips) { trip ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(2.dp)
                        .background(
                            Color.Gray.copy(alpha = 0.7f),
                            shape = RoundedCornerShape(12.dp)
                        ),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(5.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(7.dp)
                                .weight(1f)  // Add weight modifier here
                        ) {
                            Text(
                                text = "${trip.date} At ${trip.time}",
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Text(text = "Starting: ${trip.starting}")
                            Text(text = "Destination: ${trip.end}")
                            Text(text = "seats: ${trip.seats}")
                            //     Text(text = "id: ${trip.id}")

                        }

                        Column { // icons column

                            // preview trip
                            IconButton(
                                onClick = {

                                    pickUpViewModel.setPrevPickUpTitle(trip.starting)
                                    pickUpViewModel.setPrevTargetTitle(trip.end)
                                    pickUpViewModel.setPrevPickUPLatLng(trip.startingLatLng)
                                    pickUpViewModel.setPrevTargetLatLng(trip.destinationLatLng)
                                    pickUpViewModel.setPrevDistance(trip.tripDistance)

                                    if (passengerViewModel.isNetworkAvailable(context)) {
                                        navController.navigate("pickUpPreview")
                                    } else {
                                        passengerViewModel.ShowWifiProblemDialog(context)
                                    }
                                }
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.search_location1),
                                    contentDescription = "Preview"
                                )
                            }

                            val passengers = remember { mutableStateOf(mutableListOf<Passenger>()) }
                            IconButton(
                                onClick = {
                                    val tripId = trip.id
                                    val tripsRef = database.getReference("Trips")
                                    val passengersIdsRef =
                                        tripsRef.child(tripId).child("passengersIds")
                                    val passengersRef = database.getReference("Passengers")
                                    showBottomSheet = true

                                    // Retrieve the passengerIds
                                    passengersIdsRef.addListenerForSingleValueEvent(object :
                                        ValueEventListener {
                                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                                            val passengerIds = dataSnapshot.children.mapNotNull {
                                                it.getValue(String::class.java)
                                            }

                                            // Retrieve the names and phone numbers of the passengers
                                            passengerIds.forEach { passengerId ->
                                                passengersRef.child(passengerId)
                                                    .addListenerForSingleValueEvent(object :
                                                        ValueEventListener {
                                                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                                                            val name = dataSnapshot.child("name")
                                                                .getValue(String::class.java) ?: ""
                                                            val surname =
                                                                dataSnapshot.child("surname")
                                                                    .getValue(String::class.java)
                                                                    ?: ""
                                                            val phoneNumber =
                                                                dataSnapshot.child("phone")
                                                                    .getValue(String::class.java)
                                                                    ?: ""
                                                            val emergencyNb =
                                                                dataSnapshot.child("emergencyNumber")
                                                                    .getValue(String::class.java)
                                                                    ?: ""
                                                            val photoUrl =
                                                                dataSnapshot.child("photoUrl")
                                                                    .getValue(String::class.java)
                                                                    ?: ""
                                                            passengers.value.add(
                                                                Passenger(
                                                                    name = name,
                                                                    surname = surname,
                                                                    phone = phoneNumber,
                                                                    emergencyNumber = emergencyNb,
                                                                    photoUrl = photoUrl
                                                                )
                                                            )

                                                            // Check if all passengers have been retrieved
                                                            if (passengers.value.size == passengerIds.size) {
                                                                // All passengers have been retrieved, show the bottom sheet
                                                                isLoading = false
                                                            }
                                                        }

                                                        override fun onCancelled(databaseError: DatabaseError) {
                                                            // Handle possible errors.
                                                        }
                                                    })
                                            }
                                        }

                                        override fun onCancelled(databaseError: DatabaseError) {
                                            // Handle possible errors.
                                        }
                                    })
                                }
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.preview_icon),
                                    contentDescription = "preview"
                                )
                            }

                            if (showBottomSheet) {
                                // Show the bottom sheet here
                                ModalBottomSheet(
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .heightIn(min = 700.dp),
                                    sheetState = sheetState,
                                    onDismissRequest = {
                                        showBottomSheet = false
                                    }
                                ) {
                                    if (isLoading) {
                                        Text(
                                            text = "Passengers",
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(16.dp)
                                        )
                                        Column(
                                            modifier = Modifier.fillMaxSize(),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            CircularProgressIndicator(
                                                modifier = Modifier
                                                    .size(80.dp)
                                                    .padding(16.dp)
                                            )
                                        }

                                    } else {
                                        LazyColumn(
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            item {
                                                Text(
                                                    text = "Passengers",
                                                    fontSize = 24.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(16.dp)
                                                )
                                            }
                                            items(passengers.value) { passenger ->
                                                Card(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(8.dp)
                                                        .background(Color.Gray.copy(alpha = 0.2f)),
                                                    shape = RoundedCornerShape(20.dp)
                                                ) {
                                                    Row(
                                                        modifier = Modifier.padding(16.dp)
                                                    ) {
                                                        Box(
                                                            modifier = Modifier
                                                                .weight(1f)
                                                                .size(100.dp)
                                                                .background(Color.Gray),
                                                            contentAlignment = Alignment.Center
                                                        ) {
                                                            // You can add your image here later
                                                            AsyncImage(
                                                                model = passenger.photoUrl, // Use the photoUrl directly
                                                                contentDescription = "Profile Picture",
                                                                modifier = Modifier
                                                                    .size(100.dp)
                                                                    .scale(1.2f)
                                                            )
                                                        }
                                                        Spacer(modifier = Modifier.width(16.dp))
                                                        Column(
                                                            modifier = Modifier.weight(4f)
                                                        ) {
                                                            Text(
                                                                text = "${passenger.name} ${passenger.surname}",
                                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                                    fontSize = 18.sp
                                                                )
                                                            )
                                                            Text(
                                                                text = "Phone: ${passenger.phone}",
                                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                                    fontSize = 17.sp
                                                                )
                                                            )
                                                            Text(
                                                                text = "Emergency Number: ${passenger.emergencyNumber}",
                                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                                    fontSize = 17.sp
                                                                )
                                                            )
                                                        }
                                                    }
                                                }
                                            }

                                        }
                                    }
                                }
                            }

                            // preview passengers in that trip
                            IconButton(
                                onClick = {
                                    // delete locally and from fire base
                                    tripToDelete.value = trip
                                    showDialog.value = true
                                }
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.delete_icon),
                                    contentDescription = "Delete"
                                )
                            }
                        }
                    }
                }
            }
        }
    }


    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text("Delete Trip") },
            text = { Text("Are you sure you want to delete this trip?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        // Delete the trip here
                        dbHelper.deleteTrip(tripToDelete.value!!.id)
                        // Remove the trip from the list
                        trips = trips.filter { it.id != tripToDelete.value!!.id }

                        // Delete on Firebase
                        val tripRef = database.getReference("Trips").child(tripToDelete.value!!.id)
                        tripRef.removeValue().addOnSuccessListener {
                            Toast.makeText(
                                context,
                                "Trip deleted successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                        }.addOnFailureListener { e ->
                            Toast.makeText(
                                context,
                                "Failed to delete trip: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        // Dismiss the dialog
                        showDialog.value = false
                    }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDialog.value = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }



    MaterialDialog(
        dialogState = dateDialogState,
        buttons = {
            positiveButton(text = "OK") {
                timeDialogState.show()
            }
            negativeButton(text = "Cancel") {}
        }

    ) {
        datepicker(
            initialDate = LocalDate.now(),
            title = "Pick a date",
            allowedDateValidator = { date ->
                val today = LocalDate.now()
                date >= today
            }
        ) {
            pickedDate = it
        }
    }

    MaterialDialog(
        dialogState = timeDialogState,
        buttons = {
            positiveButton(text = "OK") {
            }
            negativeButton(text = "Cancel")
        }
    ) {
        val currentTime = LocalTime.now()
        val today = LocalDate.now()

        timepicker(
            initialTime = if (pickedDate == today) currentTime.plusMinutes(5) else LocalTime.NOON,
            title = "Pick a time",
            timeRange = if (pickedDate == today) currentTime.plusMinutes(5)..LocalTime.MAX else LocalTime.MIDNIGHT..LocalTime.MAX,
        ) {
            pickedTime = it
            enableConfirmation2 = true
            isButtonClicked2 = true
            val dateAndTimeField = "$formattedDate, $formattedTime"
            tripViewModel.setTripDateAndTime(dateAndTimeField)
        }
    }

}

@Composable
fun PickUpPreview(
    context: Context,
    navController: NavHostController,
    pickUpViewModel: PickUpViewModel
) {

    val pickUpLatLng = pickUpViewModel.prevPickUPLatLng.value
    val targetLatLng = pickUpViewModel.prevTargetLatLng.value

    val passengerViewModel = PassengerViewModel()
    val midPoint = passengerViewModel.calculateMidPoint(pickUpLatLng, targetLatLng)

    val (polylinePoints, setPolylinePoints) = remember { mutableStateOf(emptyList<LatLng>()) }

    var tripDistance by remember {
        mutableStateOf(0.0)
    }
    var distanceAlpha by remember {
        mutableStateOf(0.5f)
    }

    passengerViewModel.updatePolyline(pickUpLatLng, targetLatLng, { decodedPolyline ->
        setPolylinePoints(decodedPolyline)
    }, { distance ->
        tripDistance = "%.2f".format(distance).toDouble()
        distanceAlpha = 0.9f
    })


    val distance = passengerViewModel.calculateDistance(pickUpLatLng, targetLatLng)
    val zoomLevel = when {
        distance <= 5 -> 13f
        distance <= 10 -> 12f
        distance <= 20 -> 11.5f
        distance <= 40 -> 10.5f
        distance <= 80 -> 10f
        distance <= 100 -> 9f
        else -> 8f
    }

    val uiSetting by remember { mutableStateOf(MapUiSettings()) }
    val properties by remember {
        mutableStateOf(MapProperties(mapType = MapType.NORMAL))
    }

    val cameraPosition = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(midPoint, zoomLevel)
    }

    var pricePerKm by remember {
        mutableStateOf(1.0)
    }

    if(passengerViewModel.isNetworkAvailable(context)){
        val database = FirebaseDatabase.getInstance()
        val priceRef = database.getReference("PricePerKm/price")


        priceRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                pricePerKm = dataSnapshot.getValue(Double::class.java) ?: 0.0
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle possible errors.
                println("Error reading PricePerKm: ${databaseError.message}")
            }
        })
    }else{
        passengerViewModel.ShowWifiProblemDialog(context)
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(0.dp) // Add padding to adjust the button position
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPosition,
            properties = properties,
            uiSettings = uiSetting.copy(zoomControlsEnabled = false)
        ) {
            // Start location marker
            Marker(
                state = MarkerState(position = pickUpLatLng),
                title = "Start Location",
                visible = true
            )
            // Destination location marker
            Marker(
                state = MarkerState(position = targetLatLng),
                title = "Destination Location",
                visible = true
            )
            Polyline(
                points = polylinePoints,
                color = colorResource(id = R.color.polyline_color_1),
            )
        }
        // Add a button that navigates back to the search page
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            Box(
                modifier = Modifier
                    .alpha(0.9f)
                    .padding(start = 15.dp, end = 15.dp, top = 15.dp)
                    .background(color = Color.White, shape = RoundedCornerShape(8.dp))
                    .border(width = 1.dp, color = Color.Black, shape = RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                // row for pick up location and cancel button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Pick Up: ",
                        textAlign = TextAlign.Right,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                    Text(
                        text = pickUpViewModel.prevPickUpTitle.value,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
            //display target details
            Box(
                modifier = Modifier
                    .alpha(0.9f)
                    .padding(start = 15.dp, end = 15.dp, top = 5.dp)
                    .background(color = Color.White, shape = RoundedCornerShape(8.dp))
                    .border(width = 1.dp, color = Color.Black, shape = RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                // row for target location and cancel button
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Target: ",
                        textAlign = TextAlign.Right,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                    Text(
                        text = pickUpViewModel.prevTargetTitle.value,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
            Row(                     // distance row
                modifier = Modifier
                    .alpha(distanceAlpha)
                    .padding(start = 15.dp, end = 15.dp, top = 5.dp)
                    .background(color = Color.White, shape = RoundedCornerShape(8.dp))
                    .border(width = 0.5.dp, color = Color.Black, shape = RoundedCornerShape(8.dp))
                    .padding(8.dp)
                    .alpha(0.9f),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = "Distance: ",
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
                if (tripDistance == 0.0) {
                    distanceAlpha = 1f
                    CircularProgressIndicator(
                        modifier = Modifier.size(25.dp)
                    )
                } else {
                    Text(
                        text = "$tripDistance",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
            Row(                // price
                modifier = Modifier
                    .alpha(if (tripDistance.toInt() ==0) 0f else 0.9f)
                    .padding(start = 15.dp, end = 15.dp, top = 5.dp)
                    .background(color = Color.White, shape = RoundedCornerShape(8.dp))
                    .border(width = 0.5.dp, color = Color.Black, shape = RoundedCornerShape(8.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = "price: ",
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
                Text(
                    text = "$"+"%.2f".format(tripDistance * pricePerKm),
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        modifier = Modifier
                            .weight(1f)
                            .height(55.dp)
                            .padding(5.dp)
                            .alpha(0.9f),
                        shape = RoundedCornerShape(15.dp),
                        onClick = {
                            navController.popBackStack()
                        }
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = R.drawable.back),
                                contentDescription = "location Icon",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Button(
                        modifier = Modifier
                            .weight(3f)
                            .height(55.dp)
                            .padding(5.dp)
                            .alpha(0f),
                        shape = RoundedCornerShape(15.dp),
                        onClick = {
                            //   navController.navigate("setTrips")
                        }
                    ) {
                        /* Text(
                             text = "Order Trip",
                             fontSize = 22.sp
                         )*/
                    }
                }
            }
        }
    }

}


@Composable
fun MapView(navController: NavHostController, tripViewModel: TripViewModel) {

    // var defaultLocation=  LatLng(33.8938,35.5018)
    val context = LocalContext.current

    var pickUpMarkerState by remember {
        mutableStateOf(false)
    }
    var targetMarkerState by remember {
        mutableStateOf(false)
    }
    val uiSetting by remember { mutableStateOf(MapUiSettings()) }
    val properties by remember {
        mutableStateOf(MapProperties(mapType = MapType.NORMAL))
    }

    val currentPosition by remember { mutableStateOf(LatLng(33.8938, 35.5018)) }

    var pickUpLatLng by remember {
        mutableStateOf(LatLng(33.8938, 35.5018))
    }
    var pickUpTitle by remember {
        mutableStateOf("Starting location")
    }

    var targetLatLng by remember {
        mutableStateOf(LatLng(33.8938, 35.5018))
    }
    var targetTitle by remember {
        mutableStateOf("Where to?")
    }
    var mainButtonState by remember {
        mutableStateOf("Set Starting location")
    }

    val cameraPosition = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(currentPosition, 13f)
    }

    var distanceAlpha by remember {
        mutableStateOf(0.5f)
    }

    var tripDistance by remember {
        mutableStateOf(0.0)
    }

    val passengerClass = PassengerViewModel()

    val (polylinePoints, setPolylinePoints) = remember { mutableStateOf(emptyList<LatLng>()) }

    val showDialog = remember { mutableStateOf(false) }
    val places = remember { mutableStateListOf<Place>() }

    var isLoading by remember { mutableStateOf(false) }

    if (mainButtonState == "Confirm pick up" && pickUpLatLng != targetLatLng) {
        isLoading = true  // Start loading
        passengerClass.updatePolyline(pickUpLatLng, targetLatLng, { decodedPolyline ->
            setPolylinePoints(decodedPolyline)
        }, { distance ->
            tripDistance = "%.2f".format(distance).toDouble()
        })
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(0.dp) // Add padding to adjust the button position
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPosition,
            properties = properties,
            uiSettings = uiSetting.copy(zoomControlsEnabled = false)
        ) {
            //Starting marker
            Marker(
                state = MarkerState(position = pickUpLatLng),
                title = pickUpTitle,
                visible = pickUpMarkerState
            )
            //target market
            Marker(
                state = MarkerState(position = targetLatLng),
                title = targetTitle,
                visible = targetMarkerState
            )
            if (mainButtonState == "Confirm Starting" && pickUpLatLng != targetLatLng) {
                Polyline(
                    points = polylinePoints,
                    color = colorResource(id = R.color.polyline_color_1),
                )
            }
        }
        Column(
            modifier = Modifier.fillMaxSize(),
            //  horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            //display Starting details
            Box(
                modifier = Modifier
                    .padding(start = 15.dp, end = 15.dp, top = 15.dp)
                    .background(color = Color.White, shape = RoundedCornerShape(8.dp))
                    .border(width = 1.dp, color = Color.Black, shape = RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                // row for Starting location and cancel button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = pickUpTitle.ifEmpty { "Starting location" },
                        modifier = Modifier.weight(0.9f),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    IconButton(
                        onClick = {
                            pickUpTitle = "Starting location"
                            pickUpMarkerState = false
                            mainButtonState = "Set Starting location"
                            tripDistance = 0.0
                            distanceAlpha = 0.5f
                            isLoading = false
                        },
                        modifier = Modifier
                            .weight(0.1f)
                            .size(22.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.cancel_icon),
                            contentDescription = "Cancel",
                        )
                    }
                }
            }
            //display target details
            Box(
                modifier = Modifier
                    .padding(start = 15.dp, end = 15.dp, top = 5.dp)
                    .background(color = Color.White, shape = RoundedCornerShape(8.dp))
                    .border(width = 1.dp, color = Color.Black, shape = RoundedCornerShape(8.dp))
                    .padding(8.dp)

            ) {
                // row for target location and cancel button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = targetTitle.ifEmpty { "Where to?" },
                        modifier = Modifier.weight(0.9f),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black

                    )
                    IconButton(
                        onClick = {
                            targetTitle = "Where to?"
                            targetMarkerState = false
                            if (mainButtonState != "Set Starting location") {
                                mainButtonState = "Set Target location"
                                tripDistance = 0.0
                                distanceAlpha = 0.5f
                                isLoading = false
                            }
                        },
                        modifier = Modifier
                            .weight(0.1f)
                            .size(22.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.cancel_icon),
                            contentDescription = "Cancel",
                        )
                    }
                }

            }
            Row(                                // distance row
                modifier = Modifier
                    .alpha(distanceAlpha)
                    .padding(start = 15.dp, end = 15.dp, top = 5.dp)
                    .background(color = Color.White, shape = RoundedCornerShape(8.dp))
                    .border(width = 0.5.dp, color = Color.Black, shape = RoundedCornerShape(8.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = if (tripDistance == 0.0) "distance:" else "distance: $tripDistance Km",
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                if (isLoading && tripDistance == 0.0) {
                    distanceAlpha = 1f
                    CircularProgressIndicator(
                        modifier = Modifier.size(25.dp)
                    )
                } else if (tripDistance != 0.0) {
                    distanceAlpha = 1f
                }
            }
        }
        // center marker
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            IconButton(onClick = { }) {
                Image(
                    painter = painterResource(id = R.drawable.pin3),
                    contentDescription = "marker",
                )
            }
        }

        // go to my location button

        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .padding(bottom = 80.dp)
        ) {
            IconButton(
                modifier = Modifier.size(45.dp),
                onClick = {
                    passengerClass.getCurrentLocation(context,
                        { latitude, longitude ->

                            cameraPosition.move(
                                CameraUpdateFactory.newLatLngZoom(LatLng(latitude, longitude), 13f)
                            )
                        }
                    ) {
                        // Call the function WhenItIsNull here
                        passengerClass.WhenItIsNull(context)
                    }
                }) {
                Image(
                    painter = painterResource(id = R.drawable.aim),
                    contentDescription = "Get Current Location",

                    )
            }
        }

        // search location button

        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .padding(bottom = 180.dp)
        ) {


            if (passengerClass.isNetworkAvailable(context)) {
                // retrieving Trips
                LaunchedEffect(Unit) {
                    val database = FirebaseDatabase.getInstance()
                    val ref = database.getReference("lebanon_places")


                    val postListener = object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val allPlaces = mutableListOf<Place>()
                            places.clear()
                            for (postSnapshot in dataSnapshot.children) {
                                val place = postSnapshot.getValue(Place::class.java)
                                if (place != null) {
                                    allPlaces.add(place)
                                    //  Log.d("xxxx", "Place added: ${place.title}")
                                }
                            }
                            // Update the displayed list
                            places.clear()
                            places.addAll(allPlaces)

                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            //  Log.d("xxxx", "Database error: ${databaseError.message}")
                            Toast.makeText(context, "Failed to load locations.", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                    ref.addValueEventListener(postListener)
                    //  Log.d("xxxx", "Listener added to reference")
                }

            } else {
                passengerClass.ShowWifiProblemDialog(context)
            }


            IconButton(
                modifier = Modifier.size(45.dp),
                onClick = {
                    showDialog.value = true
                }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.search2),
                    contentDescription = "Search Location",
                )
            }

            SearchLocationDialog(showDialog, places) { place ->
                // Move camera to the selected place
                cameraPosition.move(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(
                            place.latitude,
                            place.longitude
                        ), 13f
                    )
                )
            }


        }

        // centered button ( set Starting, target, confirm)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 10.dp),

            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                modifier = Modifier
                    .size(width = 220.dp, height = 50.dp),

                shape = RoundedCornerShape(15.dp),
                onClick = {

                    if (mainButtonState == "Set Starting location") {
                        pickUpLatLng = LatLng(
                            cameraPosition.position.target.latitude - 0.00001,
                            cameraPosition.position.target.longitude
                        )
                        // avoid null geolocation
                        if (passengerClass.isNetworkAvailable(context = context))
                            if (passengerClass.reverseGeocode(pickUpLatLng, context)
                                    .isNullOrEmpty()
                            ) {
                                passengerClass.ShowWifiProblemDialog(context)
                            } else {
                                pickUpTitle =
                                    passengerClass.reverseGeocode(pickUpLatLng, context).toString()
                                pickUpMarkerState = true
                                mainButtonState = if (targetTitle == "Where to?")
                                    "Set Target location"
                                else
                                    "Confirm Starting"
                            }
                        else {
                            passengerClass.ShowWifiProblemDialog(context)
                            Toast.makeText(context, "No Network", Toast.LENGTH_SHORT).show()
                        }
                    } else if (mainButtonState == "Set Target location") {
                        targetLatLng = LatLng(
                            cameraPosition.position.target.latitude - 0.00001,
                            cameraPosition.position.target.longitude
                        )
                        if (passengerClass.reverseGeocode(targetLatLng, context).isNullOrEmpty()) {
                            passengerClass.ShowWifiProblemDialog(context)
                        } else {
                            targetTitle =
                                passengerClass.reverseGeocode(targetLatLng, context).toString()

                            targetMarkerState = true

                            if (targetTitle == "Starting location")
                                mainButtonState = "Set Starting location"
                            else {
                                mainButtonState = "Confirm Starting"
                            }
                        }
                    } else if (mainButtonState == "Confirm Starting") {

                        if (pickUpLatLng == targetLatLng) {
                            Toast.makeText(
                                context,
                                "Pick up and target locations are the same",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            tripViewModel.setPickUpTitle(pickUpTitle)
                            tripViewModel.setTargetTitle(targetTitle)

                            tripViewModel.setPickUpLatLng(pickUpLatLng)
                            tripViewModel.setTargetLatLng(targetLatLng)

                            tripViewModel.setDistance(tripDistance)
                            navController.navigate("setTrips")

                            Toast.makeText(context, "Confirmation", Toast.LENGTH_SHORT)
                                .show()  //confirmation
                        }
                    }

                }) {
                Text(
                    text = mainButtonState,
                    fontSize = 18.sp

                )
            }


        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ProfileScreen(navController: NavHostController, context: Context) {
    var isEditing by remember { mutableStateOf(false) }
    var showBottomSheet by remember { mutableStateOf(false) }
    val viewModelFactory = DriverProfileVMFactory(context)
    val viewModel = viewModel<DriverProfileVM>(factory = viewModelFactory)
    val loginViewModelFactory = remember {
        LoginViewModelFactory(context)
    }
    val loginViewModel = viewModel<LoginViewModel>(factory = loginViewModelFactory)

    val sheetState = rememberModalBottomSheetState()
    val confirmDialog = remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Profile") },
                actions = {
                    if (!isEditing) {
                        IconButton(onClick = {
                            confirmDialog.value = true
                        }) {
                            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout")
                        }
                        IconButton(onClick = {
                            showBottomSheet = true
                        }) {
                            Icon(Icons.Filled.Person, contentDescription = "Accounts")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (!isEditing) {
                FloatingActionButton(
                    onClick = {
                        isEditing = true
                    },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Filled.Edit, contentDescription = "Edit")
                }
            } else {
                Column {
                    FloatingActionButton(
                        onClick = {
                            viewModel.saveProfileData()
                            isEditing = false
                        },
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(Icons.Filled.Save, contentDescription = "Save")
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    FloatingActionButton(
                        onClick = {
                            viewModel.loadProfileData()
                            isEditing = false
                        },
                        containerColor = MaterialTheme.colorScheme.error
                    ) {
                        Icon(Icons.Filled.Clear, contentDescription = "Cancel")
                    }
                }
            }
        }
    ) {
        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                },
                sheetState = sheetState
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    item {
                        Text(
                            text = "Accounts",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                    items(viewModel.users) { user ->
                        UserProfileRow(user, viewModel.currentId == user.id) {
                            loginViewModel.loginAsUser(it)
                        }
                    }
                }
                LoginDialog(context)
                Spacer(modifier = Modifier.height(25.dp))
            }
        }
        if (confirmDialog.value) {
            AlertDialog(
                title = { Text("Log Out") },
                text = { Text("Are you sure you want to log out?") },
                onDismissRequest = { confirmDialog.value = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            confirmDialog.value = false
                            viewModel.sharedPref.edit().clear().apply()
                            Intent(context, MainActivity::class.java).also {
                                context.startActivity(it)
                            }
                        }
                    ) {
                        Text("Yes")
                    }

                },
                dismissButton = {
                    TextButton(onClick = { confirmDialog.value = false }) {
                        Text("Cancel")
                    }
                })
        }

        LaunchedEffect(Unit) {
            viewModel.loadProfileData()
        }

        // rate and comment section
        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        val driverId = viewModel.sharedPref.getString("lastUserId", null)
        var rate by remember {
            mutableStateOf(0f)
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp, top = 100.dp),
            // .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!isEditing) {
                if (!driverId.isNullOrEmpty()) {
                    Text(
                        text = "Name: ${viewModel.name.value} ${viewModel.surname.value}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Phone: ${viewModel.phone.value}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    // rate


                    Text(text = "Rate", style = MaterialTheme.typography.headlineMedium)
                    Spacer(modifier = Modifier.height(2.dp))
                    HorizontalDivider() // Separator
                    Spacer(modifier = Modifier.height(8.dp))

                    if (rate == 0f) {
                        Text(
                            text = "Rate: "
                        )
                        CircularProgressIndicator(
                            modifier = Modifier.size(25.dp),
                        )
                    } else {
                        Text(
                            text = "Rate: ${"%.1f".format(rate)}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    // get the rate from firebase, adding it as fun in view model didn't work
                    val database = FirebaseDatabase.getInstance()
                    driverId?.let { database.getReference("rating").child(it) }
                        ?.addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                var sum = 0.0
                                var count = 0

                                for (ratingSnapshot in dataSnapshot.children) {
                                    val rating =
                                        ratingSnapshot.child("rate").getValue(Double::class.java)
                                    if (rating != null) {
                                        sum += rating
                                        count++
                                    }
                                }

                                if (count > 0) {
                                    val averageRating = sum / count
                                    rate =
                                        averageRating.toFloat() // Update rate variable with the average rating
                                }
                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                                // Handle possible errors.
                                println("Error reading ratings: ${databaseError.message}")
                            }
                        })

                    Spacer(modifier = Modifier.height(25.dp))
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                    // comments section

                    Text(text = "Comments", style = MaterialTheme.typography.headlineMedium)
                    Spacer(modifier = Modifier.height(2.dp))
                    HorizontalDivider() // Separator

                    Spacer(modifier = Modifier.height(10.dp))

                    // Fetch comments from Firebase
                    val comments = remember { mutableStateListOf<Map<String, String>>() }
                    LaunchedEffect(Unit) {
                        val commentsRef = database.getReference("comments")
                        commentsRef.addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                comments.clear()
                                dataSnapshot.children.mapNotNullTo(comments) {
                                    it.getValue(object :
                                        GenericTypeIndicator<Map<String, String>>() {})
                                }
                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                                // Handle possible errors.
                            }
                        })
                    }
                    // Display comments in a LazyColumn
                    var noComments by remember {
                        mutableStateOf(true)
                    }
                    LazyColumn {
                        items(comments.reversed()) { comment ->
                            Box(
                                modifier = Modifier
                                    .background(
                                        Color.Gray.copy(alpha = 0.5f),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .padding(10.dp)
                                    .fillMaxWidth()
                            ) {
                                Column {
                                    if (comment["DriverId"] == driverId) {
                                        noComments = false
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "${comment["passengerName"]}",
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                            Text(
                                                text = "${comment["commentDate"]}",
                                                style = MaterialTheme.typography.bodySmall,
                                                textAlign = TextAlign.End
                                            )
                                        }
                                        Text(
                                            text = "${comment["comment"]}",
                                            style = MaterialTheme.typography.bodyLarge,
                                            modifier = Modifier.padding(top = 8.dp)
                                        )
                                    }
                                    if (noComments) {
                                        Text(
                                            text = "No Comments yet",
                                            style = MaterialTheme.typography.bodyMedium,

                                            )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(100.dp))
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 80.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Text(text = "driver id : $driverId")      // on finish
                        CircularProgressIndicator(
                            modifier = Modifier.size(60.dp),
                        )
                    }
                }

                //Emergency call
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                Spacer(modifier = Modifier.height(25.dp))
                Text(text = "Emergency Call", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(2.dp))
                HorizontalDivider() // Separator
                Spacer(modifier = Modifier.height(10.dp))

                Button(onClick = {
                    val dialIntent = Intent(Intent.ACTION_DIAL)
                    dialIntent.data = Uri.parse("tel:${viewModel.emergencyNumber.value}")
                    context.startActivity(dialIntent)
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.siren),
                        contentDescription = "Emergency Call Icon",
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp)) // Add some spacing between the icon and the text
                    Text("Call Emergency Number")
                }
            } else {
                OutlinedTextField(
                    value = viewModel.name.value,
                    onValueChange = {
                        viewModel.name.value = it
                    },
                    label = {
                        Text("Name")
                    }
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = viewModel.surname.value,
                    onValueChange = {
                        viewModel.surname.value = it
                    },
                    label = {
                        Text("Surname")
                    }
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = viewModel.phone.value,
                    onValueChange = {
                        viewModel.phone.value = it
                    },
                    label = {
                        Text("Phone")
                    }
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = viewModel.emergencyNumber.value,
                    onValueChange = {
                        viewModel.emergencyNumber.value = it
                    },
                    label = {
                        Text("Emergency Number")
                    }
                )
            }
        }
    }
}