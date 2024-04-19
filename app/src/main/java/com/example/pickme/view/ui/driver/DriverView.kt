package com.example.pickme.view.ui.driver


import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pickme.R
import com.example.pickme.ui.passenger.ui.theme.PickMeUpTheme
import com.example.pickme.viewModel.PassengerViewModel
import com.example.pickme.viewModel.TripViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter


data class BottomNavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

class DriverView : ComponentActivity() {
    private val tripViewModel: TripViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PickMeUpTheme {
                val items = listOf(
                    BottomNavigationItem("trips", Icons.Filled.Create, Icons.Outlined.Create),
                    BottomNavigationItem("home", Icons.Filled.Home, Icons.Outlined.Home),
                    BottomNavigationItem("profile", Icons.Filled.Person, Icons.Outlined.Person)
                )
                var selectedItemIndex by remember { mutableStateOf(items.indexOfFirst { it.title == "home" }) }
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
                                    TripsScreen(navController, tripViewModel)
                                }
                                composable("home") {
                                    HomeScreen(navController)
                                }
                                composable("profile") {
                                    ProfileScreen(navController)
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
fun HomeScreen(navController: NavHostController) {

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Home screen",
            fontSize = 20.sp
        )
    }
}

@Composable
fun TripsScreen(navController: NavHostController, tripViewModel: TripViewModel) {

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "setTrips") {
        composable("setTrips") {
            SetTrips(navController, tripViewModel)
        }
        composable("mapView") {
            MapView(navController, tripViewModel)
        }
    }
}

@Composable
fun SetTrips(navController: NavHostController, tripViewModel: TripViewModel) {

    val context= LocalContext.current
    var tripTitle by remember { mutableStateOf("") }

    var startingTitle = tripViewModel.tripStartTitle.value

    var destinationTitle = tripViewModel.tripDestTitle.value

    var seats by remember { mutableStateOf(0) }

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
                .ofPattern("hh:mm")
                .format(pickedTime)
        }
    }

    val dateDialogState = rememberMaterialDialogState()
    val timeDialogState = rememberMaterialDialogState()


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
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
                modifier = Modifier.padding(start = 10.dp, top = 16.dp)
            )
        }

        TextField(
            value = tripTitle,
            onValueChange = { tripTitle = it },
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(fontSize = 25.sp),
            label = {
                Text(
                    "Trip Title",
                    style = TextStyle(fontSize = 20.sp) // Adjust the value (20) to make the text bigger
                )
            }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Seats")
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = { if (seats > 0) seats-- }
                ) {
                    Icon(
                        painterResource(id = com.example.pickme.R.drawable.remove_icon),
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
        ///////////////////////////////////////////////////////////////////////////////////////////////
        Spacer(modifier = Modifier.padding(top = 5.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 80.dp, max = 180.dp), // row of two field and location button
            horizontalArrangement = Arrangement.Center,

            ) {

            if (isButtonClicked1) {
                Box(
                    modifier = Modifier.weight(0.85f)
                ) {

                    Column(
                        //   modifier = Modifier.padding(16.dp)
                    ) {
                        Box(                        // pick up location box
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
                                .padding(8.dp)
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
                                .padding(start = 4.dp, end = 4.dp, top = 8.dp)
                                .background(
                                    color = Color.White,
                                    shape = RoundedCornerShape(8.dp)
                                )
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
                                Text(
                                    text = destinationTitle,
                                    fontSize = 18.sp,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }
                Box(                    //add button box
                    modifier = Modifier
                        .weight(0.15f)
                        .fillMaxSize()
                        .heightIn(max = 130.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = {
                            startingTitle = "Starting point"
                            destinationTitle = "Destination"
                            isButtonClicked1 = false
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
                        .size(width = 220.dp, height = 50.dp),
                    shape = RoundedCornerShape(15.dp),
                    onClick = {
                        navController.navigate("mapView")
                    }
                ) {
                    Text(
                        text = "Set location",
                        fontSize = 20.sp
                    )
                }
            }
        }

///////////////////////////////////////////////////////////////////////////////////////////////

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,

            ) {
            if (isButtonClicked2) { // calender
                Box(                        // pick up location box
                    modifier = Modifier
                        .padding(start = 4.dp, end = 12.dp, top = 15.dp)
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

            } else {

                Button(
                    modifier = Modifier
                        .size(width = 220.dp, height = 50.dp),
                    shape = RoundedCornerShape(15.dp),
                    onClick = {
                        dateDialogState.show()
                    }
                ) {
                    Text(
                        text = "Set Date",
                        fontSize = 20.sp
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {                                     //confirmation
            Button(onClick = {
                Toast.makeText(context, "Confirmation",Toast.LENGTH_SHORT).show()
            }) {
                Text(
                    text ="Confirmation"
                )
            }
        }

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
            isButtonClicked2 = true
            val dateAndTimeField = "$formattedDate, $formattedTime"
            tripViewModel.setDateAndTime(dateAndTimeField)
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

    var distance by remember {
        mutableStateOf(0.0)
    }

    val passengerClass = PassengerViewModel()

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
                            distance = 0.0
                            distanceAlpha = 0.5f
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
                                distance = 0.0
                                distanceAlpha = 0.5f
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
            Row(
                modifier = Modifier
                    .alpha(distanceAlpha)
                    .padding(start = 15.dp, end = 15.dp, top = 5.dp)
                    .background(color = Color.White, shape = RoundedCornerShape(8.dp))
                    .border(width = 0.5.dp, color = Color.Black, shape = RoundedCornerShape(8.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = if (distance == 0.0) "distance:" else "distance: $distance Km",
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
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
            IconButton(
                modifier = Modifier.size(45.dp),
                onClick = {

                }) {
                Image(
                    painter = painterResource(id = R.drawable.search2),
                    contentDescription = "Get Current Location",

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
                                distanceAlpha = 1f
                                distance =
                                    passengerClass.calculateDistance(pickUpLatLng, targetLatLng)
                            }
                        }
                    } else if (mainButtonState == "Confirm Starting") {

                        tripViewModel.setPickUpTitle(pickUpTitle)
                        tripViewModel.setTargetTitle(targetTitle)

                        tripViewModel.setPickUpLatLng(pickUpLatLng)
                        tripViewModel.setTargetLatLng(targetLatLng)

                        tripViewModel.setDistance(distance)
                        navController.navigate("setTrips")

                        Toast.makeText(context, "Confirmation", Toast.LENGTH_SHORT)
                            .show()  //confirmation
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


@Composable
fun ProfileScreen(navController: NavHostController) {

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "profile",
            fontSize = 20.sp
        )

    }
}

