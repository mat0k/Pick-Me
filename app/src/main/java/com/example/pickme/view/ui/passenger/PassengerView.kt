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
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.example.pickme.MainActivity
import com.example.pickme.PickUpAcceptedService
import com.example.pickme.PickUpService
import com.example.pickme.R
import com.example.pickme.data.model.DriverData
import com.example.pickme.data.model.LocalPickUpDbHelper
import com.example.pickme.data.model.PickUp
import com.example.pickme.data.model.Place
import com.example.pickme.ui.passenger.ui.theme.PickMeUpTheme
import com.example.pickme.viewModel.PassengerViewModel
import com.example.pickme.viewModel.PickUpViewModel
import com.example.pickme.viewModel.ProfileViewModel
import com.example.pickme.viewModel.ProfileViewModelFactory
import com.example.pickme.viewModel.TripViewModel
import com.google.android.gms.maps.CameraUpdateFactory
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
import com.example.pickme.data.model.UserDatabaseHelper
import com.example.pickme.data.model.User
import com.example.pickme.data.repository.PickUpRepository
import com.example.pickme.notifications.TripNotificationService
import com.example.pickme.view.ui.driver.DriverView
import com.example.pickme.view.ui.login.LoginViewModel
import com.example.pickme.view.ui.login.LoginViewModelFactory
import com.google.firebase.database.DatabaseReference

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

        startService(Intent(this, PickUpAcceptedService::class.java))
        stopService(Intent(this, PickUpService::class.java))
        stopService(Intent(this, TripNotificationService::class.java))
        setContent {
            PickMeUpTheme {
                val items = listOf(
                    BottomNavigationItem("Search", Icons.Filled.Search, Icons.Outlined.Search),
                    BottomNavigationItem("Home", Icons.Filled.Home, Icons.Outlined.Home),
                    BottomNavigationItem("Profile", Icons.Filled.Person, Icons.Outlined.Person)
                )
                var selectedItemIndex by remember { mutableStateOf(items.indexOfFirst { it.title == "Home" }) }
                val navController = rememberNavController()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme .background
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PickUps(context: Context, navController: NavHostController, pickUpViewModel: PickUpViewModel) {

    val sharedPref = context.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
    val currentId = sharedPref.getString("lastUserId", "")
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

    val localPickUpList = remember { mutableStateListOf<PickUp>() }
    val showDeleteConfirm = remember { mutableStateOf<PickUp?>(null) }

    val passengerViewModel = PassengerViewModel()

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

    if (pickUpViewModel.dateDialogState.value) {
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
                        if (passengerViewModel.isNetworkAvailable(context)) {
                            navController.navigate("mapView")
                        } else {
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
            Button(
                modifier = Modifier
                    .size(width = 220.dp, height = 50.dp),
                shape = RoundedCornerShape(15.dp),
                enabled = isButtonEnabled1 && isButtonEnabled2,
                onClick = {
                    pickUpViewModels.add(pickUpViewModel)
                    var passengerId = ""
                    currentId?.let {
                        currentId -> passengerId = currentId
                    }
                    // ADD PICK UP LOCAL OBJECT TO DATA BASE
                    val localPickUp = PickUp(
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
                        dateAndTime = pickUpViewModel.dateAndTime.value,
                        passengerId = passengerId,
                        price = pickUpViewModel.pickUpPrice.value.toDouble(),
                        driverId = "" // change that to actual driver id
                    )

                    // to firebase
                    val pickUpRepository = PickUpRepository()
                    val newId = pickUpRepository.addPickUp(localPickUp)
                    newId?.let {
                        localPickUp.id = it
                    }
                    // locally
                    val dbHelper = LocalPickUpDbHelper(context)
                    dbHelper.insertLocalPickUp(localPickUp)

                    // Add the new pick-up to the list
                  //  localPickUpList.add(0, localPickUp) // Add to the start of the list to show it at the top

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
            currentId?.let { databaseHelper.getAllLocalPickUps(it) }?.let { localPickUpList.addAll(it) }
        }
        var showPreviewBottomSheet by remember {
            mutableStateOf(false)
        }
        val sheetState = rememberModalBottomSheetState()
        val pickUpRepository = PickUpRepository()
        val pickUps: LiveData<List<PickUp>> = pickUpRepository.getLivePickUps(currentId?:"")
        val livePickUpList = pickUps.observeAsState(initial = emptyList())

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            items(livePickUpList.value) { pickUp -> // here now
                Log.d("driverIds", "id = ${pickUp.driverId}")
                Card(modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        //  verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = pickUp.dateAndTime,
                                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                                fontSize = 20.sp,
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Pick up: ${pickUp.pickUpTitle}",
                                style = MaterialTheme.typography.bodyLarge,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Destination: ${pickUp.targetTitle}",
                                style = MaterialTheme.typography.bodyLarge,
                                fontSize = 14.sp
                            )
                        }

                        // icons column
                        Column {
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

                            if(pickUp.driverId.isNotEmpty()) {
                                IconButton(
                                    onClick = {

                                        showPreviewBottomSheet = true
                                    }
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.preview_icon),
                                        contentDescription = "preview"
                                    )
                                }
                            }


                            IconButton(
                                onClick = { showDeleteConfirm.value = pickUp }
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.delete_icon),
                                    contentDescription = "Delete"
                                )
                            }


                        }
                    }

                    if (showPreviewBottomSheet) {          // bottom sheet
                        ModalBottomSheet(
                            modifier = Modifier
                                .padding(10.dp)
                                .fillMaxHeight(),
                            onDismissRequest = {
                                showPreviewBottomSheet = false
                            },
                            sheetState = sheetState
                        ) {
                            var driver by remember { mutableStateOf<DriverData?>(null) }
                            val viewModelFactory = remember {
                                ProfileViewModelFactory(context)
                            }
                            val viewModel = viewModel<ProfileViewModel>(factory = viewModelFactory)
                            viewModel.loadProfileData()

                            if (pickUp.driverId.isNotEmpty()) {

                                LaunchedEffect(pickUp.driverId) {
                                    driver = passengerViewModel.getDriverInfo(pickUp.driverId)
                                }
                                if (driver != null) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(10.dp),
                                        // .verticalScroll(rememberScrollState()),
                                        horizontalAlignment = Alignment.CenterHorizontally

                                    ) {
                                        // url of both images to preview:
                                        val photoUrl = driver?.photo
                                        val carPhotoUrl = driver?.carPhoto  // car photo
                                        // Text(text = "Photo URL: ${driver?.photo}")
                                        //  Text(text = "Car Photo URL: ${driver?.carPhoto}")


                                        Box(
                                            modifier = Modifier
                                                .size(100.dp)
                                                .clip(CircleShape)
                                                .background(Color.Gray), // image box
                                            contentAlignment = Alignment.Center
                                        ) {
                                            // You can add your image here later
                                            AsyncImage(
                                                model = photoUrl, // Use the photoUrl directly
                                                contentDescription = "Profile Picture",
                                                modifier = Modifier
                                                    .size(100.dp)
                                                    .scale(1.2f)
                                            )
                                        }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                                        // information
                                        Spacer(modifier = Modifier.height(14.dp))

                                        Row(
                                            horizontalArrangement = Arrangement.Center,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = " ${driver?.firstName} ${driver?.lastName}",
                                                modifier = Modifier.padding(end = 8.dp),
                                                fontSize = 20.sp
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(14.dp))

                                        // Other data
                                        Text(
                                            text = "Phone Number: ${driver?.phoneNb}",
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                        // Text(text = "Rate: ${driver?.rate}", style = MaterialTheme.typography.bodyLarge)
                                        Text(
                                            text = "Is Verified: ${driver?.isVerified}",
                                            style = MaterialTheme.typography.bodyLarge
                                        )

                                        Spacer(modifier = Modifier.height(5.dp))
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                                        // car image
                                        var showCarDialog by remember { mutableStateOf(false) }

                                        Box(
                                            modifier = Modifier.size(60.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            IconButton(onClick = { showCarDialog = true }) {
                                                Image(
                                                    painter = painterResource(id = R.drawable.car2),
                                                    contentDescription = "Car Icon",
                                                    modifier = Modifier.size(100.dp)
                                                )
                                            }
                                        }

                                        if (showCarDialog) {
                                            AlertDialog(
                                                onDismissRequest = { showCarDialog = false },
                                                title = { Text(text = "Car Image") },
                                                text = {
                                                    Box(
                                                        modifier = Modifier.size(200.dp),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        val painter = rememberAsyncImagePainter(carPhotoUrl)
                                                        Image(
                                                            painter = painter,
                                                            contentDescription = "Car Image",
                                                            modifier = Modifier.size(200.dp)
                                                        )
                                                        if (painter.state is AsyncImagePainter.State.Loading) {
                                                            CircularProgressIndicator(
                                                                modifier = Modifier
                                                                    .size(48.dp)
                                                                    .align(Alignment.Center)
                                                            )
                                                        }
                                                    }
                                                },
                                                confirmButton = {
                                                    TextButton(onClick = { showCarDialog = false }) {
                                                        Text("Close")
                                                    }
                                                }
                                            )
                                        }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                                        // rate
                                        var rate by remember {
                                            mutableStateOf(0f)
                                        }

                                        Text(
                                            text = "Rate",
                                            style = MaterialTheme.typography.headlineMedium
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        HorizontalDivider() // Separator
                                        Spacer(modifier = Modifier.height(8.dp))

                                        Text(
                                            text = "Rate: ${"%.1f".format(rate)}",
                                            style = MaterialTheme.typography.bodyLarge
                                        )

                                        // get the rate from firebase, adding it as fun in view model didn't work
                                        // calculate rate from firebase
                                        val database = FirebaseDatabase.getInstance()
                                        val ratingRef =
                                            database.getReference("rating").child(pickUp.driverId)
                                        ratingRef.addValueEventListener(object :
                                            ValueEventListener {
                                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                                var sum = 0.0
                                                var count = 0

                                                for (ratingSnapshot in dataSnapshot.children) {
                                                    val rating = ratingSnapshot.child("rate")
                                                        .getValue(Double::class.java)
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


                                        // add rate button here
                                        var showRateDialog by remember { mutableStateOf(false) } // State to control the visibility of the rate dialog
                                        var userRating by remember { mutableStateOf(1f) } // State to hold the user's rating

                                        Button(
                                            onClick = { showRateDialog = true },
                                            shape = RoundedCornerShape(4.dp) // Less rounded corners
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text("Rate this driver")
                                                Spacer(Modifier.width(12.dp)) // Add some spacing between the text and the icon
                                                Icon(
                                                    painter = painterResource(id = R.drawable.rate),
                                                    contentDescription = "Rate Icon",
                                                    modifier = Modifier.size(20.dp) // Adjust the size as needed
                                                )
                                            }
                                        }


//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                                        Spacer(modifier = Modifier.height(25.dp))
                                        // comment section
                                        Text(
                                            text = "Comments",
                                            style = MaterialTheme.typography.headlineMedium
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        HorizontalDivider() // Separator
                                        Spacer(modifier = Modifier.height(8.dp))

                                        var comment by remember { mutableStateOf("") } // State to hold the comment text
                                        var showDialog by remember { mutableStateOf(false) } // State to control the visibility of the dialog

                                        Button(
                                            onClick = { showDialog = true },
                                            shape = RoundedCornerShape(4.dp) // Less rounded corners
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text("Add a new comment")
                                                Spacer(Modifier.width(12.dp)) // Add some spacing between the text and the icon
                                                Icon(
                                                    painter = painterResource(id = R.drawable.comment),
                                                    contentDescription = "Comment Icon",
                                                    modifier = Modifier.size(20.dp) // Adjust the size as needed
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))
                                        // Fetch comments from Firebase
                                        val comments =
                                            remember { mutableStateListOf<Map<String, String>>() }
                                        LaunchedEffect(Unit) {
                                            val database = FirebaseDatabase.getInstance()
                                            val commentsRef = database.getReference("comments")
                                            commentsRef.addValueEventListener(object :
                                                ValueEventListener {
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
                                                }
                                                Spacer(modifier = Modifier.height(10.dp))
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(100.dp))
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

                                        // Rate dialog
                                        if (showRateDialog) {
                                            AlertDialog(
                                                onDismissRequest = { showRateDialog = false },
                                                title = { Text("Rate this driver") },
                                                text = {
                                                    Column {
                                                        Text(
                                                            text = "Select a rating from 0 to 5",
                                                            modifier = Modifier.padding(bottom = 6.dp)
                                                        )

                                                        StarRatingBar(
                                                            maxStars = 5,
                                                            rating = userRating,
                                                            onRatingChanged = { newRating ->
                                                                userRating = newRating
                                                            }
                                                        )

                                                        Text(
                                                            text = "Selected rating: $userRating",
                                                            modifier = Modifier.padding(top = 6.dp)
                                                        )
                                                    }
                                                },
                                                confirmButton = {
                                                    Button(onClick = {
                                                        showRateDialog = false
                                                        // Add the rating to the Firebase database
                                                        val database =
                                                            FirebaseDatabase.getInstance()
                                                        val ratingRef =
                                                            database.getReference("rating")
                                                                .child(pickUp.driverId)
                                                        val key = ratingRef.push().key
                                                        if (key != null) {
                                                            ratingRef.child(key)
                                                                .setValue(mapOf("rate" to userRating))
                                                        }
                                                    }) {
                                                        Text("OK")
                                                    }
                                                },
                                                dismissButton = {
                                                    TextButton(onClick = {
                                                        showRateDialog = false
                                                    }) {
                                                        Text("Cancel")
                                                    }
                                                }
                                            )
                                        }
                                        if (showDialog) {       //dialog to add comment
                                            AlertDialog(
                                                onDismissRequest = { showDialog = false },
                                                title = { Text("Add a new comment") },
                                                text = {
                                                    OutlinedTextField(
                                                        value = comment,
                                                        onValueChange = { comment = it },
                                                        label = { Text("Enter your comment") }
                                                    )
                                                },
                                                confirmButton = {
                                                    Button(
                                                        onClick = {
                                                            if (comment.isNotBlank()) {
                                                                // Get a reference to the Firebase Database
                                                                val database =
                                                                    FirebaseDatabase.getInstance()

                                                                // Get a reference to the "comments" node
                                                                val commentsRef =
                                                                    database.getReference("comments")

                                                                // Create a new comment object
                                                                val newComment = mapOf(
                                                                    "DriverId" to pickUp.driverId,
                                                                    "passengerName" to (viewModel.name.value + " " + viewModel.surname.value),
                                                                    "comment" to comment,
                                                                    "commentDate" to LocalDate.now()
                                                                        .format(
                                                                            DateTimeFormatter.ofPattern(
                                                                                "dd/MM/yyyy"
                                                                            )
                                                                        )
                                                                )

                                                                // Push the new comment to the "comments" node
                                                                commentsRef.push()
                                                                    .setValue(newComment)
                                                                comment = ""
                                                                showDialog = false
                                                            } else {
                                                                Toast.makeText(
                                                                    context,
                                                                    "Please enter a comment",
                                                                    Toast.LENGTH_SHORT
                                                                ).show()
                                                            }
                                                        }
                                                    ) {
                                                        Text("Add")
                                                    }
                                                },
                                                dismissButton = {
                                                    TextButton(onClick = { showDialog = false }) {
                                                        Text("Cancel")
                                                    }
                                                }
                                            )
                                        }
                                    }
                                } else {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(top = 40.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(60.dp),
                                        )
                                    }
                                }

                            } else {
                                Text(text = "No data to preview ")
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

    var isLoading by remember { mutableStateOf(false) }

    var pricePerKm by remember {
        mutableStateOf(1.0)
    }

    if(passengerClass.isNetworkAvailable(context)){
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
        passengerClass.ShowWifiProblemDialog(context)
    }
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

            if (mainButtonState == "Confirm pick up" && pickUpLatLng != targetLatLng) {
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
                    .alpha(0.91f)
                    .padding(start = 15.dp, end = 15.dp, top = 15.dp)
                    .background(color = Color.White, shape = RoundedCornerShape(8.dp))
                    .border(width = 1.dp, color = Color.Black, shape = RoundedCornerShape(8.dp))
                    .padding(8.dp)
                    .alpha(0.85f)
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
                    .alpha(0.91f)
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
                if (isLoading && tripDistance == 0.0) {
                    distanceAlpha = 0.85f
                    CircularProgressIndicator(
                        modifier = Modifier.size(25.dp)
                    )
                } else if (tripDistance != 0.0) {
                    distanceAlpha = 0.85f
                }
            }
                    // pricing row
            Row(
                modifier = Modifier
                    .alpha(if (tripDistance.toInt() == 0) 0f else 1f)
                    .padding(start = 15.dp, end = 15.dp, top = 5.dp)
                    .background(color = Color.White, shape = RoundedCornerShape(8.dp))
                    .border(width = 0.5.dp, color = Color.Black, shape = RoundedCornerShape(8.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = "price: $${"%.2f".format(tripDistance * pricePerKm)}",
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
            IconButton(onClick = {
            }) {
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
                                }
                            }
                            // Update the displayed list
                            places.clear()
                            places.addAll(allPlaces)
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                           // Log.d("xxxx", "Database error: ${databaseError.message}")
                            Toast.makeText(context, "Failed to load locations.", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                    ref.addValueEventListener(postListener)
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

                        if (pickUpLatLng == targetLatLng) {
                            Toast.makeText(
                                context,
                                "Pick up and target locations are the same",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            if (tripDistance != 0.0) {
                                pickUpViewModel.setPickUpTitle(pickUpTitle)
                                pickUpViewModel.setTargetTitle(targetTitle)

                                pickUpViewModel.setPickUpLatLng(pickUpLatLng)
                                pickUpViewModel.setTargetLatLng(targetLatLng)

                                pickUpViewModel.setDistance(tripDistance)
                                pickUpViewModel.setPickUpPrice("%.2f".format(tripDistance * pricePerKm).toDouble())
                                navController.navigate("pickUps")

                                Toast.makeText(context, "Confirmation", Toast.LENGTH_SHORT)
                                    .show()  //confirmation
                                //  Log.i("xxxx", "pick up lat lng: $pickUpLatLng target lat lng: $targetLatLng")
                            }else{
                                Toast.makeText(context,"Please wait to calculate distance",Toast.LENGTH_LONG).show()
                            }
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


@Composable
fun SearchLocationDialog(
    showDialog: MutableState<Boolean>,
    places: List<Place>,
    onPlaceSelected: (Place) -> Unit
) {
    val context = LocalContext.current // Get the current context
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
                            items(places.filter {
                                it.title.contains(
                                    input,
                                    ignoreCase = true
                                )
                            }) { place ->
                                Text(
                                    text = place.title,
                                    fontSize = 15.sp,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            input =
                                                place.title // Set the input text to the selected suggestion
                                            selectedPlace = place
                                        }
                                        .padding(5.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp)) // Add space between suggestions and Button

                    // Search Button
                    Button(
                        onClick = {
                            if (selectedPlace != null) {
                                onPlaceSelected(selectedPlace!!)
                                showDialog.value = false
                            } else {
                                Toast.makeText(context, "Location not found", Toast.LENGTH_SHORT)
                                    .show()
                            }
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
                    distanceAlpha = 0.9f
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
                    .alpha(if (tripDistance.toInt() == 0) 0f else 0.9f)
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
                            navController.navigate("pickUps")
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
    val viewModelFactory = remember {
        ProfileViewModelFactory(context)
    }
    val viewModel = viewModel<ProfileViewModel>(factory = viewModelFactory)
    viewModel.loadProfileData()
    var isEditing by remember { mutableStateOf(false) }
    var valuesChanged by remember { mutableStateOf(false) }
    val pickImageLauncher = rememberLauncherForActivityResult( // jump
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.photoUrl.value = it.toString()
            viewModel.photoChanged.value = true
        }
    }
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    val userDatabaseHelper = UserDatabaseHelper(context)
    val users: List<User> = userDatabaseHelper.getAllUsers()
    val loginViewModelFactory = remember {
        LoginViewModelFactory(context)
    }
    val loginViewModel = viewModel<LoginViewModel>(factory = loginViewModelFactory)
    val confirmDialog = remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Profile") },
                actions = {
                    if (!isEditing) {
                        IconButton(onClick = {
                            confirmDialog.value = true
                        }) {
                            Icon(Icons.Filled.ExitToApp, contentDescription = "Logout")
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
                        },
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(Icons.Default.Check, contentDescription = "Save")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    FloatingActionButton(
                        onClick = {
                            isEditing = false
                            viewModel.loadProfileData()
                        },
                        containerColor = MaterialTheme.colorScheme.error
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
                        items(users) { user ->
                            UserProfileRow(user, viewModel.currentPassengerId == user.id) {
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
                    text = {
                        Text("Are you sure you want to log out?")
                    },
                    onDismissRequest = { confirmDialog.value = false },
                    confirmButton = {
                        TextButton(onClick = {
                            confirmDialog.value = false
                            viewModel.sharedPref.edit().clear().apply()
                            Intent(context, MainActivity::class.java).also {
                                context.startActivity(it)
                            }
                            viewModel.loading.value = true
                        }) {
                            Text("Yes")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                confirmDialog.value = false
                            }
                        ) {
                            Text("Cancel")
                        }
                    })
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
        }


    }


}

@Composable
fun LoginDialog(context: Context) {
    var showDialog by remember { mutableStateOf(false) }
    val loginViewModelFactory = remember {
        LoginViewModelFactory(context)
    }
    val loginViewModel = viewModel<LoginViewModel>(factory = loginViewModelFactory)
    var loggingIn by remember {
        mutableStateOf(false)
    }
    OutlinedButton(
        onClick = { showDialog = true },
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Text("Add User")
    }
    LaunchedEffect(key1 = true) {
        loginViewModel.loginResult.collect { loginResult ->
            if (loginResult == true) {
                val intent = when (loginViewModel.getUserRole()) {
                    0 -> Intent(context, PassengerView::class.java)
                    else -> Intent(context, DriverView::class.java)
                }
                context.startActivity(intent)
            } else if (loginResult == false) {
                Toast.makeText(context, "Invalid phone number or password", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Login") },
            text = {
                Column {
                    OutlinedTextField(
                        value = loginViewModel.phoneNumber,
                        onValueChange = { loginViewModel.updatePhoneNumber(it) },
                        label = { Text("Phone Number") },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone)
                    )
                    OutlinedTextField(
                        value = loginViewModel.password,
                        onValueChange = { loginViewModel.updatePassword(it) },
                        label = { Text("Password") },
                        visualTransformation = PasswordVisualTransformation()
                    )
                    Row {
                        FilterChip(selected = loginViewModel.role == 0,
                            onClick = {
                                if (loginViewModel.role != 0) loginViewModel.updateRole(0)
                            },
                            label = { Text("Passenger") },
                            leadingIcon = {
                                if (loginViewModel.role == 0) {
                                    Icon(
                                        imageVector = Icons.Filled.Done,
                                        contentDescription = null
                                    )
                                }
                            }
                        )

                        Spacer(modifier = Modifier.width(8.dp))
                        FilterChip(selected = loginViewModel.role == 1,
                            onClick = {
                                if (loginViewModel.role != 1) loginViewModel.updateRole(1)
                            },
                            label = { Text("Driver") },
                            leadingIcon = {
                                if (loginViewModel.role == 1) {
                                    Icon(
                                        imageVector = Icons.Filled.Done,
                                        contentDescription = null
                                    )
                                }
                            }
                        )
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    loginViewModel.login()
                }) {
                    Text("Login")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showDialog = false },
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun UserProfileRow(user: User, selected: Boolean, onLoginClick: (User) -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = rememberRipple(bounded = true),
                onClick = { if (!selected) onLoginClick(user) }
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Load the user's profile picture using the Coil library

        AsyncImage(
            model = user.photoUrl,
            contentDescription = "User Profile Picture",
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
        )

        // Add a spacer for some padding between the image and the texts
        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = "${user.firstName} ${user.lastName}",
                fontWeight = FontWeight.W600,
                fontSize = 20.sp
            )

            Text(
                text = when (user.role) {
                    1 -> "Driver"
                    0 -> "Passenger"
                    else -> "Unknown"
                },
                fontWeight = FontWeight.W400,
                fontSize = 14.sp
            )

        }
        Spacer(modifier = Modifier.weight(1f))
        if (selected) {
            Icon(Icons.Filled.Done, contentDescription = "Selected")
        }
        Spacer(modifier = Modifier.width(8.dp))
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


@OptIn(ExperimentalMaterial3Api::class)
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
    val minDriverRating by remember { mutableStateOf(0) } // default minimum rating is 0
    var minAvailableSeats by remember { mutableStateOf(1) } // default minimum available seats is 1

    var showSearch by remember { mutableStateOf(false) }

    var timeRange by remember { mutableIntStateOf(0) } // Default time range is 0 hour

    val sheetState = rememberModalBottomSheetState()
    var showFilterBottomSheet by remember { mutableStateOf(false) }

    var showPreviewBottomSheet by remember {
        mutableStateOf(false)
    }

    var driverId by remember {
        mutableStateOf("")
    }

    if (tripViewModel.tripDateAndTime.value.isNotEmpty()) {
        enableConfirmation2 = true
        pickedDate = tripViewModel.pickedDate.value
    }
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
                        if (passengerViewModel.isNetworkAvailable(context)) {
                            navController.navigate("mapView2")
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
            if (isButtonClicked2 || tripViewModel.tripDateAndTime.value.isNotEmpty()) { // calender
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
                            Text(
                                text = tripViewModel.tripDateAndTime.value,                  // date & time text
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
                            enableConfirmation1 = false
                            tripViewModel.setTripDateAndTime("")
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
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(0.75f),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {                                     //confirmation
                Button(
                    modifier = Modifier
                        .weight(3f) // This will take 3/4 of the available space
                        .height(45.dp),
                    shape = RoundedCornerShape(15.dp),
                    enabled = enableConfirmation1 && enableConfirmation2,
                    onClick = {
                        // Your onClick logic here
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
                Spacer(Modifier.width(5.dp))


                Button(
                    modifier = Modifier
                        .weight(1f) // This will take 1/4 of the available space
                        .height(45.dp),
                    shape = RoundedCornerShape(15.dp),
                    onClick = {
                        showFilterBottomSheet = true
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.filter2),
                        contentDescription = "search Icon",
                        modifier = Modifier
                            .size(34.dp)
                    )
                }
            }
        }


        if (showFilterBottomSheet) {
            ModalBottomSheet(
                modifier = Modifier
                    .padding(10.dp)
                    .height(360.dp),
                onDismissRequest = {
                    showFilterBottomSheet = false
                },
                sheetState = sheetState
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(6.dp),
                ) {

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
                    /*  Row(                      // don't filter according to rate
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

                         *//* StarRatingBar(
                            maxStars = 5,
                            rating = minDriverRating.toFloat(),
                            onRatingChanged = { newRating ->
                                minDriverRating = newRating.toInt()
                            }
                        )*//*
                    }*/

                }
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
                                    // Create a new mutable map and copy the contents of the original map into it
                                    val mutableTrip = trip.toMutableMap()
                                    // Add the trip id to the mutable map
                                    mutableTrip["tripId"] = postSnapshot.key ?: ""
                                    // Add the mutable map to the allTrips list
                                    allTrips.add(mutableTrip)
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
                            // Handle possible errors.
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
                                text = "Title: ${trip["title"]}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "Starting: ${trip["starting"]}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "End: ${trip["end"]}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            /*            Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            //       horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "Rating: ${trip["rate"]}",
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                            Text(
                                                text = "\tDriver Verified: ${if (trip["verified"] as? Boolean == true) "Yes" else "No"}",
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                        }
                                        Text(
                                            text = "id: ${trip["id"]}",
                                            style = MaterialTheme.typography.bodyMedium
                                        )*/
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

                                        if (passengerViewModel.isNetworkAvailable(context)) {
                                            tripViewModel.setSelectedTripId(
                                                trip["tripId"] as? String ?: ""
                                            )
                                            navController.navigate("mapView3")
                                        } else {
                                            passengerViewModel.ShowWifiProblemDialog(context)
                                        }


                                    }
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.search_location1),
                                        contentDescription = "Trip Preview"
                                    )
                                }

                                IconButton(onClick = {

                                    driverId = trip["driverId"] as? String ?: ""
                                    showPreviewBottomSheet = true
                                }) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.preview_icon),
                                        contentDescription = "Driver Preview"
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

    if (showPreviewBottomSheet) {          // bottom sheet
        ModalBottomSheet(
            modifier = Modifier
                .padding(10.dp)
                .heightIn(min = 700.dp),
            onDismissRequest = {
                showPreviewBottomSheet = false
            },
            sheetState = sheetState
        ) {
            var driver by remember { mutableStateOf<DriverData?>(null) }
            val viewModelFactory = remember {
                ProfileViewModelFactory(context)
            }
            val viewModel = viewModel<ProfileViewModel>(factory = viewModelFactory)
            viewModel.loadProfileData()


            if (driverId.isNotEmpty()) {

                LaunchedEffect(driverId) {
                    driver = passengerViewModel.getDriverInfo(driverId)
                }
                if (driver != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp),
                        // .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally

                    ) {
                        // url of both images to preview:
                        val photoUrl = driver?.photo
                        val carPhotoUrl = driver?.carPhoto
                        // Text(text = "Photo URL: ${driver?.photo}")
                        //  Text(text = "Car Photo URL: ${driver?.carPhoto}")


                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(Color.Gray), // image box
                            contentAlignment = Alignment.Center
                        ) {
                            // You can add your image here later
                            AsyncImage(
                                model = photoUrl, // Use the photoUrl directly
                                contentDescription = "Profile Picture",
                                modifier = Modifier
                                    .size(100.dp)
                                    .scale(1.2f)
                            )
                        }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = " ${driver?.firstName} ${driver?.lastName}",
                                modifier = Modifier.padding(end = 8.dp),
                                fontSize = 20.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Other data
                        Text(
                            text = "Phone Number: ${driver?.phoneNb}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        // Text(text = "Rate: ${driver?.rate}", style = MaterialTheme.typography.bodyLarge)
                        Text(
                            text = "Is Verified: ${driver?.isVerified}",
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Spacer(modifier = Modifier.height(5.dp))
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                        // car image
                        var showCarDialog by remember { mutableStateOf(false) }

                        Box(
                            modifier = Modifier.size(60.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            IconButton(onClick = { showCarDialog = true }) {
                                Image(
                                    painter = painterResource(id = R.drawable.car2),
                                    contentDescription = "Car Icon",
                                    modifier = Modifier.size(100.dp)
                                )
                            }
                        }

                        if (showCarDialog) {
                            AlertDialog(
                                onDismissRequest = { showCarDialog = false },
                                title = { Text(text = "Car Image") },
                                text = {
                                    Box(
                                        modifier = Modifier.size(200.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        val painter = rememberAsyncImagePainter(carPhotoUrl)
                                        Image(
                                            painter = painter,
                                            contentDescription = "Car Image",
                                            modifier = Modifier.size(200.dp)
                                        )
                                        if (painter.state is AsyncImagePainter.State.Loading) {
                                            CircularProgressIndicator(
                                                modifier = Modifier
                                                    .size(48.dp)
                                                    .align(Alignment.Center)
                                            )
                                        }
                                    }
                                },
                                confirmButton = {
                                    TextButton(onClick = { showCarDialog = false }) {
                                        Text("Close")
                                    }
                                }
                            )
                        }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                        // rate
                        var rate by remember {
                            mutableStateOf(0f)
                        }

                        Text(text = "Rate", style = MaterialTheme.typography.headlineMedium)
                        Spacer(modifier = Modifier.height(2.dp))
                        HorizontalDivider() // Separator
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Rate: ${"%.1f".format(rate)}",
                            style = MaterialTheme.typography.bodyLarge
                        )

                        // get the rate from firebase, adding it as fun in view model didn't work
                        val database = FirebaseDatabase.getInstance()
                        val ratingRef = database.getReference("rating").child(driverId)
                        ratingRef.addValueEventListener(object : ValueEventListener {
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
                        Spacer(modifier = Modifier.height(3.dp))

                        // Fetch comments from Firebase
                        val comments =
                            remember { mutableStateListOf<Map<String, String>>() }
                        LaunchedEffect(Unit) {
                            val database = FirebaseDatabase.getInstance()
                            val commentsRef = database.getReference("comments")
                            commentsRef.addValueEventListener(object :
                                ValueEventListener {
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
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(100.dp))
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(60.dp),
                        )
                    }
                }

            } else {
                Text(text = "No data to preview ")
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
            tripViewModel.setFormattedDate(pickedDate)
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
            tripViewModel.setTripDateAndTime("$formattedDate, $formattedTime")
        }
    }

}

@Composable
fun StarRatingBar(
    maxStars: Int = 5,
    rating: Float,
    onRatingChanged: (Float) -> Unit
) {
    val density = LocalDensity.current.density
    val starSize = (12f * density).dp
    val starSpacing = (0.5f * density).dp

    Row(
        modifier = Modifier.selectableGroup(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..maxStars) {
            val isSelected = i <= rating
            val icon = if (isSelected) Icons.Filled.Star else Icons.Default.Star
            val iconTintColor = if (isSelected) Color(0xFFFFC700) else Color(0x20FFFFFF)
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTintColor,
                modifier = Modifier
                    .selectable(
                        selected = isSelected,
                        onClick = {
                            onRatingChanged(i.toFloat())
                        }
                    )
                    .width(starSize)
                    .height(starSize)
            )

            if (i < maxStars) {
                Spacer(modifier = Modifier.width(starSpacing))
            }
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

    val showDialog = remember { mutableStateOf(false) }
    val places = remember { mutableStateListOf<Place>() }

    var isLoading by remember { mutableStateOf(false) }

    var pricePerKm by remember {
        mutableStateOf(1.0)
    }

    if(passengerClass.isNetworkAvailable(context)){
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
        passengerClass.ShowWifiProblemDialog(context)
    }


    if (mainButtonState == "Confirm Starting" && pickUpLatLng != targetLatLng) {
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
                    .alpha(0.91f)
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
                    .alpha(0.91f)
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
            Row(                // distance row
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
                    distanceAlpha = 0.91f
                    CircularProgressIndicator(
                        modifier = Modifier.size(25.dp)
                    )
                } else if (tripDistance != 0.0) {
                    distanceAlpha = 0.91f
                }
            }
            Row(
                modifier = Modifier
                    .alpha(if (tripDistance.toInt() == 0) 0f else 0.91f)
                    .padding(start = 15.dp, end = 15.dp, top = 5.dp)
                    .background(color = Color.White, shape = RoundedCornerShape(8.dp))
                    .border(width = 0.5.dp, color = Color.Black, shape = RoundedCornerShape(8.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = "price: $${"%.2f".format(tripDistance * pricePerKm)}",
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
                                    //  Log.d("xxxx", "Place added: ${place.title}")
                                }
                            }
                            // Update the displayed list
                            places.clear()
                            places.addAll(allPlaces)
                            Log.d("xxxx", "Places list updated")
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            Log.d("xxxx", "Database error: ${databaseError.message}")
                            Toast.makeText(context, "Failed to load locations.", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                    ref.addValueEventListener(postListener)
                   // Log.d("xxxx", "Listener added to reference")
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
                                Log.i(
                                    "xxxx",
                                    "pick up lat lng set to : ${tripViewModel.tripStartLatLng.value}"
                                )
                                tripViewModel.setDistance(tripDistance)
                                navController.navigate("searchTrips")

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


@Composable
fun TripPreview(navController: NavHostController, tripViewModel: TripViewModel) {

    val passengerViewModel = PassengerViewModel()

    val startLatLng = tripViewModel.tripStartLatLng.value
    val destLatLng = tripViewModel.tripDestLatLng.value

    val tripStartLatLng = tripViewModel.searchedTripStartLatLng.value
    val tripDestLatLng = tripViewModel.searchedTripDestLatLng.value

    val (polylinePoints1, setPolylinePoints1) = remember { mutableStateOf(emptyList<LatLng>()) }
    val (polylinePoints2, setPolylinePoints2) = remember { mutableStateOf(emptyList<LatLng>()) }

    val midPoint = passengerViewModel.calculateMidPoint(tripStartLatLng, tripDestLatLng)

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

    val context = LocalContext.current
    val sharedPref = context.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
    val passengerId = sharedPref.getString("lastUserId", "")

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

    passengerViewModel.updatePolyline(startLatLng, destLatLng, { decodedPolyline ->
        setPolylinePoints1(decodedPolyline)
    }, { distance -> //
        tripDistance = "%.2f".format(distance).toDouble()
        distanceAlpha = 1f
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
        Row(            // distance
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
            if (tripDistance == 0.0) {
                distanceAlpha = 0.91f
                CircularProgressIndicator(
                    modifier = Modifier.size(25.dp)
                )
            }
        }
        Row(            // pricing row
            modifier = Modifier
                .alpha(if (tripDistance.toInt() == 0) 0f else 0.91f)
                .padding(start = 15.dp, end = 15.dp, top = 80.dp)
                .background(color = Color.White, shape = RoundedCornerShape(8.dp))
                .border(width = 0.5.dp, color = Color.Black, shape = RoundedCornerShape(8.dp))
                .padding(8.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = "price: $${"%.2f".format(tripDistance * pricePerKm)}",
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
                    }
                }

                fun bookSeat(tripId: String, database: FirebaseDatabase, tripsRef: DatabaseReference, context: Context, navController: NavHostController, passengerId: String) {
                    val availableSeatsRef = tripsRef.child(tripId).child("availableSeats")

                    availableSeatsRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            var availableSeats = dataSnapshot.getValue(Int::class.java) ?: 0
                            if (availableSeats == 0) {
                                Toast.makeText(context, "No available seats", Toast.LENGTH_SHORT).show()
                                navController.navigate("searchTrips")
                            } else {
                                // Decrease the number of available seats by 1
                                availableSeats -= 1

                                // Update the number of available seats in the database
                                availableSeatsRef.setValue(availableSeats)

                                val passengersIdsRef = tripsRef.child(tripId).child("passengersIds")
                                passengersIdsRef.push().setValue(passengerId)
                                navController.navigate("searchTrips")
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            Toast.makeText(context, "Error: ${databaseError.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
                }
                var showDialog by remember { mutableStateOf(false) }
                var count by remember {
                    mutableStateOf(0)
                }
                val tripId = tripViewModel.selectedTripId.value
                val database = FirebaseDatabase.getInstance()
                val tripsRef = database.getReference("Trips")
                val passengersIdsRef = tripsRef.child(tripId).child("passengersIds")

                Button( // book trip
                    modifier = Modifier
                        .weight(3f)
                        .height(55.dp)
                        .padding(5.dp)
                        .alpha(0.9f),
                    shape = RoundedCornerShape(15.dp),
                    onClick = {

                        passengersIdsRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {

                                for (childSnapshot in dataSnapshot.children) {
                                    if (childSnapshot.getValue(String::class.java) == passengerId) {
                                        count++
                                    }
                                }
                                if (count > 0) {
                                    // Show an alert dialog asking the user if they want to add more seats
                                    showDialog = true
                                } else {
                                    // Proceed with the booking process
                                    if (passengerId != null) {
                                        bookSeat(tripId, database, tripsRef, context, navController, passengerId)
                                    }
                                }
                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                                Toast.makeText(context, "Error: ${databaseError.message}", Toast.LENGTH_SHORT).show()
                            }
                        })

                    }
                ) {
                    Text(
                        text = "Book Trip",
                        fontSize = 22.sp
                    )
                }

                if (showDialog) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        title = { Text("Booking Confirmation") },
                        text = { Text("You have already booked $count seats. Do you want to book one more?") },
                        confirmButton = {
                            Button(onClick = {
                                // Proceed with the booking process
                                if (passengerId != null) {
                                    bookSeat(tripId, database, tripsRef, context, navController, passengerId)
                                }
                                showDialog = false
                            }) {
                                Text("Yes")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDialog = false }) {
                                Text("No")
                            }
                        }
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
