package com.example.pickme.view.ui.passenger

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
import com.example.pickme.R
import com.example.pickme.data.model.LocalPickUp
import com.example.pickme.data.model.LocalPickUpDbHelper
import com.example.pickme.data.model.Place
import com.example.pickme.ui.passenger.ui.theme.PickMeUpTheme
import com.example.pickme.viewModel.PassengerViewModel
import com.example.pickme.viewModel.PickUpViewModel
import com.example.pickme.viewModel.ProfileViewModel
import com.example.pickme.viewModel.ProfileViewModelFactory
import com.example.pickme.viewModel.TripViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
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
import java.util.Locale
import com.google.maps.android.compose.Polyline

data class BottomNavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

class PassengerView : ComponentActivity() {
    private val pickUpViewModel: PickUpViewModel by viewModels()
    private val tripViewModel: TripViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PickMeUpTheme {
                val items = listOf(
                    BottomNavigationItem("search", Icons.Filled.Search, Icons.Outlined.Search),
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
                                            if (selectedItemIndex != index) {
                                                selectedItemIndex = index
                                                navController.navigate(item.title)
                                            }
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
                                composable("search") {
                                    SearchScreen(navController, tripViewModel)
                                }
                                composable("home") {
                                    HomeScreen(navController, this@PassengerView, pickUpViewModel)
                                }
                                composable("profile") {
                                    ProfileScreen(navController, this@PassengerView)
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
fun HomeScreen(
    navController: NavHostController,
    context: Context,
    pickUpViewModel: PickUpViewModel
) {

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "pickUps") {
        composable("pickUps") {
            PickUps(context, navController, pickUpViewModel)
        }
        composable("mapView") {
            MapView(context, navController, pickUpViewModel)
        }
        composable("pickUpPreview") {
            PickUpPreview(context, navController, pickUpViewModel)
        }
    }
}


@Composable
fun PickUps(context: Context, navController: NavHostController, pickUpViewModel: PickUpViewModel) {


    var pickUpTitle by remember {
        mutableStateOf("Pick Up")
    }

    var targetTitle by remember {
        mutableStateOf("Destination")
    }

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

    var isButtonEnabled1 by remember { mutableStateOf(false) }
    var isButtonEnabled2 by remember { mutableStateOf(false) }

    val pickUpTitleTest = pickUpViewModel.pickUpTitle.value
    val targetTitleTest = pickUpViewModel.targetTitle.value

    val showDialog = remember { mutableStateOf(false) }

    val localPickUpList = remember { mutableStateListOf<LocalPickUp>() }
    val showDeleteConfirm = remember { mutableStateOf<LocalPickUp?>(null) }

    val passengerViewModel= PassengerViewModel()

    if (pickUpTitleTest.isNotEmpty()) {
        pickUpTitle = pickUpTitleTest
        isButtonEnabled1 = true
    }
    if (targetTitleTest.isNotEmpty()) {
        targetTitle = targetTitleTest
    }
    val passengerClass = PassengerViewModel()

    val pickUpViewModels = remember {
        mutableStateListOf<PickUpViewModel>()
    }

    fun resetTitles() {
        pickUpViewModel.pickUpTitle.value = ""
        pickUpViewModel.targetTitle.value = ""
        pickUpTitle = "Pick Up"
        targetTitle = "Destination"
        isButtonEnabled1 = false
    }

    if(pickUpViewModel.dateDialogState.value){
        dateDialogState.show()
        pickUpViewModel.setDialogState(false)
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Request new Pick UP",
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
                modifier = Modifier.padding(start = 10.dp, top = 16.dp)
            )

        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, top = 12.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = "Location:",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 135.dp, max = 200.dp), // row of two field and location button
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier.weight(0.8f)
            ) {

                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    Box(                        // pick up location box
                        modifier = Modifier
                            .padding(start = 4.dp, end = 12.dp, top = 4.dp)
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
                            Text(
                                text = pickUpTitle,
                                fontSize = 16.sp,
                                color = Color.Black
                            )
                        }
                    }
                    Box(                    //target location box
                        modifier = Modifier
                            .padding(start = 4.dp, end = 12.dp, top = 12.dp)
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
                            Text(
                                text = targetTitle,
                                fontSize = 16.sp,
                                color = Color.Black
                            )
                        }
                    }
                }
            }
            Box(                    //add button box
                modifier = Modifier
                    .weight(0.2f)
                    .padding(5.dp)
                    .heightIn(max = 135.dp)
            ) {
                Button(
                    modifier = Modifier
                        .fillMaxSize(),          // Fill the entire available space in the box
                    onClick = {
                        if(passengerViewModel.isNetworkAvailable(context)){
                            navController.navigate("mapView")
                        }else{
                            passengerViewModel.ShowWifiProblemDialog(context)
                        }
                    },
                    shape = MaterialTheme.shapes.medium, // Set the button shape to medium (cubic)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.location),
                            contentDescription = "location Icon",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, bottom = 10.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = "Schedule:",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // time row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier.weight(0.8f)
            ) {

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Box(                        // pick up location box
                        modifier = Modifier
                            .padding(start = 4.dp, end = 14.dp, top = 12.dp)
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
                                fontSize = 16.sp,
                                color = Color.Black
                            )
                        }
                    }
                }
            }
            Box(                    //add button box
                modifier = Modifier
                    .weight(0.2f)
                    .fillMaxSize()
                    .padding(5.dp)
            ) {
                Button(
                    onClick = {
                        dateDialogState.show()
                    },
                    shape = MaterialTheme.shapes.medium,
                    modifier
                    = Modifier.fillMaxSize(),
                    enabled = isButtonEnabled1
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.calendar),
                            contentDescription = "Calendar Icon",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(                                     // second confirm
                modifier = Modifier
                    .size(width = 220.dp, height = 50.dp),
                shape = RoundedCornerShape(15.dp),
                enabled =
                isButtonEnabled1 && isButtonEnabled2,
                onClick = {
                    pickUpViewModels.add(pickUpViewModel)
                    // showDialog.value = true

                    //     ADD PICK UP LOCAL OBJECT TO DATA BASE
                    val localPickUp = LocalPickUp(
                        pickUpTitle = pickUpTitle, targetTitle = targetTitle,
                        pickUpLatLng = LatLng(
                            pickUpViewModel.pickUpLatLng.value.latitude,
                            pickUpViewModel.pickUpLatLng.value.longitude
                        ),
                        targetLatLng = LatLng(
                            pickUpViewModel.targetLatLng.value.latitude,
                            pickUpViewModel.targetLatLng.value.longitude
                        ),
                        distance = pickUpViewModel.distance.value,
                        dateAndTime = pickUpViewModel.dateAndTime.value
                    )
                    val databaseHelper = LocalPickUpDbHelper(context)
                    val rowId = databaseHelper.insertLocalPickUp(localPickUp)
                    if (rowId != -1L) {
                        // LocalPickUp object added successfully
                    } else {
                        // Error adding LocalPickUp object
                    }
                    resetTitles()
                }) {
                Text(
                    text = "Confirm pick up",
                    fontSize = 20.sp
                )
            }
        }
        // pick ups HISTORY

        val databaseHelper = LocalPickUpDbHelper(context)
        LaunchedEffect(Unit) {
            localPickUpList.addAll(databaseHelper.getAllLocalPickUps())
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            items(localPickUpList.reversed()) { localPickUp ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp),

                    ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(color = Color.White, shape = RoundedCornerShape(8.dp))
                            .border(
                                width = 1.dp,
                                color = Color.Black,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = localPickUp.dateAndTime,
                                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                                fontSize = 17.sp,
                                color = Color.Black,
                            )
                            Text(
                                text = localPickUp.pickUpTitle,
                                style = MaterialTheme.typography.bodyLarge,
                                fontSize = 14.sp,
                                color = Color.Black,
                            )
                            Text(
                                text = localPickUp.targetTitle,
                                style = MaterialTheme.typography.bodyLarge,
                                fontSize = 14.sp,
                                color = Color.Black,
                            )
                        }
                        Column {
                            IconButton(
                                onClick = { showDeleteConfirm.value = localPickUp }
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.delete_icon),
                                    contentDescription = "Delete"
                                )
                            }
                            IconButton(
                                onClick = {
                                    pickUpViewModel.setPrevPickUpTitle(localPickUp.pickUpTitle)
                                    pickUpViewModel.setPrevTargetTitle(localPickUp.targetTitle)
                                    pickUpViewModel.setPrevPickUPLatLng(localPickUp.pickUpLatLng)
                                    pickUpViewModel.setPrevTargetLatLng(localPickUp.targetLatLng)
                                    pickUpViewModel.setPrevDistance(localPickUp.distance)

                                    if(passengerViewModel.isNetworkAvailable(context)){
                                        navController.navigate("pickUpPreview") //here
                                    }else{
                                        passengerViewModel.ShowWifiProblemDialog(context)
                                    }
                                }
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.preview_icon),
                                    contentDescription = "Preview"
                                )
                            }
                        }
                    }

                    val itemToDelete = showDeleteConfirm.value
                    if (itemToDelete != null) {
                        AlertDialog(
                            onDismissRequest = { showDeleteConfirm.value = null },
                            title = { Text("Confirm Delete") },
                            text = { Text("Are you sure you want to delete this item?") },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        databaseHelper.deleteLocalPickUp(itemToDelete.id)
                                        localPickUpList.remove(itemToDelete)
                                        showDeleteConfirm.value = null
                                    }
                                ) {
                                    Text("Yes")
                                }
                            },
                            dismissButton = {
                                TextButton(
                                    onClick = { showDeleteConfirm.value = null }
                                ) {
                                    Text("No")
                                }
                            }
                        )
                    }
                }

            }
        }


    }


    if (showDialog.value) {
        passengerClass.InfoDialog(pickUpViewModel, context)
        showDialog.value = false
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
            isButtonEnabled2 = true
            val dateAndTimeField = "$formattedDate, $formattedTime"
            pickUpViewModel.setDateAndTime(dateAndTimeField)
        }
    }


}



@Composable
fun MapView(context: Context, navController: NavHostController, pickUpViewModel: PickUpViewModel) {

    // var defaultLocation=  LatLng(33.8938,35.5018)

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
        mutableStateOf("Pick up location")
    }

    var targetLatLng by remember {
        mutableStateOf(LatLng(33.8938, 35.5018))
    }
    var targetTitle by remember {
        mutableStateOf("Where to?")
    }
    var mainButtonState by remember {
        mutableStateOf("Set Pick Up location")
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

    if(mainButtonState== "Confirm pick up"){
        passengerClass.updatePolyline(pickUpLatLng, targetLatLng, { decodedPolyline ->
            setPolylinePoints(decodedPolyline)
        }, { distance ->
            tripDistance = "%.2f".format(distance).toDouble()
            distanceAlpha= 1f
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

            //pick up marker
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

            if(mainButtonState== "Confirm pick up") {
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
            //display pick up details
            Box(
                modifier = Modifier
                    .padding(start = 15.dp, end = 15.dp, top = 15.dp)
                    .background(color = Color.White, shape = RoundedCornerShape(8.dp))
                    .border(width = 1.dp, color = Color.Black, shape = RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                // row for pick up location and cancel button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = pickUpTitle.ifEmpty { "Pick up location" },
                        modifier = Modifier.weight(0.9f),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    IconButton(
                        onClick = {
                            pickUpTitle = "Pick up location"
                            pickUpMarkerState = false
                            mainButtonState = "Set Pick Up location"
                            tripDistance = 0.0
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
                            if (mainButtonState != "Set Pick Up location") {
                                mainButtonState = "Set Target location"
                                tripDistance = 0.0
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

            }       //distance row
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
                    text = if (tripDistance == 0.0) "distance:" else "distance: $tripDistance Km",
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
                    painter = painterResource(id =R.drawable.pin3),
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

                    Log.d("xxxx", "Database reference obtained")

                    val postListener = object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            Log.d("xxxx", "Data change detected")
                            val allPlaces = mutableListOf<Place>()
                            places.clear()
                            for (postSnapshot in dataSnapshot.children) {
                                val place = postSnapshot.getValue(Place::class.java)
                                if (place != null) {
                                    allPlaces.add(place)
                                    Log.d("xxxx", "Place added: ${place.title}")
                                }
                            }
                            // Update the displayed list
                            places.clear()
                            places.addAll(allPlaces)
                            Log.d("xxxx", "Places list updated")
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            Log.d("xxxx", "Database error: ${databaseError.message}")
                            Toast.makeText(context, "Failed to load locations.", Toast.LENGTH_SHORT).show()
                        }
                    }
                    ref.addValueEventListener(postListener)
                    Log.d("xxxx", "Listener added to reference")
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
                // cameraPosition.move(CameraUpdateFactory.newLatLngZoom(LatLng(place.latitude, place.longitude), 13f))
            }


        }

        // centered button ( set pick up, target, confirm)
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

                    if (mainButtonState == "Set Pick Up location") {
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
                                if (targetTitle == "Where to?") {
                                    mainButtonState = "Set Target location"
                                } else {
                                    mainButtonState = "Confirm pick up"
                                }
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

                            if (targetTitle == "Pick up location")
                                mainButtonState = "Set Pick Up location"
                            else {
                                mainButtonState = "Confirm pick up"
                            }
                        }
                    } else if (mainButtonState == "Confirm pick up") {

                        pickUpViewModel.setPickUpTitle(pickUpTitle)
                        pickUpViewModel.setTargetTitle(targetTitle)

                        pickUpViewModel.setPickUpLatLng(pickUpLatLng)
                        pickUpViewModel.setTargetLatLng(targetLatLng)

                        pickUpViewModel.setDistance(tripDistance)
                        navController.navigate("pickUps")

                        Toast.makeText(context, "Confirmation", Toast.LENGTH_SHORT)
                            .show()  //confirmation
                        Log.i(
                            "xxxx",
                            "pick up lat lng: $pickUpLatLng target lat lng: $targetLatLng"
                        )
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
fun SearchLocationDialog(
    showDialog: MutableState<Boolean>,
    places: List<Place>,
    onPlaceSelected: (Place) -> Unit
) {
    if (showDialog.value) {
        var input by remember { mutableStateOf("") }
        var selectedPlace by remember { mutableStateOf<Place?>(null) }

        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text(text = "Search Location") },
            text = {
                Column {
                    // AutoComplete TextField
                    OutlinedTextField(
                        value = input,
                        onValueChange = { newValue ->
                            input = newValue
                            selectedPlace = places.find {
                                it.title.contains(newValue, ignoreCase = true)
                            }
                        },
                        label = { Text("Search") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp)) // Add some space between the TextField and Button

                    // Autocomplete suggestions
                    if (input.length >= 2) { // Only show suggestions when input length is 2 or more
                        LazyColumn {
                            items(places.filter { it.title.contains(input, ignoreCase = true) }) { place ->
                                Text(
                                    text = place.title,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            input = place.title // Set the input text to the selected suggestion
                                            selectedPlace = place
                                        }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp)) // Add space between suggestions and Button

                    // Search Button
                    Button(
                        onClick = {
                            selectedPlace?.let { onPlaceSelected(it) }
                            showDialog.value = false
                        },
                        modifier = Modifier
                            .fillMaxWidth() // Make the button as wide as the TextField
                            .height(35.dp), // Keep the height as before
                        shape = RoundedCornerShape(15.dp),
                    ) {
                        Text("Search")
                    }
                }
            },
            confirmButton = { }
        )
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

    val passengerViewModel= PassengerViewModel()
    val midPoint= passengerViewModel.calculateMidPoint(pickUpLatLng,targetLatLng)

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
            Row(
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
                Text(
                    text = "$tripDistance",
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
                            navController.navigate("pickUps")
                        }
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = R.drawable.back),
                                contentDescription = "location Icon",
                                modifier = Modifier.size(24.dp)
                            )
                        }                    }

                    Button(
                        modifier = Modifier
                            .weight(3f)
                            .height(55.dp)
                            .padding(5.dp)
                            .alpha(0.9f),
                        shape = RoundedCornerShape(15.dp),
                        onClick = {
                            pickUpViewModel.setPickUpTitle(pickUpViewModel.prevPickUpTitle.value)
                            pickUpViewModel.setTargetTitle(pickUpViewModel.prevTargetTitle.value)
                            pickUpViewModel.setPickUpLatLng(pickUpViewModel.prevPickUPLatLng.value)
                            pickUpViewModel.setTargetLatLng(pickUpViewModel.prevTargetLatLng.value)
                            pickUpViewModel.setDistance(pickUpViewModel.prevDistance.value)
                            pickUpViewModel.setDialogState(true)
                            navController.navigate("pickUps")
                        }
                    ) {
                        Text(
                            text = "Order Trip",
                            fontSize = 22.sp
                            )
                    }
                }
            }
        }
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavHostController, context: Context) {
    context.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
    val viewModelFactory = remember {
        ProfileViewModelFactory(context)
    }
    val viewModel = viewModel<ProfileViewModel>(factory = viewModelFactory)
    var isEditing by remember { mutableStateOf(false) }
    var valuesChanged by remember { mutableStateOf(false) }
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.photoUrl.value = it.toString()
            viewModel.photoChanged.value = true
        }
    }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Profile") },
                actions = {
                    if (!isEditing) {
                        IconButton(onClick = {
                            viewModel.sharedPref.edit().clear().apply()
                            Intent(context, MainActivity::class.java).also {
                                context.startActivity(it)
                            }
                            viewModel.loading.value = true
                        }) {
                            Icon(Icons.Filled.ExitToApp, contentDescription = "Logout")
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
                    }
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                }
            } else {
                Column {
                    FloatingActionButton(
                        onClick = {
                            isEditing = false
                            viewModel.loading.value = true
                            viewModel.saveProfileData()
                            viewModel.loading.value = false
                        }
                    ) {
                        Icon(Icons.Default.Check, contentDescription = "Save")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    FloatingActionButton(
                        onClick = {
                            isEditing = false
                            viewModel.loadProfileData()
                        }
                    ) {
                        Icon(Icons.Default.Clear, contentDescription = "Cancel")
                    }
                }
            }
        }
    ) { pad ->
        Column(
            modifier = Modifier
                .padding(pad)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        CircleShape
                    )
            ) {
                AsyncImage(
                    model = viewModel.photoUrl.value,
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(100.dp)
                        .scale(1.2f)
                )
                if (isEditing) {
                    IconButton(
                        onClick = {
                            pickImageLauncher.launch("image/*")
                        },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .background(Color.White, CircleShape)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = viewModel.name.value,
                onValueChange = {
                    if (isEditing) viewModel.name.value = it
                    valuesChanged = true
                },
                label = { Text("Name") },
                enabled = isEditing
            )

            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = viewModel.surname.value,
                onValueChange = {
                    if (isEditing) viewModel.surname.value = it
                    valuesChanged = true
                },
                label = { Text("Surname") },
                enabled = isEditing
            )

            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = viewModel.phone.value,
                onValueChange = {
                    if (isEditing) viewModel.phone.value = it
                    valuesChanged = true
                },
                label = { Text("Phone") },
                enabled = isEditing,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone)
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = viewModel.emergencyNumber.value,
                onValueChange = {
                    if (isEditing) viewModel.emergencyNumber.value = it
                    valuesChanged = true
                },
                label = { Text("Emergency Number") },
                enabled = isEditing,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone)
            )
            if (viewModel.loading.value) {
                CircularProgressIndicator()
            }
        }
    }

}


@Composable
fun SearchScreen(navController: NavHostController, tripViewModel: TripViewModel) {

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "searchTrips") {
        composable("searchTrips") {
            SearchTrip(navController, tripViewModel)
        }
        composable("mapView2") {
            TripMap(navController, tripViewModel)
        }
        composable("mapView3") {
            TripPreview(navController, tripViewModel)
        }
    }

}

@Composable
fun SearchTrip(navController: NavHostController, tripViewModel: TripViewModel) {

    val context = LocalContext.current

    var startingTitle = tripViewModel.tripStartTitle.value

    var destinationTitle = tripViewModel.tripDestTitle.value

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
    val passengerViewModel: PassengerViewModel = PassengerViewModel()

    var searchRadius by remember { mutableStateOf(1) } // default radius is 1 km
    var isDriverVerified by remember { mutableStateOf(false) }
    var minDriverRating by remember { mutableStateOf(0) } // default minimum rating is 0
    var minAvailableSeats by remember { mutableStateOf(1) } // default minimum available seats is 1

    var showSearch by remember { mutableStateOf(false) }

    var timeRange by remember { mutableIntStateOf(0) } // Default time range is 0 hour

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 2.dp)
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Search for a Trip",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
                modifier = Modifier.padding(start = 10.dp, top = 0.dp)
            )
        }
        ///////////////////////////////////////////////////////////////////////////////////////////////
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 50.dp, max = 140.dp), // row of two field and location button
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
                Box(                    //clear button box
                    modifier = Modifier
                        .weight(0.15f)
                        .fillMaxSize()
                        .heightIn(max = 80.dp),
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
                        .size(width = 220.dp, height = 50.dp),
                    shape = RoundedCornerShape(15.dp),
                    onClick = {
                        if(passengerViewModel.isNetworkAvailable(context)){
                            navController.navigate("mapView2")
                        }else{
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
                        text = "Set location",
                        fontSize = 20.sp
                    )
                }
            }
        }

///////////////////////////////////////////////////////////////////////////////////////////////

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 50.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,         // date and time adder

        ) {
            if (isButtonClicked2) { // calender
                Box(
                    modifier = Modifier.weight(0.85f)
                ) {
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
                }
                Box(                    //clear button box
                    modifier = Modifier
                        .weight(0.15f)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = {
                            enableConfirmation2 = false
                            isButtonClicked2 = false
                            isButtonClicked1 = false
                            enableConfirmation1 = false
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        contentPadding = PaddingValues(0.dp),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "edit icon",
                                modifier = Modifier.size(20.dp)
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
                        dateDialogState.show()
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.calendar),
                        contentDescription = "Calendar Icon",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp)) // Add some spacing between the icon and the text
                    Text(
                        text = "Trip Date",
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
            var expanded by remember { mutableStateOf(false) }



            if (expanded) {
                Column {

                    Row(
                        // verified check box
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Checkbox(
                            checked = isDriverVerified,
                            onCheckedChange = { isDriverVerified = it },
                        )
                        Text("Only show trips from verified drivers")
                    }
                    Row(
                        // search radius
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Text(text = if (searchRadius == 6) "Search Radius: any" else "Search Radius: $searchRadius km")
                        Slider(
                            modifier = Modifier
                                .width(200.dp),
                            value = searchRadius.toFloat(),
                            onValueChange = { searchRadius = it.toInt() },
                            valueRange = 1f..6f, // allow radius from 1 km to 10 km
                            steps = 5,
                        )
                    }
                    Row(
                        // time range
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Text(text = if (timeRange == 0) "Search Time interval: any" else "Search Time interval: $timeRange ${if (timeRange > 1) "hours" else "hour"}")
                        Slider(
                            modifier = Modifier
                                .width(200.dp),
                            value = timeRange.toFloat(),
                            onValueChange = { timeRange = it.toInt() },
                            valueRange = 0f..3f, // Time range from 0 to 3 hours
                            steps = 3,
                        )
                    }
                    Row(
                        // minimum rating
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Text("Minimum Driver Rating: $minDriverRating")
                        Slider(
                            modifier = Modifier
                                .width(200.dp),
                            value = minDriverRating.toFloat(),
                            onValueChange = { minDriverRating = it.toInt() },
                            valueRange = 0f..5f, // allow rating from 0 to 5
                            steps = 5,
                        )
                    }
                    Row(
                        // minimum available seats slider
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Text("Minimum Available Seats: $minAvailableSeats")
                        Slider(
                            modifier = Modifier
                                .width(200.dp),
                            value = minAvailableSeats.toFloat(),
                            onValueChange = { minAvailableSeats = it.toInt() },
                            valueRange = 1f..5f, // allow available seats from 1 to 5
                            steps = 5,
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Button(
                            modifier = Modifier
                                .size(width = 180.dp, height = 45.dp),
                            shape = RoundedCornerShape(8.dp),
                            onClick = { expanded = false })
                        {
                            Icon(
                                painter = painterResource(id = R.drawable.collapse),
                                contentDescription = "collapse Icon",
                                modifier = Modifier
                                    .size(26.dp)
                                    .padding(end = 6.dp)
                            )
                            Text(
                                text = "Search filter",
                                fontSize = 15.sp
                            )
                        }
                    }
                }

            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Button(
                        modifier = Modifier
                            .size(width = 180.dp, height = 45.dp),
                        shape = RoundedCornerShape(8.dp),
                        onClick = { expanded = true })
                    {
                        Icon(
                            painter = painterResource(id = R.drawable.expand),
                            contentDescription = "expand Icon",
                            modifier = Modifier
                                .size(26.dp)
                                .padding(end = 6.dp)
                        )
                        Text(
                            text = "Search filter",
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }

        ///////////////////////////////////////////////////////////////////////////////////////////////
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {                                     //confirmation
            Button(
                modifier = Modifier
                    .size(width = 200.dp, height = 45.dp),
                shape = RoundedCornerShape(15.dp),
                enabled = enableConfirmation1 && enableConfirmation2,
                onClick = {
                    val starting = startingTitle
                    val end = destinationTitle
                    val startingLatLng = tripViewModel.tripStartLatLng.value
                    val destinationLatLng = tripViewModel.tripDestLatLng.value
                    val tripDistance = tripViewModel.distance.value
                    val date = formattedDate
                    val time = formattedTime
                    val searchRad = searchRadius
                    val isVerified = isDriverVerified
                    val minRat = minDriverRating
                    val minSeats = minAvailableSeats

                    showSearch = true
                }) {
                Icon(
                    painter = painterResource(id = R.drawable.search3),
                    contentDescription = "search Icon",
                    modifier = Modifier
                        .size(24.dp)
                        .padding(start = 5.dp)
                )
                Text(
                    modifier = Modifier.padding(start = 15.dp),
                    text = "Search",
                    fontSize = 20.sp
                )
            }
        }
        ///////////////////////////////////////////////////////////////////////////////////////////////

        if (passengerViewModel.isNetworkAvailable(context)) {
            if (showSearch) {
                // retrieving Trips
                val tripList = remember { mutableStateListOf<Map<String, Any>>() }

                LaunchedEffect(
                    isDriverVerified,
                    formattedDate,
                    minDriverRating,
                    minAvailableSeats,
                    searchRadius,
                    formattedDate,
                    timeRange,
                    tripViewModel
                ) {
                    val database = FirebaseDatabase.getInstance()
                    val myRef = database.getReference("Trips")

                    val postListener = object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val allTrips = mutableListOf<Map<String, Any>>()
                            tripList.clear()
                            for (postSnapshot in dataSnapshot.children) {
                                val trip = postSnapshot.getValue(object :
                                    GenericTypeIndicator<Map<String, Any>>() {})
                                if (trip != null) {
                                    allTrips.add(trip)
                                }
                            }
                            // Filter the trips
                            val filteredTrips =
                                passengerViewModel.filterTrips(
                                    allTrips,
                                    isDriverVerified,
                                    formattedDate,
                                    minDriverRating,
                                    minAvailableSeats,
                                    searchRadius,
                                    formattedTime,
                                    timeRange,
                                    tripViewModel
                                )
                            // Update the displayed list
                            tripList.clear()
                            tripList.addAll(filteredTrips)
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            Toast.makeText(context, "Failed to load trips.", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                    myRef.addValueEventListener(postListener)
                }

                LazyColumn {
                    items(tripList) { trip ->
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "${trip["date"]} At ${trip["time"]}",
                                style = MaterialTheme.typography.headlineMedium
                            )
                            Text(
                                text = "Starting: ${trip["starting"]}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "End: ${trip["end"]}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                //       horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Rating: ${trip["rate"]}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "    Seats: ${trip["seats"]}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            Text(
                                text = "Driver Verified: ${if (trip["verified"] as? Boolean == true) "Yes" else "No"}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                IconButton(
                                    onClick = {
                                        // get trips starting and destination lat lng here
                                        val tripStartLatLngMap =
                                            trip["startingLatLng"] as? Map<String, Double>
                                                ?: emptyMap()
                                        val tripDestinationLatLngMap =
                                            trip["destinationLatLng"] as? Map<String, Double>
                                                ?: emptyMap()

                                        val tripStartLatLng = LatLng(
                                            tripStartLatLngMap["latitude"] ?: 0.0,
                                            tripStartLatLngMap["longitude"] ?: 0.0
                                        )
                                        val tripDestLatLng = LatLng(
                                            tripDestinationLatLngMap["latitude"] ?: 0.0,
                                            tripDestinationLatLngMap["longitude"] ?: 0.0
                                        )
                                        Log.i(
                                            "trip",
                                            "start lat lng: ${tripStartLatLng.latitude}, ${tripStartLatLng.longitude}"
                                        )
                                        Log.i(
                                            "trip",
                                            "dest lat lng: ${tripDestLatLng.latitude}, ${tripDestLatLng.longitude}"
                                        )

                                        tripViewModel.setSearchedTripStartLatLng(tripStartLatLng)
                                        tripViewModel.setSearchedTripDestLatLng(tripDestLatLng)

                                        if(passengerViewModel.isNetworkAvailable(context)){
                                            navController.navigate("mapView3")
                                        }else{
                                            passengerViewModel.ShowWifiProblemDialog(context)
                                        }


                                    }
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.preview_icon),
                                        contentDescription = "Preview"
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else {
            passengerViewModel.ShowWifiProblemDialog(context)
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
            enableConfirmation2 = true
            isButtonClicked2 = true
            val dateAndTimeField = "$formattedDate, $formattedTime"
            tripViewModel.setDateAndTime(dateAndTimeField)
        }
    }

}

@Composable
fun TripMap(navController: NavHostController, tripViewModel: TripViewModel) {

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

    if(mainButtonState == "Confirm Starting"){
        passengerClass.updatePolyline(pickUpLatLng, targetLatLng, { decodedPolyline ->
            setPolylinePoints(decodedPolyline)
        }, { distance ->
            tripDistance = "%.2f".format(distance).toDouble()
            distanceAlpha=1f
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
            if(mainButtonState == "Confirm Starting"){
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
                    text = if (tripDistance == 0.0) "distance:" else "distance: $tripDistance Km",
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
                            }
                        }
                    } else if (mainButtonState == "Confirm Starting") {

                        tripViewModel.setPickUpTitle(pickUpTitle)
                        tripViewModel.setTargetTitle(targetTitle)

                        tripViewModel.setPickUpLatLng(pickUpLatLng)
                        tripViewModel.setTargetLatLng(targetLatLng)
                        Log.i(
                            "xxxx",
                            "pick up lat lng set to : ${tripViewModel.tripStartLatLng.value}"
                        )
                        tripViewModel.setDistance(tripDistance)
                        navController.navigate("searchTrips")

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
fun TripPreview(navController: NavHostController, tripViewModel: TripViewModel) {

    val passengerViewModel = PassengerViewModel()

    val startLatLng = tripViewModel.tripStartLatLng.value
    val destLatLng = tripViewModel.tripDestLatLng.value

    val tripStartLatLng = tripViewModel.searchedTripStartLatLng.value
    val tripDestLatLng = tripViewModel.searchedTripDestLatLng.value

    val (polylinePoints1, setPolylinePoints1) = remember { mutableStateOf(emptyList<LatLng>()) }
    val (polylinePoints2, setPolylinePoints2) = remember { mutableStateOf(emptyList<LatLng>()) }

    val midPoint= passengerViewModel.calculateMidPoint(tripStartLatLng,tripDestLatLng)

    val distance = passengerViewModel.calculateDistance(tripStartLatLng, tripDestLatLng)
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
    var tripDistance by remember {
        mutableStateOf(0.0)
    }
    var distanceAlpha by remember {
        mutableStateOf(0.5f)
    }
    passengerViewModel.updatePolyline(startLatLng, destLatLng, { decodedPolyline ->
        setPolylinePoints1(decodedPolyline)
    }, { distance -> //
        tripDistance = "%.2f".format(distance).toDouble()
        distanceAlpha=1f
    })

    passengerViewModel.updatePolyline(tripStartLatLng, tripDestLatLng, { decodedPolyline ->
        setPolylinePoints2(decodedPolyline)
    }, { distance ->
    })

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
                state = MarkerState(position = startLatLng),
                title = "Start Location",
                visible = true
            )
            // Destination location marker
            Marker(
                state = MarkerState(position = destLatLng),
                title = "Destination Location",
                visible = true
            )
            Marker(
                state = MarkerState(position = tripStartLatLng),
                title = "Searched Trip Start Location",
                visible = true,
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)
            )
            // Searched trip destination location marker
            Marker(
                state = MarkerState(position = tripDestLatLng),
                title = "Searched Trip Destination Location",
                visible = true,
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)
            )
            Polyline(
                points = polylinePoints1,
                color = colorResource(id = R.color.polyline_color_1),

                )
            Polyline(
                points = polylinePoints2,
                color = colorResource(id = R.color.polyline_color_2),

                )

        }
        Row(
            modifier = Modifier
                .alpha(distanceAlpha)
                .padding(start = 15.dp, end = 15.dp, top = 30.dp)
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
        }
        // Add a button that navigates back to the search page
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
                        navController.navigate("searchTrips")
                    }
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.back),
                            contentDescription = "location Icon",
                            modifier = Modifier.size(24.dp)
                        )
                    }                    }

                Button(
                    modifier = Modifier
                        .weight(3f)
                        .height(55.dp)
                        .padding(5.dp)
                        .alpha(0.9f),
                    shape = RoundedCornerShape(15.dp),
                    onClick = {

                        navController.navigate("searchTrips")
                    }
                ) {
                    Text(
                        text = "Book Trip",
                        fontSize = 22.sp
                    )
                }
            }
        }
    }
}















                            // not used
@Composable
fun ShowAllTrips() {
    val context = LocalContext.current
    val passengerViewModel: PassengerViewModel = PassengerViewModel()

    if (passengerViewModel.isNetworkAvailable(context)) {
        // retrieving Trips
        val tripList = remember { mutableStateListOf<Map<String, Any>>() }

        LaunchedEffect(Unit) {
            val database = FirebaseDatabase.getInstance()
            val myRef = database.getReference("Trips")

            val postListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val allTrips = mutableListOf<Map<String, Any>>()
                    tripList.clear()
                    for (postSnapshot in dataSnapshot.children) {
                        val trip = postSnapshot.getValue(object :
                            GenericTypeIndicator<Map<String, Any>>() {})
                        if (trip != null) {
                            allTrips.add(trip)
                        }
                    }
                    // Update the displayed list
                    tripList.clear()
                    tripList.addAll(allTrips)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(context, "Failed to load trips.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            myRef.addValueEventListener(postListener)
        }
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Button(onClick = {
                deleteOldTrips()
            }) {
                Text(text = "Delete old trips")
            }
            LazyColumn {
                items(tripList) { trip ->
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "${trip["date"]} At ${trip["time"]}",
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Text(
                            text = "Starting: ${trip["starting"]}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "End: ${trip["end"]}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Rating: ${trip["rate"]}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Seats: ${trip["seats"]}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Driver Verified: ${if (trip["verified"] as? Boolean == true) "Yes" else "No"}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

        }
    } else {
        passengerViewModel.ShowWifiProblemDialog(context)
    }
}

fun deleteOldTrips() {
    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("Trips")

    val postListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            for (postSnapshot in dataSnapshot.children) {
                val trip =
                    postSnapshot.getValue(object : GenericTypeIndicator<Map<String, Any>>() {})
                if (trip != null) {
                    val tripDateStr = trip["date"] as? String ?: ""
                    val formatter = DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH)
                    val tripDate = LocalDate.parse(tripDateStr, formatter)

                    if (tripDate.isBefore(LocalDate.now())) {
                        // This trip is before the current date, delete it
                        postSnapshot.ref.removeValue()
                    }
                }
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
            // Handle possible errors.
        }
    }
    myRef.addListenerForSingleValueEvent(postListener)
}

fun addPlacesToFirebase() {
    val database = FirebaseDatabase.getInstance()
    val ref = database.getReference("lebanon_places")

    // Define the list of places with their titles, latitude, and longitude
    val places = listOf(
        Place("Tripoli", 34.4367, 35.8497),
        Place("Tyre", 33.2733, 35.1939),
    )

    // Loop through the list and add each place to Firebase
    places.forEach { place ->
        ref.child(place.title).setValue(place)
            .addOnSuccessListener {
                println("Place ${place.title} added successfully!")
            }
            .addOnFailureListener { e ->
                println("Error adding place ${place.title}: $e")
            }
    }
}
